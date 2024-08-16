package com.boom.aiobrowser.tools

import android.app.KeyguardManager
import android.content.Context
import android.os.PowerManager
import com.boom.aiobrowser.APP

class WakeManager {
    fun isScreenOn(): Boolean {
        runCatching {
            return (APP.instance.getSystemService(Context.POWER_SERVICE) as PowerManager).isInteractive
        }.onFailure {
            AppLogs.eLog(APP.instance.TAG,"isScreenOn失败:${it.stackTraceToString()}")
        }
        return true
    }
    fun isDeviceLocked(): Boolean {
        runCatching {
            return (APP.instance.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager).isDeviceLocked
        }.onFailure {
            AppLogs.eLog(APP.instance.TAG,"isDeviceLocked失败:${it.stackTraceToString()}")
        }
        return true
    }
}