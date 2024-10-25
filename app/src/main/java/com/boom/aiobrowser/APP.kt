package com.boom.aiobrowser

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.webkit.WebSettings
import android.webkit.WebView
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.NetworkUtils.OnNetworkStatusChangedListener
import com.boom.aiobrowser.ad.AioADDataManager.initAD
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.data.WebConfigData
import com.boom.aiobrowser.firebase.FirebaseManager.initFirebase
import com.boom.aiobrowser.net.NetController
import com.boom.aiobrowser.nf.NFReceiver
import com.boom.aiobrowser.point.Install
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.clean.CleanConfig
import com.boom.aiobrowser.tools.download.DownloadCacheManager
import com.boom.aiobrowser.tools.event.ProtectedUnPeekLiveData
import com.boom.aiobrowser.tools.isOtherPkg
import com.boom.aiobrowser.tools.video.VideoManager.initVideo
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.boom.downloader.VideoDownloadManager
import com.boom.downloader.model.VideoTaskItem
import com.facebook.FacebookSdk
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class APP: Application() {
    var lifecycleApp = BrowserLifeCycle()

    var TAG = "AIO_APP"
    // 如果为false 证明当前有正在进行的 启动页 这时不再额外启动
    var allowShowStart = true
    var isGoOther = false

    var cleanComplete = false
    var isHideSplash = false
    var GID=""
    var webUA=""
    var firstInsertHomeAD = true

    var clickSetBrowser = false

    @Volatile
    var shareText = ""

    var showForeground = false


    companion object{
        lateinit var instance:APP
        val isDebug = BuildConfig.DEBUG

        val jumpLiveData  by lazy { ProtectedUnPeekLiveData<JumpData>() }
        val jumpResumeData  by lazy { ProtectedUnPeekLiveData<Int>() }
        val jumpWebLiveData  by lazy { ProtectedUnPeekLiveData<JumpData>() }
        val engineLiveData  by lazy { ProtectedUnPeekLiveData<Int>() }
        val bottomLiveData  by lazy { ProtectedUnPeekLiveData<String>() }
        val videoScanLiveData by lazy { ProtectedUnPeekLiveData<VideoDownloadData>() }
        val videoNFLiveData by lazy { ProtectedUnPeekLiveData<VideoDownloadData>() }

        val videoLiveData by lazy { ProtectedUnPeekLiveData<HashMap<Int, VideoTaskItem>>() }
        val videoUpdateLiveData by lazy { ProtectedUnPeekLiveData<String>() }

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        CoroutineScope(Dispatchers.IO).launch{
            configWebViewCacheDirWithAndroidP()
        }
        if (isOtherPkg(this)) return
        registerActivityLifecycleCallbacks(lifecycleApp)
        initOtherSdk()
    }


    private fun configWebViewCacheDirWithAndroidP() {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                var processName = getProcessName()
                if (packageName != processName) {
                    WebView.setDataDirectorySuffix(processName)
                }
            }
        }.onFailure {
            AppLogs.eLog(TAG,it.stackTraceToString())
        }
    }

    private fun initOtherSdk() {
        CoroutineScope(Dispatchers.IO).launch{
            //1. mmkv
            runCatching {
                MMKV.initialize(this@APP)
                initFirebase()
                initAD()
                CleanConfig.initCleanConfig()
                initFB()
                initAdjust()
                initVideo()
                initOther()
                Install.requestRefer(instance,0,{})
                PointEvent.posePoint(PointEventKey.session_st)
            }
        }
        CoroutineScope(Dispatchers.IO).launch{
            while (true){
                delay(60*60*1000)
                //session_st
                PointEvent.posePoint(PointEventKey.session_st)
            }
        }
        registerAny()
    }

    private fun initAdjust() {
        runCatching {
            val config = AdjustConfig(this, "ih2pm2dr3k74", AdjustConfig.ENVIRONMENT_SANDBOX)
            config.setDelayStart(5.5)
            Adjust.addSessionCallbackParameter("customer_user_id",CacheManager.getID())
            config.setOnEventTrackingSucceededListener {
                AppLogs.dLog(APP.instance.TAG, "adjust 初始化成功 event：${it.eventToken}")
            }
            Adjust.onCreate(config)
        }.onFailure {
            AppLogs.eLog(APP.instance.TAG,"adjust init error :${it.stackTraceToString()}")
        }

    }

    private fun initFB() {
        runCatching {
            FacebookSdk.setClientToken("")
            FacebookSdk.sdkInitialize(APP.instance)
        }.onFailure {
            AppLogs.eLog(APP.instance.TAG,"fb init error :${it.stackTraceToString()}")
        }
    }

    private fun registerAny() {
        NetworkUtils.registerNetworkStatusChangedListener(netBack)
        registerNotifyReceiver(instance)
    }

    var nfReceiver: NFReceiver? = null
    fun registerNotifyReceiver(context: Context) {
        // logsi(FJST, "registerNotifyReceiver()111")
        nfReceiver = NFReceiver()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.registerReceiver(nfReceiver, IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_OFF)
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_USER_PRESENT)
                addAction(NFEnum.NF_DOWNLOAD_VIDEO.channelId)
            }, Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(nfReceiver, IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_OFF)
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_USER_PRESENT)
                addAction(NFEnum.NF_DOWNLOAD_VIDEO.channelId)
            })
        }
    }

    suspend fun initOther() {
        CacheManager.videoDownloadTempList = mutableListOf()
        runCatching {
            var gid = CacheManager.GID
            if (gid.isNullOrEmpty()) {
                GID = Uri.encode(AdvertisingIdClient.getAdvertisingIdInfo(APP.instance).id)
                CacheManager.GID = GID
            } else {
                GID = gid
            }
            AppLogs.dLog(TAG, "gid获取成功---${GID}")
        }.onFailure {
            AppLogs.dLog(TAG, "gid获取失败---${it.stackTraceToString()}")
        }

        runCatching {
            webUA = WebSettings.getDefaultUserAgent(APP.instance) ?: ""
        }.onFailure {
            AppLogs.eLog(TAG,"webConfig获取失败---"+it.stackTraceToString())
        }
    }

    var netBack = object : OnNetworkStatusChangedListener {
        override fun onDisconnected() {
            AppLogs.dLog(TAG, "network onDisconnected")
            CoroutineScope(Dispatchers.IO).launch{
                var list = DownloadCacheManager.queryDownloadModelOther()
                var urlList = mutableListOf<String>()
                list?.forEach {
                    urlList.add(it.url?:"")
                }
                VideoDownloadManager.getInstance().pauseDownloadTask(urlList)
            }
        }

        override fun onConnected(networkType: NetworkUtils.NetworkType?) {
            AppLogs.dLog(TAG, "onConnected")
        }
    }


    fun getWebConfig(){
        CoroutineScope(Dispatchers.IO).launch {
            CacheManager.pageList = mutableListOf()
            CacheManager.fetchList = mutableListOf()
            var pageList = CacheManager.pageList
            var fetchList = CacheManager.fetchList
            NetController.getWebConfig().data?.forEach {
                if (it.kdepen == "FETCH"){
                    fetchList.add(WebConfigData().apply {
                        cType = it.kdepen
                        cUrl = it.dsurpr
                        cDetail = NetController.getWebDetail(it.dsurpr,it.kdepen).data?:""
                    })
                }else if (it.kdepen == "PAGE"){
                    pageList.add(WebConfigData().apply {
                        cType = it.kdepen
                        cUrl = it.dsurpr
                        cDetail = NetController.getWebDetail(it.dsurpr,it.kdepen).data?:""
                    })
                }
            }
            CacheManager.pageList = pageList
            CacheManager.fetchList = fetchList
        }
    }
}