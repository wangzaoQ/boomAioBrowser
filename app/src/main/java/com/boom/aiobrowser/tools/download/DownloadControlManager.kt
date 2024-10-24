package com.boom.aiobrowser.tools.download

import com.blankj.utilcode.util.FileUtils
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.downloader.VideoDownloadManager
import java.io.File

object DownloadControlManager {

    fun videoDelete(data: VideoDownloadData,deleteTemp:Boolean = true) {
        var model = DownloadCacheManager.queryDownloadModel(data)
        if (model!=null){
            DownloadCacheManager.deleteModel(model)
        }
        runCatching {
            var isSuccessParent = FileUtils.delete(File(data!!.downloadFilePath).parent)
        }.onFailure {
            AppLogs.eLog("VideoManager",it.stackTraceToString())
        }
        runCatching {
            var isSuccess = FileUtils.delete(File(data!!.downloadFilePath))
        }.onFailure {
            AppLogs.eLog("VideoManager",it.stackTraceToString())
        }
        VideoDownloadManager.getInstance().deleteVideoTask(data!!.url,false)
        if (deleteTemp){
            var list = CacheManager.videoDownloadTempList
            list.remove(data)
            CacheManager.videoDownloadTempList = list
        }
    }
}