package com.boom.aiobrowser.tools

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.boom.aiobrowser.R

object CleanDataManager {

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
}