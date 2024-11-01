package com.boom.aiobrowser.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseWebFragment
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.databinding.BrowserFragmentWebBinding
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.ParamsConfig
import com.boom.aiobrowser.ui.activity.WebDetailsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class WebFragment:BaseWebFragment<BrowserFragmentWebBinding>() {

    var jumpData:JumpData?=null


    override fun getInsertParent(): ViewGroup {
        return fBinding.fl
    }

    override fun loadWebOnPageStared(url: String) {
        addLast(url)
    }

    private fun addLast(url: String) {
        var showNext = false
        rootActivity.addLaunch(success = {
            var linkedUrlList = CacheManager.linkedUrlList
            if (linkedUrlList.contains(url)){
                jumpData?.apply {
                    jumpUrl = url
                    //之前加载过
                    var index = linkedUrlList.indexOf(url)
                    if (index == linkedUrlList.size-1){
                        AppLogs.dLog(fragmentTAG,"之前加载过但是是最后一个")
                        showNext = false
                        //是最后一个
                        nextJumpUrl = ""
                    }else{
                        showNext = true
                        AppLogs.dLog(fragmentTAG,"之前加载过 index:${index}")
                        nextJumpUrl = linkedUrlList.get(index+1)
                        nextJumpType = JumpConfig.JUMP_WEB
                    }
                    JumpDataManager.updateCurrentJumpData(this,"webFragment 存储jumpData")
                }
            }else{
                //没加载过
                jumpData?.apply {
                    jumpUrl = url
                    nextJumpUrl = url
                    showNext = false
                    AppLogs.dLog(fragmentTAG,"之前未加载过")
                    JumpDataManager.updateCurrentJumpData(this,"webFragment 存储jumpData")
                }
                linkedUrlList.add(url)
                CacheManager.linkedUrlList = linkedUrlList
            }
            withContext(Dispatchers.Main){
                if (rootActivity is WebDetailsActivity){
                    (rootActivity as WebDetailsActivity).apply {
                        updateBottom(true,showNext, jumpData = jumpData,"webView loadWebOnPageStared")
                    }
                }
            }
            if(CacheManager.browserStatus == 0){
                var list = CacheManager.historyDataList
                jumpData?.apply {
                    updateTime = System.currentTimeMillis()
                    list.add(0,this)
                    CacheManager.historyDataList = list
                }
            }
        }, failBack = {})
    }

    override fun startLoadData() {
    }

    override fun setListener() {
        APP.engineLiveData.observe(this){
            fBinding.flTop.updateEngine(it)
        }
        fBinding.flTop.updateTopView(2,searchRefresh={
            refresh()
        })
    }

    private fun refresh() {
        if (mAgentWeb != null) {
            mAgentWeb!!.urlLoader.reload() // 刷新
        }
    }

    override fun loadWebFinished() {
        super.loadWebFinished()
        fBinding.flTop.binding.tvToolbarSearch.text = "${jumpData?.jumpTitle} ${getSearchTitle()}"
        fBinding.refreshLayout.isRefreshing = false
        var key = mAgentWeb?.webCreator?.webView?.url?:""
//        if (rootActivity is WebDetailsActivity){
//            (rootActivity as WebDetailsActivity).updateDownloadButtonStatus(true)
//        }
    }

    fun getSearchTitle():String{
        var search = when (CacheManager.engineType) {
            else -> { "Google"}
        }
        var unit = rootActivity.getString(R.string.app_search)
       return " - $search $unit"
    }

    open fun updateData(data:JumpData?){
        jumpData = data
        initWeb()
        fBinding.flTop.updateEngine(CacheManager.engineType)
        fBinding.flTop.binding.tvToolbarSearch.text = jumpData?.jumpUrl
        fBinding.refreshLayout.isEnabled = false
        fBinding.flTop.setData(jumpData)
        back = {
            jumpData?.apply {
                nextJumpType = JumpConfig.JUMP_WEB
                nextJumpUrl = mAgentWeb?.webCreator?.webView?.url
                JumpDataManager.updateCurrentJumpData(this,tag="webFragment goBack")
                if (rootActivity is WebDetailsActivity){
                    (rootActivity as WebDetailsActivity).apply {
                        updateBottom(true,true, jumpData = jumpData,"webView goBack")
                    }
                }
            }
        }
    }



    /**
     * 进入
     */

    override fun setShowView() {

    }

    override fun onResume() {
        super.onResume()
    }

    override fun getUrl(): String {
        return jumpData?.jumpUrl?:""
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