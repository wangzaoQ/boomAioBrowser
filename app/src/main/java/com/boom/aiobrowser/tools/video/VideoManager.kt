package com.boom.aiobrowser.tools.video

import android.os.Bundle
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.APP.Companion.videoLiveData
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.nf.NFShow
import com.boom.aiobrowser.other.ShortManager
import com.boom.aiobrowser.other.ShortManager.allowRate
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.PointsManager
import com.boom.aiobrowser.tools.download.DownloadCacheManager
import com.boom.aiobrowser.tools.jobCancel
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
import com.ironsource.sc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference


object VideoManager {

    val TAG = "VideoManager"
//    private val _showDialogFlow = MutableSharedFlow<VideoTaskItem>(replay = 10, onBufferOverflow = BufferOverflow.DROP_OLDEST)
//    val showDialogFlow : SharedFlow<VideoTaskItem> = _showDialogFlow

//    var flow = flow<VideoTaskItem>{}

    val jobsMap = HashMap<String, MutableList<Job>>()


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
                if (item == null) return
                var job:Job?=null
                job = getDownloadScope(item,"onDownloadStart").launch {
                    var model = DownloadCacheManager.queryDownloadModelByUrl(item.url)
                    if (model != null) {
                        if (item.downloadSize >= item.totalSize) {
                            return@launch
                        }
                        model.downloadType = VideoDownloadData.DOWNLOAD_PREPARE
                        DownloadCacheManager.updateModel(model)
                        CacheManager.updateTempList(model)
                        NFShow.showDownloadNF(VideoDownloadData().createVideoDownloadData(model))
                    }
                    AppLogs.dLog(TAG, "收到进度消息 :${model?.downloadSize} url:${model?.fileName}")
                    videoLiveData.postValue(HashMap<Int, VideoTaskItem>().apply {
                        put(VideoDownloadData.DOWNLOAD_PREPARE, item)
                    })
                    removeJob(item,job,"onDownloadStart")
                }
                addJob(item,job,"onDownloadStart")
            }

            override fun onDownloadProgress(item: VideoTaskItem?) {
                if (item == null) return
//                AppLogs.dLog(TAG, "onDownloadProgress:totalSize:${item?.totalSize} downloadSize:${item?.downloadSize} url:${item.url}")
                var job:Job?=null
                job = getDownloadScope(item,"onDownloadProgress").launch {
                    var model = DownloadCacheManager.queryDownloadModelByUrl(item.url)
                    if (model != null) {
                        if (item.downloadSize >= item.totalSize) {
                            return@launch
                        }
                        model.downloadSize = item.downloadSize
                        model.downloadType = VideoDownloadData.DOWNLOAD_LOADING
                        model.size = item.totalSize
                        DownloadCacheManager.updateModel(model)
                        CacheManager.updateTempList(model)
                        NFShow.showDownloadNF(VideoDownloadData().createVideoDownloadData(model))
                    }
                    AppLogs.dLog(TAG, "收到进度消息 :${model?.downloadSize} url:${model?.fileName}")
                    videoLiveData.postValue(HashMap<Int, VideoTaskItem>().apply {
                        put(VideoDownloadData.DOWNLOAD_LOADING, item)
                    })
                    removeJob(item,job,"onDownloadProgress")
                }
                addJob(item,job,"onDownloadProgress")
            }

            override fun onDownloadSpeed(item: VideoTaskItem?) {}

            override fun onDownloadPause(item: VideoTaskItem?) {
                AppLogs.dLog(TAG, "onDownloadPause:${item?.fileName} totalSize:${item?.totalSize} downloadSize:${item?.downloadSize}")
                if (item == null) return
                var job:Job ?=null
                job = getDownloadScope(item,"onDownloadPause").launch {
                    var model = DownloadCacheManager.queryDownloadModelByUrl(item.url)
                    if (model != null) {
                        model.downloadSize = item.downloadSize
                        model.downloadType = VideoDownloadData.DOWNLOAD_PAUSE
                        DownloadCacheManager.updateModel(model)
                        CacheManager.updateTempList(model)
                        NFShow.showDownloadNF(VideoDownloadData().createVideoDownloadData(model))
                    }
                    videoLiveData.postValue(HashMap<Int, VideoTaskItem>().apply {
                        put(VideoDownloadData.DOWNLOAD_PAUSE, item)
                    })
                    removeJob(item,job,"onDownloadPause")
                }
                addJob(item,job,"onDownloadPause")
            }

            override fun onDownloadError(item: VideoTaskItem?) {
                AppLogs.dLog(TAG, "onDownloadError:${item?.fileName}")
                ToastUtils.showShort(APP.instance.getString(R.string.download_failed))
                if (item == null) return
                var job:Job ?=null
                job = getDownloadScope(item,"onDownloadError").launch {
                    runCatching {
                        var isSuccessParent = FileUtils.delete(File(item.filePath).parent)
                    }.onFailure {
                        AppLogs.eLog(TAG,it.stackTraceToString())
                    }
                    runCatching {
                        var isSuccess = FileUtils.delete(File(item.filePath))
                    }.onFailure {
                        AppLogs.eLog(TAG,it.stackTraceToString())
                    }
                    var model = DownloadCacheManager.queryDownloadModelByUrl(item.url)
                    if (model != null) {
                        model.downloadSize = 0
                        model.downloadType = VideoDownloadData.DOWNLOAD_ERROR
                        DownloadCacheManager.updateModel(model)
                        CacheManager.updateTempList(model)
                        NFShow.showDownloadNF(VideoDownloadData().createVideoDownloadData(model),true)
                    }
                    videoLiveData.postValue(HashMap<Int, VideoTaskItem>().apply {
                        put(VideoDownloadData.DOWNLOAD_ERROR, item)
                    })
                    removeJob(item,job,"onDownloadError")
                }
                addJob(item,job,"onDownloadError")
                PointEvent.posePoint(PointEventKey.all_noti_t,Bundle().apply {
                    putString(PointValueKey.video_url, item?.url?:"")
                    putString(PointValueKey.push_type, PointEventKey.download_push_fail)
                })
            }

            override fun onDownloadSuccess(item: VideoTaskItem?) {
                AppLogs.dLog(
                    TAG,
                    "onDownloadSuccess:${item?.fileName} downloadSize:${item?.downloadSize} totalSize:${item?.totalSize}"
                )
                if (item == null) return
                ToastUtils.showShort(APP.instance.getString(R.string.download_finished))
                var job:Job ?=null
                job = getDownloadScope(item,"onDownloadSuccess").launch {
                    var model = DownloadCacheManager.queryDownloadModelByUrl(item.url)
                    if (model != null) {
                        model.downloadFileName = item.fileName
                        model.downloadFilePath = item.filePath
                        model.downloadType = VideoDownloadData.DOWNLOAD_SUCCESS
                        model.size = item.totalSize
                        model.completeTime = System.currentTimeMillis()
                        DownloadCacheManager.updateModel(model)
                        CacheManager.updateTempList(model)
                        NFShow.showDownloadNF(VideoDownloadData().createVideoDownloadData(model),true)
                    }
                    videoLiveData.postValue(HashMap<Int, VideoTaskItem>().apply {
                        put(VideoDownloadData.DOWNLOAD_SUCCESS, item)
                    })
                    PointsManager.downloadVideo()
                    withContext(Dispatchers.Main){
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
                        var isDownload = CacheManager.dayFirstDownloadVideoSuccess
                        var activity:BaseActivity<*>?=null
                        var stackList = APP.instance.lifecycleApp.stack
                        if (stackList.size>0){
                            for (i in stackList.size-1 downTo 0){
                                if (stackList.get(i) is BaseActivity<*>){
                                    activity = stackList.get(i) as BaseActivity<*>
                                    break
                                }
                            }
                        }
                        if (isDownload && activity!=null && allowRate()){
                            ShortManager.addRate(WeakReference(activity))
                        }
                    }
                    removeJob(item,job,"onDownloadSuccess")
                }
                jobsMap.get(item.downloadVideoId)?.apply {
                    forEach {
                        it.jobCancel()
                    }
                    clear()
                    job?.let {
                        add(it)
                    }
                }
                PointEvent.posePoint(PointEventKey.all_noti_t,Bundle().apply {
                    putString(PointValueKey.video_url, item?.url?:"")
                    putString(PointValueKey.push_type, PointEventKey.download_push_success)
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

    private fun addJob(item: VideoTaskItem,job: Job?, tag: String) {
//        AppLogs.dLog(TAG,"addJob:${tag} 原始数据大小:${jobsMap.get(item.downloadVideoId)?.size}")
        if (job == null)return
        var jobList = jobsMap.get(item.downloadVideoId)
        if (jobList.isNullOrEmpty()){
            jobList = mutableListOf<Job>()
            jobsMap.put(item.downloadVideoId,jobList)
        }
        jobsMap.get(item.downloadVideoId)?.add(job)
//        AppLogs.dLog(TAG,"addJob:${tag} 添加数据大小:${jobsMap.get(item.downloadVideoId)?.size}")
    }

    fun removeJob(item: VideoTaskItem,job: Job?, tag: String){
//        AppLogs.dLog(TAG,"removeJob:${tag} 原始数据大小:${jobsMap.get(item.downloadVideoId)?.size}")
        if (job == null)return
        var jobList = jobsMap.get(item.downloadVideoId)
        jobList?.apply {
            job?.jobCancel()
            remove(job)
        }
//        AppLogs.dLog(TAG,"removeJob:${tag} 删除后数据大小:${jobsMap.get(item.downloadVideoId)?.size}")
    }

    private fun getDownloadScope(item: VideoTaskItem, tag: String):CoroutineScope  {
//        var scope = scopeMap.get(item.downloadVideoId)
//        if (scope == null){
//            scope = CoroutineScope(Dispatchers.IO)
//            scopeMap.put(item.downloadVideoId,scope)
//        }
//        return scopeMap.get(item.downloadVideoId)!!
        var scope = CoroutineScope(Dispatchers.IO)
        return scope
    }
}