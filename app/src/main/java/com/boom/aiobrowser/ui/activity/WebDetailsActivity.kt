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
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.tools.web.WebScan
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.ParamsConfig
import com.boom.aiobrowser.ui.fragment.WebFragment
import com.boom.aiobrowser.ui.pop.ClearPop
import com.boom.aiobrowser.ui.pop.DownLoadPop
import com.boom.aiobrowser.ui.pop.DownloadVideoGuidePop
import com.boom.aiobrowser.ui.pop.TabPop
import com.boom.aiobrowser.ui.pop.TipsPop
import pop.basepopup.BasePopupWindow.OnDismissListener

class WebDetailsActivity : BaseActivity<BrowserActivityWebDetailsBinding>() {

    override fun getBinding(inflater: LayoutInflater): BrowserActivityWebDetailsBinding {
        return BrowserActivityWebDetailsBinding.inflate(layoutInflater)
    }

    override fun setListener() {

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


    }

//    /**
//     * 移除item
//     */
//    private fun itemRemoveData(it: VideoDownloadData) {
//        var videoId = it.videoId
//        var list = CacheManager.videoDownloadTempList
//        for (i in 0 until list.size) {
//            if (list.get(i).videoId == videoId) {
//                list.removeAt(i)
//                break
//            }
//        }
//        CacheManager.videoDownloadTempList = list
//        runCatching {
//            if (list.isNullOrEmpty()){
//                popDown?.dismiss()
//            }
//        }
//        updateDownloadButtonStatus(true)
//    }


//
//    fun updateBottom(showBack:Boolean,showNext:Boolean,jumpData:JumpData?=null,tag:String) {
//        AppLogs.dLog(acTAG,"updateBottom:${tag}")
//        acBinding.apply {
//            if(showNext){
//                if (jumpData?.nextJumpUrl.isNullOrEmpty()){
//                    ivRight.isEnabled = false
//                }else{
//                    ivRight.isEnabled = true
//                    ivRight.setOneClick {
//                        jumpData?.jumpUrl = jumpData?.nextJumpUrl?:""
//                        jumpData?.nextJumpUrl = ""
//                        jumpData?.jumpType = JumpConfig.JUMP_WEB
//                        var webFragment = supportFragmentManager.findFragmentById(R.id.webFragment) as WebFragment
//                        webFragment.updateData(jumpData)
//                        PointEvent.posePoint(PointEventKey.webpage_ahead,Bundle().apply {
//                            putString(PointValueKey.model_type,if (CacheManager.browserStatus == 1) "private" else "normal")
//                        })
//                    }
//                }
//            }else{
//                ivRight.isEnabled = false
//            }
//            if (showBack){
//                ivLeft.setOneClick {
//                    PointEvent.posePoint(PointEventKey.webpage_back,Bundle().apply {
//                        putString(PointValueKey.model_type,if (CacheManager.browserStatus == 1) "private" else "normal")
//                    })
//                    var mMainNavFragment = supportFragmentManager.findFragmentById(R.id.webFragment)
//                    if (mMainNavFragment != null && mMainNavFragment is WebFragment) {
//                        mMainNavFragment.goBack()
//                    }else{
//                        finish()
//                    }
//                }
//                ivLeft.isEnabled = true
//            }else{
//                ivLeft.isEnabled = false
//            }
//        }
//    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
//        super.onBackPressed()
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
    }


    override fun setShowView() {
//        getWebData(getBeanByGson(
//            intent.getStringExtra(ParamsConfig.JSON_PARAMS),
//            JumpData::class.java
//        ))
//        updateDownloadButtonStatus(false)
//        acBinding.ivDownload.visibility = View.VISIBLE
//        acBinding.root.postDelayed({

//        },0)
    }

    override fun onDestroy() {
        APP.jumpWebLiveData.removeObservers(this)
        APP.videoScanLiveData.removeObservers(this)
        APP.videoLiveData.removeObservers(this)
        APP.videoUpdateLiveData.removeObservers(this)
        APP.videoNFLiveData.removeObservers(this)
        super.onDestroy()
    }
}