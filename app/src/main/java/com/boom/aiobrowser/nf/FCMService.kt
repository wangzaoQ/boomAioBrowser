package com.boom.aiobrowser.nf

import android.os.Bundle
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.toJson
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {
    val TAG: String = "FCMService:"

    /**
     *     {
     *         "channel_id": "ch_a",
     *         "NEWS_ID": "8993843577094145",
     *         "tag": "tg_a",
     *         "body": "33.A driver was brutally attacked by a large group of people after having his vehicle damaged in downtown Los Angeles.",
     *         "image": "https://clevertap.com/wp-content/uploads/2021/05/Push-Notification-Header.png?w\u003d1024",
     *         "title": "33-ck_a-ch_a-lbl_a-tg_a",
     *         "KEY_NOW_NAV_TYPE": "3"
     *     }
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        AppLogs.dLog(TAG,"onMessageReceived:${toJson(message)}")
        val map: Map<String, String> = message.data
        AppLogs.dLog(TAG,"onMessageReceived message.data:${toJson(map)}")
//        APP.instance.appModel.
        PointEvent.posePoint(PointEventKey.fcm_data, Bundle().apply {
            putString(PointValueKey.input_text,toJson(map))
        })
        NFManager.showFCM(map)
    }

    override fun onNewToken(token: String) {
        AppLogs.dLog(TAG, "onNewToken =$token")
        super.onNewToken(token)
    }

}
