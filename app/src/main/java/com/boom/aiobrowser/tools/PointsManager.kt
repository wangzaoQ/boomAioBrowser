package com.boom.aiobrowser.tools

import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.DailyQuestsData
import com.boom.aiobrowser.data.PointsData
import com.ironsource.da
import kotlinx.coroutines.Dispatchers

object PointsManager {

    var TAG = "PointsManager"

    const val DOWNLOAD_VIDEO_POINTS = 20
    const val READ_NEWS_POINTS = 10
    const val SHOW_VIDEO_POINTS = 10

    /**
     * 每日签到梯度积分值：第一天50，第二天50，第三天80，第四天50，第五天50，第六天50，第七天80
     */
    fun signPoints(result:(pointData:PointsData?)-> Unit) {
        APP.instance.appModel.getCurrentTime {
            if (it == 0L){
                AppLogs.dLog(TAG,"sign error 获取服务器时间失败")
                result.invoke(null)
                return@getCurrentTime
            }
            if (TimeManager.isSameDay(it).not()){
                AppLogs.dLog(TAG,"sign error 当前时间和本地时间不同")
                result.invoke(null)
                return@getCurrentTime
            }
            var data = CacheManager.pointsData
            if (data.lastCheckInTime>0){
                if (TimeManager.areConsecutiveDays(data.lastCheckInTime,it).not()){
                    AppLogs.dLog(TAG,"sign error 上次签到时间和服务器时间不连续")
                    result.invoke(null)
                    return@getCurrentTime
                }
            }
            when (data.checkInCount) {
                0,1,3,4,5-> {
                    data.allPoints += 50
                }
                2,6 -> {
                    data.allPoints += 80
                }
                else -> {}
            }
            data.todaySignIn = true
            data.lastCheckInTime = System.currentTimeMillis()
            CacheManager.pointsData = data
            result.invoke(data)
        }
    }

    fun login(){
        var data = CacheManager.pointsData
        if (data.isDailyLogin.not()){
            data.allPoints+=50
        }
        data.isDailyLogin = true
        CacheManager.pointsData = data
    }

    fun readNews(newsId:String){
        var data = CacheManager.pointsData
        AppLogs.dLog(TAG,"readNews start points:${data.allPoints}")
//        if (data.readNewsList.size>=10){
//            AppLogs.dLog(TAG,"readNews 当前已达上限:${data.readNewsList.size}")
//            return
//        }
        var index = -1
        for (i in 0 until data.readNewsList.size){
            if (data.readNewsList[i].id == newsId){
                index = i
                break
            }
        }
        if (index>=0){
            AppLogs.dLog(TAG,"readNews 重复数据")
            return
        }
        data.readNewsList.add(DailyQuestsData().apply {
            id = newsId
        })
        CacheManager.pointsData = data
        AppLogs.dLog(TAG,"readNews complete points:${data.allPoints}")
    }

    fun downloadVideo(videoId: String){
        var data = CacheManager.pointsData
//        if (data.downloadVideoList.size>=3){
//            AppLogs.dLog(TAG,"downloadVideo 当前已达上限:${data.downloadVideoList.size}")
//            return
//        }
        var index = -1
        for (i in 0 until data.downloadVideoList.size){
            if (data.downloadVideoList[i].id == videoId){
                index = i
                break
            }
        }
        if (index>=0){
            AppLogs.dLog(TAG,"downloadVideo 重复数据")
            return
        }
        data.downloadVideoList.add(DailyQuestsData().apply {
            id = videoId
        })
        CacheManager.pointsData = data
        AppLogs.dLog(TAG,"downloadVideo complete points:${data.allPoints}")
    }

    fun showVideo(videoId:String){
        var data = CacheManager.pointsData
//        if (data.showVideoList.size>=5){
//            AppLogs.dLog(TAG,"showVideo 当前已达上限:${data.showVideoList.size}")
//            return
//        }
        var index = -1
        for (i in 0 until data.showVideoList.size){
            if (data.showVideoList[i].id == videoId){
                index = i
                break
            }
        }
        if (index>=0){
            AppLogs.dLog(TAG,"showVideo 重复数据")
            return
        }
        data.showVideoList.add(DailyQuestsData().apply {
            id = videoId
        })
        CacheManager.pointsData = data
        AppLogs.dLog(TAG,"showVideo complete points:${data.allPoints}")
    }

