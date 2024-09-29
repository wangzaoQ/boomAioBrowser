package com.boom.aiobrowser.model

import androidx.lifecycle.MutableLiveData
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.tools.download.DownloadCacheManager
import com.jeffmony.downloader.VideoDownloadManager

class VideoDownloadModel  : BaseDataModel() {
    var dataLiveData = MutableLiveData<MutableList<VideoDownloadData>>()


    fun queryDataByType(type:Int){
        var list :MutableList<VideoDownloadData>?=null
        if (type == 0){
            list = DownloadCacheManager.queryDownloadModelOther()
        }else if (type == 1){
            list = DownloadCacheManager.queryDownloadModelDone()
        }
      dataLiveData.postValue(list?: mutableListOf())
    }
}