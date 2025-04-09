package com.boom.aiobrowser

//import com.appsflyer.AppsFlyerLib
//import com.appsflyer.AppsFlyerProperties
//import com.appsflyer.attribution.AppsFlyerRequestListener
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.adrevenue.AppsFlyerAdRevenue
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.NetworkUtils.OnNetworkStatusChangedListener
import com.boom.aiobrowser.ad.AioADDataManager.initAD
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.data.TopicBean
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.data.VideoUIData
import com.boom.aiobrowser.firebase.FirebaseManager.initFirebase
import com.boom.aiobrowser.model.AppViewModel
import com.boom.aiobrowser.nf.NFManager
import com.boom.aiobrowser.nf.NFReceiver
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.CloakManager
import com.boom.aiobrowser.tools.SubscribeManager
import com.boom.aiobrowser.tools.UIManager
import com.boom.aiobrowser.tools.download.DownloadCacheManager
import com.boom.aiobrowser.tools.event.ProtectedUnPeekLiveData
import com.boom.aiobrowser.tools.isOtherPkg
import com.boom.aiobrowser.tools.video.VideoManager.initVideo
import com.boom.downloader.VideoDownloadManager
import com.boom.downloader.model.VideoTaskItem
import com.boom.refresh.layout.SmartRefreshLayout
import com.boom.refresh.layout.header.ClassicsFooter
import com.boom.refresh.layout.header.ClassicsHeader
import com.facebook.FacebookSdk
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class APP: Application(), ViewModelStoreOwner {
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
    var copyText = ""

    var showForeground = false
    val appModel by lazy {
        ViewModelProvider(APP.instance).get(AppViewModel::class.java)
    }

    val installTime: Long by lazy {
        try {
            instance.packageManager.getPackageInfo(APP.instance.packageName, 0).firstInstallTime
        } catch (e: Exception) {
            0L
        }
    }

    // 0 未默认未弹窗弹窗
    @Volatile
    var showPopLevel = 0

    companion object{
        lateinit var instance:APP
        val isDebug = BuildConfig.DEBUG

        val jumpLiveData  by lazy { ProtectedUnPeekLiveData<JumpData>() }
        val jumpResumeData  by lazy { ProtectedUnPeekLiveData<Int>() }
        val jumpWebLiveData  by lazy { ProtectedUnPeekLiveData<JumpData>() }
        val engineLiveData  by lazy { ProtectedUnPeekLiveData<Int>() }
        val bottomLiveData  by lazy { ProtectedUnPeekLiveData<String>() }
        val videoScanLiveData by lazy { ProtectedUnPeekLiveData<Int>() }
        val videoNFLiveData by lazy { ProtectedUnPeekLiveData<VideoDownloadData>() }
        val downloadPageLiveData by lazy { ProtectedUnPeekLiveData<String>() }

        val videoLiveData by lazy { ProtectedUnPeekLiveData<HashMap<Int, VideoTaskItem>>() }
        val videoUpdateLiveData by lazy { ProtectedUnPeekLiveData<String>() }
        val topicLiveData by lazy { ProtectedUnPeekLiveData<MutableList<TopicBean>>() }
        val homeTopicLiveData by lazy { ProtectedUnPeekLiveData<MutableList<TopicBean>>() }
        val topicJumpData by lazy { ProtectedUnPeekLiveData<TopicBean>() }
        val homeTabLiveData by lazy { ProtectedUnPeekLiveData<MutableList<JumpData>>() }
        val homeJumpLiveData by lazy { ProtectedUnPeekLiveData<Int>() }
        val showRateLiveData by lazy { ProtectedUnPeekLiveData<Int>() }
        val locationListUpdateLiveData by lazy { ProtectedUnPeekLiveData<Int>() }
        val trendNewsComplete by lazy { ProtectedUnPeekLiveData<Int>() }
        val downloadButtonLiveData by lazy { ProtectedUnPeekLiveData<Int>() }
        val videoGuideLiveData by lazy { ProtectedUnPeekLiveData<Int>() }
        val firstToDownloadLiveData by lazy { ProtectedUnPeekLiveData<Int>() }

    }

    var toNewsFrom = 0
    @Volatile
    var isAllowNFPreload = false
    override fun onCreate() {
        super.onCreate()
        instance = this
        CoroutineScope(Dispatchers.IO).launch{
            configWebViewCacheDirWithAndroidP()
        }
        if (isOtherPkg(this)) return
        initOtherSdk()
        registerActivityLifecycleCallbacks(lifecycleApp)
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
        MMKV.initialize(this@APP)
        initFirebase()
        initAD()
        initFB()
        initAdjust()
        initAF()
        CloakManager().getCloak()
        CacheManager.showForeground+=1
        CoroutineScope(Dispatchers.IO).launch{
            //1. mmkv
            runCatching {
                runCatching {
                    //设置全局的Header构建器
                    SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
                        layout.setPrimaryColorsId(R.color.white, R.color.black) //全局设置主题颜色
                        ClassicsHeader(context) //.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header
                    }
                    //设置全局的Footer构建器
                    SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout -> //指定为经典Footer，默认是 BallPulseFooter
                        ClassicsFooter(context).setDrawableSize(20f)
                    }
                }
                PointEvent.posePoint(PointEventKey.session_st)
//                CleanConfig.initCleanConfig()

                initVideo()
                initOther()
            }
            delay(3000)
            if (APP.instance.lifecycleApp.stack.isNullOrEmpty()){
                //后台拉活
                isAllowNFPreload = true
            }
        }
        registerAny()
        initNFConfig()
