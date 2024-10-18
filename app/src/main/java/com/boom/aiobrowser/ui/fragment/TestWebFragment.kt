package com.boom.aiobrowser.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseWebFragment
import com.boom.aiobrowser.databinding.BrowserFragmentWebBinding
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.ui.pop.DisclaimerPop
import kotlinx.coroutines.delay

class TestWebFragment: BaseWebFragment<BrowserFragmentWebBinding>() {

    companion object{
        fun newInstance(url:String): TestWebFragment{
            val args = Bundle()
            args.putString("url",url)
            val fragment = TestWebFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getInsertParent(): ViewGroup {
       return fBinding.fl
    }

    override fun loadWebOnPageStared(url: String) {
    }

    override fun startLoadData() {
    }

    override fun setListener() {
        APP.videoScanLiveData.observe(this){
            if ((it.size?:0L) <= 0L)return@observe
            allowDownload = true
            rootActivity.finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    var allowDownload = false

    var webUrl = ""
    override fun setShowView() {
        webUrl =  arguments?.getString("url")?:""
        initWeb()
        rootActivity.addLaunch(success = {
            delay(6000)
            if (allowDownload.not()){
                ToastUtils.showLong(getString(R.string.app_dead_linked))
                rootActivity.finish()
            }
        }, failBack = {})
    }

    override fun getUrl(): String {
        return webUrl
    }

    override fun onDestroy() {
        APP.videoScanLiveData.removeObservers(this)
        super.onDestroy()
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BrowserFragmentWebBinding {
        return BrowserFragmentWebBinding.inflate(inflater)
    }
}