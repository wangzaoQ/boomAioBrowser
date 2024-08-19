package com.boom.aiobrowser.ui.activity

import android.view.LayoutInflater
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.BrowserActivityCleanScanBinding

class CleanScanActivity: BaseActivity<BrowserActivityCleanScanBinding>()  {
    override fun getBinding(inflater: LayoutInflater): BrowserActivityCleanScanBinding {
        return BrowserActivityCleanScanBinding.inflate(layoutInflater)
    }

    override fun setListener() {

    }

    override fun setShowView() {
    }
}