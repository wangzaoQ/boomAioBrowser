package com.boom.aiobrowser.nf

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.ParamsConfig
import com.boom.aiobrowser.ui.activity.MainActivity
import kotlin.random.Random

object NFJump {

    fun getRefreshIntent(data: VideoDownloadData, enum: NFEnum): PendingIntent {
        if (data.downloadType == VideoDownloadData.DOWNLOAD_SUCCESS){
            return getJumpIntent(JumpConfig.JUMP_VIDEO,data)
        }else {
            val intent = Intent(APP.instance, NFReceiver::class.java).apply {
                action = enum.channelId
                putExtra(ParamsConfig.NF_DATA, toJson(data))
            }
            return PendingIntent.getBroadcast(
                APP.instance,
                getCode(), intent, getFlags())
        }
    }


    fun getJumpIntent(nfTo:String,data: Any?=null): PendingIntent {
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