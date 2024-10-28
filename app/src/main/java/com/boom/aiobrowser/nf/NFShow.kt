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
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.GlideManager
import com.boom.aiobrowser.tools.web.WebScan
import com.boom.aiobrowser.ui.isAndroid12
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import kotlin.random.Random

object NFShow {

    suspend fun showNewsNFFilter(enum: NFEnum) {
        if (nfAllow().not())return
        var refreshSession = false
        var count = 0
        var newsList: MutableList<NewsData>? = null
        var oldTime = CacheManager.getLastRefreshTime(enum.menuName)
//        var limit = if (NewsAPP.isDebug)1*60*1000 else 12*60*60*1000
        var limit = 3 * 60 * 60 * 1000
        var refresh = (System.currentTimeMillis() - oldTime) > limit
        AppLogs.dLog(
            NFManager.TAG,
            "name:${enum.menuName} 判断是否需要强制刷新数据/refresh:${refresh}"
        )
        if (refresh) {
            CacheManager.saveNFNewsList(enum.menuName, mutableListOf())
        }
        newsList = CacheManager.getNFNewsList(enum.menuName)
        while (count < 10 && newsList.isNullOrEmpty()) {
            AppLogs.dLog(NFManager.TAG, "name:${enum.menuName} 获取数据来源次数count:${count + 1}")
            if (enum == NFEnum.NF_NEWS) {
                newsList = NFData.getNFForYou(refreshSession)
            }
            count++
            if (count == 7) {
                refreshSession = true
            }
        }
        AppLogs.dLog(
            NFManager.TAG,
            "name:${enum.menuName} 已经过判断当前缓存已有数量:${newsList?.size}"
        )
        newsList?.removeFirstOrNull()?.apply {
            showNewsNF(this, enum)
        }
        AppLogs.dLog(
            NFManager.TAG,
            "name:${enum.menuName} 已经过判断移除当前通知后缓存已有数量:${newsList?.size}"
        )
        CacheManager.saveNFNewsList(enum.menuName, newsList ?: mutableListOf())
    }


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
    fun showNewsNF(data:NewsData,enum: NFEnum){
        if (nfAllow().not())return
        PointEvent.posePoint(PointEventKey.all_noti_t,Bundle().apply {
            putString(PointValueKey.push_type, enum.menuName)
        })
        val smallRemote = NFViews.getNewsRemoteView(enum,data)
        val largeRemote = NFViews.getNewsRemoteView(enum,data, true)
        var bulider = createBuilder(enum,smallRemote,largeRemote)
        if (data.tag.isNullOrEmpty()){
            NFManager.manager.notify(NFManager.nfNewsId, bulider.build())
        }else{
            NFManager.manager.cancel(data.tag,0)
            NFManager.manager.notify(data.tag,data.nId,bulider.build())
        }
        var width = dp2px(331f)
        var height = dp2px(181f)
        GlideManager.loadNFBitmap(APP.instance,data.iassum?:"",width,height, bitmapCall ={
            smallRemote?.setImageViewBitmap(R.id.ivPic, it)
            largeRemote?.setImageViewBitmap(R.id.ivPic, it)
            largeRemote.setViewVisibility(R.id.ivBg,View.VISIBLE)
            smallRemote.setViewVisibility(R.id.ivBg,View.VISIBLE)
            if (data.tag.isNullOrEmpty()){
                NFManager.manager.notify(NFManager.nfNewsId, bulider.build())
            }else{
                NFManager.manager.cancel(data.tag,0)
                NFManager.manager.notify(data.tag,data.nId,bulider.build())
            }
        },callFail ={
            smallRemote?.setImageViewResource(R.id.ivPic, R.mipmap.bg_news_default)
            largeRemote?.setImageViewResource(R.id.ivPic, R.mipmap.bg_news_default)
            if (data.tag.isNullOrEmpty()){
                NFManager.manager.notify(NFManager.nfNewsId, bulider.build())
            }else{
                NFManager.manager.cancel(data.tag,0)
                NFManager.manager.notify(data.tag,data.nId,bulider.build())
            }
        })
        CacheManager.saveLastRefreshTime(enum.menuName)
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