package com.boom.aiobrowser.data

/**
 *   {
 *     "source": "...", // 视频来源，可用于埋点
 *     "id": "...",
 *     "description": "...", // (optional)
 *     "thumbnail": "https://...", // (optional)
 *     "duration": 60, // (optional) 视频时长（seconds）
 *     "formats": [
 *       {
 *         "size": 48648, // (optional) 文件大小 (bytes)
 *         "url": "https://...",
 *         "format": "mp4",
 *         "cookie": true, // (optional) 该地址是否要求 cookie
 *       }
 *     ]
 *     "headers": { // (optional) 通用的 http 请求 headers
 *        "name": "value",
 *        ...
 *     }
 *   }
 */
class WebDetailsData {
    var source:String? = ""
    var id:String? = ""
    var description:String? = ""
    var thumbnail:String? = ""
    var duration:Double?= 0.0
    var formats:MutableList<DetailsData>?=null
    var headers:HeadersData?=null
}

class DetailsData{
    var size :Int?=0
    var url :String?=""
    var format :String?=""
    var cookie :Boolean?=false
    var resolution:String? = ""
}


class HeadersData{
    var name :Int?=0
    var url :String?=""
    var format :String?=""
    var cookie :Boolean?=false
}
