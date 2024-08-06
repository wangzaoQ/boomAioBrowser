package com.boom.aiobrowser.tools

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

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

    fun getTimeHD(time:Long): String {
        return SimpleDateFormat("HH:mm").format(Date(time))
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

}