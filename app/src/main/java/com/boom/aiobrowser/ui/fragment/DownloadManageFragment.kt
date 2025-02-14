package com.boom.aiobrowser.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.databinding.BrowserFragmentDownloadManageBinding
import com.boom.aiobrowser.databinding.BrowserHomeDownloadBinding

class DownloadManageFragment: BaseFragment<BrowserFragmentDownloadManageBinding>() {
    override fun startLoadData() {

    }

    override fun setListener() {
    }


    override fun setShowView() {
        fBinding.apply {
            rv.apply {
                layoutManager = GridLayoutManager(rootActivity,2)
            }
        }
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BrowserFragmentDownloadManageBinding {
        return BrowserFragmentDownloadManageBinding.inflate(layoutInflater)
    }
}