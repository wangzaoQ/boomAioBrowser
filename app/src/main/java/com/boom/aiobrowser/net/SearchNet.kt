package com.boom.aiobrowser.net

import com.boom.aiobrowser.APP
import com.boom.aiobrowser.net.intercept.HttpLoggingInterceptorNew
import com.boom.aiobrowser.tools.CacheManager
import okhttp3.OkHttpClient
import java.net.Proxy
import java.util.logging.Level

object SearchNet {

    public val net: OkHttpClient by lazy {
        getOkhttp()
    }

    fun getSearchUrl(content:String):String{
        if (content.indexOf(".com")>=0){
            return if (content.startsWith("http")|| content.startsWith("https")){
                content
            }else{
                "https://${content}"
            }
        }
        var url = when (CacheManager.engineType) {
            1->{
                "https://www.bing.com/search?q=${content}"
            }
            2->{
                "https://search.yahoo.com/search?q=${content}"
            }
            3->{
                "https://www.perplexity.ai/search?q=${content}"
            }
            else -> {
                "https://www.google.com/search?q=${content}"
            }
        }
        return url
    }


    var TAG = "SearchNet:"

    private fun getOkhttp(): OkHttpClient {
        //TODO:master not commit
        val loggingInterceptor =
            HttpLoggingInterceptorNew(TAG)
        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptorNew.Level.BODY)
        //log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.INFO)
        val builder =  OkHttpClient.Builder()
            .apply {
                if (APP.isDebug.not()) { // 非测试环境
                    proxy(Proxy.NO_PROXY) // 禁止抓包
                }
            }
        if (APP.isDebug){
            builder.addInterceptor(loggingInterceptor)
        }
        return builder.build()
    }
}