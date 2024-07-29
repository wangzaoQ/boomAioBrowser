package com.fast.newsnow.view.statusbar

import android.app.Activity
import android.os.Build
import com.boom.aiobrowser.ui.view.statusbar.StatusBar

object StatusBarHelper {


    val OTHER = -1

    val ANDROID_M = 3

    private var statusBarMode = 0

    fun getStatusBarMode(): Int {
        return statusBarMode
    }

    fun isStatusM(): Boolean {
        return statusBarMode == ANDROID_M
    }


    fun initStatusBarMode(activity: Activity, isFontColorDark: Boolean) {
        try {
            if (statusBarMode == 0) {
                statusBarMode = setStatusBarMode(activity, isFontColorDark)
            } else {
                setStatusBarMode(activity, statusBarMode, isFontColorDark)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    /**
     * 设置状态栏黑色字体图标，
     * 适配4.4以上版本MIUI、Flyme和6.0以上版本其他Android
     *
     * @return 1:MIUI 2:Flyme 3:android6.0
     */
    fun setStatusBarMode(activity: Activity, isFontColorDark: Boolean): Int {
        var result = 0
        try {
            result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (StatusBar().setStatusBarLightMode(activity, isFontColorDark)) {
                    ANDROID_M
                } else {
                    OTHER
                }
            } else {
                OTHER
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return result
    }


    private fun setStatusBarMode(activity: Activity, type: Int, isFontColorDark: Boolean) {
        if (type == ANDROID_M) {
            StatusBar().setStatusBarLightMode(activity, isFontColorDark)
        }
    }


}