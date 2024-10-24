package com.boom.aiobrowser.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseWebFragment
import com.boom.aiobrowser.databinding.BrowserFragmentTempBinding
import com.boom.aiobrowser.databinding.BrowserFragmentWebBinding
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.ui.pop.DisclaimerPop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.net.URLEncoder

class TestWebFragment: BaseWebFragment<BrowserFragmentTempBinding>() {

    companion object{
        fun newInstance(url:String,fromPage:String): TestWebFragment{
            val args = Bundle()
            args.putString("url",url)
            args.putString("fromPage",fromPage)
            val fragment = TestWebFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getInsertParent(): ViewGroup {
       return fBinding.fl
    }

    override fun loadWebOnPageStared(url: String) {
        var script = ""
        var list = CacheManager.fetchList
        var index = -1
        for (i in 0 until list.size){
            var fetchData = list.get(i)
            var uri = Uri.parse(webUrl)
            if (uri.host?.contains(fetchData.cUrl)?:false){
                script = fetchData.cDetail
                script = script.replace("__INPUT_URL__",URLEncoder.encode(webUrl))
                AppLogs.dLog("webReceive","fetch 模式执行特定脚本")
                mAgentWeb!!.getWebCreator().getWebView().evaluateJavascript(script) {
                    AppLogs.dLog("webReceive", "evaluateJavascript 接收:$it thread:${Thread.currentThread()}")
                }
                index = i
                break
            }
        }
        if (index == -1){
            //通用
            for (i in 0 until list.size){
                var fetchData = list.get(i)
                if (fetchData.cUrl == "*"){
                    script = fetchData.cDetail
                    script = script.replace("__INPUT_URL__",URLEncoder.encode(webUrl))
                    AppLogs.dLog("webReceive","fetch 模式执行通用脚本")
                    mAgentWeb!!.getWebCreator().getWebView().evaluateJavascript(script) {
                        AppLogs.dLog("webReceive", "evaluateJavascript 接收:$it thread:${Thread.currentThread()}")
                    }
                    break
                }
            }
        }
    }

    override fun startLoadData() {
    }

    override fun setListener() {
        APP.videoScanLiveData.observe(this){
            if ((it.size?:0L) <= 0L)return@observe
            allowDownload = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    var allowDownload = false

    var webUrl = ""
    override fun setShowView() {
        webUrl =  arguments?.getString("url")?:""
//        if (APP.isDebug){
//            webUrl = "https://vt.tiktok.com/ZSjdnyE59/"
//        }
        var fromPage =  arguments?.getString("fromPage")?:""
        initWeb()
        rootActivity.addLaunch(success = {
            delay(6000)
            if (allowDownload.not()){
                withContext(Dispatchers.Main){
                    ToastUtils.showLong(getString(R.string.app_dead_linked))
                    rootActivity.finish()
                }
            }
        }, failBack = {})
        APP.instance.shareText = ""
    }

    override fun getUrl(): String {
        return "file:///android_asset/load.html"
    }

    override fun getRealParseUrl(): String {
        return webUrl
    }

    override fun onDestroy() {
        APP.videoScanLiveData.removeObservers(this)
        super.onDestroy()
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BrowserFragmentTempBinding {
        return BrowserFragmentTempBinding.inflate(inflater)
    }
}