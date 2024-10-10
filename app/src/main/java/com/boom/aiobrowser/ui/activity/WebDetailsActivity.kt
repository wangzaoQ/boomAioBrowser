package com.boom.aiobrowser.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.databinding.BrowserActivityWebDetailsBinding
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.download.DownloadCacheManager
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.ParamsConfig
import com.boom.aiobrowser.ui.fragment.WebFragment
import com.boom.aiobrowser.ui.pop.ClearPop
import com.boom.aiobrowser.ui.pop.DownLoadPop
import com.boom.aiobrowser.ui.pop.DownloadVideoGuidePop
import com.boom.aiobrowser.ui.pop.TabPop
import com.jeffmony.downloader.VideoDownloadManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pop.basepopup.BasePopupWindow.OnDismissListener

class WebDetailsActivity : BaseActivity<BrowserActivityWebDetailsBinding>() {

    override fun getBinding(inflater: LayoutInflater): BrowserActivityWebDetailsBinding {
        return BrowserActivityWebDetailsBinding.inflate(layoutInflater)
    }

    override fun setListener() {
        acBinding.tvTabCount.setOneClick {
            showTabPop()
        }
        acBinding.ivClear.setOneClick {
            clearData()
        }
        acBinding.ivHome.setOneClick {
            JumpDataManager.toMain()
            PointEvent.posePoint(PointEventKey.webpage_home,Bundle().apply {
                putString(PointValueKey.model_type,if (CacheManager.browserStatus == 1) "private" else "normal")
            })
        }
        APP.jumpWebLiveData.observe(this){
            when (it.jumpType){
                JumpConfig.JUMP_WEB->{
                    getWebData(it)
                }
                JumpConfig.JUMP_HOME->{
                    JumpDataManager.toMain()
                }
            }
        }
        APP.videoScanLiveData.observe(this){
//            ToastUtils.showLong("视频获取成功")
            popDown?.updateData()
            updateDownloadButtonStatus(true)
        }
        APP.videoLiveData.observe(this){
            var map = it
            it.keys.forEach {
                popDown?.updateStatus(this@WebDetailsActivity,it,map.get(it)){
                    itemRemoveData(it)
                }
            }
        }
        APP.videoUpdateLiveData.observe(this){
            var list = CacheManager.videoDownloadTempList
            if (popDown?.isShowing == true && list.isNotEmpty()){
                addLaunch(success = {
                    var modelList = DownloadCacheManager.queryDownloadModelOther()
                    if (modelList.isNullOrEmpty().not()){
                        for (i in 0 until modelList!!.size){
                            var data = modelList.get(i)
                            var index = -1
                            for (k in 0 until list.size){
                                var bean = list.get(k)
                                if (data.url == bean.url){
                                    index = k
                                    break
                                }
                            }
                            if (index>=0){
                                list.removeAt(index)
                            }
                        }
                        CacheManager.videoDownloadTempList = list
                        withContext(Dispatchers.Main){
                            popDown?.updateData()
                        }
                    }
                }, failBack = {})
            }
        }
        acBinding.ivDownload.setOneClick {
            DownloadVideoGuidePop(this).createPop {  }
            PointEvent.posePoint(PointEventKey.webpage_download, Bundle().apply {
                putString(PointValueKey.type,"no_have")
                putString(PointValueKey.url,jumpData?.jumpUrl)
                putString(PointValueKey.model_type,if (CacheManager.browserStatus == 1) "private" else "normal")
            })
        }
        acBinding.ivDownload2.setOneClick {
            showDownloadPop()
            PointEvent.posePoint(PointEventKey.webpage_download, Bundle().apply {
                putString(PointValueKey.type,"have")
                putString(PointValueKey.url,jumpData?.jumpUrl)
                putString(PointValueKey.model_type,if (CacheManager.browserStatus == 1) "private" else "normal")
            })
        }
        acBinding.ivLeft.setOneClick{
            finish()
        }
    }

    /**
     * 移除item
     */
    private fun itemRemoveData(it: VideoDownloadData) {
        var videoId = it.videoId
        var list = CacheManager.videoDownloadTempList
        for (i in 0 until list.size) {
            if (list.get(i).videoId == videoId) {
                list.removeAt(i)
                break
            }
        }
        CacheManager.videoDownloadTempList = list
        runCatching {
            if (list.isNullOrEmpty()){
                popDown?.dismiss()
            }
        }
        updateDownloadButtonStatus(true)
    }

