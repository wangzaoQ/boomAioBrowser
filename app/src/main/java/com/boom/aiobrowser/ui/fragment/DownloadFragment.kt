package com.boom.aiobrowser.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.databinding.VideoFragmentDownloadBinding
import com.boom.aiobrowser.model.VideoDownloadModel
import com.boom.aiobrowser.nf.NFManager
import com.boom.aiobrowser.nf.NFShow
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.download.DownloadCacheManager
import com.boom.aiobrowser.tools.download.DownloadControlManager
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.tools.video.VideoManager
import com.boom.aiobrowser.ui.activity.VideoPreActivity
import com.boom.aiobrowser.ui.adapter.VideoDownloadAdapter
import com.boom.aiobrowser.ui.pop.DownloadVideoGuidePop
import com.boom.aiobrowser.ui.pop.VideoMorePop
import com.boom.base.adapter4.util.addOnDebouncedChildClick
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import com.boom.downloader.VideoDownloadManager
import com.boom.downloader.model.VideoTaskItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference

class DownloadFragment : BaseFragment<VideoFragmentDownloadBinding>()  {

    private val viewModel by lazy {
        viewModels<VideoDownloadModel>()
    }

    companion object{
        fun newInstance(type:Int):DownloadFragment {
            val args = Bundle()
            args.putInt("fromType",type)
            val fragment = DownloadFragment()
            fragment.arguments = args
            return fragment
        }
    }

    var type = 0

    override fun startLoadData() {
    }

