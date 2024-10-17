package com.boom.aiobrowser.nf

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.nf.NFManager.nfAllow
import com.boom.aiobrowser.nf.NFManager.videoNFMap
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValue
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import kotlin.random.Random

object NFShow {

    fun showDownloadNFAll(mutableList: MutableList<VideoDownloadData>){
        if (nfAllow().not())return
        mutableList.forEach {
            showDownloadNF(it)
        }
    }

    fun showDownloadNF(data: VideoDownloadData,posePoint:Boolean = false) {
        if (nfAllow().not())return
        val smallRemote = NFViews.getDownLoadRemoteView(NFEnum.NF_DOWNLOAD_VIDEO,data)
        val largeRemote = NFViews.getDownLoadRemoteView(NFEnum.NF_DOWNLOAD_VIDEO,data, true)
        var bulider = createBuilder(NFEnum.NF_DOWNLOAD_VIDEO,smallRemote,largeRemote)
        var nfId = videoNFMap.get(data.videoId)
        if (data.videoId.isNullOrEmpty().not()){
            if (nfId == null){
                nfId = Random.nextInt(0,999999)
                videoNFMap.put(data.videoId!!,nfId)
                if (posePoint){
                    if (data.downloadType == VideoDownloadData.DOWNLOAD_LOADING || data.downloadType == VideoDownloadData.DOWNLOAD_PREPARE){
                        PointEvent.posePoint(PointEventKey.download_push_conduct,Bundle().apply {
                            putString(PointValueKey.ponit_action, PointValue.show)
                        })
                    }
                }
            }
            if (posePoint){
                if (data.downloadType == VideoDownloadData.DOWNLOAD_SUCCESS){
                    PointEvent.posePoint(PointEventKey.download_push_success,Bundle().apply {
                        putString(PointValueKey.ponit_action, PointValue.show)
                    })
                }else if (data.downloadType == VideoDownloadData.DOWNLOAD_ERROR){
                    PointEvent.posePoint(PointEventKey.download_push_fail,Bundle().apply {
                        putString(PointValueKey.ponit_action, PointValue.show)
                    })
                }
            }
            if (ActivityCompat.checkSelfPermission(
                    APP.instance,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return
            }

            NFManager.manager.notify(nfId, bulider.build())
        }
    }

    fun createBuilder(enum: NFEnum, smallRemote: RemoteViews, largeRemote: RemoteViews):NotificationCompat.Builder{
        NFManager.newChannel(enum)
        val nfBuilder = NotificationCompat.Builder(APP.instance, enum.channelId)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            nfBuilder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
//        }

        nfBuilder
            .setSmallIcon(R.mipmap.ic_start_logo)
//            .setColor(ContextCompat.getColor(App.ins, R.color.red_ff4b54))
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setCategory(Notification.CATEGORY_CALL)
            .setOngoing(false)
            .setSound(null)
            .setAutoCancel(true)
            .setContentTitle(APP.instance.getString(R.string.app_name))
            .setContentText(APP.instance.getString(R.string.app_name))

            // .setContentText("content") //通知文本
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setGroupSummary(false)
            .setGroup(enum.menuName)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setWhen(System.currentTimeMillis())
//            .setContentIntent(NFViews.getJumpIntent(0))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            nfBuilder
                .setCustomHeadsUpContentView(smallRemote)
                .setCustomContentView(smallRemote)
                .setCustomBigContentView(largeRemote)
        } else {
            nfBuilder
                .setCustomHeadsUpContentView(largeRemote)
                .setCustomContentView(largeRemote)
                .setCustomBigContentView(largeRemote)
        }
        return nfBuilder
    }
}