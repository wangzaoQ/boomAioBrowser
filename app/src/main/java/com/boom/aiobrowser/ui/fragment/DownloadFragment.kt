package com.boom.aiobrowser.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.databinding.VideoFragmentDownloadBinding
import com.boom.aiobrowser.model.VideoDownloadModel
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.download.DownloadCacheManager
import com.boom.aiobrowser.tools.video.VideoManager
import com.boom.aiobrowser.ui.adapter.VideoDownloadAdapter
import com.jeffmony.downloader.VideoDownloadManager
import com.jeffmony.downloader.listener.IDownloadInfosCallback
import com.jeffmony.downloader.model.VideoTaskItem

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