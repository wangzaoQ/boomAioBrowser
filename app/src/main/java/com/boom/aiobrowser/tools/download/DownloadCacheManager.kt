package com.boom.aiobrowser.tools.download

import com.boom.aiobrowser.APP
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.data.model.DownloadModel
import com.boom.aiobrowser.db.AppDatabase
import com.boom.aiobrowser.tools.AppLogs

object DownloadCacheManager {

    var TAG = "DownloadCacheManager"

    var downloadDao = AppDatabase.getDatabase(APP.instance).downloadDao()


    fun addDownLoadPrepare(data: VideoDownloadData){
        var model=DataTransformationManager.downloadTransformation(data)
        AppLogs.dLog(TAG,"添加一条:${model.toString()}")
        downloadDao.insertDownloadModel(model)
        if (APP.isDebug) AppLogs.dLog(TAG,"当前总共有:${downloadDao.queryAllDownload().size}条数据")
    }

    fun queryDownloadModel(data: VideoDownloadData):DownloadModel?{
        return downloadDao.queryDataById(data.videoId?:"")
    }

    fun updateModel( model: DownloadModel) {
        downloadDao.updateModel(model)
    }
}