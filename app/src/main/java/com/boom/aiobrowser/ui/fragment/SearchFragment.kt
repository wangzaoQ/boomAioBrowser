package com.boom.aiobrowser.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.databinding.BrowserFragmentSearchBinding
import com.boom.aiobrowser.databinding.BrowserFragmentTempBinding

class SearchFragment : BaseFragment<BrowserFragmentSearchBinding>() {
    override fun startLoadData() {

    }

    override fun setListener() {
    }

    override fun setShowView() {
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BrowserFragmentSearchBinding {
        return BrowserFragmentSearchBinding.inflate(layoutInflater)
    }
}