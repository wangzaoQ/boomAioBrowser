package com.boom.aiobrowser.data

import android.app.NotificationManager
import androidx.core.app.NotificationCompat

/**
 * 类型枚举
 */
enum class NFEnum(val menuName: String, val channelId:String, val channelPriority:Int, val nfPriority:Int,val position: Int) {
    NF_DOWNLOAD_VIDEO("nf_down_load", "DOWNLOAD_VIDEO", NotificationManager.IMPORTANCE_DEFAULT, NotificationCompat.PRIORITY_DEFAULT,position = 0),
    NF_SEARCH_VIDEO("nf_foreground","SEARCH_VIDEO", NotificationManager.IMPORTANCE_DEFAULT,NotificationCompat.PRIORITY_DEFAULT,position = 95000),
    //fcm channelId 无效通过
    NF_NEWS_FCM("fcm","FCM", NotificationManager.IMPORTANCE_MAX,NotificationCompat.PRIORITY_MAX,position = 93999),
    NF_NEWS("for_you_push","ForYou", NotificationManager.IMPORTANCE_MAX,NotificationCompat.PRIORITY_MAX,position = 94000),
    NF_EDITOR("editor_push","Editor", NotificationManager.IMPORTANCE_MAX,NotificationCompat.PRIORITY_MAX,position = 94001),
    NF_LOCAL("local_push","Local", NotificationManager.IMPORTANCE_MAX,NotificationCompat.PRIORITY_MAX,position = 94002),
    NF_HOT("hot_push","Hot", NotificationManager.IMPORTANCE_MAX,NotificationCompat.PRIORITY_MAX,position = 94003),
    NF_NEW_USER("new_user_push","NewUser", NotificationManager.IMPORTANCE_MAX,NotificationCompat.PRIORITY_MAX,position = 94004),
    NF_UNLOCK("unlock_push","Unlock", NotificationManager.IMPORTANCE_MAX,NotificationCompat.PRIORITY_MAX,position = 94005),
    NF_DEFAULT("default_push","Default", NotificationManager.IMPORTANCE_MAX,NotificationCompat.PRIORITY_MAX,position = 94006),
    NF_TREND("trend_push","Trend", NotificationManager.IMPORTANCE_MAX,NotificationCompat.PRIORITY_MAX,position = 94007),
    NF_POINTS_DAY0("points_push_day0","PointsDay0", NotificationManager.IMPORTANCE_MAX,NotificationCompat.PRIORITY_MAX,position = 90000),
    NF_POINTS_DAY1("points_push_day1","PointsDay1", NotificationManager.IMPORTANCE_MAX,NotificationCompat.PRIORITY_MAX,position = 90001),
    NF_POINTS_DAY3("points_push_day3","PointsDay3", NotificationManager.IMPORTANCE_MAX,NotificationCompat.PRIORITY_MAX,position = 90002),
}