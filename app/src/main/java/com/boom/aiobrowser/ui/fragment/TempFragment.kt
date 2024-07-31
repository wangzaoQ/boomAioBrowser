package com.boom.aiobrowser.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.databinding.BrowserFragmentTempBinding

class TempFragment: BaseFragment<BrowserFragmentTempBinding>() {
    override fun startLoadData() {
    }

    override fun setListener() {
    }

    override fun setShowView() {

    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BrowserFragmentTempBinding {
        return BrowserFragmentTempBinding.inflate(layoutInflater)
    }
}