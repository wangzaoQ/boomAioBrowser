package com.boom.aiobrowser.tools

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.os.PowerManager
import androidx.core.content.ContextCompat
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.nf.NFManager

object WakeManager {
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

    @SuppressLint("InvalidWakeLockTag")
    fun wwakeUp() {
        if (isScreenOn().not()){
            AppLogs.dLog(NFManager.TAG,"当前为黑屏")
            runCatching {
                val powerManager = ContextCompat.getSystemService(APP.instance, PowerManager::class.java)
                // 判断是否支持 CPU 唤醒
                val isWakeLockLevelSupported = powerManager!!.isWakeLockLevelSupported(PowerManager.PARTIAL_WAKE_LOCK)
//             = powerManager!!.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WAKE_LOCK");
                AppLogs.dLog(NFManager.TAG,"是否支持唤醒：${isWakeLockLevelSupported}")
                if (isWakeLockLevelSupported){
                    var mWakeLock = powerManager.newWakeLock( 805306394,"WAKE_LOCK")
                    mWakeLock.acquire(100L)
                    mWakeLock.release()
                }
            }.onFailure {
                AppLogs.eLog(NFManager.TAG,"唤醒失败:${it.stackTraceToString()}")
            }
        }else{
            AppLogs.dLog(NFManager.TAG,"当前为亮屏")
        }
    }
}