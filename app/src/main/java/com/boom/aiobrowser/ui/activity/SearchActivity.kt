package com.boom.aiobrowser.ui.activity

import android.view.LayoutInflater
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.BrowserActivitySearchBinding
import com.boom.aiobrowser.tools.FragmentManager
import com.boom.aiobrowser.other.ParamsConfig
import com.boom.aiobrowser.ui.fragment.SearchFragment

class SearchActivity : BaseActivity<BrowserActivitySearchBinding>(){

    val fManager by lazy {
        FragmentManager()
    }

    override fun getBinding(inflater: LayoutInflater): BrowserActivitySearchBinding {
        return BrowserActivitySearchBinding.inflate(layoutInflater)
    }

    override fun setListener() {
    }

    override fun setShowView() {
        fManager.addFragment(supportFragmentManager, SearchFragment.newInstance(intent.getStringExtra(
            ParamsConfig.JSON_PARAMS)?:""),R.id.flSearch)
    }
}