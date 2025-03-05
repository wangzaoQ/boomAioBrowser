package com.boom.aiobrowser.nf

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.other.ParamsConfig
import com.boom.aiobrowser.ui.activity.MainActivity
import kotlin.random.Random

object NFJump {


    /**
     *  NF_DOWNLOAD_VIDEO  0 进度中点击 1 失败点击 2成功点击  3 成功点击观看视频
     *
     *  NF_SEARCH_VIDEO  0 暂无 1 点击search  2-4 右侧按钮
     *
     *  NF_NEWS  0 点击item
     */


    fun getRefreshIntent(data: VideoDownloadData, enum: NFEnum): PendingIntent {
        if (data.downloadType == VideoDownloadData.DOWNLOAD_SUCCESS){
            return getJumpIntent(3,data,enum)
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


    fun getJumpIntent(nfTo:Int,data: Any?=null,enum: NFEnum,newsList:MutableList<NewsData>?=null): PendingIntent {
        val intent = Intent(APP.instance, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        //具体点的是当前通知哪一个区域 状态各有不同
        intent.putExtra(ParamsConfig.NF_TO, nfTo)
        //点击的是哪一种通知
        intent.putExtra(ParamsConfig.NF_ENUM_NAME,enum.menuName)
        if (newsList.isNullOrEmpty()){
            if (data!=null){
                intent.putExtra(ParamsConfig.NF_DATA, toJson(data))
            }
        }else{
            var tempList = mutableListOf<NewsData>()
            if (data!=null){
                tempList.add(0,data as NewsData)
            }
            tempList.addAll(newsList)
            intent.putExtra(ParamsConfig.NF_DATA, toJson(tempList))
        }
        return PendingIntent.getActivity(APP.instance, getCode(), intent, getFlags())
    }


    fun getCode() = (1001..99998).random(Random(System.currentTimeMillis()))

    fun getFlags() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_UPDATE_CURRENT

}