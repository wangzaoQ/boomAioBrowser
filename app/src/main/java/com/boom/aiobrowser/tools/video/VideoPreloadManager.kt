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

    var releaseAll = false

    @Volatile
    var cacheMap :HashMap<String, CacheHelper> = HashMap()

    fun releaseAll() {
        releaseAll = true
        AppLogs.dLog(TAG, "全部取消")
        cacheMap.keys.forEach {
            runCatching {
                cacheMap.get(it)!!.cancel()
            }
        }
        cacheMap.clear()
    }

    fun release(url:String){
        AppLogs.dLog(TAG, "单个取消前 size:${cacheMap.size}")
        runCatching {
            cacheMap.get(url)?.cancel()
            cacheMap.remove(url)
        }
        AppLogs.dLog(TAG, "单个取消后 size:${cacheMap.size}")
    }



    //串行
    @androidx.media3.common.util.UnstableApi
    fun serialList(page:Int,videoList: MutableList<NewsData>?) {
        releaseAll = false
        CoroutineScope(Dispatchers.IO).launch{
            runCatching {
                saveVideo2(page,videoList)
            }
        }
    }


    @androidx.media3.common.util.UnstableApi
    private fun saveVideo2(page:Int,videoList: MutableList<NewsData>?) {
        videoList?.apply {
            var videoData: NewsData? = this.removeFirstOrNull() ?: return
            var file = File(getCachePath(computeMD5(videoData?.vbreas?:"")))
            AppLogs.dLog(TAG,"saveVideo2 缓存的地址:${file.absolutePath} url:${videoData!!.vbreas?:""}")
            var start = 0L
            runCatching {
                var cacheHelper = CacheHelper()
                cacheMap.put(videoData!!.vbreas?:"",cacheHelper)
                AppLogs.dLog(TAG, "start_cache:${videoData!!.itackl}")
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
                            if (cacheMap.isNullOrEmpty() || releaseAll){
                                runCatching {
                                    AppLogs.dLog(TAG, "saveVideo2${videoData!!.vbreas} 非正常取消")
                                    cacheHelper.cancel()
                                }
                                return
                            }
                            if(videoData.vsound == 0 && start == 0L){
                                start = System.currentTimeMillis()
                            }
                            if (videoData.vsound == 0){
                                if ((System.currentTimeMillis()-start)>3000){
//                                    AppLogs.dLog(
//                                        TAG,
//                                        "url:${videoData.vbreas} requestLength " + requestLength + " bytesCached " + bytesCached + " newBytesCached  " + newBytesCached
//                                    )
                                    release(videoData!!.vbreas?:"")
                                    saveVideo2(page,this@apply)
                                    AppLogs.dLog(TAG, "saveVideo2${videoData!!.vbreas} 缓存成功_ 无时间")
                                }
                            }else{
                                var videoTime = videoData.vsound?:0
                                if (videoTime<20){
                                    videoTime = 3
                                }else if (videoTime<60){
                                    videoTime = 5
                                }else{
                                    videoTime = 8
                                }
                                var end= BigDecimal(requestLength).divide(
                                    BigDecimal(videoData.vsound?:0),
                                    10,
                                    BigDecimal.ROUND_HALF_UP
                                ).toLong() * videoTime
                                AppLogs.dLog(
                                    TAG,
                                    "url:${videoData.vbreas} requestLength " + requestLength + " bytesCached " + bytesCached + " newBytesCached  " + newBytesCached+" end "+end
                                )
                                if (bytesCached > end && end > 0) {
                                    release(videoData!!.vbreas?:"")
                                    AppLogs.dLog(TAG, "saveVideo2 缓存成功  有时间 :${videoData!!.vbreas} ")
                                    saveVideo2(page,this@apply)
                                }
                            }
                        }

                    })
            }.onFailure {
//                AppLogs.dLog(
//                    TAG,
//                    it.stackTraceToString()
//                )
//                release(videoData!!.vbreas?:"")
//                saveVideo2(page,this@apply)
            }
        }
    }



    fun getCachePath(name:String): String? {
        var cachePath = ""
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
            || !Environment.isExternalStorageRemovable()) {
            cachePath = APP.instance.externalCacheDir?.path ?:""
            AppLogs.dLog(TAG,"getCachePath 1")
        } else {
            cachePath = APP.instance.cacheDir.path
            AppLogs.dLog(TAG,"getCachePath 2")
        }
//        cachePath = FileUtils.getAppPath(cachePath,name)
        cachePath = FileUtils.getAppPath(cachePath,"videoCache")

        val file = File(cachePath)
        if (!file.exists()) {
            file.mkdirs()
        }
        AppLogs.dLog(TAG,"getCachePath:${file.absolutePath}")
        return file.absolutePath
    }

    var TAG = "VideoPreloadManager"
}