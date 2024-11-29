package com.boom.aiobrowser.tools

import java.text.SimpleDateFormat
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