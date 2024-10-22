package com.boom.aiobrowser.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADShowManager
import com.boom.aiobrowser.base.BaseWebFragment
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.databinding.BrowserFragmentWebBinding
import com.boom.aiobrowser.point.AD_POINT
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.tools.web.WebScan
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.ParamsConfig
import com.boom.aiobrowser.ui.activity.WebDetailsActivity
import com.boom.aiobrowser.ui.pop.ClearPop
import com.boom.aiobrowser.ui.pop.DownLoadPop
import com.boom.aiobrowser.ui.pop.DownloadVideoGuidePop
import com.boom.aiobrowser.ui.pop.TabPop
import com.boom.aiobrowser.ui.pop.TipsPop
import com.boom.aiobrowser.ui.pop.VideoPop2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import pop.basepopup.BasePopupWindow.OnDismissListener
import java.lang.ref.WeakReference


class WebFragment:BaseWebFragment<BrowserFragmentWebBinding>() {

    var jumpData:JumpData?=null


    override fun getInsertParent(): ViewGroup {
        return fBinding.fl
    }

    override fun loadWebOnPageStared(url: String) {
        addLast(url)
        if (WebScan.isTikTok(url)){
            mAgentWeb!!.getWebCreator().getWebView().loadUrl("javascript:${CacheManager.pageList.get(0).cDetail}");
        }
    }

    private fun addLast(url: String) {
        jumpData?.apply {
            jumpUrl = url
            jumpType = JumpConfig.JUMP_WEB
            JumpDataManager.updateCurrentJumpData(this,"MainFragment onResume 更新 jumpData")
        }
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
        fBinding.tvTabCount.setOneClick {
            showTabPop()
        }
        fBinding.ivClear.setOneClick {
            clearData()
        }
        fBinding.ivHome.setOneClick {
            JumpDataManager.toMain()
            PointEvent.posePoint(PointEventKey.webpage_home, Bundle().apply {
                putString(PointValueKey.model_type,if (CacheManager.browserStatus == 1) "private" else "normal")
            })
        }

        fBinding.ivDownload.setOneClick {
            if (WebScan.isYoutube(jumpData?.jumpUrl?:"")){
                TipsPop(rootActivity).createPop {  }
                return@setOneClick
            }
            rootActivity.addLaunch(success = {
                delay(500)
                withContext(Dispatchers.Main){
                    VideoPop2(rootActivity).createPop {  }
                }
            }, failBack = {})
            PointEvent.posePoint(PointEventKey.webpage_download, Bundle().apply {
                putString(PointValueKey.type,"no_have")
                putString(PointValueKey.url,jumpData?.jumpUrl)
                putString(PointValueKey.model_type,if (CacheManager.browserStatus == 1) "private" else "normal")
            })
        }
        fBinding.ivDownload2.setOneClick {
            rootActivity.addLaunch(success = {
                delay(500)
                withContext(Dispatchers.Main){
                    showDownloadPop()
                }
            }, failBack = {})
            PointEvent.posePoint(PointEventKey.webpage_download, Bundle().apply {
                putString(PointValueKey.type,"have")
                putString(PointValueKey.url,jumpData?.jumpUrl)
                putString(PointValueKey.model_type,if (CacheManager.browserStatus == 1) "private" else "normal")
            })
        }

        APP.videoScanLiveData.observe(this){
            popDown?.updateDataByScan(it,true)
            updateDownloadButtonStatus(true)
        }
        APP.videoNFLiveData.observe(this){
            popDown?.updateDataByScan(it,false)
        }
        APP.videoLiveData.observe(this){
            var map = it
            it.keys.forEach {
                popDown?.updateStatus(rootActivity,it,map.get(it)){
//                    itemRemoveData(it)
                }
            }
        }
        APP.videoUpdateLiveData.observe(this){
            var list = CacheManager.videoDownloadTempList
            if (popDown?.isShowing == true && list.isNotEmpty()){
                for (i in 0 until list!!.size){
                    var data = list.get(i)
                    if (data.videoId == it){
                        data.downloadType = VideoDownloadData.DOWNLOAD_NOT
                        CacheManager.videoDownloadTempList = list
                        popDown?.updateItem()
                        break
                    }
                }
            }
        }
    }


