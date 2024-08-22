package com.boom.aiobrowser

import android.app.Application
import android.os.Build
import android.webkit.WebView
import com.boom.aiobrowser.ad.AioADDataManager.initAD
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.firebase.FirebaseManager.initFirebase
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CleanDataManager.z
import com.boom.aiobrowser.tools.clean.CleanConfig
import com.boom.aiobrowser.tools.event.ProtectedUnPeekLiveData
import com.boom.aiobrowser.tools.isOtherPkg
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class APP: Application() {
    var lifecycleApp = BrowserLifeCycle()

    var TAG = "AIO_APP"
    // 如果为false 证明当前有正在进行的 启动页 这时不再额外启动
    var allowShowStart = true
    var isGoOther = false

    companion object{
        lateinit var instance:APP
        val isDebug = BuildConfig.DEBUG

        val jumpLiveData  by lazy { ProtectedUnPeekLiveData<JumpData>() }
        val engineLiveData  by lazy { ProtectedUnPeekLiveData<Int>() }
        val bottomLiveData  by lazy { ProtectedUnPeekLiveData<String>() }

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
//        AppLogs.dLog(TAG,"test:${z("l2DWQm4YwLbZFALHwaIojw==")}")
//        AppLogs.dLog(TAG,"test:${z("DNfI+dH+oZBelg9WhrUsmQ==")}")
//        AppLogs.dLog(TAG,"test:${z("9QaXiXZwoRpLXuXGp4Sphg==")}")
//        AppLogs.dLog(TAG,"test:${z("0Hv3t6TxAeH94Cxqm127vjwKjbgOqD38txypA4YdaOWCxD80AW4K1mvU//VRD9NE")}")

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
            }
        }
    }
}