    fun resetData() {
        var data = CacheManager.pointsData
        data.apply {
            if (todaySignIn){
                checkInCount+=1
                todaySignIn = false
            }
            isDailyLogin = false
            readNewsList.clear()
            downloadVideoList.clear()
            showVideoList.clear()
        }
        CacheManager.pointsData = data
    }

    fun addTempVip(type: Int,result:(type:Int)-> Unit) {
        APP.instance.appModel.getCurrentTime{
            var data = CacheManager.pointsData
            if (type == 0 && data.allPoints<500){
                AppLogs.dLog(TAG,"exchange error 积分不足当前积分:${data.allPoints}")
                result.invoke(-2)
                return@getCurrentTime
            }else if (type == 1 && data.allPoints<200){
                AppLogs.dLog(TAG,"exchange error 积分不足当前积分:${data.allPoints}")
                result.invoke(-2)
                return@getCurrentTime
            }else if (type == 2 && data.allPoints<100){
                AppLogs.dLog(TAG,"exchange error 积分不足当前积分:${data.allPoints}")
                result.invoke(-2)
                return@getCurrentTime
            }
            if (it == 0L){
                AppLogs.dLog(TAG,"exchange error 获取服务器时间失败")
                result.invoke(-1)
                return@getCurrentTime
            }
            if (TimeManager.isSameMinutes(it).not()){
                AppLogs.dLog(TAG,"exchange error 当前时间和本地时间不同")
                result.invoke(-1)
                return@getCurrentTime
            }
            if (type == 0){
                if ((System.currentTimeMillis()-data.tempVipDuration)>data.tempVipStartTime){
                    data.tempVipStartTime = 0
                    data.tempVipDuration = 0
                }
            }else{
                if ((System.currentTimeMillis()-data.tempNoADDuration)>data.tempNoADStartTime){
                    data.tempNoADStartTime = 0
                    data.tempNoADDuration = 0
                }
            }

            if (type == 0){
                data.tempVipDuration+= 3*24*60*60*1000
                data.tempVipStartTime = System.currentTimeMillis()
                data.allPoints -= 500
            }else if (type == 1){
                data.tempNoADDuration+= 2*60*60*1000
                data.tempNoADStartTime = System.currentTimeMillis()
                data.allPoints -= 200
            }else{
                data.tempNoADDuration+= 30*60*1000
                data.tempNoADStartTime = System.currentTimeMillis()
                data.allPoints -= 100
            }
            CacheManager.pointsData = data
            result.invoke(type)
        }
    }

    fun resetTempVip(result:(pointData:PointsData)-> Unit){
        var data = CacheManager.pointsData
        if (data.tempVipDuration!=0L || data.tempVipStartTime!=0L || data.tempNoADDuration !=0L || data.tempNoADStartTime!=0L){
            APP.instance.appModel.getCurrentTime{
                if ((it-data.tempVipDuration)>data.tempVipStartTime){
                    data.tempVipDuration = 0L
                    data.tempVipStartTime = 0L
                }
                if ((it-data.tempNoADDuration)>data.tempNoADStartTime){
                    data.tempNoADDuration = 0L
                    data.tempNoADStartTime = 0L
                }
                CacheManager.pointsData = data
                result.invoke(data)
            }
        }
    }

    fun receiveShowVideoPoints(result:(points:Int)-> Unit) {
        var data = CacheManager.pointsData
        var points = data.showVideoPoints()
        if (points == 0){
            result.invoke(0)
            return
        }
        data.showVideoList.forEach {
            it.isReceive = true
        }
        data.allPoints+=points
        CacheManager.pointsData = data
        result.invoke(points)
    }

    fun receiveReadNewsPoints(result:(points:Int)-> Unit) {
        var data = CacheManager.pointsData
        var points = data.newsPoints()
        if (points == 0){
            result.invoke(0)
            return
        }
        data.readNewsList.forEach {
            it.isReceive = true
        }
        data.allPoints+=points
        CacheManager.pointsData = data
        result.invoke(points)
    }

    fun receiveDownloadVideoPoints(result:(points:Int)-> Unit) {
        var data = CacheManager.pointsData
        var points = data.downloadVideoPoints()
        if (points == 0){
            result.invoke(0)
            return
        }
        data.downloadVideoList.forEach {
            it.isReceive = true
        }
        data.allPoints+=points
        CacheManager.pointsData = data
        result.invoke(points)
    }


}