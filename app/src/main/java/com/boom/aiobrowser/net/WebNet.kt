package com.boom.aiobrowser.net

import android.annotation.SuppressLint
import android.util.Base64
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.BuildConfig
import com.boom.aiobrowser.net.intercept.HttpLoggingInterceptorNew
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.decryptNet
import com.boom.aiobrowser.tools.getCurrentCountryCode
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.Proxy
import java.util.Locale
import java.util.logging.Level

object WebNet {

    private fun getNetHttp(): OkHttpClient {

        //TODO:master not commit
        val loggingInterceptor =
            HttpLoggingInterceptorNew(TAG)
        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptorNew.Level.BODY)
        //log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.INFO)
        val builder = OkHttpClient.Builder()
            .apply {
                if (APP.isDebug.not()) { // 非测试环境
                    proxy(Proxy.NO_PROXY) // 禁止抓包
                }
            }
            //其他配置
//            .addInterceptor(object : Interceptor {
//                @SuppressLint("SuspiciousIndentation")
//                override fun intercept(chain: Interceptor.Chain): Response {
//                    var language = Locale.getDefault().language
//                    var newRequestBuilder = chain.request().newBuilder()
//                    newRequestBuilder
////                        .header("X-Phate", BuildConfig.APPLICATION_ID)
//                        .header("X-Phate", "com.fast.safe.browser")
//                        .header("X-Dtroub-Icup", CacheManager.getID())
//                        .header("X-Cwar", getCurrentCountryCode())
//                    return chain.proceed(newRequestBuilder.build())
//                }
//            })
//            .addInterceptor(object : Interceptor {
//                override fun intercept(chain: Interceptor.Chain): Response {
//                    val request: Request = chain.request()
//                    val url = request.url
//                    val response: Response = chain.proceed(request)
//                    val body: ResponseBody = response.body ?: return response
//                    val contentType: MediaType? = body.contentType()
//                    val byteArr: ByteArray = body.bytes() // consumed
//                    runCatching {
//                        var valueDecoded = ByteArray(0)
//                        runCatching {
//                            valueDecoded = Base64.decode(byteArr, Base64.DEFAULT)
//                        }
//                        val string = String(decryptNet(valueDecoded)!!)
//                        AppLogs.dLog(TAG, "decode success: $string")
//                        val newBody: ResponseBody = string.toResponseBody(contentType)
//                        return response.newBuilder().body(newBody).build()
//                    }.onFailure {
//                        AppLogs.eLog(TAG, "decode error: ${it.stackTraceToString()}")
//                    }
//                    // body 包含非 base64 字符会导致 decode 失败，直接原样返回。由于 body source 已经被消费了，需要重新构建 body
//                    return response.newBuilder().body(byteArr.toResponseBody(contentType)).build()
//                }
//            })
        if (APP.isDebug) {
            builder.addInterceptor(loggingInterceptor)
        }
        var build = builder.build()
        build.dispatcher.maxRequestsPerHost = 10
        return build
    }
    val netClient: OkHttpClient by lazy {
        getNetHttp()
    }

    var TAG = "WebNet:"

}