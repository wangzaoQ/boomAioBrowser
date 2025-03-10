package com.boom.aiobrowser.other

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.R
import com.boom.aiobrowser.other.ShortManager.TAG
import com.boom.aiobrowser.tools.AppLogs

class WidgetReceiver : BroadcastReceiver() {
    // val FJST: String = TextReadReceiver::class.java.simpleName
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null)return
        ToastUtils.showLong(context.getString(R.string.app_add_widget_success))
    }
}