//        if (isDebug){
//            var map5 = "   {\n" +
//                    "        \"NEWS_ID\": \"11593148900048897\",\n" +
//                    "        \"body\": \"Internauta viralizou nas redes sociais após afirmar que avó estragou seu \\u0027Spotify Wrapped\\u0027\",\n" +
//                    "        \"image\": \"https://medias.itatiaia.com.br/dims4/default/34a0380/2147483647/strip/true/crop/1067x601+0+0/resize/1000x563!/quality/90/?url\\u003dhttps%3A%2F%2Fk2-prod-radio-itatiaia.s3.us-east-1.amazonaws.com%2Fbrightspot%2F1f%2F5a%2F71d4ee094407abb06b312895cc27%2Fpadre-marcelo-rossi.jpg\",\n" +
//                    "        \"title\": \"Jovem culpa avó por Padre Marcelo Rossi parar no topo do Spotify Wrapped e ele tem a melhor reação\",\n" +
//                    "        \"KEY_NOW_NAV_TYPE\": \"3\"\n" +
//                    "    }"
//                NFManager.showFCM(stringToMap(map5))
//            AppLogs.dLog(NFManager.TAG,"language:${Locale.getDefault().language}  country:${Locale.getDefault().country}")
//        }
        NFManager.notifyByTimerTask()
//        registerDirectory(APP.instance, DirectoryProvider::class.java, true)
    }

    private fun initAF() {
        var startTime = System.currentTimeMillis()
        runCatching {
            var key = "Ed2FymhzHg3qqYpyH8Z9Eg"
            val conversionDataListener  = object : AppsFlyerConversionListener{
                override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                    if (CacheManager.afFrom.equals("organic", true).not()) return
                    // ...
                    data?.map {
                        AppLogs.dLog(APP.instance.TAG, " af onConversionDataSuccess: ${it.key} = ${it.value}")
                    }
                    runCatching {
                        var afStatus = data?.get("af_status") as? String
                        if (afStatus.isNullOrBlank()) return
                        CacheManager.afFrom = afStatus
                        PointEvent.posePoint(PointEventKey.af_suc, Bundle().apply {
                            var userStatus = 1
                            if (afStatus.equals("organic",true)){
                                userStatus = 1
                            }else{
                                userStatus = 0
                            }
                            putInt("af_user",userStatus)
                            putLong(PointValueKey.load_time,(System.currentTimeMillis()-startTime)/1000)
                            putString(PointValueKey.network,afStatus)
                        })
                        UIManager.isBuyUser()
                    }
                }
                override fun onConversionDataFail(error: String?) {
                    AppLogs.dLog(instance.TAG, "af onConversionDataFail :  $error")
                }
                override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
                    // Must be overriden to satisfy the AppsFlyerConversionListener interface.
                    // Business logic goes here when UDL is not implemented.
                    data?.map {
                        AppLogs.dLog(APP.instance.TAG, "af onAppOpenAttribution: ${it.key} = ${it.value}")
                    }
                }
                override fun onAttributionFailure(error: String?) {
                    // Must be overriden to satisfy the AppsFlyerConversionListener interface.
                    // Business logic goes here when UDL is not implemented.
                    AppLogs.dLog(APP.instance.TAG, "af onAttributionFailure :  $error")
                }
            }
            AppsFlyerLib.getInstance().setCustomerUserId(CacheManager.getID())
            AppsFlyerLib.getInstance().init(key, conversionDataListener, applicationContext)
            AppsFlyerLib.getInstance().setDebugLog(isDebug)
            AppsFlyerLib.getInstance().start(this)
            val afRevenueBuilder = AppsFlyerAdRevenue.Builder(this)
            AppsFlyerAdRevenue.initialize(afRevenueBuilder.build())
        }
    }

    private fun initNFConfig() {
    }

    private fun initAdjust() {
        var startTime = System.currentTimeMillis()
        runCatching {
            val config = AdjustConfig(this, "ih2pm2dr3k74", AdjustConfig.ENVIRONMENT_SANDBOX)
            config.setOnAttributionChangedListener {
                AppLogs.dLog(UIManager.TAG,"adjust network:${it.network}")
                if (CacheManager.adJustFrom.contains("organic", true).not()) return@setOnAttributionChangedListener
                if (it.network.isNullOrBlank()) return@setOnAttributionChangedListener
                CacheManager.adJustFrom = it.network
//                adjust_user：
//                【0】买量用户
//                【1】自然量用户
                var userType = 1
                if (it.network.contains("organic", true).not()){
                    // 买量用户
                    userType = 0
                }
                PointEvent.posePoint(PointEventKey.adjust_suc,Bundle().apply {
                    putInt(PointValueKey.adjust_user,userType)
                    putLong(PointValueKey.load_time,(System.currentTimeMillis()-startTime)/1000)
                    putString(PointValueKey.network,it.network)
                })
                UIManager.isBuyUser()
            }
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

    fun getWebConfig() {
        if (CacheManager.isFirstStart.not()){
            CloakManager().getCloak()
        }
//        appModel.getCampaign()
        appModel.getWebConfig()
        appModel.getTrendsNews()
        appModel.getTopics()
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


    override val viewModelStore: ViewModelStore
        get() = ViewModelStore()
}