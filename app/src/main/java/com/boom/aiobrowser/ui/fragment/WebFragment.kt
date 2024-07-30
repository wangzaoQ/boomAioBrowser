package com.boom.aiobrowser.ui.fragment

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.databinding.BrowserFragmentWebBinding

class WebFragment:BaseFragment<BrowserFragmentWebBinding>() {


    override fun startLoadData() {

    }

    override fun setListener() {
    }

    override fun setShowView() {
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BrowserFragmentWebBinding {
        return BrowserFragmentWebBinding.inflate(inflater)
    }
}