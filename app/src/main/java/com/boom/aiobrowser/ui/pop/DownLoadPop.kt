package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.databinding.VideoPopDownloadBinding
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.download.DownloadCacheManager
import com.boom.aiobrowser.tools.download.DownloadControlManager
import com.boom.aiobrowser.tools.video.VideoManager
import com.boom.aiobrowser.tools.web.WebScan
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.activity.DownloadActivity
import com.boom.aiobrowser.ui.adapter.VideoDownloadAdapter
import com.boom.base.adapter4.util.addOnDebouncedChildClick
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import com.jeffmony.downloader.VideoDownloadManager
import com.jeffmony.downloader.model.VideoTaskItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pop.basepopup.BasePopupWindow
import pop.util.animation.AnimationHelper
import pop.util.animation.TranslationConfig

class DownLoadPop(context: Context) : BasePopupWindow(context) {

    init {
        setContentView(R.layout.video_pop_download)
    }

    private val downloadAdapter by lazy {
        VideoDownloadAdapter()
    }

    var defaultBinding: VideoPopDownloadBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = VideoPopDownloadBinding.bind(contentView)
    }

    fun updateData() {
        (context as BaseActivity<*>).addLaunch(success = {
            var modelList = DownloadCacheManager.queryDownloadModelOther()
            var list = CacheManager.videoDownloadTempList
            var endList = mutableListOf<VideoDownloadData>()
            if (modelList.isNullOrEmpty()){
                endList.addAll(list)
            }else{
                for (i in 0 until list.size){
                    var data = list.get(i)
                    endList.add(data)
                    for (k in 0 until modelList.size){
                        var bean = modelList.get(k)
                        if (bean.videoId == data.videoId){
                            data.covertByDbData(bean)
                            break
                        }
                    }
                }
            }
//            https://cv-h.phncdn.com/hls/videos/202403/29/450295781/720P_4000K_450295781.mp4/master.m3u8?QaCiO5Bzg6BTFRoBus2eSVuz8wzBYxlR7tfu7LRNzTsUi0V1YeayzwFnKZtEkEpUnDTcqQ9YCLE-wAPtDNn7s6u_nTTLimIVMQ__B05X4auO40cH8UPPzY6x9So6IE0zQabTkhZcQvFOuJ5X0wVeyYsZeHvCDLYy8ht0bkbGmZjR0seYLgOuD215SYvEM--0aeZBLpVk-YY
//            https://ev-h.phncdn.com/hls/videos/202403/29/450295781/720P_4000K_450295781.mp4/master.m3u8?validfrom=1728381435&validto=1728388635&ipa=35.212.235.107&hdl=-1&hash=L%2F0SzVQqsviZDjj4JDrDhkI%2F3ys%3D
            withContext(Dispatchers.Main){
                downloadAdapter.submitList(endList)
            }
        }, failBack = {})
    }

    fun updateStatus(activity:BaseActivity<*>,type: Int, data: VideoTaskItem?,callBack: (data:VideoDownloadData) -> Unit) {
        AppLogs.dLog(VideoManager.TAG,"updateStatus type:${type} url:${data?.url}")
        if (data == null) return
        var position = -1
        for (i in 0 until downloadAdapter.items.size) {
            var item = downloadAdapter.getItem(i)
            if (item?.videoId ?: "" == data.downloadVideoId) {
                position = i
                break
            }
        }
        AppLogs.dLog(VideoManager.TAG,"updateStatus position:${position} url:${data?.url}")
        if (position == -1) return
        var item = downloadAdapter.getItem(position)?:return
        item.downloadType = type
        item.downloadSize = data.downloadSize
        item.size = data.totalSize
        if (type == VideoDownloadData.DOWNLOAD_SUCCESS){
            downloadAdapter.remove(item)
            callBack.invoke(item)
        }else{
            downloadAdapter.notifyItemChanged(position, "updateLoading")
        }
    }

    fun createPop(callBack: (data:VideoDownloadData) -> Unit) {
        defaultBinding?.apply {
            rv.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = downloadAdapter
            }
            tvDownload.setOnClickListener {
                context.startActivity(Intent(context,DownloadActivity::class.java).apply {
                    putExtra("fromPage","webpage_download_pop")
                })
                PointEvent.posePoint(PointEventKey.webpage_download_pop_record)
            }
        }


        downloadAdapter.addOnDebouncedChildClick(R.id.ivDownload) { adapter, view, position ->
            if (CacheManager.isDisclaimerFirst){
                CacheManager.isDisclaimerFirst = false
                DisclaimerPop(context).createPop {
                    clickDownload(position)
                }
            }else{
                clickDownload(position)
            }
        }
        downloadAdapter.setOnDebouncedItemClick{adapter, view, position ->
            var data = downloadAdapter.getItem(position)
            data?.apply {
                if (downloadType == VideoDownloadData.DOWNLOAD_PAUSE) {
                    if (NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_NO ){
                        return@setOnDebouncedItemClick
                    }
                    downloadType = VideoDownloadData.DOWNLOAD_LOADING
                    var success = VideoDownloadManager.getInstance().resumeDownload(url)
                    if (success.not()){
                        var headerMap = HashMap<String,String>()
                        paramsMap?.forEach {
                            headerMap.put(it.key,it.value.toString())
                        }
                        VideoDownloadManager.getInstance().startDownload(data.createDownloadData(data),headerMap)
                    }
                }else if (downloadType == VideoDownloadData.DOWNLOAD_LOADING){
                    downloadType = VideoDownloadData.DOWNLOAD_PAUSE
                    VideoDownloadManager.getInstance().pauseDownloadTask(url)
                }else if (downloadType == VideoDownloadData.DOWNLOAD_ERROR){
                    if (NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_NO ){
                        return@setOnDebouncedItemClick
                    }
                    downloadType = VideoDownloadData.DOWNLOAD_LOADING
                    var success = VideoDownloadManager.getInstance().resumeDownload(url)
                    if (success.not()){
                        var headerMap = HashMap<String,String>()
                        paramsMap?.forEach {
                            headerMap.put(it.key,it.value.toString())
                        }
                        VideoDownloadManager.getInstance().startDownload(data.createDownloadData(data),headerMap)
                    }
                }
                downloadAdapter.notifyItemChanged(position,"updateLoading")
            }
        }
        downloadAdapter.addOnDebouncedChildClick(R.id.ivVideoClose) { adapter, view, position ->
            var item = downloadAdapter.getItem(position)?:return@addOnDebouncedChildClick
            downloadAdapter.remove(item)
            DownloadControlManager.videoDelete(item!!)
            callBack.invoke(item)
        }
        updateData()
        setOutSideDismiss(true)
        showPopupWindow()
        PointEvent.posePoint(PointEventKey.webpage_download_pop)
    }

    private fun clickDownload(position: Int) {
        var data = downloadAdapter.getItem(position)
        if (data?.size ?: 0L == 0L) {
            return
        }
        data?.apply {
            (context as BaseActivity<*>).addLaunch(success = {
                var model = DownloadCacheManager.queryDownloadModel(data)
                if (model == null) {
                    data.downloadType = VideoDownloadData.DOWNLOAD_PREPARE
                    DownloadCacheManager.addDownLoadPrepare(data)
                    withContext(Dispatchers.Main) {
                        downloadAdapter.notifyItemChanged(position, "updateStatus")
                        var headerMap = HashMap<String, String>()
                        paramsMap?.forEach {
                            headerMap.put(it.key, it.value.toString())
                        }
                        VideoDownloadManager.getInstance()
                            .startDownload(data.createDownloadData(data), headerMap)
                    }

                } else {
                    withContext(Dispatchers.Main) {
                        ToastUtils.showLong(APP.instance.getString(R.string.app_already_download))
                    }
                }
            }, failBack = {})

        }
    }

    override fun onCreateShowAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withTranslation(TranslationConfig.FROM_BOTTOM)
            .toShow()
    }

    override fun onCreateDismissAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withTranslation(TranslationConfig.TO_BOTTOM)
            .toDismiss()
    }

    fun updateProgress(list: MutableList<VideoDownloadData>) {
        downloadAdapter.submitList(list)
    }

}