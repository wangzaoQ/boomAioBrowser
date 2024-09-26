package com.boom.aiobrowser.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.boom.aiobrowser.base.BaseWebFragment
import com.boom.aiobrowser.databinding.BrowserFragmentWebBinding

class TestWebFragment: BaseWebFragment<BrowserFragmentWebBinding>() {
    override fun getInsertParent(): ViewGroup {
       return fBinding.fl
    }

    override fun loadWebOnPageStared(url: String) {
    }

    override fun startLoadData() {
    }

    override fun setListener() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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