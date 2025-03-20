package com.boom.aiobrowser.tools

import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

object TimeManager {


    private val weatherMons: List<String> = listOf(
        "Jan",
        "Feb",
        "Mar",
        "Apr",
        "May",
        "Jun",
        "Jul",
        "Aug",
        "Sep",
        "Oct",
        "Nov",
        "Dec",
    )

    fun getADTime():String {
        val tmpDate = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(System.currentTimeMillis())).toString()
        return "$tmpDate"
    }

    fun getTimeHD(time:Long): String {
        return SimpleDateFormat("HH:mm").format(Date(time))
    }


    fun getVideoTime(time:Long?): String {
        return SimpleDateFormat("yyyy.MM.dd").format(Date(time?:System.currentTimeMillis()))
    }

    fun getSignTime(time:Long?): String {
        return SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(Date(time?:System.currentTimeMillis()))
    }

    fun isSameDay(serverTime: Long):Boolean{
        val localDate = SimpleDateFormat("yyyyMMdd").format(Date(System.currentTimeMillis())).toString()
        val serverDate = SimpleDateFormat("yyyyMMdd").format(Date(serverTime)).toString()
        return localDate == serverDate
    }
    fun isSameMinutes(serverTime: Long):Boolean{
        val localDate = SimpleDateFormat("yyyyMMdd HH:mm").format(Date(System.currentTimeMillis())).toString()
        val serverDate = SimpleDateFormat("yyyyMMdd HH:mm").format(Date(serverTime)).toString()
        return localDate == serverDate
    }

    fun getUserRetention(firstUseTimestamp: Long, currentTimestamp: Long = System.currentTimeMillis()): Int {
        // 将首次使用时间转换成只保留年月日的日期
        val calFirst = Calendar.getInstance().apply {
            timeInMillis = firstUseTimestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        // 将当前时间转换成只保留年月日的日期
        val calCurrent = Calendar.getInstance().apply {
            timeInMillis = currentTimestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        // 计算两者之间相差的天数
        val daysDiff = ((calCurrent.timeInMillis - calFirst.timeInMillis) / (24 * 3600 * 1000)).toInt()
//        return when (daysDiff) {
//            0 -> "d0" // 同一天
//            1 -> "d1" // 跨天后第二天（次日），即使不到24小时
//            2 -> "d2" // 第二天之后的第三天
//            else -> "d$daysDiff"
//        }
        return daysDiff
    }



    fun areConsecutiveDays(timestamp1: Long, timestamp2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply {
            timeInMillis = timestamp1
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val cal2 = Calendar.getInstance().apply {
            timeInMillis = timestamp2
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        // 将第一个日期加一天
        cal1.add(Calendar.DAY_OF_YEAR, 1)
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }




    fun getHistoryDay(time:Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        return "${weatherMons[calendar.get(Calendar.MONTH)]} ${calendar.get(Calendar.DAY_OF_MONTH)},${calendar.get(Calendar.YEAR)}"
    }

    fun getNewsTime(time:Long):String{
        var type = 0
        var toInt = ((System.currentTimeMillis() - time) / (1000 * 60)).toInt()
        if (toInt>=60){
            toInt /= 60
            type = 1
            if (toInt>=24){
                toInt /= 24
                type = 2
            }
        }


        return when (type) {
            0 -> {
                "1h"
            }
            1 -> {
                "${toInt}h"
            }
            2 -> {
                if (toInt>3){
                    toInt = 3
                }
                "${toInt}d"
            }
            else -> {""}
        }
    }


    fun isWithin30Days(timestamp1: Long, timestamp2: Long): Boolean {
        // 计算两个时间戳的差值（毫秒）
        val differenceInMillis = abs((timestamp1 - timestamp2).toDouble()).toLong()

        // 将差值转换为天数
        val differenceInDays = differenceInMillis / (1000 * 60 * 60 * 24)

        // 检查是否小于30天
        return differenceInDays < 30
    }

}