package com.boom.aiobrowser.data

import com.boom.aiobrowser.APP

class NewsTempData {
    var newsId:String=""
    var newsTime = System.currentTimeMillis()

    fun isExpire():Boolean{
        var intervalTime= System.currentTimeMillis()-newsTime
        return intervalTime>if (APP.isDebug) 2*60*1000 else 3*24*60*60*1000
    }
}