package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.databinding.VideoPopDownloadBinding
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.download.DownloadCacheManager
import com.boom.aiobrowser.tools.video.VideoManager
import com.boom.aiobrowser.tools.web.WebScan
import com.boom.aiobrowser.ui.adapter.VideoDownloadAdapter
import com.boom.base.adapter4.util.addOnDebouncedChildClick
import com.jeffmony.downloader.VideoDownloadManager
import com.jeffmony.downloader.model.VideoTaskItem
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
        downloadAdapter.submitList(CacheManager.videoDownloadTempList)
    }

    fun updateStatus(type: Int, data: VideoTaskItem?,callBack: (data:VideoDownloadData) -> Unit) {
        AppLogs.dLog(VideoManager.TAG,"updateStatus type:${type} url:${data?.url}")
        if (data == null) return
        var position = -1
        for (i in 0 until downloadAdapter.items.size) {
            var item = downloadAdapter.getItem(i)
            if (item?.url ?: "" == data.url) {
                position = i
                break
            }
        }
        AppLogs.dLog(VideoManager.TAG,"updateStatus position:${position} url:${data?.url}")
        if (position == -1) return
        var item = downloadAdapter.getItem(position)?:return
        item.downloadType = type
        item.downloadSize = data.downloadSize
        if (type == VideoDownloadData.DOWNLOAD_SUCCESS){
            downloadAdapter.remove(item)
            callBack.invoke(item)
        }else{
            downloadAdapter.notifyItemChanged(position, "updateLoading")
        }
    }

    fun createPop() {
        defaultBinding?.apply {
            rv.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = downloadAdapter
            }
        }
        downloadAdapter.submitList(CacheManager.videoDownloadTempList)
        downloadAdapter.addOnDebouncedChildClick(R.id.ivDownload) { adapter, view, position ->
            var data = downloadAdapter.getItem(position)
            data?.apply {
                var model = DownloadCacheManager.queryDownloadModel(data)
                if (model == null){
                    downloadType = VideoDownloadData.DOWNLOAD_LOADING
                    downloadAdapter.notifyItemChanged(position, "updateStatus")
                    DownloadCacheManager.addDownLoadPrepare(data)
                    var headerMap = HashMap<String,String>()
                    paramsMap?.forEach {
                        headerMap.put(it.key,it.value.toString())
                    }
                    VideoDownloadManager.getInstance().startDownload(data.createDownloadData(this),headerMap)
                }else{
                    ToastUtils.showLong(APP.instance.getString(R.string.app_already_download))
                }
            }
        }
        downloadAdapter.addOnDebouncedChildClick(R.id.ivVideoStatus) { adapter, view, position ->
            var data = downloadAdapter.getItem(position)
            data?.apply {
                if (downloadType == VideoDownloadData.DOWNLOAD_PAUSE) {
                    downloadType = VideoDownloadData.DOWNLOAD_LOADING
                    VideoDownloadManager.getInstance().resumeDownload(url)
                }else if (downloadType == VideoDownloadData.DOWNLOAD_LOADING){
                    downloadType = VideoDownloadData.DOWNLOAD_PAUSE
                    VideoDownloadManager.getInstance().pauseDownloadTask(url)
                }
                var model = DownloadCacheManager.queryDownloadModel(data)
                if (model!=null){
                    model.downloadType = downloadType
                    DownloadCacheManager.updateModel(model)
                }
//                downloadAdapter.notifyItemChanged(position, "updateStatus")
            }
        }
        setOutSideDismiss(true)
        showPopupWindow()
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

}