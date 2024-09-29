package com.boom.aiobrowser.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.databinding.VideoFragmentDownloadBinding
import com.boom.aiobrowser.model.VideoDownloadModel
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