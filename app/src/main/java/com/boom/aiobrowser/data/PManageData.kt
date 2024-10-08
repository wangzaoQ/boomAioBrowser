package com.boom.aiobrowser.data

class PManageData {
    var video_duration = 0
    var image_url = ""
    var video_title = ""
    var mediaDefinitions :MutableList<PVideoData>?=null
}

class PVideoData{
    var videoUrl = ""
    var defaultQuality = false
}