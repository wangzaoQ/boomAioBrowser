package com.boom.aiobrowser.nf

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.tools.AppLogs
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import java.lang.ref.WeakReference
import java.util.Objects

object NFManager {

    var TAG = "NFManager"

    val videoNFMap = LinkedHashMap<String,Int>()


    val manager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(APP.instance)
    }

    fun newChannel(enum: NFEnum) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel: NotificationChannel? = manager.getNotificationChannel(enum.channelId)
            if (Objects.isNull(channel)) {
                val channelNew: NotificationChannel = NotificationChannel(enum.channelId, enum.channelId, enum.priority)
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
                    onSuccess.invoke()
                }

                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                    super.onDenied(permissions, doNotAskAgain)
                    onFail.invoke()
                }
            })
        }.onFailure {
            onFail.invoke()
        }
    }
}