package com.boom.aiobrowser.point

import android.net.Uri
import android.os.Build
import com.android.installreferrer.api.ReferrerDetails
import com.blankj.utilcode.util.DeviceUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.BuildConfig
import com.boom.aiobrowser.net.intercept.HttpLoggingInterceptorNew
import com.boom.aiobrowser.point.GeneralParams.telephonyManager
import com.boom.aiobrowser.point.Install.isLoading
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.net.Proxy
import java.util.UUID
import java.util.concurrent.TimeUnit
import java.util.logging.Level

object PointManager {

    var NET_TAG = "Point_Net"

    val pointUrl by lazy {
        StringBuffer(
            if (APP.isDebug) "https://test-tune.safebrowsers.net/stannic/folktale"
            else "https://tune.safebrowsers.net/indulge/educable"
        )
            .append("?sought=${GeneralParams.urlEncoder(APP.instance.GID)}")
            .append("&allotted=${GeneralParams.urlEncoder(System.currentTimeMillis().toString())}")
            .append("&trait=${GeneralParams.urlEncoder(BuildConfig.VERSION_NAME)}")
            .append("&sappy=${GeneralParams.urlEncoder(telephonyManager.networkOperator)}")
            .toString()
    }

    fun getPonitNet() : OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptorNew(NET_TAG)
        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptorNew.Level.BODY)
        loggingInterceptor.setColorLevel(Level.INFO)

        var okHttpClientBuilder = OkHttpClient.Builder()
            .apply {
                if (!APP.isDebug) { // 非测试环境
                    proxy(Proxy.NO_PROXY) // 禁止抓包
                }
            }
            .retryOnConnectionFailure(false)
            //其他配置
            .addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    var newRequest = chain.request().newBuilder()
                        .header("warden", GeneralParams.urlEncoder(GeneralParams.locale.country))
                        .header("paycheck", GeneralParams.urlEncoder(Build.VERSION.RELEASE))
                        .header("hackle", GeneralParams.urlEncoder(UUID.randomUUID().toString()))
                        .build()
                    return chain.proceed(newRequest)
                }
            })
        if (APP.isDebug) {
            okHttpClientBuilder.addInterceptor(loggingInterceptor)
        }
        return okHttpClientBuilder.build()
    }


    interface PointCallback {

        fun onSuccess(response: Response)

        fun onFailed() {}

    }


    fun sendValue(jsonObject: JSONObject?, tag:String?=null,defaultTryCount:Int, callback: PointCallback?=null){
        if (jsonObject == null)return
        val request = Request.Builder()
        request.url(pointUrl)
        request.post(jsonObject.toString().toRequestBody("application/json".toMediaType()))
        getPonitNet().newCall(request.build())?.enqueue(object :
            Callback {
            override fun onFailure(call: Call, e: IOException) {
                AppLogs.eLog(NET_TAG,e.stackTraceToString())
                tbaFailedTry(jsonObject,tag,defaultTryCount,callback)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == 200) callback?.onSuccess(response) else tbaFailedTry(jsonObject,tag,defaultTryCount,callback,"onResponse")
            }
        })
    }


    private fun tbaFailedTry(jsonObject: JSONObject?, tag:String?=null, defaultTryCount:Int,callback: PointCallback?=null,from:String="onFailure") {
        AppLogs.dLog(NET_TAG,"tbaFailedTry tag:${tag} defaultTryCount:${defaultTryCount} from:${from}")
        var tempCount = defaultTryCount-1
        if (tempCount > 0) {
            CoroutineScope(Dispatchers.IO).launch {
                delay(3000L)
                withContext(Dispatchers.Main){
                    sendValue(jsonObject,tag,tempCount,callback)
                }
            }
        }else{
            callback?.onFailed()
        }
    }

    fun postEvent(jsonObject: JSONObject, tag:String?="", callback: PointCallback?=null) {
        AppLogs.dLog(NET_TAG,"postEvent tag:${tag}")
        sendValue(jsonObject,tag,3,callback)
    }

}