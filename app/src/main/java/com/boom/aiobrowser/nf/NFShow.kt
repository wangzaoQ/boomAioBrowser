package com.boom.aiobrowser.nf

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.blankj.utilcode.util.SizeUtils.dp2px
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.nf.NFManager.nfAllow
import com.boom.aiobrowser.nf.NFManager.videoNFMap
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValue
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.GlideManager
import com.boom.aiobrowser.tools.web.WebScan
import com.boom.aiobrowser.ui.isAndroid12
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

    @SuppressLint("MissingPermission")
    fun showDownloadNF(data: VideoDownloadData, posePoint:Boolean = false) {
        if (nfAllow().not())return
        val smallRemote = NFViews.getDownLoadRemoteView(NFEnum.NF_DOWNLOAD_VIDEO,data)
        val largeRemote = NFViews.getDownLoadRemoteView(NFEnum.NF_DOWNLOAD_VIDEO,data, true)
        var bulider = createBuilder(NFEnum.NF_DOWNLOAD_VIDEO,smallRemote,largeRemote)
        bulider.setOngoing(true)
        var nfId = videoNFMap.get(data.videoId)
        if (data.videoId.isNullOrEmpty().not()){
            if (nfId == null){
                nfId = Random.nextInt(0,999999)
                videoNFMap.put(data.videoId!!,nfId)
                if (posePoint){
                    if (data.downloadType == VideoDownloadData.DOWNLOAD_LOADING || data.downloadType == VideoDownloadData.DOWNLOAD_PREPARE){
                        var source = ""
                        if (WebScan.isTikTok(data.url?:"")) {
                            source = "tiktok"
                        } else if (WebScan.isPornhub(data.url?:"")) {
                            source = "pornhub"
                        }
                        PointEvent.posePoint(PointEventKey.download_push_conduct,Bundle().apply {
                            putString(PointValueKey.ponit_action, PointValue.show)
                            putString(PointValueKey.video_source,source)
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
            NFManager.manager.notify(nfId, bulider.build())
        }
    }

    @SuppressLint("MissingPermission")
    fun showForegroundNF(){
        if (nfAllow().not())return
        getForegroundNF()
        NFManager.manager.notify(NFManager.nfForegroundId,NFManager.nfForeground!!)
        NFManager.startForeground("showForegroundNF")
    }

    @SuppressLint("MissingPermission")
    fun showNewsNF(data:NewsData){
        if (nfAllow().not())return
        val smallRemote = NFViews.getNewsRemoteView(NFEnum.NF_NEWS,data)
        val largeRemote = NFViews.getNewsRemoteView(NFEnum.NF_NEWS,data, true)
        var bulider = createBuilder(NFEnum.NF_NEWS,smallRemote,largeRemote)
        NFManager.manager.notify(NFManager.nfNewsId, bulider.build())
        var width = dp2px(331f)
        var height = dp2px(181f)
        GlideManager.loadNFBitmap(APP.instance,data.iassum?:"",width,height, bitmapCall ={
            smallRemote?.setImageViewBitmap(R.id.ivPic, it)
            largeRemote?.setImageViewBitmap(R.id.ivPic, it)
            largeRemote.setViewVisibility(R.id.ivBg,View.VISIBLE)
            smallRemote.setViewVisibility(R.id.ivBg,View.VISIBLE)
            NFManager.manager.notify(NFManager.nfNewsId, bulider.build())
        },callFail ={
            smallRemote?.setImageViewResource(R.id.ivPic, R.mipmap.bg_news_default)
            largeRemote?.setImageViewResource(R.id.ivPic, R.mipmap.bg_news_default)
            NFManager.manager.notify(NFManager.nfNewsId, bulider.build())
        } )
    }

    fun getForegroundNF(){
        val smallRemote = NFViews.getForegroundRemoteView(NFEnum.NF_SEARCH_VIDEO)
        val largeRemote = NFViews.getForegroundRemoteView(NFEnum.NF_SEARCH_VIDEO, true)
        var bulider = createBuilder(NFEnum.NF_SEARCH_VIDEO,smallRemote,largeRemote)
        bulider.setOngoing(true)
        NFManager.nfForeground = bulider.build()
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
            .setPriority(enum.nfPriority)
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