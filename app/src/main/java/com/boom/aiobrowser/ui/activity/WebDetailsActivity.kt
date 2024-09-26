package com.boom.aiobrowser.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.databinding.BrowserActivityWebDetailsBinding
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.ParamsConfig
import com.boom.aiobrowser.ui.fragment.WebFragment
import com.boom.aiobrowser.ui.pop.ClearPop
import com.boom.aiobrowser.ui.pop.DownLoadPop
import com.boom.aiobrowser.ui.pop.TabPop
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
            ToastUtils.showLong("视频获取成功")
            popDown?.updateData()
        }
        acBinding.ivDownload.setOneClick {
            popDown = DownLoadPop(this@WebDetailsActivity)
            popDown?.createPop()
        }
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
                    }
                }
            }else{
                ivRight.isEnabled = false
            }
            if (showBack){
                ivLeft.setOneClick {
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

    private fun getWebData(data: JumpData?) {
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
    }

    override fun onDestroy() {
        APP.jumpWebLiveData.removeObservers(this)
        APP.videoScanLiveData.removeObservers(this)
        super.onDestroy()
    }
}