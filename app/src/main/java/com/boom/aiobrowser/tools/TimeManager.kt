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
}