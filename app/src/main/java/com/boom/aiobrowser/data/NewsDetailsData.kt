package com.boom.aiobrowser.data

/**
 *  *          详情数据
 *  *          "content": [ // cvehic
 *  *             {
 *  *                 "type": "img", // tmouth
 *  *                 "data": "https://this.is/an/image/url" // dgas
 *  *             },
 *  *             {
 *  *                 "type": "text", // tmouth
 *  *                 "data": "This is paragraph 1, with link." // dgas
 *  *                 "links": [ // lcousi
 *  *                     {
 *  *                         "span": [26, 29], // slong 链接在当前问本中的位置，此处即对应 link
 *  *                         "link": "https://this.is/an/url/to" // lsong
 *  *                     }
 *  *                 ]
 *  *             },
 *  *             {
 *  *                 "type": "text",
 *  *                 "data": "This is paragraph 2"
 *  *             }
 *  *         ]
 */
class NewsDetailsData {
    var tmouth:String?=""
    var dgas:String?=""
    var lcousi : MutableList<LinkData>?=null
}

class LinkData{
    var slong :List<Int>?=null
    var lsong :String?=""
}