package com.boom.aiobrowser.tools.web

import android.net.Uri
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.net.WebNet
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.math.BigDecimal


object WebScan {

    var TAG = "WebScan"

    var isloading = false

    fun filterUri(uri: Uri?) {
        var type = ""
        uri?.apply {
           var url = uri.toString()
            if (url.contains(WebConfig.FILTER_TIKTOK,true)){
                type = WebConfig.TIKTOK
                AppLogs.dLog(TAG,"命中tiktok uri:${uri}")
                var id = "tiktok_${getTikTokId(url)}"
                var list = CacheManager.videoDownloadTempList
                var allow = true
                for (i in 0 until list.size){
                    var data = list.get(i)
                    if(data.videoId == id){
                        allow = false
                        break
                    }
                }
                if (allow){
                    var map = HashMap<String,Any>()
                    map.put("Cookie",WebConfig.cookieTikTok)
                    var data = VideoDownloadData().createDefault(videoId = id, fileName = id, url = url, paramsMap = map, size = BigDecimal(0).toLong(), videoType = "")
                    var list = CacheManager.videoDownloadTempList
                    var allow = true
                    for (i in 0 until list.size){
                        var data = list.get(i)
                        if(data.videoId == id){
                            allow = false
                            break
                        }
                    }
                    if (allow){
                        list.clear()
                        list.add(0,data)
                    }
                    CacheManager.videoDownloadTempList = list
                    if (isloading.not()){
                        AppLogs.dLog(TAG,"filterUri 发送数据变化 id:${data.videoId}")
                        APP.videoScanLiveData.postValue(data)
                        getVideoHeaderInfo(type,url,WebConfig.cookieTikTok)
                    }
                }
            }
        }
    }

    private  var call: Call?=null

    fun getVideoHeaderInfo(type:String,videoUrl: String,cookie:String) {
        isloading = true
        val request: Request = Request.Builder()
            .url(videoUrl)
            .addHeader("Cookie",cookie)
            .head() // 只请求头信息
            .build()
        call = WebNet.netClient.newCall(request)
        call?.enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                AppLogs.eLog(TAG,e.stackTraceToString())
                isloading = false
            }

            override fun onResponse(call: Call, response: Response) {
                // 获取文件的内容类型
                val contentType = response.header("Content-Type")
                // 获取视频文件的大小
                val contentLength = response.header("Content-Length")
                AppLogs.dLog(TAG,"Content-Type:$contentType")
                AppLogs.dLog(TAG,"Content-Length:$contentLength bytes")
                isloading = false
                if (type == WebConfig.TIKTOK){
                    var id = "tiktok_${getTikTokId(videoUrl)}"
                    var map = HashMap<String,Any>()
                    map.put("Cookie",cookie)
                    var list = CacheManager.videoDownloadTempList
                    for (i in 0 until list.size){
                        var data = list.get(i)
                        if(data.videoId == id){
                            data.videoType = contentType
                            data.size = BigDecimal(contentLength).toLong()
                            CacheManager.videoDownloadTempList = list
                            AppLogs.dLog(TAG,"getVideoHeaderInfo 发送数据变化 id:${data.videoId}")
                            APP.videoScanLiveData.postValue(data)
                            break
                        }
                    }
                }
            }
        })
    }

    private fun getTikTokId(videoUrl: String): String {
        val substringBefore = videoUrl.substringBefore("?")
        val split = substringBefore.split("/")
        if (split.size>2){
            return split.get(split.size-2)
        }else{
            return videoUrl
        }
    }

    fun isTikTok(url:String):Boolean{
        return url.contains(WebConfig.TIKTOK)
    }

    fun reset() {
        isloading = false
        call?.cancel()
    }
}