    open fun updateDownloadButtonStatus(status: Boolean) {
        var size = CacheManager.videoDownloadTempList.size
        if (status && size>0){
            acBinding.ivDownload.visibility = View.GONE
            acBinding.ivDownload2.visibility = View.VISIBLE
            acBinding.tvDownload.visibility = View.VISIBLE
            acBinding.tvDownload.text = "$size"
            acBinding.ivDownload2.apply {
                setAnimation("download.json")
                playAnimation()
            }
        }else{
            acBinding.ivDownload.visibility = View.VISIBLE
            acBinding.ivDownload2.visibility = View.GONE
            acBinding.tvDownload.visibility = View.GONE
            acBinding.ivDownload2.cancelAnimation()
        }
    }

    private fun showDownloadPop() {
        popDown = DownLoadPop(this@WebDetailsActivity)
        popDown?.createPop(){}
    }

    var popDown: DownLoadPop?=null

    fun updateBottom(showBack:Boolean,showNext:Boolean,jumpData:JumpData?=null,tag:String) {
        AppLogs.dLog(acTAG,"updateBottom:${tag}")
        acBinding.apply {
            if(showNext){
                if (jumpData?.nextJumpUrl.isNullOrEmpty()){
                    ivRight.isEnabled = false
                }else{
                    ivRight.isEnabled = true
                    ivRight.setOneClick {
                        jumpData?.jumpUrl = jumpData?.nextJumpUrl?:""
                        jumpData?.nextJumpUrl = ""
                        jumpData?.jumpType = JumpConfig.JUMP_WEB
                        var webFragment = supportFragmentManager.findFragmentById(R.id.webFragment) as WebFragment
                        webFragment.updateData(jumpData)
                        PointEvent.posePoint(PointEventKey.webpage_ahead,Bundle().apply {
                            putString(PointValueKey.model_type,if (CacheManager.browserStatus == 1) "private" else "normal")
                        })
                    }
                }
            }else{
                ivRight.isEnabled = false
            }
            if (showBack){
                ivLeft.setOneClick {
                    PointEvent.posePoint(PointEventKey.webpage_back,Bundle().apply {
                        putString(PointValueKey.model_type,if (CacheManager.browserStatus == 1) "private" else "normal")
                    })
                    var mMainNavFragment = supportFragmentManager.findFragmentById(R.id.webFragment)
                    if (mMainNavFragment != null && mMainNavFragment is WebFragment) {
                        mMainNavFragment.goBack()
                    }else{
                        finish()
                    }
                }
                ivLeft.isEnabled = true
            }else{
                ivLeft.isEnabled = false
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        acBinding.ivLeft.performClick()
//        super.onBackPressed()
    }

    fun clearData(){
        ClearPop(this).createPop {
            CacheManager.clearAll()
            JumpDataManager.toMain()
        }
    }

    fun showTabPop() {
        var tabPop = TabPop(this)
        tabPop.createPop()
        tabPop.setOnDismissListener(object : OnDismissListener(){
            override fun onDismiss() {
                updateTabCount()
            }
        })
    }

    fun updateTabCount() {
        acBinding.tvTabCount.text = "${JumpDataManager.getBrowserTabList(CacheManager.browserStatus,tag ="WebDetailsActivity 更新tab 数量").size}"
    }

    @SuppressLint("MissingSuperCall")
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        getWebData(getBeanByGson(
            intent.getStringExtra(ParamsConfig.JSON_PARAMS),
            JumpData::class.java
        ))
    }

    var jumpData:JumpData?= null

    private fun getWebData(data: JumpData?) {
        jumpData = data
        var webFragment = supportFragmentManager.findFragmentById(R.id.webFragment) as WebFragment
        webFragment.updateData(
            data
        )
        updateTabCount()
    }


    override fun setShowView() {
        getWebData(getBeanByGson(
            intent.getStringExtra(ParamsConfig.JSON_PARAMS),
            JumpData::class.java
        ))
        updateDownloadButtonStatus(false)
        acBinding.ivDownload.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        APP.jumpWebLiveData.removeObservers(this)
        APP.videoScanLiveData.removeObservers(this)
        APP.videoLiveData.removeObservers(this)
        APP.videoUpdateLiveData.removeObservers(this)
        super.onDestroy()
    }
}