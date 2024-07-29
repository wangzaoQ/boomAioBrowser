package com.boom.aiobrowser.ui.view.statusbar

import android.app.Activity
import android.os.Build
import android.view.View

class StatusBar {
    /**
     * @return if version is lager than M
     */
    fun setStatusBarLightMode(activity: Activity, isFontColorDark: Boolean): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isFontColorDark) {
                activity.window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
            return true
        }
        return false
    }

}