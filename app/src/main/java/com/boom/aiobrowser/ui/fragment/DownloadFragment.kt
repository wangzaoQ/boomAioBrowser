package com.boom.aiobrowser.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.databinding.VideoFragmentDownloadBinding
import com.boom.aiobrowser.ui.adapter.VideoDownloadAdapter

class DownloadFragment : BaseFragment<VideoFragmentDownloadBinding>()  {
    override fun startLoadData() {
    }

    override fun setListener() {
    }

    private val downloadAdapter by lazy {
        VideoDownloadAdapter()
    }

    override fun setShowView() {
        arguments?.getInt("fromType",0)
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