package com.boom.aiobrowser.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.NetworkUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.databinding.VideoFragmentDownloadBinding
import com.boom.aiobrowser.model.VideoDownloadModel
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.download.DownloadCacheManager
import com.boom.aiobrowser.tools.download.DownloadControlManager
import com.boom.aiobrowser.tools.video.VideoManager
import com.boom.aiobrowser.ui.activity.VideoPreActivity
import com.boom.aiobrowser.ui.adapter.VideoDownloadAdapter
import com.boom.aiobrowser.ui.pop.VideoMorePop
import com.boom.base.adapter4.util.addOnDebouncedChildClick
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import com.jeffmony.downloader.VideoDownloadManager
import com.jeffmony.downloader.listener.IDownloadInfosCallback
import com.jeffmony.downloader.model.VideoTaskItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class DownloadFragment : BaseFragment<VideoFragmentDownloadBinding>()  {

    private val viewModel by lazy {
        viewModels<VideoDownloadModel>()
    }

    companion object{
        fun newInstance(type:Int,fromPage:String):DownloadFragment {
            val args = Bundle()
            args.putInt("fromType",type)
            args.putString("fromPage",fromPage)
            val fragment = DownloadFragment()
            fragment.arguments = args
            return fragment
        }
    }

    var type = 0

    override fun startLoadData() {
        viewModel.value.queryDataByType(type)
    }

    override fun setListener() {
        viewModel.value.dataLiveData.observe(this){
            downloadAdapter.submitList(it)
            if(it.isEmpty()){
                fBinding.llEmpty.visibility = View.VISIBLE
            }else{
                fBinding.llEmpty.visibility = View.GONE
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
                    VideoDownloadManager.getInstance().pauseDownloadTask(url)
                    downloadAdapter.notifyItemChanged(position,"updateLoading")
                }else if (downloadType == VideoDownloadData.DOWNLOAD_SUCCESS){
                    rootActivity.jumpActivity<VideoPreActivity>(Bundle().apply {
                        putString("video_path",data.downloadFilePath)
                    })
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
                    var file =  File(it)
                    data.downloadFilePath = file.absolutePath
                    data.downloadFileName = file.name
                    var model = DownloadCacheManager.queryDownloadModel(data)
                    if (model!=null){
                        model.downloadFilePath = file.absolutePath
                        model.downloadFileName = file.name
                        DownloadCacheManager.updateModel(model)
                    }
                    withContext(Dispatchers.Main){
                        startLoadData()
                    }
                }, failBack = {})
            }, deleteBack = {
                downloadAdapter.remove(data)
                rootActivity.addLaunch(success = {
                    var model = DownloadCacheManager.queryDownloadModel(data)
                    if (model!=null){
                        DownloadCacheManager.deleteModel(model)
                    }
                }, failBack = {})
            }).setFileData(data)
        }
        downloadAdapter.addOnDebouncedChildClick(R.id.ivVideoClose) { adapter, view, position ->
            var item = downloadAdapter.getItem(position)?:return@addOnDebouncedChildClick
            downloadAdapter.remove(item)
            DownloadControlManager.videoDelete(item!!)
            APP.videoUpdateLiveData.postValue(0)
        }
    }

    private val downloadAdapter by lazy {
        VideoDownloadAdapter()
    }


    fun updateStatus(type: Int, data: VideoTaskItem?, callBack: (data: VideoDownloadData) -> Unit) {
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

    override fun setShowView() {
        type = arguments?.getInt("fromType",0)?:0
        fBinding.rv.apply {
            layoutManager = LinearLayoutManager(rootActivity,LinearLayoutManager.VERTICAL,false)
            adapter = downloadAdapter
        }
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): VideoFragmentDownloadBinding {
        return VideoFragmentDownloadBinding.inflate(layoutInflater)
    }
}