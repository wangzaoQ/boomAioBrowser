package com.boom.aiobrowser.tools.web

import android.text.TextUtils
import com.boom.aiobrowser.net.WebNet
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.BigDecimalUtils
import com.boom.downloader.VideoInfoParserManager
import com.boom.downloader.common.DownloadConstants
import com.boom.downloader.utils.HttpUtils
import com.boom.downloader.utils.LogUtils
import com.boom.downloader.utils.VideoDownloadUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import okhttp3.Request
import okhttp3.Response
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.math.BigDecimal
import java.net.URL
import java.util.regex.Pattern

object PManager {

    /**
     * parse network M3U8 file. p站
     *
     * @param videoUrl
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    suspend fun parseNetworkM3U8InfoByP(
        videoUrl: String,
        headers: HashMap<String, String>,
        retryCount: Int,
        allTime:Int
    ): Long {
        var bufferedReader: BufferedReader? = null
        try {
            val connection = HttpUtils.getConnection(
                videoUrl,
                headers,
                VideoDownloadUtils.getDownloadConfig().shouldIgnoreCertErrors()
            )
            val responseCode = connection.responseCode
            LogUtils.i(
                DownloadConstants.TAG,
                "parseNetworkM3U8Info responseCode=$responseCode"
            )
            if (responseCode == HttpUtils.RESPONSE_503 && retryCount < HttpUtils.MAX_RETRY_COUNT) {
                return parseNetworkM3U8InfoByP(videoUrl, headers, retryCount + 1,allTime)
            }
            var bateRate = "0"
            bufferedReader = BufferedReader(InputStreamReader(connection.inputStream))
            var line: String
            while ((bufferedReader.readLine().also { line = it }) != null) {
                line = line.trim { it <= ' ' }
                if (TextUtils.isEmpty(line)) {
                    continue
                }
                val pattern = Pattern.compile("BANDWIDTH=(\\d+)")
                val matcher = pattern.matcher(line)

                if (matcher.find() && bateRate == "0") {
                    bateRate = matcher.group(1)
                }
                if (bateRate!="0"){
                    VideoDownloadUtils.close(bufferedReader)
                    break
                }
                LogUtils.i(DownloadConstants.TAG, "line = $line")
            }

            var allSize = 0L
            allSize = BigDecimalUtils.mul(
                BigDecimalUtils.div(bateRate, "8"),
                allTime.toDouble()
            ).toLong()
            return allSize
        } catch (e: IOException) {
            e.printStackTrace()
            throw e
        } finally {
            VideoDownloadUtils.close(bufferedReader)
        }
    }


    fun getFullUrl(baseUrl: String, relativeUrl: String): String {
        if (relativeUrl.startsWith("http")){
            return URL(relativeUrl).toString()
        }else{
            return URL(URL(baseUrl), relativeUrl).toString()
        }
    }

    fun parseM3U8(baseUrl: String,m3u8Text: String): List<String> {
        val segmentUrls = mutableListOf<String>()
        val lines = m3u8Text.split("\n")

        for (line in lines) {
            if (line.isNotEmpty() && !line.startsWith("#")) {
                segmentUrls.add(getFullUrl(baseUrl,line.trim()))
            }
        }

        return segmentUrls
    }

    suspend fun calculateM3U8Size(m3u8Url: String,headers:HashMap<String, String>):Long {
//        val m3u8Text = fetchM3U8File(m3u8Url)
//        val segmentUrls = parseM3U8(m3u8Url,m3u8Text)
        var sizePromises = 0L
//        // 使用协程并发获取文件大小
//        AppLogs.dLog("m3u8","calculateM3U8Size start url:${m3u8Url}")
//        runBlocking {
//            val deferredResults = segmentUrls.map { tsUrl ->
//                async {
//                    getVideoSegmentSize(tsUrl)
//                }
//            }
//            // 等待所有任务完成并累加文件大小
//            sizePromises = deferredResults.sumOf { it.await() }
//        }

        val m3u8Text = fetchM3U8File(m3u8Url,headers)
        if (m3u8Text.isNullOrEmpty() || !m3u8Text.contains("#EXTINF")) {
            return 0L // 没有有效的 M3U8 数据
        }

        val samples = mutableListOf<String>()
        var sampleDuration = 0.0
        var duration = 0.0
        val lines = m3u8Text.split("\n")
        val durationPattern = "#EXTINF:\\s*([\\d\\.]+)".toRegex()
        for (i in 0 until lines.size){
            var line = lines.get(i)
            if (line.isBlank()) continue
            if (line.startsWith("#")){
                val match = durationPattern.find(line)
                match?.let {
                    duration += it.groupValues[1].toDouble()
                    if (samples.size < 1) {
                        sampleDuration = duration
                    }
                }
            }else{
                if (samples.size < 1) {
                    samples.add(getFullUrl(m3u8Url,line.trim()))  // 绝对路径化 .ts 文件
                }
            }
        }
//        // 解析 M3U8 文件
//        for (line in lines) {
//
//            if (line.startsWith("#")) {
//                val match = durationPattern.find(line)
//                match?.let {
//                    duration += it.groupValues[1].toDouble()
//                    if (samples.size < 2) {
//                        sampleDuration = duration
//                    }
//                }
//            } else {
//                if (samples.size < 2) {
//                    samples.add(getFullUrl(m3u8Url,line.trim()))  // 绝对路径化 .ts 文件
//                }
//            }
//        }

        if (sampleDuration == 0.0) {
            return 0L  // 没有有效的样本时返回 0
        }
        runBlocking {
        // 获取样本文件的大小
        val sizes = samples.mapNotNull { tsUrl ->
                async {getVideoSegmentSize(tsUrl,headers)  }
            }
            // 根据样本大小和时长估算整个 M3U8 文件的大小
            sizePromises = sizes.awaitAll().sum()
        }
        val estimatedSize = (sizePromises / sampleDuration) * duration
        AppLogs.dLog("m3u8","calculateM3U8Size url:${m3u8Url} size:${sizePromises}")
        return estimatedSize.toLong()  // 返回估算的总大小（字节）
    }

    suspend fun fetchM3U8File(url: String,headers:HashMap<String, String>): String {
        val builder = Request.Builder()
            .url(url)
        headers.forEach {
            builder.header(it.key,it.value)
        }
//            .head() // 只请求头信息
        var result:Response?=null
        runCatching {
            result = WebNet.netClient.newCall(builder.build()).execute()
        }
        return result?.body?.string() ?: ""
    }


    fun getVideoSegmentSize(url: String,headers:HashMap<String, String>,isRetry :Boolean= false): Long {
        AppLogs.dLog("m3u8","calculateM3U8Size 开始 获取大小 url:${url}")

//        var connection = HttpUtils.getConnection(
//            url,
//            headers,
//            VideoDownloadUtils.getDownloadConfig().shouldIgnoreCertErrors(),true
//        )
//
//        //这是非M3U8类型, 需要获取视频的totalLength ===> contentLength
//        return VideoInfoParserManager.getInstance().getContentLength(url, headers, connection, false)
        val builder = Request.Builder()
            if (isRetry.not()){
                builder.head() // 只请求头信息
            }else{
                builder.addHeader("Range","bytes=0-")
            }
            .url(url)
        headers.forEach {
            builder.header(it.key,it.value)
        }
        var result: Response?=null
        runCatching {
            result = WebNet.netClient.newCall(builder.build()).execute()
        }
        var contentLength = result?.header("content-length")?:"0"
        if (contentLength == "0" && isRetry.not()){
            return getVideoSegmentSize(url,headers,isRetry = true)
        }
//        var contentLength = result?.header("Content-Length")?:"0"
        return BigDecimal(contentLength).toLong()
    }

}