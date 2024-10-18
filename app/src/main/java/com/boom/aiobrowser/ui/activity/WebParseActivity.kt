package com.boom.aiobrowser.ui.activity

import android.view.LayoutInflater
import android.view.View
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.BrowserActivityWebParseBinding
import com.boom.aiobrowser.tools.FragmentManager
import com.boom.aiobrowser.ui.fragment.TestWebFragment


class WebParseActivity: BaseActivity<BrowserActivityWebParseBinding>() {
    override fun getBinding(inflater: LayoutInflater): BrowserActivityWebParseBinding {
        return BrowserActivityWebParseBinding.inflate(layoutInflater)
    }

    override fun setListener() {
        APP.videoScanLiveData.observe(this){

        }
    }

    override fun setShowView() {
        FragmentManager().addFragment(supportFragmentManager,TestWebFragment.newInstance(intent.getStringExtra("url")?:""),
            R.id.flRoot
        )

    }
}