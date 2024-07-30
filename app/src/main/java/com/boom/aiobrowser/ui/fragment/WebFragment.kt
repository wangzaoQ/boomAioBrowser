package com.boom.aiobrowser.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.boom.aiobrowser.base.BaseWebFragment
import com.boom.aiobrowser.databinding.BrowserFragmentWebBinding


class WebFragment:BaseWebFragment<BrowserFragmentWebBinding>() {
    override fun getInsertParent(): ViewGroup {
        return fBinding.fl
    }


    override fun startLoadData() {

    }

    override fun setListener() {
    }

    override fun setShowView() {
        initWeb()
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BrowserFragmentWebBinding {
        return BrowserFragmentWebBinding.inflate(inflater)
    }
}