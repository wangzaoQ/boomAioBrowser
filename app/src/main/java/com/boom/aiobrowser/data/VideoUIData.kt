package com.boom.aiobrowser.data

/**
 *   "source": "PAGE",
 *     "id": "https://cdn77-vid.xvideos-cdn.com/9MfQhvRg3448vrRtIgvCPA==,1731304013/videos/hls/c3/ec/1e/c3ec1ee7d14f286052c14c58aaee7128/hls.m3u8",
 *     "thumbnail": "https://cdn77-pic.xvideos-cdn.com/videos/thumbs169poster/c3/ec/1e/c3ec1ee7d14f286052c14c58aaee7128/c3ec1ee7d14f286052c14c58aaee7128.15.jpg",
 *     "description": "XVIDEOS.COM",
 */
class VideoUIData {
    //防止重复回调
    var videoResultId:String=""
    var source=""
    var thumbnail:String?=""
    var description:String?=""
    var formatsList :MutableList<VideoDownloadData> = mutableListOf()
}