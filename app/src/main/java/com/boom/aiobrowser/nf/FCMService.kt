package com.boom.aiobrowser.nf

import com.boom.aiobrowser.APP
import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.toJson
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {
    val TAG: String = "FCMService:"

    /**
     * {"a":"b","tag":"tg_a","body":"从 Android 7.0（API 级别 24）开始，您可以在一个组中显示相关通知。例如，如果您的应用针对收到的电子邮件显示通知，请将有关新电子邮件的所有通知放入同一个群组中，以便它们收起来。","image":"https://clevertap.com/wp-content/uploads/2021/05/Push-Notification-Header.png?w\u003d1024","title":"Hello - ck_a - ch_a - lbl_a - tg_a - 44","channel":"ch_a","news_url":"https://www.baidu.com/"}
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        AppLogs.dLog(TAG,"onMessageReceived:${toJson(message)}")
        val map: Map<String, String> = message.data
        AppLogs.dLog(TAG,"onMessageReceived message.data:${toJson(map)}")
//        APP.instance.appModel.
        NFManager.showFCM(map)
    }

    override fun onNewToken(token: String) {
        AppLogs.dLog(TAG, "onNewToken =$token")
        super.onNewToken(token)
    }

}
