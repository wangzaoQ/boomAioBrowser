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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import pop.basepopup.BasePopupWindow.OnDismissListener


class WebFragment:BaseWebFragment<BrowserFragmentWebBinding>() {

    var jumpData:JumpData?=null


    override fun getInsertParent(): ViewGroup {
        return fBinding.fl
    }

    override fun loadWebOnPageStared(url: String) {
        addLast(url)
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
            showDownloadAD{
                if (WebScan.isYoutube(jumpData?.jumpUrl?:"")){
                    TipsPop(rootActivity).createPop {  }
                    return@showDownloadAD
                }
                DownloadVideoGuidePop(rootActivity).createPop {  }
            }
            PointEvent.posePoint(PointEventKey.webpage_download, Bundle().apply {
                putString(PointValueKey.type,"no_have")
                putString(PointValueKey.url,jumpData?.jumpUrl)
                putString(PointValueKey.model_type,if (CacheManager.browserStatus == 1) "private" else "normal")
            })
        }
        fBinding.ivDownload2.setOneClick {
            showDownloadAD{
                showDownloadPop()
            }
            PointEvent.posePoint(PointEventKey.webpage_download, Bundle().apply {
                putString(PointValueKey.type,"have")
                putString(PointValueKey.url,jumpData?.jumpUrl)
                putString(PointValueKey.model_type,if (CacheManager.browserStatus == 1) "private" else "normal")
            })

        }

        APP.videoScanLiveData.observe(this){
            popDown?.updateDataByScan(it)
            updateDownloadButtonStatus(true)
        }
        APP.videoNFLiveData.observe(this){
            popDown?.updateDataByScan(it)
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

    private fun showDownloadAD(result: () -> Unit) {
        if (CacheManager.isFirstClickDownloadButton){
            result.invoke()
            CacheManager.isFirstClickDownloadButton = false
        }else{
            var manager = AioADShowManager(rootActivity, ADEnum.INT_AD, tag = "插屏") {
                result.invoke()
            }
            manager.showScreenAD(AD_POINT.aobws_download_int)
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
    }

    fun updateTabCount() {
        fBinding.tvTabCount.text = "${JumpDataManager.getBrowserTabList(CacheManager.browserStatus,tag ="WebDetailsActivity 更新tab 数量").size}"
    }


    fun clearData(){
        ClearPop(rootActivity).createPop {
            CacheManager.clearAll()
            JumpDataManager.toMain()
        }
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
        if(CacheManager.browserStatus == 0){
            rootActivity.addLaunch(success = {
                var doc = Jsoup.connect(key).get()
                var list = CacheManager.historyDataList
                var title = doc.title()
                runCatching {
                    var splits = doc.title().split(" ")
                    if (splits.isNotEmpty()){
                        title = splits.get(0)
                    }
                }
                JumpData().apply {
                    jumpTitle = title
                    jumpUrl = key
                    jumpType = JumpConfig.JUMP_WEB
                    updateTime = System.currentTimeMillis()
                    list.add(0,this)
                    CacheManager.historyDataList = list
                    CacheManager.addHistoryJump(this)
                }
            }, failBack = {})
        }
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