package com.boom.aiobrowser.tools.web

import android.webkit.CookieManager
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.PManageData
import com.boom.aiobrowser.data.PVideoData
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.data.VideoDownloadData.Companion.TYPE_M3U8
import com.boom.aiobrowser.net.WebNet
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.BigDecimalUtils
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.clean.CleanConfig.videoExtension
import com.boom.aiobrowser.tools.clean.formatSize
import com.boom.aiobrowser.tools.clean.getFileSize
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.tools.toJson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import java.io.IOException
import java.lang.ref.WeakReference
import java.math.BigDecimal
import java.util.regex.Pattern


object WebScan {

    var TAG = "WebScan"

    var isloading = false

    fun filterUri(uri:String,reference: WeakReference<BaseActivity<*>>) {
        var activity: BaseActivity<*>? = reference.get() ?: return
        var type = ""
        uri?.apply {
            var url = uri.toString()
            if (url.contains(WebConfig.FILTER_TIKTOK, true)) {
                var cookieManager = CookieManager.getInstance()
                WebConfig.cookieTikTok = cookieManager.getCookie(url)?:""
                if (APP.isDebug){
                    AppLogs.dLog(TAG, "判断 0 cookie :${WebConfig.cookieTikTok}  命中tiktok uri:${uri}")
                }
                if (WebConfig.cookieTikTok.isNullOrEmpty())return
                type = WebConfig.TIKTOK
                if (APP.isDebug){
                    AppLogs.dLog(TAG, "判断 1 isloading :${isloading}  命中tiktok uri:${uri}")
                }
                var id = "tiktok_${getTikTokId(url)}"
                var list = CacheManager.videoDownloadTempList
                var allow = true
                for (i in 0 until list.size) {
                    var data = list.get(i)
                    if (data.videoId == id) {
                        allow = false
                        break
                    }
                }
                if (APP.isDebug){
                    AppLogs.dLog(TAG, "判断 2 allow:${allow} list:${toJson(CacheManager.videoDownloadTempList)}")
                }
                if (allow) {
                    var map = HashMap<String, Any>()
                    map.put("Cookie", WebConfig.cookieTikTok)
                    var data = VideoDownloadData().createDefault(
                        videoId = id,
                        fileName = id,
                        url = url,
                        imageUrl = url,
                        paramsMap = map,
                        size = BigDecimal(0).toLong(),
                        videoType = ""
                    )
                    var list = CacheManager.videoDownloadTempList
//                    var allow = true
//                    for (i in 0 until list.size){
//                        var data = list.get(i)
//                        if(data.videoId == id){
//                            allow = false
//                            break
//                        }
//                    }
                    list.clear()
                    list.add(0, data)
                    CacheManager.videoDownloadTempList = list
                    if (isloading.not()) {
                        AppLogs.dLog(TAG, "判断 3 filterUri 发送数据变化 id:${data.videoId}")
                        APP.videoScanLiveData.postValue(data)
                        getVideoHeaderInfo(type, url, WebConfig.cookieTikTok)
                    }
                }
            } else if (url.contains(WebConfig.FILTER_PORNHUB,true)){
                activity?.addLaunch(success = {
                    if (url.contains("https://cn.pornhub.com/view_video.php?viewkey",true)){
                        var startTime = System.currentTimeMillis()
                        var doc = Jsoup.connect(url).get()

                        var element = doc.getElementById("videoElementPoster")
                        var img = element.attr("src")
                        var title = doc.title()
                        val scripts = doc.getElementsByTag("script")
                        val pattern = Pattern.compile("var\\s+flashvars_\\d+\\s*=\\s*(\\{.*?\\});")
                        var json = ""
//                        var content = scripts.get(46).html()
//                        val matcher = pattern.matcher(content)
                        for (i in 0 until scripts.size){
                            var data = scripts.get(i)
                            val scriptContent = data.html()
                            val matcher = pattern.matcher(scriptContent)
                            if (matcher.find()){
//                                AppLogs.dLog(TAG,"p 站视频数据:${matcher.group(1)}")
                                json = matcher.group(1)
                                break
                            }
                        }
                        var pData = getBeanByGson(json,PManageData::class.java)
                        var pVideo:PVideoData?= null
                        pData?.mediaDefinitions?.forEach {
                            if (it.defaultQuality){
                                pVideo = it
                            }
                        }
                        var videoUrl = pVideo?.videoUrl
//                        val videoPattern = Pattern.compile("(\\d+K)")
//                        val matcher = videoPattern.matcher(videoUrl)
//                        if (matcher.find()){
//                            val group = matcher.group(1)
//                            bitRate = BigDecimal(group.substring(0,group.length-1)).toLong()
//                            bitRate = bitRate.getFileSize(pData?.video_duration?:0)
//                        }
//                        bitRate =

                        var allow = true
                        var videoId = "video_${url}"
                        var list = CacheManager.videoDownloadTempList
                        for (i in 0 until list.size) {
                            var data = list.get(i)
                            if (data.videoId == videoId) {
                                allow = false
                                break
                            }
                        }
                        if (allow.not())return@addLaunch
                        var size = PManager.parseNetworkM3U8InfoByP(videoUrl?:"",HashMap<String,String>(),0,pData?.video_duration?:0)
                        AppLogs.dLog(TAG,"p站  title :${doc.title()}-imgs:${img}" +
                                "文件大小"+size.formatSize()+"-"+
                                " 视频播放地址" +videoUrl+"-"+
                                " 视频播放时长" +"${pData?.video_duration?:0}"+"-"+
                                " 耗时:${System.currentTimeMillis()-startTime}")
                        var map = HashMap<String, Any>()
                        var data = VideoDownloadData().createDefault(
                            videoId = videoId,
                            fileName = pData?.video_title?:"",
                            url = videoUrl?:"",
                            imageUrl = pData?.image_url?:"",
                            paramsMap = map,
                            size = size,
                            videoType = TYPE_M3U8
                        )
                        list.add(0, data)
                        CacheManager.videoDownloadTempList = list
                        AppLogs.dLog(TAG, "filterUri 发送数据变化 id:${data.videoId}")
                        APP.videoScanLiveData.postValue(data)
                    }
                }, failBack = {})
            }else{
//                url = "https://cm-h.phncdn.com/hls/videos/202406/30/454565161/720P_4000K_454565161.mp4/master.m3u8?YV-e9ptSuEg0nZgHhnYuKKQ2NyWf9EA6uIIBFxLuwVNzdneBBvTitDXuNa7W6Z-AAQfYXja9zzso1uvOfzZU70Xv2e2ISYWK_QoJus9A5wLSQjdpILfiAjFktEX27kZ70pJnrr_33YAptYx_XKGoJ4wm1aBLHUVQZi94M46h8enNbugZu4HIuveyLNVUZPo7_y5nd5EkmA"
//                var allow = false
//                for (i in 0 until videoExtension.size) {
//                    var data = videoExtension.get(i)
//                    if (url.contains(data, true) && url.contains("master.m3u8")) {
//                        AppLogs.dLog(TAG, "命中其它视频 uri:${uri}")
//                        allow = true
//                        break
//                    }
//                }
//                if (allow.not()) return
//                var id = "video_${url}"
//                var list = CacheManager.videoDownloadTempList
//                for (i in 0 until list.size) {
//                    var data = list.get(i)
//                    if (data.videoId == id) {
//                        allow = false
//                        break
//                    }
//                }
//                if (allow.not()) return
//                var map = HashMap<String, Any>()
//                var data = VideoDownloadData().createDefault(
//                    videoId = id,
//                    fileName = id,
//                    url = url,
//                    paramsMap = map,
//                    size = BigDecimal(0).toLong(),
//                    videoType = ""
//                )
//                list.add(0, data)
//                CacheManager.videoDownloadTempList = list
//                AppLogs.dLog(TAG, "filterUri 发送数据变化 id:${data.videoId}")
//                APP.videoScanLiveData.postValue(data)
//                getVideoHeaderInfo(type, url, WebConfig.cookieTikTok)
            }
        }
    }

