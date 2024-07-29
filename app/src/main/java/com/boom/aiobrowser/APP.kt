package com.boom.aiobrowser

import android.app.Application
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class APP: Application() {
    var lifecycleApp = BrowserLifeCycle()

    companion object{
        lateinit var instance:APP
        val isDebug = BuildConfig.DEBUG
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        registerActivityLifecycleCallbacks(lifecycleApp)
        initOtherSdk()
    }

    private fun initOtherSdk() {
        CoroutineScope(Dispatchers.IO).launch{
            //1. mmkv
            runCatching {
                MMKV.initialize(this@APP)
            }
        }
    }
}