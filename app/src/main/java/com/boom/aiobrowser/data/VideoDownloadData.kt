package com.boom.aiobrowser.data

import com.boom.aiobrowser.data.model.DownloadModel
import com.boom.aiobrowser.tools.getMapByGson
import com.jeffmony.downloader.model.Video
import com.jeffmony.downloader.model.VideoTaskItem

class VideoDownloadData{

    companion object{
        var DOWNLOAD_NOT = 0
        var DOWNLOAD_LOADING = 1
        var DOWNLOAD_PAUSE = 2
        var DOWNLOAD_ERROR = 3
        var DOWNLOAD_SUCCESS = 4

        var TYPE_MP4 = "video/mp4"
    }

    var videoId :String?=""
    var fileName:String? = ""
    var url:String?=""
    var size:Long?=0
    var downloadSize:Long?=0
    var videoType:String?=""
    var paramsMap:HashMap<String,Any>?=null

    var downloadFileName :String = ""
    var downloadFilePath :String = ""

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
        var task = VideoTaskItem(
            data.url,
            data.url,
            data.fileName,
            "group-1"
        )
        if (data.videoType == TYPE_MP4){
            task.videoType = Video.Type.MP4_TYPE
        }
        task.filePath
        return task
    }

    fun createVideoDownloadData(model:DownloadModel):VideoDownloadData{
        var data = VideoDownloadData()
        data.videoId = model.videoId
        data.fileName = model.fileName
        data.url = model.url
        data.size = model.size
        data.downloadSize = model.downloadSize
        data.paramsMap = getMapByGson(model.paramsMapJson?:"")
        data.downloadType = model.downloadType?:DOWNLOAD_NOT
        data.downloadFileName = model.downloadFileName
        data.downloadFilePath = model.downloadFilePath
        return data
    }
}