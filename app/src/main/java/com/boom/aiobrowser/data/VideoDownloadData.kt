package com.boom.aiobrowser.data

import com.jeffmony.downloader.model.VideoTaskItem

class VideoDownloadData{

    companion object{
        var DOWNLOAD_NOT = 0
        var DOWNLOAD_LOADING = 1
        var DOWNLOAD_PAUSE = 2
        var DOWNLOAD_ERROR = 3
        var DOWNLOAD_SUCCESS = 4
    }

    var videoId :String?=""
    var fileName:String? = ""
    var url:String?=""
    var size:Long?=0
    var downloadSize:Long?=0
    var videoType:String?=""
    var paramsMap:HashMap<String,Any>?=null

    // 0 未开始 1 进行中 2 暂停 3 错误 4 成功
    var downloadType = 0

    var isShow = false


    fun createDefault(videoId:String, fileName:String, url:String, paramsMap:HashMap<String,Any>, size:Long, videoType: String):VideoDownloadData{
        this.fileName = fileName
        this.url = url
        this.paramsMap = paramsMap
        this.size = size
        this.videoType = videoType
        this.downloadType = DOWNLOAD_NOT
        this.isShow = false
        this.videoId = videoId
        return this
    }

    fun createDownloadData(data:VideoDownloadData):VideoTaskItem{
        return VideoTaskItem(
            data.url,
            data.url,
            data.fileName,
            "group-1"
        )
    }
}