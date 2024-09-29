package com.boom.aiobrowser.tools.video

import com.boom.aiobrowser.APP
import com.boom.aiobrowser.APP.Companion.videoLiveData
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.download.DownloadCacheManager
import com.boom.aiobrowser.tools.toJson
import com.jeffmony.downloader.VideoDownloadConfig
import com.jeffmony.downloader.VideoDownloadManager
import com.jeffmony.downloader.common.DownloadConstants
import com.jeffmony.downloader.listener.DownloadListener
import com.jeffmony.downloader.model.VideoTaskItem
import com.jeffmony.downloader.utils.VideoStorageUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch


object VideoManager {

    val TAG = "VideoManager"
//    private val _showDialogFlow = MutableSharedFlow<VideoTaskItem>(replay = 10, onBufferOverflow = BufferOverflow.DROP_OLDEST)
//    val showDialogFlow : SharedFlow<VideoTaskItem> = _showDialogFlow

    var job:Job?=null

    fun initVideo() {
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
                AppLogs.dLog(TAG,"onDownloadStart:${item?.url}")
            }

            override fun onDownloadProgress(item: VideoTaskItem?) {
                if (item==null)return
                AppLogs.dLog(TAG,"onDownloadProgress:${item?.downloadSizeString} url:${item.url}")
                videoLiveData.postValue(HashMap<Int,VideoTaskItem>().apply {
                    put(VideoDownloadData.DOWNLOAD_LOADING,item)
                })
//                CoroutineScope(Dispatchers.IO).launch {
//                    var model =  DownloadCacheManager.queryDownloadModelByUrl(item.url)
//                    if (model!=null){
//                        model.downloadSize = item.downloadSize
//                        DownloadCacheManager.updateModel(model)
//                    }
//                    AppLogs.dLog(TAG,"收到进度消息 :${model?.downloadSize} url:${model?.url}")
//                    videoLiveData.postValue(HashMap<Int,VideoTaskItem>().apply {
//                        put(VideoDownloadData.DOWNLOAD_LOADING,item)
//                    })
//                }
            }

            override fun onDownloadSpeed(item: VideoTaskItem?) {}

            override fun onDownloadPause(item: VideoTaskItem?) {
                AppLogs.dLog(TAG,"onDownloadPause:${item?.url}")
                if (item==null)return
                videoLiveData.postValue(HashMap<Int,VideoTaskItem>().apply {
                    put(VideoDownloadData.DOWNLOAD_PAUSE,item)
                })
//                CoroutineScope(Dispatchers.IO).launch {
//                    var model =  DownloadCacheManager.queryDownloadModelByUrl(item.url)
//                    if (model!=null){
//                        model.downloadSize = item.downloadSize
//                        DownloadCacheManager.updateModel(model)
//                    }
//                    videoLiveData.postValue(HashMap<Int,VideoTaskItem>().apply {
//                        put(VideoDownloadData.DOWNLOAD_PAUSE,item)
//                    })
//                }
            }

            override fun onDownloadError(item: VideoTaskItem?) {
                AppLogs.dLog(TAG,"onDownloadError:${item?.url}")
                if (item==null)return
                videoLiveData.postValue(HashMap<Int,VideoTaskItem>().apply {
                    put(VideoDownloadData.DOWNLOAD_ERROR,item)
                })
            }

            override fun onDownloadSuccess(item: VideoTaskItem?) {
                AppLogs.dLog(TAG,"onDownloadSuccess:${item?.url}")
                if (item==null)return
                videoLiveData.postValue(HashMap<Int,VideoTaskItem>().apply {
                    put(VideoDownloadData.DOWNLOAD_SUCCESS,item)
                })
//                CoroutineScope(Dispatchers.IO).launch {
//                    var model =  DownloadCacheManager.queryDownloadModelByUrl(item.url)
//                    if (model!=null){
//                        model.downloadFileName = item.fileName
//                        model.downloadFilePath = item.filePath
//                        DownloadCacheManager.updateModel(model)
//                    }
//                    videoLiveData.postValue(HashMap<Int,VideoTaskItem>().apply {
//                        put(VideoDownloadData.DOWNLOAD_SUCCESS,item)
//                    })
//                }
            }
        })
    }
}