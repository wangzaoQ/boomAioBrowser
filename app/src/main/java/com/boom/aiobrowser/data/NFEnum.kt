package com.boom.aiobrowser.data

import android.app.NotificationManager

/**
 * 类型枚举
 */
enum class NFEnum(val menuName: String,val channelId:String,val priority:Int, val position: Int) {
    NF_DOWNLOAD_VIDEO("nf_down_load", "DOWNLOAD_VIDEO", NotificationManager.IMPORTANCE_DEFAULT,position = 0),

}