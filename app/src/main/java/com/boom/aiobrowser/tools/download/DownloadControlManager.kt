package com.boom.aiobrowser.tools.download

import com.blankj.utilcode.util.FileUtils
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.tools.CacheManager
import com.jeffmony.downloader.VideoDownloadManager
import java.io.File

object DownloadControlManager {

    fun videoDelete(data: VideoDownloadData) {
        var model = DownloadCacheManager.queryDownloadModel(data)
        if (model!=null){
            DownloadCacheManager.deleteModel(model)
        }
        var isSuccessParent = FileUtils.delete(File(data!!.downloadFilePath).parent)
        var isSuccess = FileUtils.delete(File(data!!.downloadFilePath))
        VideoDownloadManager.getInstance().deleteVideoTask(data!!.url,false)
        var list = CacheManager.videoDownloadTempList
        list.remove(data)
        CacheManager.videoDownloadTempList = list
    }
}