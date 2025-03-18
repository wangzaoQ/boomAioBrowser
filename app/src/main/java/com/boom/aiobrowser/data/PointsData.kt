package com.boom.aiobrowser.data

class PointsData {
    //每日登陆
    var isDailyLogin = false
    var readNewsIds = mutableListOf<String>()
    var downloadVideoCount = 0
    var showVideoIds = mutableListOf<String>()

    var checkInCount = 0
    var todaySignIn = false
    var lastCheckInTime = 0L

    var allPoints = 0

    var vip30MinutesStartTime = 0L
    var vip2HoursStartTime = 0L
    var vip3DayStartTime = 0L


    fun isReadNewsComplete():Boolean{
        return readNewsIds.size>=10
    }

    fun isDownloadVideoComplete():Boolean{
        return downloadVideoCount>=3
    }

    fun isShowVideoComplete():Boolean{
        return showVideoIds.size>=5
    }
}