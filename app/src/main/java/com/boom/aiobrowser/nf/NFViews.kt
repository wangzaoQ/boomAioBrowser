package com.boom.aiobrowser.nf

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.BigDecimalUtils
import com.boom.aiobrowser.tools.clean.formatLength
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.ParamsConfig
import com.boom.aiobrowser.ui.activity.MainActivity
import kotlin.random.Random

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
            var nfToRoot = JumpConfig.JUMP_DOWNLOAD_PROGRESS
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
                }
                VideoDownloadData.DOWNLOAD_SUCCESS ->{
                    leftIcon = R.mipmap.nf_video_download_success
                    rightIcon = R.mipmap.ic_nf_video_success
                    nfToRoot = JumpConfig.JUMP_DOWNLOAD_DONE
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
                getJumpIntent(nfToRoot,data)
            )
        }
        return remoteViews
    }

    fun getRefreshIntent(data: VideoDownloadData, enum: NFEnum): PendingIntent {
        if (data.downloadType == VideoDownloadData.DOWNLOAD_SUCCESS){
            return getJumpIntent(JumpConfig.JUMP_VIDEO,data)
        }else {
            val intent = Intent(APP.instance, NFReceiver::class.java).apply {
                action = enum.channelId
                putExtra(ParamsConfig.NF_DATA,toJson(data))
            }
            return PendingIntent.getBroadcast(
                APP.instance,
                getCode(), intent, getFlags())
        }
    }


    fun getJumpIntent(nfTo:String,data: VideoDownloadData?=null): PendingIntent {
        val intent = Intent(APP.instance, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(ParamsConfig.NF_TO, nfTo)
        if (data!=null){
            intent.putExtra(ParamsConfig.NF_DATA, toJson(data))
        }
        return PendingIntent.getActivity(APP.instance, getCode(), intent, getFlags())
    }


    fun getCode() = (1001..99998).random(Random(System.currentTimeMillis()))

    fun getFlags() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_UPDATE_CURRENT

}