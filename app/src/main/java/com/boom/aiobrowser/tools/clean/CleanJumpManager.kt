package com.boom.aiobrowser.tools.clean

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.APP

fun toAppDetails(activity: Activity, pkgName: String) {
    runCatching {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$pkgName")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        activity.startActivity(intent)
        APP.instance.isGoOther = true
    }.onFailure {
        ToastUtils.showShort(it.message)
    }
}