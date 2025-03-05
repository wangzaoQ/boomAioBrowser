package com.boom.aiobrowser.tools.web

import android.text.TextUtils
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.extractDomain
import com.boom.aiobrowser.tools.web.WebScan.content_img
import com.boom.aiobrowser.tools.web.WebScan.content_length
import com.boom.aiobrowser.tools.web.WebScan.content_title
import com.boom.aiobrowser.tools.web.WebScan.content_type
import com.boom.aiobrowser.tools.web.WebScan.imageUrl
import com.boom.aiobrowser.tools.web.WebScan.isXhaMaster
import com.boom.aiobrowser.tools.web.WebScan.webTitle
import com.boom.downloader.VideoInfoParserManager
import com.boom.downloader.model.Video
import com.boom.downloader.utils.HttpUtils
import com.boom.downloader.utils.VideoDownloadUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.net.HttpURLConnection

object WebDailymotion {

    suspend fun getVideoHeaderInfo(videoUrl: String, cookie: String,realUrl:String): Map<String, Any> {
        var infoMap = HashMap<String, Any>()
        var connection: HttpURLConnection? = null
        var headers = HashMap<String, String>().apply {
            put("Cookie", cookie)
            put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
        }
        // Redirect is enabled, send redirect request to get final location.
        var finalUrl = videoUrl
        try {
            connection = HttpUtils.getConnection(
                finalUrl,
                headers,
                VideoDownloadUtils.getDownloadConfig().shouldIgnoreCertErrors(),true
            )
        } catch (e: Exception) {
            AppLogs.dLog("webReceive","原生 无法创建链接 url:${finalUrl}")
            HttpUtils.closeConnection(connection)
            return infoMap
        }
        if (connection == null) {
            AppLogs.dLog("webReceive","原生 链接为 null url:${finalUrl}")
            return infoMap
        }
        finalUrl = connection.url.toString()
        if (TextUtils.isEmpty(finalUrl)) {
            HttpUtils.closeConnection(connection)
            return infoMap
        }
        val contentType = connection.contentType
        if (finalUrl.contains("m3u8?sec")){
            AppLogs.dLog("webReceive","原生 Dailymotion m3u8 url:${finalUrl}")
        }
////        finalUrl = "https://cdn77-vid.xnxx-cdn.com/g_RrfJQs9IIf11KsD3AkWQ==,1736930045/videos/hls/bf/5d/ca/bf5dcaaf9b3e82dff80c3ebb0ea7f9da/hls-250p-0b616.m3u8"
//        if (finalUrl.contains(Video.TypeInfo.M3U8) || VideoDownloadUtils.isM3U8Mimetype(contentType)) {
//            AppLogs.dLog("webReceive","原生 视频格式为 m3u8 url:${finalUrl}")
//            AppLogs.dLog("m3u8","m3u8 finalUrl:${finalUrl} cookie:${cookie}")
//            //这是M3U8视频类型
//            infoMap.put(content_type, "m3u8")
//            AppLogs.dLog("m3u8","calculateM3U8Size Start")
//            AppLogs.dLog("m3u8","realUrl:${realUrl}")
//            var hostList = extractDomain(realUrl)
//            var m3u8Size = PManager.calculateM3U8Size(finalUrl,headers,hostList)
//            AppLogs.dLog("m3u8","calculateM3U8Size end")
//
//            if (isXhaMaster(hostList) && m3u8Size<10*1024*1024){
//                AppLogs.dLog("webReceive","过滤调广告 url:${finalUrl}")
//                return infoMap
//            }
//            var startTime = System.currentTimeMillis()
//            runBlocking {
//                while (webTitle.isNullOrEmpty()&&(System.currentTimeMillis()-startTime)<3000L){
//                    delay(500)
//                }
//            }
//            infoMap.put(content_length,m3u8Size)
//            infoMap.put(content_img, imageUrl)
//            infoMap.put(content_title, webTitle)
//        } else {
//            //这是非M3U8类型, 需要获取视频的totalLength ===> contentLength
//            val contentLength: Long = VideoInfoParserManager.getInstance()
//                .getContentLength(finalUrl, headers, connection, false)
//            if (contentLength == VideoDownloadUtils.DEFAULT_CONTENT_LENGTH || contentLength<1024 *1024) {
//                HttpUtils.closeConnection(connection)
//                return infoMap
//            }
//            infoMap.put(content_type, contentType)
//            infoMap.put(content_length, contentLength)
//            infoMap.put(content_title, webTitle)
//        }
//        HttpUtils.closeConnection(connection)
//        AppLogs.dLog("webReceive","infoMap:${toJson(infoMap)} url:${videoUrl}")
        return infoMap
    }

}