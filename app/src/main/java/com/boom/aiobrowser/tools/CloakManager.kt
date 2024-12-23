package com.boom.aiobrowser.tools

import android.net.Uri
import android.os.Build
import com.blankj.utilcode.util.DeviceUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.BuildConfig
import com.boom.aiobrowser.point.GeneralParams
import com.boom.aiobrowser.point.GeneralParams.urlEncoder
import com.boom.aiobrowser.point.PointManager
import com.boom.aiobrowser.tools.CacheManager.getID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class CloakManager {
    fun getCloak(){
        if (CacheManager.cloakValue.isNotEmpty())return
        var url =
            "https://highroad.safebrowsers.net/pastor/frown/fungus?magma=${urlEncoder(BuildConfig.APPLICATION_ID)}" +
                    "&buckaroo=${"scylla"}&trait=${urlEncoder(BuildConfig.VERSION_NAME)}&hardy=${getID()}" +
                    "&allotted=${System.currentTimeMillis()}&kidnap=${urlEncoder(DeviceUtils.getModel())}&paycheck=${urlEncoder(Build.VERSION.RELEASE)}" +
                    "&sought=${urlEncoder(APP.instance.GID)}&referent=${urlEncoder(getID())}"

        getNewsClock(url,"getClock", callBack = {
            CacheManager.cloakValue = it
        })
    }

    fun getNewsClock(url:String, tag:String?=null, callBack: (content:String) -> Unit={}){
        val request = Request.Builder().get().url(url)
        PointManager.getPonitNet().newCall(request.build())?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                cloakFailedTry(url,tag,callBack)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == 200){
                    val bodyStr = response.body?.string() ?: ""
                    callBack.invoke(bodyStr)
                }  else cloakFailedTry(url,tag,callBack)
            }
        })
    }

    var clockTryCount = 20

    fun cloakFailedTry(url: String,tag:String?=null,callBack: (content:String) -> Unit={}) {
        if (clockTryCount > 0) {
            clockTryCount--
            getNewsClock(url,tag,callBack)
        }
    }
}