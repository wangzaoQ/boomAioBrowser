package com.boom.aiobrowser.ui.activity

import android.view.LayoutInflater
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.databinding.BrowserActivityMainBinding
import com.boom.aiobrowser.databinding.BrowserActivityWebDetailsBinding
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.FragmentManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.ParamsConfig
import com.boom.aiobrowser.ui.fragment.WebFragment
import com.boom.aiobrowser.ui.pop.ClearPop
import com.boom.aiobrowser.ui.pop.TabPop
import pop.basepopup.BasePopupWindow.OnDismissListener
import java.lang.ref.WeakReference

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
    }


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
                        onBackPressed()
                    }
                }
                ivLeft.isEnabled = true
            }else{
                ivLeft.isEnabled = false
            }
        }
    }

    fun clearData(){
        ClearPop(this).createPop {
            CacheManager.clearAll()
            APP.jumpLiveData.postValue(JumpDataManager.getCurrentJumpData(tag="清理数据后获取当前item"))
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

    override fun setShowView() {
        var webFragment = supportFragmentManager.findFragmentById(R.id.webFragment) as WebFragment
        webFragment.updateData(
            getBeanByGson(
                intent.getStringExtra(ParamsConfig.JSON_PARAMS),
                JumpData::class.java
            )
        )
        updateTabCount()
    }
}