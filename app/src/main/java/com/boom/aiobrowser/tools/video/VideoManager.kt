package com.boom.aiobrowser.tools.video

import android.os.Bundle
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.APP.Companion.videoLiveData
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.nf.NFShow
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.download.DownloadCacheManager
import com.boom.aiobrowser.tools.web.WebScan
import com.boom.video.cache.CacheFactory
import com.boom.video.media.exo2.Exo2PlayerManager
import com.boom.video.media.exo2.ExoPlayerCacheManager
import com.boom.video.player.PlayerFactory
import com.boom.downloader.VideoDownloadConfig
import com.boom.downloader.VideoDownloadManager
import com.boom.downloader.common.DownloadConstants
import com.boom.downloader.listener.DownloadListener
import com.boom.downloader.model.VideoTaskItem
import com.boom.downloader.utils.VideoStorageUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File


object VideoManager {

    val TAG = "VideoManager"
//    private val _showDialogFlow = MutableSharedFlow<VideoTaskItem>(replay = 10, onBufferOverflow = BufferOverflow.DROP_OLDEST)
//    val showDialogFlow : SharedFlow<VideoTaskItem> = _showDialogFlow

    var job: Job? = null
//    var flow = flow<VideoTaskItem>{}

    fun initVideo() {
        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java) //EXO模式
        CacheFactory.setCacheManager(ExoPlayerCacheManager::class.java) //exo缓存模式，支持m3u8，只支持exo
        val file = VideoStorageUtils.getVideoCacheDir(APP.instance)
        if (!file.exists()) {
            file.mkdir()
        }
        val config: VideoDownloadConfig = VideoDownloadManager.Build(APP.instance)
            .setCacheRoot(file.absolutePath)
            .setTimeOut(DownloadConstants.READ_TIMEOUT, DownloadConstants.CONN_TIMEOUT)
            .setConcurrentCount(DownloadConstants.CONCURRENT)
            .setIgnoreCertErrors(false)
            .setShouldM3U8Merged(true)
            .buildConfig();
//        CoroutineScope(Dispatchers.IO).launch {
//            showDialogFlow?.collect{
//
//            }
//        }
        VideoDownloadManager.getInstance().initConfig(config)
        VideoDownloadManager.getInstance().setGlobalDownloadListener(object : DownloadListener() {
            override fun onDownloadDefault(item: VideoTaskItem?) {
            }

            override fun onDownloadPending(item: VideoTaskItem?) {}

            override fun onDownloadPrepare(item: VideoTaskItem?) {}

            override fun onDownloadStart(item: VideoTaskItem?) {
                AppLogs.dLog(TAG, "onDownloadStart:${item?.url}")
            }

            override fun onDownloadProgress(item: VideoTaskItem?) {
                if (item == null) return
                AppLogs.dLog(TAG, "onDownloadProgress:totalSize:${item?.totalSize} downloadSize:${item?.downloadSize} url:${item.url}")
                CoroutineScope(Dispatchers.IO).launch {
                    var model = DownloadCacheManager.queryDownloadModelByUrl(item.url)
                    if (model != null) {
                        if (item.downloadSize >= item.totalSize) {
                            return@launch
                        }
                        model.downloadSize = item.downloadSize
                        model.downloadType = VideoDownloadData.DOWNLOAD_LOADING
                        DownloadCacheManager.updateModel(model)
                        NFShow.showDownloadNF(VideoDownloadData().createVideoDownloadData(model))
                    }
                    AppLogs.dLog(TAG, "收到进度消息 :${model?.downloadSize} url:${model?.url}")
                    videoLiveData.postValue(HashMap<Int, VideoTaskItem>().apply {
                        put(VideoDownloadData.DOWNLOAD_LOADING, item)
                    })
                }
            }

            override fun onDownloadSpeed(item: VideoTaskItem?) {}

            override fun onDownloadPause(item: VideoTaskItem?) {
                AppLogs.dLog(TAG, "onDownloadPause:${item?.url} totalSize:${item?.totalSize} downloadSize:${item?.downloadSize}")
                if (item == null) return
                CoroutineScope(Dispatchers.IO).launch {
                    var model = DownloadCacheManager.queryDownloadModelByUrl(item.url)
                    if (model != null) {
                        model.downloadSize = item.downloadSize
                        model.downloadType = VideoDownloadData.DOWNLOAD_PAUSE
                        DownloadCacheManager.updateModel(model)
                        NFShow.showDownloadNF(VideoDownloadData().createVideoDownloadData(model))
                    }
                    videoLiveData.postValue(HashMap<Int, VideoTaskItem>().apply {
                        put(VideoDownloadData.DOWNLOAD_PAUSE, item)
                    })
                }
            }

            override fun onDownloadError(item: VideoTaskItem?) {
                AppLogs.dLog(TAG, "onDownloadError:${item?.url}")
                ToastUtils.showShort(APP.instance.getString(R.string.download_failed))
                if (item == null) return
                CoroutineScope(Dispatchers.IO).launch {
                    var isSuccessParent = FileUtils.delete(File(item.filePath).parent)
                    var isSuccess = FileUtils.delete(File(item.filePath))
                    var model = DownloadCacheManager.queryDownloadModelByUrl(item.url)
                    if (model != null) {
                        model.downloadSize = 0
                        model.downloadType = VideoDownloadData.DOWNLOAD_ERROR
                        DownloadCacheManager.updateModel(model)
                        NFShow.showDownloadNF(VideoDownloadData().createVideoDownloadData(model),true)
                    }
                    videoLiveData.postValue(HashMap<Int, VideoTaskItem>().apply {
                        put(VideoDownloadData.DOWNLOAD_ERROR, item)
                    })
                }
            }

            override fun onDownloadSuccess(item: VideoTaskItem?) {
                AppLogs.dLog(
                    TAG,
                    "onDownloadSuccess:${item?.url} downloadSize:${item?.downloadSize} totalSize:${item?.totalSize}"
                )
                if (item == null) return
                ToastUtils.showShort(APP.instance.getString(R.string.download_finished))
                CoroutineScope(Dispatchers.IO).launch {
                    var model = DownloadCacheManager.queryDownloadModelByUrl(item.url)
                    if (model != null) {
                        model.downloadFileName = item.fileName
                        model.downloadFilePath = item.filePath
                        model.downloadType = VideoDownloadData.DOWNLOAD_SUCCESS
                        DownloadCacheManager.updateModel(model)
                        NFShow.showDownloadNF(VideoDownloadData().createVideoDownloadData(model),true)
                    }
                    videoLiveData.postValue(HashMap<Int, VideoTaskItem>().apply {
                        put(VideoDownloadData.DOWNLOAD_SUCCESS, item)
                    })
                }
                PointEvent.posePoint(PointEventKey.download_success, Bundle().apply {
                    var source = ""
                    if (WebScan.isTikTok(item.url)) {
                        source = "tiktok"
                    } else if (WebScan.isPornhub(item.url)) {
                        source = "pornhub"
                    }
                    putString(PointValueKey.video_source,source)
                    putString(PointValueKey.video_url,item.url)
                })
            }
        })

        //重置状态
        var modelList = DownloadCacheManager.queryDownloadModelLoading()
        modelList?.forEach {
            it.downloadType = VideoDownloadData.DOWNLOAD_PAUSE
            DownloadCacheManager.updateModel(it)
        }
    }
}