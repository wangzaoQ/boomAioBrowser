package com.boom.aiobrowser.tools.web

import android.text.TextUtils
import com.boom.aiobrowser.tools.BigDecimalUtils
import com.jeffmony.downloader.common.DownloadConstants
import com.jeffmony.downloader.m3u8.M3U8
import com.jeffmony.downloader.m3u8.M3U8Constants
import com.jeffmony.downloader.m3u8.M3U8Seg
import com.jeffmony.downloader.m3u8.M3U8Utils
import com.jeffmony.downloader.utils.HttpUtils
import com.jeffmony.downloader.utils.LogUtils
import com.jeffmony.downloader.utils.VideoDownloadUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.regex.Pattern
import kotlin.math.abs

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

}