package com.boom.aiobrowser.nf

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.util.Calendar

abstract class BaseNotifyWork(val appContext: Context, val params: WorkerParameters) : CoroutineWorker(appContext, params) {
    private val FJST: String = BaseNotifyWork::class.java.simpleName



    fun getStartTime(): Long {
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 7)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val time: Long = calendar.getTimeInMillis()
        // logsi(FJST, "time=$time")
        return time
    }

    fun getEndTime(): Long {
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val time: Long = calendar.getTimeInMillis()
        // logsi(FJST, "time=$time")
        return time
    }

}