    override fun setListener() {
        viewModel.value.dataLiveData.observe(this){
            downloadAdapter.submitList(it)
            if(it.isEmpty()){
                fBinding.llEmpty.visibility = View.VISIBLE
                fBinding.llGuide.visibility = View.VISIBLE
                fBinding.llGuide.setOnClickListener {
                    DownloadVideoGuidePop(rootActivity).createPop("download_page") {  }
                }
            }else{
                fBinding.llEmpty.visibility = View.GONE
                fBinding.llGuide.visibility = View.GONE
            }
        }

        downloadAdapter.setOnDebouncedItemClick{adapter, view, position ->
            var data = downloadAdapter.getItem(position)
            data?.apply {
                if (downloadType == VideoDownloadData.DOWNLOAD_PAUSE) {
                    if (NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_NO ){
                        ToastUtils.showShort(rootActivity.getString(R.string.app_download_no_net))
                        return@setOnDebouncedItemClick
                    }
                    downloadType = VideoDownloadData.DOWNLOAD_LOADING
                    NFManager.requestNotifyPermission(WeakReference((context as BaseActivity<*>)), onSuccess = {
                        NFShow.showDownloadNF(data,true)
                    }, onFail = {})
                    var isSuccess = VideoDownloadManager.getInstance().resumeDownload(url)
                    if (isSuccess.not()){
                        var headerMap = HashMap<String,String>()
                        paramsMap?.forEach {
                            headerMap.put(it.key,it.value.toString())
                        }
                        VideoDownloadManager.getInstance().startDownload(data.createDownloadData(data),headerMap)
                    }
                    downloadAdapter.notifyItemChanged(position,"updateLoading")
                }else if (downloadType == VideoDownloadData.DOWNLOAD_LOADING){
                    downloadType = VideoDownloadData.DOWNLOAD_PAUSE
                    NFManager.requestNotifyPermission(WeakReference((context as BaseActivity<*>)), onSuccess = {
                        NFShow.showDownloadNF(data,true)
                    }, onFail = {})
                    VideoDownloadManager.getInstance().pauseDownloadTask(url)
                    downloadAdapter.notifyItemChanged(position,"updateLoading")
                }else if (downloadType == VideoDownloadData.DOWNLOAD_SUCCESS){
                    VideoPreActivity.startVideoPreActivity(rootActivity,data)
                    PointEvent.posePoint(PointEventKey.download_page_play,Bundle().apply {
                        putString("video_url",url)
                    })
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
            }
        }
        downloadAdapter.addOnDebouncedChildClick(R.id.ivMore) { adapter, view, position ->
            var data = downloadAdapter.getItem(position)?:return@addOnDebouncedChildClick
            VideoMorePop(rootActivity).createPop(renameBack = {
                rootActivity.addLaunch(success = {
                    viewModel.value.queryDataByType(type)
//                    var file =  File(it)
//                    data.downloadFilePath = file.absolutePath
//                    data.downloadFileName = file.name
//                    var model = DownloadCacheManager.queryDownloadModel(data)
//                    if (model!=null){
//                        model.downloadFilePath = file.absolutePath
//                        model.downloadFileName = file.name
//                        DownloadCacheManager.updateModel(model)
//                    }
//                    withContext(Dispatchers.Main){
//                        startLoadData()
//                    }
                }, failBack = {})
            }, deleteBack = {
                downloadAdapter.remove(data)
                APP.videoUpdateLiveData.postValue(data.videoId)
            }).setFileData(data)
        }
        downloadAdapter.addOnDebouncedChildClick(R.id.ivVideoClose) { adapter, view, position ->
            var item = downloadAdapter.getItem(position)?:return@addOnDebouncedChildClick
            downloadAdapter.remove(item)
            DownloadControlManager.videoDelete(item!!,false)
            APP.videoUpdateLiveData.postValue(item.videoId)
        }
        APP.videoLiveData.observe(this){
            runCatching {
                var map = it
                it.keys.forEach {
                    updateStatus(it,map.get(it)){}
                }
            }
        }
    }

    private val downloadAdapter by lazy {
        VideoDownloadAdapter()
    }


    fun updateStatus(videoType: Int, data: VideoTaskItem?, callBack: (data: VideoDownloadData) -> Unit) {
        AppLogs.dLog(VideoManager.TAG,"updateStatus type:${videoType} url:${data?.url}")
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
        if (type == 0){
            if (position == -1){
                if (videoType == VideoDownloadData.DOWNLOAD_PREPARE){
//                    downloadAdapter.add(VideoDownloadData().createByVideoDownloadTask(videoType,data))
                    viewModel.value.queryDataByType(type)
                }
            }else{
                var item = downloadAdapter.getItem(position)?:return
                item.downloadType = videoType
                item.downloadSize = data.downloadSize
                if (item.downloadType != VideoDownloadData.DOWNLOAD_PAUSE) {
                    item.size = data.totalSize
                }
                if (videoType == VideoDownloadData.DOWNLOAD_SUCCESS){
                    downloadAdapter.remove(item)
                    callBack.invoke(item)
                    updateEmptyUI()
                }else{
                    downloadAdapter.notifyItemChanged(position, "updateLoading")
                }
            }
        }else{
            if (videoType == VideoDownloadData.DOWNLOAD_SUCCESS){
                viewModel.value.queryDataByType(type)
            }
        }
    }

    fun updateByNf(data:VideoDownloadData){
        var position = -1
        for (i in 0 until downloadAdapter.items.size) {
            var item = downloadAdapter.getItem(i)
            if (item?.videoId == data.videoId) {
                position = i
                break
            }
        }
        if (position>=0){
            downloadAdapter.notifyItemChanged(position, "updateLoading")
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.value.queryDataByType(type)
    }

    private fun updateEmptyUI() {
        if(downloadAdapter.mutableItems.isEmpty()){
            fBinding.llEmpty.visibility = View.VISIBLE
            fBinding.llGuide.visibility = View.VISIBLE
            fBinding.llGuide.setOnClickListener {
                DownloadVideoGuidePop(rootActivity).createPop("download_page") {  }
            }
        }else{
            fBinding.llEmpty.visibility = View.GONE
            fBinding.llGuide.visibility = View.GONE
        }
    }

    override fun setShowView() {
        type = arguments?.getInt("fromType",0)?:0
        fBinding.rv.apply {
            layoutManager = LinearLayoutManager(rootActivity,LinearLayoutManager.VERTICAL,false)
            adapter = downloadAdapter
        }
        viewModel.value.queryDataByType(type)
    }

    override fun onDestroy() {
//        APP.downloadPageLiveData.removeObservers(this)
        super.onDestroy()
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): VideoFragmentDownloadBinding {
        return VideoFragmentDownloadBinding.inflate(layoutInflater)
    }
}