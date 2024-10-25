package com.boom.aiobrowser.data

import android.app.NotificationManager
import androidx.core.app.NotificationCompat

/**
 * 类型枚举
 */
enum class NFEnum(val menuName: String, val channelId:String, val channelPriority:Int, val nfPriority:Int,val position: Int) {
    NF_DOWNLOAD_VIDEO("nf_down_load", "DOWNLOAD_VIDEO", NotificationManager.IMPORTANCE_DEFAULT, NotificationCompat.PRIORITY_DEFAULT,position = 0),
    NF_SEARCH_VIDEO("nf_foreground","SEARCH_VIDEO", NotificationManager.IMPORTANCE_DEFAULT,NotificationCompat.PRIORITY_DEFAULT,position = 1),

}