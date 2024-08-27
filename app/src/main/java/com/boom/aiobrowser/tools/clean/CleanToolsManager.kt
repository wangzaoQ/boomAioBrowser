package com.boom.aiobrowser.tools.clean

import android.app.Activity
import android.app.ActivityManager
import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import androidx.core.content.ContextCompat
import com.boom.aiobrowser.R
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.tools.clean.CleanConfig.runningAppInfo

object CleanToolsManager {

    fun getApkIcon(context: Context, apkFilePath: String?): Drawable? {
        val packageManager = context.packageManager
        val packageInfo =
            packageManager.getPackageArchiveInfo(apkFilePath!!, PackageManager.GET_ACTIVITIES)

        if (packageInfo != null) {
            val appInfo = packageInfo.applicationInfo

            // 必须设置以下属性才能成功获取到 APK 文件的图标
            appInfo.sourceDir = apkFilePath
            appInfo.publicSourceDir = apkFilePath
            // 返回图标
            return appInfo.loadIcon(packageManager)
        }

        return ContextCompat.getDrawable(context, R.mipmap.ic_file) // 如果无法提取图标，则返回 null
    }

    fun getApkName(context: Context, apkFilePath: String?): String {
        val packageManager = context.packageManager
        val packageInfo =
            packageManager.getPackageArchiveInfo(apkFilePath!!, PackageManager.GET_ACTIVITIES)

        if (packageInfo != null) {
            val appInfo = packageInfo.applicationInfo

            // 必须设置以下属性才能成功获取到 APK 文件的图标
            appInfo.sourceDir = apkFilePath
            appInfo.publicSourceDir = apkFilePath
            // 返回图标
            return appInfo.loadLabel(packageManager).toString()
        }
        return ""// 如果无法提取图标，则返回 null
    }

    fun getUsedMemory(): Long {
        val memoryInfo = getMemoryInfo()
        return memoryInfo.totalMem - memoryInfo.availMem
    }

    fun getMemoryInfo(): ActivityManager.MemoryInfo {
        val memoryInfo = ActivityManager.MemoryInfo().also {
            (APP.instance.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getMemoryInfo(it)
        }
        return memoryInfo
    }

    fun getUsedMemoryPercent(): Int {
        val memoryInfo = getMemoryInfo()
        return ((memoryInfo.totalMem - memoryInfo.availMem) * 100 / memoryInfo.totalMem).toInt()
    }

    fun cleanBackgroundProcess() {
        val mActivityManager = APP.instance.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        runningAppInfo.forEach {
            mActivityManager.killBackgroundProcesses(it.pkg)
        }
    }


    fun getInstallTime(pkgName: String): Long {
        return try {
            APP.instance.applicationContext.packageManager.getPackageInfo(pkgName, 0).firstInstallTime
        } catch (e: Exception) {
            0L
        }
    }



    fun getTotalStorage(context: Context): Long {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val storageStatsManager = context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
                return storageStatsManager.getTotalBytes(StorageManager.UUID_DEFAULT)
            } else return getTotalStorage()
        }.onFailure {
            return getTotalStorage()
        }
        return 0L
    }

    fun getUsedStorage(context: Context): Long {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val storageStatsManager = context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
                return storageStatsManager.getTotalBytes(StorageManager.UUID_DEFAULT) - storageStatsManager.getFreeBytes(
                    StorageManager.UUID_DEFAULT)
            } else return getUsedStorage()
        }.onFailure {
            return getUsedStorage()
        }
        return 0L
    }

    private fun getTotalStorage(): Long {
        try {
            val stat = StatFs(Environment.getExternalStorageDirectory().path)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            return totalBlocks * blockSize
        }catch (e:Exception){
            return 0L
        }
    }
    private fun getUsedStorage(): Long {
        val stat = StatFs(Environment.getExternalStorageDirectory().path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        val availableBlocks = stat.availableBlocksLong
        return (totalBlocks - availableBlocks) * blockSize
    }

}