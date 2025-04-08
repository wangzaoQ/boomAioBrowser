package com.boom.aiobrowser.nf

import android.annotation.SuppressLint
import android.app.Notification
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.SizeUtils.dp2px
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADDataManager
import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.nf.NFManager.nfAllow
import com.boom.aiobrowser.nf.NFManager.videoNFMap
import com.boom.aiobrowser.nf.NFManager.videoTimeMap
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValue
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.GlideManager
import com.boom.aiobrowser.tools.WakeManager
import com.boom.aiobrowser.tools.video.VideoManager
import com.boom.aiobrowser.tools.video.VideoPreloadManager
import com.boom.aiobrowser.tools.web.WebScan
import kotlinx.coroutines.delay
import kotlin.random.Random

object NFShow {

    suspend fun showNewsNFFilter(enum: NFEnum,sourceType:String = NFManager.FROM_TIMER) {
        if (nfAllow().not())return
        runCatching {
            var refreshSession = false
            var count = 0
            var newsList: MutableList<NewsData>? = null
            if (NFData.filterList1(enum, sourceType).not())return
            if (enum == NFEnum.NF_TREND){
                newsList = NFData.getNFData(refreshSession,enum.menuName)
                if (newsList.size>3){
                    newsList = newsList.subList(0,3)
                }
                if(newsList.isNullOrEmpty().not()){
                    showNewsNF(NewsData().apply {
                        nfSource = enum.menuName
                        trendList = newsList
                    },enum,sourceType)
                }
                return
            }
            if (NFManager.needRefreshData(enum.menuName)) {
                CacheManager.saveNFNewsList(enum.menuName, mutableListOf())
            }
            var limit = Random.nextInt(0, 3)
            newsList = CacheManager.getNFNewsList(enum.menuName)
            if (newsList.size > limit) {
                AppLogs.dLog(
                    NFManager.TAG,
                    "enum:${enum.menuName} sourceType:${sourceType} 使用缓存 当前缓存大小list.size:${newsList.size} 本次跳过的数量:${limit}"
                )
                newsList = NFData.filterList2(enum, sourceType, newsList)
                newsList = NFData.filterList3(enum, sourceType, newsList)
                if (newsList.isNullOrEmpty()){
                    CacheManager.saveNFNewsList(enum.menuName, mutableListOf())
                    AppLogs.dLog(NFManager.TAG,"经过 图片+历史过滤后 无数据")
                }
            }

            while (count < 10 && newsList.isNullOrEmpty()) {
                AppLogs.dLog(NFManager.TAG, "name:${enum.menuName} 获取数据来源次数count:${count + 1}")
                newsList = NFData.getNFData(refreshSession,enum.menuName)
                if (newsList.isNullOrEmpty().not()){
                    newsList = NFData.filterList2(enum, sourceType, newsList)
                    newsList = NFData.filterList3(enum, sourceType, newsList)
                }
                count++
                if (count == 7) {
                    refreshSession = true
                }
            }
            AppLogs.dLog(
                NFManager.TAG,
                "name:${enum.menuName} sourceType:${sourceType} 获取数据成功 新闻总size=${newsList?.size}"
            )
//            //TODO:TEST
//            newsList = mutableListOf()
            var data = newsList?.removeFirstOrNull()
            if (data == null || data.itackl.isNullOrEmpty()){
                AppLogs.dLog(NFManager.TAG,"name:${enum.menuName} sourceType:${sourceType} NFManager.defaultNewsList.size:${NFManager.defaultNewsList?.size}获取数据来源失败走本地通知")
                if (NFManager.defaultNewsList.isNullOrEmpty().not()){
                    var index = Random.nextInt(0,NFManager.defaultNewsList!!.size)
                    if (index>NFManager.defaultNewsList!!.size-1){
                        index = 0
                    }
                    data = NFManager.defaultNewsList!!.get(index)
                    //原始来源
                    data.nfSource = enum.menuName
                    showNewsNF(data, NFEnum.NF_DEFAULT,sourceType, mutableListOf<NewsData>().apply {
                        add(data)
                    })
                }
            }else{
                data.nfSource = enum.menuName
                showNewsNF(data, enum,sourceType,newsList)
            }
            CacheManager.saveNFNewsList(enum.menuName, newsList ?: mutableListOf())
            newsList?.apply {
                if (size>0){
                    var videoList = mutableListOf<NewsData>()
                    for (i in 0 until size){
                        videoList.add(get(i))
                        VideoPreloadManager.serialList(1,videoList)
                    }
                }
            }

            if (APP.isDebug){
                AppLogs.dLog(NFManager.TAG, "name:${enum.menuName} 移除第一条后 新闻总size=${CacheManager.getNFNewsList(enum.menuName).size}")
            }
        }.onFailure {
            AppLogs.eLog(NFManager.TAG,it.stackTraceToString())
        }
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
        var lastTime = videoTimeMap.get(data.videoId)?:0L
        if (data.downloadType == VideoDownloadData.DOWNLOAD_LOADING && (System.currentTimeMillis()- lastTime)<1000){
            AppLogs.dLog(VideoManager.TAG,"${data.fileName} 间隔时间过小 不刷新")
            return
        }
        videoTimeMap.put(data.videoId!!,System.currentTimeMillis())
        runCatching {
            var nfId = videoNFMap.get(data.videoId)
            if (data.videoId.isNullOrEmpty().not()){
                if (nfId == null){
                    nfId = Random.nextInt(1,999999)
                    data.nfId = nfId
                    AppLogs.dLog(NFManager.TAG,"video start name:${data.fileName} nfId:${nfId}")
                    val smallRemote = NFViews.getDownLoadRemoteView(NFEnum.NF_DOWNLOAD_VIDEO,data)
                    val largeRemote = NFViews.getDownLoadRemoteView(NFEnum.NF_DOWNLOAD_VIDEO,data, true)
                    var bulider = createBuilder(NFEnum.NF_DOWNLOAD_VIDEO,smallRemote,largeRemote)
//                    bulider.setOngoing(true)
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
                    NFManager.manager.notify(nfId, bulider.build())
                }else{
                    data.nfId = nfId
                    AppLogs.dLog(NFManager.TAG,"video progress name:${data.fileName} nfId:${nfId}")
                    val smallRemote = NFViews.getDownLoadRemoteView(NFEnum.NF_DOWNLOAD_VIDEO,data)
                    val largeRemote = NFViews.getDownLoadRemoteView(NFEnum.NF_DOWNLOAD_VIDEO,data, true)
                    var bulider = createBuilder(NFEnum.NF_DOWNLOAD_VIDEO,smallRemote,largeRemote)
                    bulider.setOngoing(true)
                    NFManager.manager.notify(nfId, bulider.build())
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
            }
        }.onFailure {
            AppLogs.eLog(NFManager.TAG,it.stackTraceToString())
        }
    }

    @SuppressLint("MissingPermission")
    fun showForegroundNF(){
        if (nfAllow().not())return
        runCatching {
            getForegroundNF()
            NFManager.manager.notify(NFEnum.NF_SEARCH_VIDEO.position,NFManager.nfForeground!!)
            NFManager.startForeground("showForegroundNF")
        }.onFailure {
            AppLogs.eLog(NFManager.TAG,it.stackTraceToString())
        }
    }

    @SuppressLint("MissingPermission")
    fun showNewsNF(data:NewsData,enum: NFEnum,sourceType: String,newsList:MutableList<NewsData>?=null){
        PointEvent.posePoint(PointEventKey.all_noti_theory_t,Bundle().apply {
            putString(PointValueKey.push_type, enum.menuName)
            if (enum == NFEnum.NF_DEFAULT && data.nfSource.isNullOrEmpty().not()){
                putString(PointValueKey.source_from, data.nfSource)
            }
            putString(PointValueKey.news_id, data?.itackl?:"")
            putString(PointValueKey.source_type,sourceType)
        })
        if (nfAllow().not())return
        if (data.nfSource == NFEnum.NF_NEWS.menuName || data.nfSource == NFEnum.NF_NEW_USER.menuName || data.nfSource == NFEnum.NF_NEWS_FCM.menuName){
            WakeManager.wwakeUp()
        }
        PointEvent.posePoint(PointEventKey.all_noti_t,Bundle().apply {
            putString(PointValueKey.push_type, enum.menuName)
            if (enum == NFEnum.NF_DEFAULT && data.nfSource.isNullOrEmpty().not()){
                putString(PointValueKey.source_from, data.nfSource)
            }
            putString(PointValueKey.news_id, data?.itackl?:"")
            putString(PointValueKey.source_type,sourceType)
        })
        runCatching {
            val smallRemote = NFViews.getNewsRemoteView(enum,data,newsList = newsList)
            val largeRemote = NFViews.getNewsRemoteView(enum,data, true,newsList = newsList)
            var bulider = createBuilder(enum,smallRemote,largeRemote,data.channel?:"")
            if (data.tag.isNullOrEmpty()){
                NFManager.manager.notify(enum.position, bulider.build())
            }else{
                NFManager.manager.notify(data.nId,bulider.build())
            }
            if (enum == NFEnum.NF_TREND){
                var width = dp2px(68f)
                var height = dp2px(51f)
                var list = data.trendList?: mutableListOf()
                if (list.size>0 && list[0].iassum.isNullOrEmpty().not()){
                    GlideManager.loadNFBitmap(APP.instance,list[0].iassum?:"",width,height, bitmapCall ={
                        largeRemote?.setImageViewBitmap(R.id.ivImg, it)
                        NFManager.manager.notify(enum.position, bulider.build())
                    },callFail ={
                        largeRemote?.setImageViewResource(R.id.ivImg, R.mipmap.ic_default_nf_small)
                        NFManager.manager.notify(enum.position, bulider.build())
                    })
                }
                if (list.size>1 && list[1].iassum.isNullOrEmpty().not()){
                    GlideManager.loadNFBitmap(APP.instance,list[1].iassum?:"",width,height, bitmapCall ={
                        largeRemote?.setImageViewBitmap(R.id.ivImg2, it)
                        NFManager.manager.notify(enum.position, bulider.build())
                    },callFail ={
                        largeRemote?.setImageViewResource(R.id.ivImg2, R.mipmap.ic_default_nf_small)
                        NFManager.manager.notify(enum.position, bulider.build())
                    })
                }
                if (list.size>2 && list[2].iassum.isNullOrEmpty().not()){
                    GlideManager.loadNFBitmap(APP.instance,list[2].iassum?:"",width,height, bitmapCall ={
                        largeRemote?.setImageViewBitmap(R.id.ivImg3, it)
                        NFManager.manager.notify(enum.position, bulider.build())
                    },callFail ={
                        largeRemote?.setImageViewResource(R.id.ivImg3, R.mipmap.ic_default_nf_small)
                        NFManager.manager.notify(enum.position, bulider.build())
                    })
                }
            }else{
                var width = dp2px(331f)
                var height = dp2px(181f)
                GlideManager.loadNFBitmap(APP.instance,data.iassum?:"",width,height, bitmapCall ={
                    smallRemote?.setImageViewBitmap(R.id.ivPic, it)
                    largeRemote?.setImageViewBitmap(R.id.ivPic, it)
                    largeRemote.setViewVisibility(R.id.ivBg,View.VISIBLE)
                    smallRemote.setViewVisibility(R.id.ivBg,View.VISIBLE)
                    if (data.tag.isNullOrEmpty()){
                        NFManager.manager.notify(enum.position, bulider.build())
                    }else{
                        NFManager.manager.notify(data.nId,bulider.build())
                    }
                },callFail ={
                    smallRemote?.setImageViewResource(R.id.ivPic, R.mipmap.ic_default_nf)
                    largeRemote?.setImageViewResource(R.id.ivPic, R.mipmap.ic_default_nf)
                    if (data.tag.isNullOrEmpty()){
                        NFManager.manager.notify(enum.position, bulider.build())
                    }else{
                        NFManager.manager.notify(data.nId,bulider.build())
                    }
                })
            }
            var key = data.nfSource
            if (key.isNullOrEmpty()){
                key = enum.menuName
            }
            CacheManager.saveLastRefreshTime(key)
            CacheManager.saveNewsDataHistory(data,key)
            CacheManager.saveNFShowLastTime(key,System.currentTimeMillis())
            CacheManager.saveDayNFShowCount(key,(CacheManager.getDayNFShowCount(key)+1))
            AppLogs.dLog(NFManager.TAG,"最终展示通知:nfSource:${key} enumName:${enum.menuName}")
        }.onFailure {
            AppLogs.eLog(NFManager.TAG,it.stackTraceToString())
        }
        if (APP.isDebug){
            AppLogs.dLog(NFManager.TAG,"isAllowNFPreload:"+APP.instance.isAllowNFPreload+"_ NowNewsKVUtils.dayPreloadCount:${CacheManager.dayPreloadCount}")
        }
        if (APP.instance.isAllowNFPreload){
            if (CacheManager.dayPreloadCount == 0){
                CacheManager.dayPreloadCount = 1
                AppLogs.dLog(NFManager.TAG,"通知触发广告开始加载 LAUNCH")
                AioADDataManager.preloadAD(ADEnum.LAUNCH_AD)
            }
        }
    }

    fun getForegroundNF(): Notification? {
        val smallRemote = NFViews.getForegroundRemoteView(NFEnum.NF_SEARCH_VIDEO)
        val largeRemote = NFViews.getForegroundRemoteView(NFEnum.NF_SEARCH_VIDEO, true)
        var bulider = createBuilder(NFEnum.NF_SEARCH_VIDEO,smallRemote,largeRemote)
        bulider.setOngoing(true)
        NFManager.nfForeground = bulider.build()
        return NFManager.nfForeground
    }

    fun createBuilder(enum: NFEnum, smallRemote: RemoteViews, largeRemote: RemoteViews,channel:String=""):NotificationCompat.Builder{
        NFManager.newChannel(enum,channel)
        val nfBuilder = NotificationCompat.Builder(APP.instance, if (channel.isNullOrEmpty()) enum.channelId else channel)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            nfBuilder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
//        }

        nfBuilder
            .setSmallIcon(R.mipmap.ic_nf_logo_show)
            .setColor(ContextCompat.getColor(APP.instance, R.color.color_blue_nf))
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