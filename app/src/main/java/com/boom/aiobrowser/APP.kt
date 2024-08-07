package com.boom.aiobrowser

import android.app.Application
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.tools.event.ProtectedUnPeekLiveData
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.LinkedList

class APP: Application() {
    var lifecycleApp = BrowserLifeCycle()

    companion object{
        lateinit var instance:APP
        val isDebug = BuildConfig.DEBUG

        val jumpLiveData  by lazy { ProtectedUnPeekLiveData<JumpData>() }
        val engineLiveData  by lazy { ProtectedUnPeekLiveData<Int>() }
        val bottomLiveData  by lazy { ProtectedUnPeekLiveData<String>() }
        var linkedUrlList= LinkedList<String>()


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