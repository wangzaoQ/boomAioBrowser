package com.boom.aiobrowser.nf

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.tools.video.VideoManager
import com.boom.aiobrowser.ui.ParamsConfig
import com.boom.aiobrowser.ui.activity.VideoPreActivity
import com.boom.downloader.VideoDownloadManager

class NFReceiver: BroadcastReceiver()  {

    override fun onReceive(context: Context?, intent: Intent?) {
        val action: String? = intent?.action
        var data = getBeanByGson(intent?.getStringExtra(ParamsConfig.NF_DATA)?:"",VideoDownloadData::class.java)

        when (action) {
            NFEnum.NF_DOWNLOAD_VIDEO.channelId -> {
                data?.apply {
                    if (downloadType == VideoDownloadData.DOWNLOAD_LOADING || downloadType == VideoDownloadData.DOWNLOAD_PAUSE){
                        if (downloadType == VideoDownloadData.DOWNLOAD_LOADING){
                            downloadType = VideoDownloadData.DOWNLOAD_PAUSE
                            APP.videoNFLiveData.postValue(data)
                            VideoDownloadManager.getInstance().pauseDownloadTask(url)
                        }else{
                            if (NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_NO ){
                                ToastUtils.showShort(context?.getString(R.string.app_download_no_net))
                                return
                            }
                            downloadType = VideoDownloadData.DOWNLOAD_LOADING
                            var success = VideoDownloadManager.getInstance().resumeDownload(url)
                            if (success.not()){
                                AppLogs.dLog(VideoManager.TAG,"从pause 恢复失败 重新下载")
                                var headerMap = HashMap<String,String>()
                                paramsMap?.forEach {
                                    headerMap.put(it.key,it.value.toString())
                                }
                                VideoDownloadManager.getInstance().startDownload(data.createDownloadData(data),headerMap)
                            }
                            APP.videoNFLiveData.postValue(data)
                        }
                        NFShow.showDownloadNF(this)
                    }else if (downloadType == VideoDownloadData.DOWNLOAD_ERROR){
                        downloadType = VideoDownloadData.DOWNLOAD_LOADING
                        var success = VideoDownloadManager.getInstance().resumeDownload(url)
                        if (success.not()){
                            var headerMap = HashMap<String,String>()
                            paramsMap?.forEach {
                                headerMap.put(it.key,it.value.toString())
                            }
                            VideoDownloadManager.getInstance().startDownload(data.createDownloadData(data),headerMap)
                        }
                    }else if (downloadType == VideoDownloadData.DOWNLOAD_SUCCESS){
                        context?.startActivity(Intent(context,VideoPreActivity::class.java).apply {
                            putExtra("video_path", toJson(data))
                        })
                    }
                }
            }
            else -> {}
        }
    }
}