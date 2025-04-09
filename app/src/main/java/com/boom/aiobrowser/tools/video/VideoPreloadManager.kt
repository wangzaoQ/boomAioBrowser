package com.boom.aiobrowser.tools.video

import android.net.Uri
import android.os.Environment
import androidx.media3.common.C
import androidx.media3.datasource.cache.CacheWriter
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.tools.AppLogs
import com.boom.downloader.utils.VideoDownloadUtils.computeMD5
import com.boom.video.media.exo2.CacheHelper
import com.boom.video.utils.FileUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.math.BigDecimal

object VideoPreloadManager {


    //串行
    @androidx.media3.common.util.UnstableApi
    fun serialList(page: Int, videoList: MutableList<NewsData>?) {
        videoList?.forEach {
            if (it.vbreas.isNullOrEmpty().not()){
                CoroutineScope(Dispatchers.IO).launch {
                    runCatching {
                        saveVideo2(page, it)
                    }
                }
            }
        }
    }


    @androidx.media3.common.util.UnstableApi
    private fun saveVideo2(page: Int, data: NewsData?) {
        var videoData: NewsData? = data
        var file = File(getCachePath(computeMD5(videoData?.vbreas ?: "")))
        AppLogs.dLog(
            TAG,
            "saveVideo2 缓存的地址:${file.absolutePath} url:${videoData!!.vbreas ?: ""}"
        )
        var start = 0L
        runCatching {
            var cacheHelper = CacheHelper()
            AppLogs.dLog(TAG, "start_cache:${videoData!!.itackl}")
            var usedTime = System.currentTimeMillis()
            cacheHelper.preCacheVideo(APP.instance,
                Uri.parse(videoData!!.vbreas),
                file,
                false,
                null,
                null,
                C.LENGTH_UNSET.toLong(),
                object : CacheWriter.ProgressListener {
                    override fun onProgress(
                        requestLength: Long,
                        bytesCached: Long,
                        newBytesCached: Long
                    ) {
                        if (requestLength == -1L) return
                        if (videoData.vsound == 0 && start == 0L) {
                            start = System.currentTimeMillis()
                        }
                        if (videoData.vsound == 0) {
                            if ((System.currentTimeMillis() - start) > 3000) {
                                cacheHelper.cancel()
                                AppLogs.dLog(
                                    TAG,
                                    "saveVideo2${videoData!!.vbreas} 缓存成功_ 无时间 总耗时:${System.currentTimeMillis()-usedTime} 下载了 ${bytesCached} 总大小:${requestLength}"
                                )
                            }
                        } else {
                            var videoTime = videoData.vsound ?: 0
                            if (videoTime<20){
                                videoTime = 3
                            }else if (videoTime<60){
                                videoTime = 5
                            }else{
                                videoTime = 10
                            }
                            var end = BigDecimal(requestLength).divide(
                                BigDecimal(videoData.vsound ?: 0),
                                10,
                                BigDecimal.ROUND_HALF_UP
                            ).toLong() * videoTime
                            if (bytesCached >= end && end > 0) {
                                cacheHelper.cancel()
                                AppLogs.dLog(
                                    TAG,
                                    "saveVideo2${videoData!!.vbreas} 缓存成功_ 有时间 总耗时:${System.currentTimeMillis()-usedTime} 下载了 ${bytesCached} 总大小:${requestLength}"
                                )
                            }
                        }
                    }
                })
        }.onFailure {
        }
    }


    fun getCachePath(name: String): String? {
        var cachePath = ""
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
            || !Environment.isExternalStorageRemovable()
        ) {
            cachePath = APP.instance.externalCacheDir?.path ?: ""
            AppLogs.dLog(TAG, "getCachePath 1")
        } else {
            cachePath = APP.instance.cacheDir.path
            AppLogs.dLog(TAG, "getCachePath 2")
        }
//        cachePath = FileUtils.getAppPath(cachePath,name)
        cachePath = FileUtils.getAppPath(cachePath, "videoCache")

        val file = File(cachePath)
        if (!file.exists()) {
            file.mkdirs()
        }
        AppLogs.dLog(TAG, "getCachePath:${file.absolutePath}")
        return file.absolutePath
    }

    var TAG = "VideoPreloadManager"
}