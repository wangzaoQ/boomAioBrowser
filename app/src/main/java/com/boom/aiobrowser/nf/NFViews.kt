package com.boom.aiobrowser.nf

import android.view.View
import android.widget.RemoteViews
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.nf.NFJump.getJumpIntent
import com.boom.aiobrowser.nf.NFJump.getRefreshIntent
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.BigDecimalUtils

object NFViews {

    fun getDownLoadRemoteView(enum: NFEnum, data: VideoDownloadData, isLargeView:Boolean=false): RemoteViews {
        var layoutId = if (isLargeView){
            R.layout.nf_download_video_large
        }else{
            R.layout.nf_download_video_small
        }
        var remoteViews = RemoteViews(APP.instance.packageName, layoutId)
        remoteViews.apply {
            var leftIcon = 0
            var rightIcon = 0
            var nfToRoot = 0
            when (data.downloadType) {
                VideoDownloadData.DOWNLOAD_LOADING,VideoDownloadData.DOWNLOAD_PAUSE,VideoDownloadData.DOWNLOAD_PREPARE -> {
                    leftIcon = R.mipmap.nf_video_download
                    if (data.downloadType == VideoDownloadData.DOWNLOAD_PAUSE){
                        rightIcon = R.mipmap.ic_video_play
                    }else{
                        rightIcon = R.mipmap.ic_video_pause
                    }
                    setViewVisibility(R.id.progress,View.VISIBLE)
                    var fileProgress = 0
                    runCatching {
                        fileProgress = BigDecimalUtils.mul(
                            100.0,
                            BigDecimalUtils.div((data.downloadSize ?: 0).toDouble(), data.size!!.toDouble())
                        ).toInt()
                    }
                    if (fileProgress >0){
                        setProgressBar(R.id.progress, 100, fileProgress, false);
                    }
                    if (isLargeView){
                        AppLogs.dLog(NFManager.TAG,"状态:${data.downloadType} fileProgress:${fileProgress}")
                    }
                }
                VideoDownloadData.DOWNLOAD_ERROR ->{
                    leftIcon = R.mipmap.nf_video_download_error
                    rightIcon = R.mipmap.ic_video_error
                    setViewVisibility(R.id.progress,View.GONE)
                    if (isLargeView){
                        AppLogs.dLog(NFManager.TAG,"状态: error ")
                    }
                    nfToRoot = 1
                }
                VideoDownloadData.DOWNLOAD_SUCCESS ->{
                    leftIcon = R.mipmap.nf_video_download_success
                    rightIcon = R.mipmap.ic_nf_video_success
                    nfToRoot = 2
                    setViewVisibility(R.id.progress,View.GONE)
                    AppLogs.dLog(NFManager.TAG,"状态: success")
                }
                else -> {}
            }
            if (isLargeView){
                setImageViewResource(R.id.ivLeft,leftIcon)
            }
            setImageViewResource(R.id.ivStatus,rightIcon)
            setTextViewText(R.id.tvName,data.fileName)
            setOnClickPendingIntent(
                R.id.ivStatus,
                getRefreshIntent(data,enum)
            )
            setOnClickPendingIntent(
                R.id.rlRoot,
                getJumpIntent(nfToRoot,data,enum)
            )
        }
        return remoteViews
    }

    fun getForegroundRemoteView(enum: NFEnum,isLargeView:Boolean=false): RemoteViews {
        var layoutId = if (isLargeView){
            R.layout.nf_foreground_large
        }else{
            R.layout.nf_foreground_small
        }
        var remoteViews = RemoteViews(APP.instance.packageName, layoutId)
        remoteViews.apply {
            setOnClickPendingIntent(
                R.id.llRoot,
                getJumpIntent(0,null,enum)
            )
            setOnClickPendingIntent(
                R.id.tvSearch,
                getJumpIntent(1,null,enum)
            )
            setOnClickPendingIntent(
                R.id.ivX,
                getJumpIntent(2,null,enum)
            )
            setOnClickPendingIntent(
                R.id.ivIns,
                getJumpIntent(3,null,enum)
            )
            setOnClickPendingIntent(
                R.id.ivDownload,
                getJumpIntent(4,null,enum)
            )
        }
        return remoteViews
    }

    fun getNewsRemoteView(enum: NFEnum, data: NewsData, isLargeView:Boolean=false): RemoteViews {
        return if (enum == NFEnum.NF_TREND){
            getNewsTrendingView(enum,data,isLargeView)
        }else{
            getNewsNormalView(enum,data,isLargeView)
        }
    }

    private fun getNewsNormalView(enum: NFEnum, data: NewsData, isLargeView: Boolean): RemoteViews {
        var layoutId = if (isLargeView){
            R.layout.nf_news_large
        }else{
            R.layout.nf_news_small
        }
        var remoteViews = RemoteViews(APP.instance.packageName, layoutId)
        remoteViews.apply {
            setTextViewText(R.id.tvTitle,data.tconsi)
            setOnClickPendingIntent(
                R.id.llRoot,
                getJumpIntent(0,data,enum)
            )
        }
        return remoteViews
    }

    fun getNewsTrendingView(enum: NFEnum, data:NewsData, isLargeView:Boolean=false): RemoteViews {
        var layoutId = if (isLargeView){
            R.layout.nf_news_trending_large
        }else{
            R.layout.nf_news_trending_small
        }
        var list = data.trendList?: mutableListOf()
        var remoteViews = RemoteViews(APP.instance.packageName, layoutId)
        if (isLargeView){
            remoteViews.apply {
                if (list.size>0){
                    setViewVisibility(R.id.ll1,View.VISIBLE)
                    setTextViewText(R.id.tvTitle, list[0].tconsi)
                    setOnClickPendingIntent(
                        R.id.ll1,
                        getJumpIntent(0,list[0],enum)
                    )
                    if (list[0].iassum.isNullOrEmpty()){
                        setViewVisibility(R.id.ivImg,View.GONE)
                    }
                }
                if (list.size>1){
                    setViewVisibility(R.id.ll2,View.VISIBLE)
                    setTextViewText(R.id.tvTitle2, list[1].tconsi)
                    setOnClickPendingIntent(
                        R.id.ll2,
                        getJumpIntent(1,list[1],enum)
                    )
                    if (list[1].iassum.isNullOrEmpty()){
                        setViewVisibility(R.id.ivImg2,View.GONE)
                    }
                }
                if (list.size>2){
                    setViewVisibility(R.id.ll3,View.VISIBLE)
                    setTextViewText(R.id.tvTitle3, list[2].tconsi)
                    setOnClickPendingIntent(
                        R.id.ll3,
                        getJumpIntent(2,list[2],enum)
                    )
                    if (list[2].iassum.isNullOrEmpty()){
                        setViewVisibility(R.id.ivImg3,View.GONE)
                    }

                }
            }
        }else{
//            remoteViews.setInt(R.id.ivAnimal, "setTranslationX", toXPosition);
//            remoteViews.removeAllViews(R.id.rlView)
//            val remoteViews2 = RemoteViews(APP.instance.packageName, R.layout.item_anim_layout)
//            remoteViews.addView(R.id.rlView,remoteViews2)
        }
        return remoteViews
    }
}