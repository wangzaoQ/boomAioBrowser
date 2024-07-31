package com.boom.aiobrowser.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.base.BaseWebFragment
import com.boom.aiobrowser.databinding.BrowserFragmentWebBinding
import com.boom.aiobrowser.tools.CacheManager


class WebFragment:BaseWebFragment<BrowserFragmentWebBinding>() {
    override fun getInsertParent(): ViewGroup {
        return fBinding.fl
    }


    override fun startLoadData() {

    }

    override fun setListener() {
        APP.engineLiveData.observe(this){
            fBinding.flTop.updateEngine(it)
        }
    }

    override fun setShowView() {
        initWeb()
        fBinding.flTop.updateEngine(CacheManager.engineType)
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BrowserFragmentWebBinding {
        return BrowserFragmentWebBinding.inflate(inflater)
    }

    override fun onDestroy() {
        APP.engineLiveData.removeObservers(this)
        super.onDestroy()
    }
}