package com.boom.aiobrowser.data

/**
 *   "id": "b974f4ef...", // itackl
 *             "title": "This is title", // tconsi
 *             "url": "https://this.is/url", // uweek
 *             "type": "V", // tmouth V: Video, N: News
 *             "img_url": "https://this.is/img/url", // iassum
 *             "video_url": "https://this.is/video/url", // vbreas
 *             "video_duration": 60, // vsound
 *             "summary": "This is summary", // sissue
 *             "publish_time": 1686606529262, // pphilo
 *             "topics": ["C"], // tdetai
 *             "area": ["123...", "456..."], // asilve
 *             "push_title": "Pop title", // pfunct (optional)
 *             "source_id": "ia", // sfunct
 *             "source": "Fortune", // sfindi
 *             "source_icon_url": "https://this.is/icon/url", // sschem
 *             "source_tag": ["a", "b"], // sexcit
 *             "comment": 2, // cmess
 *             "read": 10, // rspite
 *             "like": 2, // lprope
 *             "liking": true // lorgan 当前用户是否点赞新闻
 *
 *
 *          详情数据
 *          "content": [ // cvehic
 *             {
 *                 "type": "img", // tmouth
 *                 "data": "https://this.is/an/image/url" // dgas
 *             },
 *             {
 *                 "type": "text", // tmouth
 *                 "data": "This is paragraph 1, with link." // dgas
 *                 "links": [ // lcousi
 *                     {
 *                         "span": [26, 29], // slong 链接在当前问本中的位置，此处即对应 link
 *                         "link": "https://this.is/an/url/to" // lsong
 *                     }
 *                 ]
 *             },
 *             {
 *                 "type": "text",
 *                 "data": "This is paragraph 2"
 *             }
 *         ]
 */
class NewsData {
    var itackl:String?=""
    var tconsi:String?=""
    var iassum:String?=""
    var uweek:String?=""
    var sissue:String?=""
    var sfindi:String?=""
    var sschem:String?=""
    var vbreas:String?=""
    var pphilo:Long?=0
    var asilve:MutableList<String>?=null

    var dataType = 0


    var tag:String?=""
    var channel:String?=""
    var nId :Int=0

    var cvehic :MutableList<NewsDetailsData>?=null
    var lcousi : MutableList<LinkData>?=null
    var tdetai: MutableList<String>? = null
    var trendList:MutableList<NewsData>? = null
    var relatedList:MutableList<NewsData>? = null
    var videoList:MutableList<NewsData>? = null
    var isTrendTop = false
    var isLoading = false

    var nfSource:String?=""


    companion object{
        var TYPE_NEWS = 0
        var TYPE_AD = 1

        var TYPE_HOME_NEWS_TOP = 90
        var TYPE_HOME_NEWS_TRENDING = 91
        var TYPE_HOME_NEWS_LOCAL = 92
        var TYPE_HOME_NEWS_VIDEO = 93
        var TYPE_DETAILS_NEWS_TITLE = 100
        var TYPE_DETAILS_NEWS_TOP_VIDEO = 101
        var TYPE_DETAILS_NEWS_TOP_IMG = 102
        var TYPE_DETAILS_NEWS_SEARCH = 103
        var TYPE_DETAILS_NEWS_SEARCH_FILM = 104
        var TYPE_DETAILS_NEWS_TEXT = 110
        var TYPE_DETAILS_NEWS_IMG = 111
        var TYPE_DETAILS_NEWS_READ_SOURCE = 120
        var TYPE_DETAILS_NEWS_RELATED = 121
        var TYPE_DETAILS_NEWS_TOPIC = 130

    }
}