    private var call: Call? = null

    fun getVideoHeaderInfo(type: String, videoUrl: String, cookie: String) {
        isloading = true
        val request: Request = Request.Builder()
            .url(videoUrl)
            .addHeader("Cookie", cookie)
            .head() // 只请求头信息
            .build()
        call = WebNet.netClient.newCall(request)
        call?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                AppLogs.eLog(TAG, e.stackTraceToString())
                isloading = false
            }

            override fun onResponse(call: Call, response: Response) {
                // 获取文件的内容类型
                val contentType = response.header("Content-Type")
                // 获取视频文件的大小
                val contentLength = response.header("Content-Length")
                AppLogs.dLog(TAG, "Content-Type:$contentType")
                AppLogs.dLog(TAG, "Content-Length:$contentLength bytes")
                isloading = false
                var id = ""
                if (type == WebConfig.TIKTOK) {
                    id = "tiktok_${getTikTokId(videoUrl)}"
//                    var map = HashMap<String, Any>()
//                    map.put("Cookie", cookie)
                }else{
                    id = "video_${videoUrl}"
                }
                var list = CacheManager.videoDownloadTempList
                for (i in 0 until list.size) {
                    var data = list.get(i)
                    if (data.videoId == id) {
                        data.videoType = contentType
                        data.size = BigDecimal(contentLength).toLong()
                        CacheManager.videoDownloadTempList = list
                        AppLogs.dLog(TAG, "getVideoHeaderInfo 发送数据变化 id:${data.videoId}")
                        APP.videoScanLiveData.postValue(data)
                        break
                    }
                }
            }
        })
    }

    private fun getTikTokId(videoUrl: String): String {
        val substringBefore = videoUrl.substringBefore("?")
        val split = substringBefore.split("/")
        if (split.size > 2) {
            return split.get(split.size - 2)
        } else {
            return videoUrl
        }
    }

    fun isTikTok(url: String): Boolean {
        return url.contains(WebConfig.TIKTOK)
    }

    fun isPornhub(url: String):Boolean{
        return url.contains(WebConfig.PORNHUB)
    }

    fun isYoutube(url: String):Boolean{
        return url.contains(WebConfig.YOUTUBE)
    }

    fun reset() {
        isloading = false
        call?.cancel()
    }
}