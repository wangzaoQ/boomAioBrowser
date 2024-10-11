package com.boom.aiobrowser.data

import com.boom.aiobrowser.data.model.DownloadModel
import com.boom.aiobrowser.tools.getMapByGson
import com.boom.downloader.model.Video
import com.boom.downloader.model.VideoTaskItem

class VideoDownloadData{

    companion object{
        var DOWNLOAD_NOT = 0
        var DOWNLOAD_LOADING = 1
        var DOWNLOAD_PAUSE = 2
        var DOWNLOAD_ERROR = 3
        var DOWNLOAD_SUCCESS = 4
        var DOWNLOAD_PREPARE = 5

        var TYPE_MP4 = "video/mp4"
        var TYPE_M3U8 = "m3u8"
    }

    var videoId :String?=""
    var fileName:String? = ""
    var url:String?=""
    var imageUrl:String?=""

    var size:Long?=0
    var downloadSize:Long?=0
    var videoType:String?=""
    var paramsMap:HashMap<String,Any>?=null

    var downloadFileName :String = ""
    var downloadFilePath :String = ""

    // 0 未开始 1 进行中 2 暂停 3 错误 4 成功
    var downloadType = 0

    var isShow = false


    fun createDefault(videoId:String, fileName:String, url:String,imageUrl:String, paramsMap:HashMap<String,Any>, size:Long, videoType: String):VideoDownloadData{
        this.fileName = fileName
        this.url = url
        this.imageUrl = imageUrl
        this.paramsMap = paramsMap
        this.size = size
        this.videoType = videoType
        this.downloadType = DOWNLOAD_NOT
        this.isShow = false
        this.videoId = videoId
        return this
    }

    fun createDownloadData(data:VideoDownloadData): VideoTaskItem {
        var task = VideoTaskItem(
            data.url,
            data.url,
            data.fileName,
            "group-1"
        )
        if (data.videoType == TYPE_MP4){
            task.videoType = Video.Type.MP4_TYPE
        }else if (data.videoType == TYPE_M3U8){
            task.totalSize = data.size?:0L
        }
        task.fileName = data.fileName
        task.downloadVideoId = data.videoId
//        task.filePath
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
        data.imageUrl = model.imageUrl
        data.videoType = model.videoType
        return data
    }
//    https://cv-h.phncdn.com/hls/videos/202408/28/457057941/720P_4000K_457057941.mp4/master.m3u8?QX0Q05F7u-cPEoc1oXSytihMGpXwo8esTE2tapcHZWC6phpPN5Nqub0V5tPbYS_QpS18MVf-1bidyf4SDPIq78jddyAYqPndUYzNHm6SXevfi3s2W_N1e_aIbxoBl-jQk_kv5tabRxpCLPXka3tLUK9rIe65ARwkyloDNDRTPUfLcqMiNfwSvCK9Cd6YLiQdqPbZR-O8
//
//    https://ev-h.phncdn.com/hls/videos/202408/28/457057941/720P_4000K_457057941.mp4/master.m3u8?validfrom=1728387179&validto=1728394379&ipa=38.90.18.212&hdl=-1&hash=AfL5m2Hv74LAA6ZSCulqlDAGn6w%3D
    fun covertByDbData(bean: VideoDownloadData) {
        downloadSize = bean.downloadSize
        downloadType = bean.downloadType
        url = bean.url
        size = bean.size
        videoType = bean.videoType
        imageUrl = bean.imageUrl
    }
}