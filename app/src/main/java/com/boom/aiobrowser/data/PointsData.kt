package com.boom.aiobrowser.data

import com.boom.aiobrowser.tools.PointsManager

class PointsData {
    //每日登陆
    var isDailyLogin = false
    var readNewsList = mutableListOf<DailyQuestsData>()
    var downloadVideoList = mutableListOf<DailyQuestsData>()
    var showVideoList = mutableListOf<DailyQuestsData>()

    var checkInCount = 0
    var todaySignIn = false
    var lastCheckInTime = 0L

    var allPoints = 0

//    var vip30MinutesStartTime = 0L
//    var vip2HoursStartTime = 0L
//    var vip3DayStartTime = 0L

    var tempVipStartTime = 0L
    var tempVipDuration = 0L

    var tempNoADStartTime = 0L
    var tempNoADDuration = 0L


    fun dailyLoginPoints():Int{
       return PointsManager.DAILY_LOGIN_POINTS
    }

    fun newReadCount():Int{
        var unclaimedCounts = 0
        readNewsList.forEach {
            if (it.isReceive.not()){
                unclaimedCounts+= 1
            }
        }
        return unclaimedCounts
    }

    fun showVideoCount():Int{
        var unclaimedCounts = 0
        showVideoList.forEach {
            if (it.isReceive.not()){
                unclaimedCounts+= 1
            }
        }
        return unclaimedCounts
    }

    fun downVideoCount():Int{
        var unclaimedCounts = 0
        downloadVideoList.forEach {
            if (it.isReceive.not()){
                unclaimedCounts+= 1
            }
        }
        return unclaimedCounts
    }

    fun newsPoints():Int{
        var unclaimedPoints:Int = 0
        readNewsList.forEach {
            if (it.isReceive.not()){
                unclaimedPoints+= PointsManager.READ_NEWS_POINTS
            }
        }
        return unclaimedPoints
    }

    fun showVideoPoints():Int{
        var unclaimedPoints:Int = 0
        showVideoList.forEach {
            if (it.isReceive.not()){
                unclaimedPoints+= PointsManager.SHOW_VIDEO_POINTS
            }
        }
        return unclaimedPoints
    }

    fun downloadVideoPoints():Int{
        var unclaimedPoints:Int = 0
        downloadVideoList.forEach {
            if (it.isReceive.not()){
                unclaimedPoints+= PointsManager.DOWNLOAD_VIDEO_POINTS
            }
        }
        return unclaimedPoints
    }

}