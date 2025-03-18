package com.boom.aiobrowser.tools

import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.PointsData
import com.ironsource.da
import kotlinx.coroutines.Dispatchers

object PointsManager {

    var TAG = "PointsManager"

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
        data.isDailyLogin = true
        CacheManager.pointsData = data
    }

    fun readNews(newsId:String){
        var data = CacheManager.pointsData
        AppLogs.dLog(TAG,"readNews start points:${data.allPoints}")
        if (data.readNewsIds.size>=10){
            AppLogs.dLog(TAG,"readNews 当前已达上限:${data.readNewsIds.size}")
            return
        }
        var index = -1
        for (i in 0 until data.readNewsIds.size){
            if (data.readNewsIds[i] == newsId){
                break
            }
        }
        if (index>=0){
            AppLogs.dLog(TAG,"重复数据")
            return
        }
        data.readNewsIds.add(newsId)
        data.allPoints+=10
        CacheManager.pointsData = data
        AppLogs.dLog(TAG,"readNews complete points:${data.allPoints}")
    }

    fun downloadVideo(){
        var data = CacheManager.pointsData
        if (data.downloadVideoCount>=3){
            AppLogs.dLog(TAG,"downloadVideo 当前已达上限:${data.downloadVideoCount}")
            return
        }
        data.downloadVideoCount+=1
        data.allPoints+=20
        CacheManager.pointsData = data
        AppLogs.dLog(TAG,"downloadVideo complete points:${data.allPoints}")
    }

    fun showVideo(videoId:String){
        var data = CacheManager.pointsData
        if (data.showVideoIds.size>=5){
            AppLogs.dLog(TAG,"showVideo 当前已达上限:${data.showVideoIds.size}")
            return
        }
        var index = -1
        for (i in 0 until data.showVideoIds.size){
            if (data.showVideoIds[i] == videoId){
                break
            }
        }
        if (index>=0){
            AppLogs.dLog(TAG,"重复数据")
            return
        }
        data.showVideoIds.add(videoId)
        data.allPoints+=10
        CacheManager.pointsData = data
        AppLogs.dLog(TAG,"showVideo complete points:${data.allPoints}")
    }

    fun resetData() {
        var data = CacheManager.pointsData
        data.apply {
            isDailyLogin = false
            checkInCount+=1
            readNewsIds.clear()
            downloadVideoCount = 0
            showVideoIds.clear()
            todaySignIn = false
        }
        CacheManager.pointsData = data
    }
    var vip30MinutesStartTime = 0L
    var vip2HoursStartTime = 0L
    var vip3DayStartTime = 0L
    fun addTempVip(type: Int,result:(type:Int)-> Unit) {
        APP.instance.appModel.getCurrentTime{
            var data = CacheManager.pointsData
            if (data.vip3DayStartTime >0 || data.vip2HoursStartTime>0 ||data.vip30MinutesStartTime >0){
                AppLogs.dLog(TAG,"exchange error 有生效中")
                result.invoke(-1)
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
                data.vip3DayStartTime = System.currentTimeMillis()
            }else if (type == 1){
                data.vip2HoursStartTime = System.currentTimeMillis()
            }else{
                data.vip30MinutesStartTime = System.currentTimeMillis()
            }
            CacheManager.pointsData = data
            result.invoke(type)
        }
    }

    fun resetTempVip(result:(pointData:PointsData)-> Unit){
        APP.instance.appModel.getCurrentTime{
            var data = CacheManager.pointsData
            if (data.vip3DayStartTime>0){
                var middle = (System.currentTimeMillis()-data.vip3DayStartTime)
                if (middle>3*24*60*60*1000){
                    AppLogs.dLog(TAG,"resetTempVip 3天vip 已超时")
                    data.vip3DayStartTime = 0L
                }
            }
            if (data.vip2HoursStartTime >0){
                var middle = (System.currentTimeMillis()-data.vip2HoursStartTime)
                if (middle>2*60*60*1000){
                    AppLogs.dLog(TAG,"resetTempVip 2小时vip 已超时")
                    data.vip2HoursStartTime = 0L
                }
            }
            if (data.vip30MinutesStartTime >0){
                var middle = (System.currentTimeMillis()-data.vip30MinutesStartTime)
                if (middle>30*60*1000){
                    AppLogs.dLog(TAG,"resetTempVip 半小时vip 已超时")
                    data.vip30MinutesStartTime = 0L
                }
            }
            CacheManager.pointsData = data
            result.invoke(data)
        }
    }

}