    open fun updateDownloadButtonStatus(status: Boolean) {
        var size = CacheManager.videoDownloadTempList.size
        fBinding.apply {
            if (status && size>0){
                ivDownload.visibility = View.GONE
                ivDownload2.visibility = View.VISIBLE
                tvDownload.visibility = View.VISIBLE
                tvDownload.text = "$size"
                ivDownload2.apply {
                    setAnimation("download.json")
                    playAnimation()
                }
            }else{
                ivDownload.visibility = View.VISIBLE
                ivDownload2.visibility = View.GONE
                tvDownload.visibility = View.GONE
                ivDownload2.cancelAnimation()
            }
        }
    }


    private fun showDownloadPop() {
        popDown = DownLoadPop(rootActivity)
        popDown?.createPop(){}
    }

    var popDown: DownLoadPop?=null



    fun showTabPop() {
        var tabPop = TabPop(rootActivity)
        tabPop.createPop()
        tabPop.setOnDismissListener(object : OnDismissListener(){
            override fun onDismiss() {
                updateTabCount()
            }
        })
        PointEvent.posePoint(PointEventKey.webpage_tag)
    }

    fun updateTabCount() {
        fBinding.tvTabCount.text = "${JumpDataManager.getBrowserTabList(CacheManager.browserStatus,tag ="WebDetailsActivity 更新tab 数量").size}"
    }


    fun clearData(){
        ClearPop(rootActivity).createPop {
            CacheManager.clearAll()
            JumpDataManager.toMain()
        }
        PointEvent.posePoint(PointEventKey.webpage_delete)
    }

    private fun refresh() {
        if (mAgentWeb != null) {
            mAgentWeb!!.urlLoader.reload() // 刷新
        }
    }

    var isFirstYoutube = true

    override fun loadWebFinished() {
        super.loadWebFinished()
        if (WebScan.isYoutube(jumpData?.jumpUrl?:"")&&isFirstYoutube){
            isFirstYoutube = false
            TipsPop(rootActivity).createPop {  }
        }
        fBinding.flTop.binding.tvToolbarSearch.text = "${jumpData?.jumpTitle} ${getSearchTitle()}"
        fBinding.refreshLayout.isRefreshing = false
        var key = mAgentWeb?.webCreator?.webView?.url?:""
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
        fBinding.root.postDelayed({
            jumpData?.apply {
                jumpType = JumpConfig.JUMP_WEB
                JumpDataManager.updateCurrentJumpData(this,"MainFragment onResume 更新 jumpData")
                if(CacheManager.browserStatus == 0 ){
                    CacheManager.saveRecentSearchData(this)
                }
            }
        },0)
        back = {
//            jumpData?.apply {
//                nextJumpType = JumpConfig.JUMP_WEB
//                nextJumpUrl = mAgentWeb?.webCreator?.webView?.url
//                JumpDataManager.updateCurrentJumpData(this,tag="webFragment goBack")
//                if (rootActivity is WebDetailsActivity){
//                    (rootActivity as WebDetailsActivity).apply {
//                        updateBottom(true,true, jumpData = jumpData,"webView goBack")
//                    }
//                }
//            }
        }
    }



    /**
     * 进入
     */

    override fun setShowView() {
        updateData(getBeanByGson(arguments?.getString(ParamsConfig.JSON_PARAMS)?:"",JumpData::class.java))
        updateTabCount()
        CacheManager.videoDownloadTempList = mutableListOf()
        updateDownloadButtonStatus(false)
        fBinding.ivDownload.visibility = View.VISIBLE
        PointEvent.posePoint(PointEventKey.webpage_page,Bundle().apply {
            putString(PointValueKey.model_type,if (CacheManager.browserStatus == 1) "private" else "normal")
        })
    }

    override fun onResume() {
        super.onResume()
    }

    override fun getUrl(): String {
        return jumpData?.jumpUrl?:""
    }

    override fun getRealParseUrl(): String {
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