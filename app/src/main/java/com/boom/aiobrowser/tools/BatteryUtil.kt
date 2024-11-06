package com.boom.aiobrowser.tools

import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.base.BaseActivity
import java.lang.ref.WeakReference

class BatteryUtil(var weakReference: WeakReference<BaseActivity<*>>) {

    private fun isIgnoringBatteryOptimizations(): Boolean {
        var isIgnoring = false

        weakReference.get()?.apply {
            val powerManager = getSystemService(PowerManager::class.java)
            if (powerManager != null) {
                isIgnoring = powerManager.isIgnoringBatteryOptimizations(packageName)
            }
        }
        return isIgnoring
    }

    fun requestIgnoreBatteryOptimizations() {
//        if (isIgnoringBatteryOptimizations().not()){
//            AppLogs.dLog("BatteryUtil","不支持")
//            return
//        }
        if (CacheManager.dayShowBattery.not()){
            AppLogs.dLog("BatteryUtil","今天已展示过")
            return
        }
        CacheManager.dayShowBattery = false
        runCatching {
            weakReference.get()?.apply {
                AppLogs.dLog("BatteryUtil","展示优化弹窗")
                val intent: Intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                intent.setData(Uri.parse("package:" + getPackageName()))
                startActivity(intent)
            }
        }.onFailure {
            AppLogs.dLog("BatteryUtil","errot:${it.stackTraceToString()}")
        }
    }
}