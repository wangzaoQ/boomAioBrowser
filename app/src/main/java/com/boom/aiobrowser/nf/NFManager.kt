package com.boom.aiobrowser.nf

import android.app.Notification
import android.app.NotificationChannel
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValue
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.isAndroid12
import com.boom.aiobrowser.ui.isAndroid14
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import java.lang.ref.WeakReference
import java.util.Objects
import kotlin.random.Random

object NFManager {

    var TAG = "NFManager"

    val videoNFMap = LinkedHashMap<String,Int>()

    val nfForegroundId = 95000
    val nfNewsId = 94000
    var nfForeground:Notification?=null


    val manager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(APP.instance)
    }

    fun newChannel(enum: NFEnum) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel: NotificationChannel? = manager.getNotificationChannel(enum.channelId)
            if (Objects.isNull(channel)) {
                val channelNew: NotificationChannel = NotificationChannel(enum.channelId, enum.channelId, enum.channelPriority)
                channelNew.setSound(null,null)
                channelNew.setShowBadge(true)
                channelNew.lockscreenVisibility = Notification.VISIBILITY_PUBLIC;
                manager.createNotificationChannel(channelNew)
            }
        }
    }

    fun nfAllow():Boolean{
        var refuseContent = ""
        if (XXPermissions.isGranted(APP.instance, Permission.POST_NOTIFICATIONS).not()){
            refuseContent = "通知无权限"
        }
        if (refuseContent.isNullOrEmpty()){
            AppLogs.dLog(NFManager.TAG,"NF allow ")
            return true
        }else{
            AppLogs.dLog(NFManager.TAG,"NF refuse: $refuseContent")
            return false
        }
        return true
    }

    fun requestNotifyPermission(weakReference: WeakReference<BaseActivity<*>>,onSuccess: () -> Unit = {}, onFail: () -> Unit = {}) {
        var activity = weakReference.get()
        if (activity == null){
            onFail.invoke()
            return
        }
        val hasPermission = XXPermissions.isGranted(
            activity!!,
            Permission.POST_NOTIFICATIONS
        )
        if (hasPermission){
            onSuccess.invoke()
            return
        }
        val xxPermissions = XXPermissions.with(activity!!)
        xxPermissions.permission(Permission.POST_NOTIFICATIONS)
        runCatching {
            xxPermissions.request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                    AppLogs.dLog(TAG,"onGranted:${allGranted}")
                    PointEvent.posePoint(PointEventKey.noti_req_allow)
                    onSuccess.invoke()
                }

                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                    super.onDenied(permissions, doNotAskAgain)
                    PointEvent.posePoint(PointEventKey.noti_req_refuse)
                    onFail.invoke()
                }
            })
        }.onFailure {
            onFail.invoke()
        }
    }

    fun clickPoint(nfData: String, nfTo: Int,enumName: String) {
        when (enumName) {
            NFEnum.NF_DOWNLOAD_VIDEO.menuName -> {
                var data = getBeanByGson(nfData,VideoDownloadData::class.java)
                // 0 进度中点击 1 失败点击 2成功点击  3 成功点击观看视频
                when (nfTo) {
                     0-> {
                         PointEvent.posePoint(PointEventKey.download_push_conduct, Bundle().apply {
                             putString(PointValueKey.ponit_action, PointValue.click)
                             putString(PointValueKey.video_url, data?.url?:"")
                         })
                     }
                    1->{
                        PointEvent.posePoint(PointEventKey.download_push_fail, Bundle().apply {
                            putString(PointValueKey.ponit_action, PointValue.click)
                            putString(PointValueKey.video_url, data?.url?:"")
                        })
                    }
                    2,3->{
                        PointEvent.posePoint(PointEventKey.download_push_success, Bundle().apply {
                            putString(PointValueKey.ponit_action, PointValue.click)
                            putString(PointValueKey.video_url, data?.url?:"")
                        })
                    }
                    else -> {}
                }
            }
            NFEnum.NF_SEARCH_VIDEO.menuName -> {
                // 0 暂无 1 点击search  2-4 右侧按钮
                var type = ""
                if (nfTo == 0){
                    type = "home"
                }else if (nfTo == 1){
                    type = "search"
                }else if (nfTo == 2){
                    type = "x"
                }else if (nfTo == 3){
                    type = "ins"
                }else if (nfTo == 4){
                    type = "download"
                }
                PointEvent.posePoint(PointEventKey.fixed_explore, Bundle().apply {
                    putString(PointValueKey.type,type)
                })
            }
            NFEnum.NF_NEWS.menuName -> {
                PointEvent.posePoint(PointEventKey.all_noti_c, Bundle().apply {
                    putString(PointValueKey.push_type, enumName)
                })
            }
            NFEnum.NF_NEWS_FCM.menuName->{
                PointEvent.posePoint(PointEventKey.all_noti_c, Bundle().apply {
                    putString(PointValueKey.push_type, enumName)
                })
            }
            else -> {}
        }

    }

    fun startForeground(tag: String) {
        AppLogs.dLog(NFManager.TAG,"前台服务 触发 ${tag}-${Build.VERSION.SDK_INT}")
        if (nfAllow().not())return
        if (isAndroid12() && APP.instance.lifecycleApp.isBackGround())return
        if (isAndroid14()){
//            if (XXPermissions.isGranted(APP.instance, Permissions.FOREGROUND_SERVICE_DATA_SYNC, Permission.ACCESS_COARSE_LOCATION).not())return
        }
        AppLogs.dLog(NFManager.TAG,"前台服务 允许展示 ${tag}-${Build.VERSION.SDK_INT}")
        runCatching {
            if (APP.instance.showForeground.not()){
                ContextCompat.startForegroundService(APP.instance,
                    Intent(APP.instance, NFService::class.java)
                )
            }else{
                AppLogs.dLog(NFManager.TAG,"前台服务 正在展示中不需要重复启动 ${tag}-${Build.VERSION.SDK_INT}")
            }
        }.onFailure {
            AppLogs.eLog(NFManager.TAG,it.stackTraceToString())
        }
    }

    fun showFCM(map: Map<String, String>) {
        var chanel = map.get("channel")?:""
        var tag = map.get("tag")?:""
        var body = map.get("body")?:""
        var image = map.get("image")?:""
        var newsId = map.get("NEWS_ID")?:""
        var title = map.get("title")?:""
        var url = map.get("news_url")?:""
        var data = NewsData().apply {
            tconsi = title
            iassum = image
            itackl = newsId
            uweek = url
            sissue = body
            this.tag = tag
            this.channel = chanel
            this.nId = Random.nextInt(1000000)
        }
        NFShow.showNewsNF(data,NFEnum.NF_NEWS_FCM)
    }
}