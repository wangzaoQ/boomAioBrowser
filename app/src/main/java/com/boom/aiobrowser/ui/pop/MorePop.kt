package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.widget.CompoundButton
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.BrowserPopMoreBinding
import com.boom.aiobrowser.other.ShortManager
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.tools.BrowserManager
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.ui.activity.AboutActivity
import com.boom.aiobrowser.ui.activity.DownloadActivity
import com.boom.aiobrowser.ui.activity.HistoryActivity
import com.boom.aiobrowser.ui.activity.MainActivity
import pop.basepopup.BasePopupWindow
import pop.util.animation.AnimationHelper
import pop.util.animation.TranslationConfig


class MorePop(context: Context) : BasePopupWindow(context) {

    init {
        setContentView(R.layout.browser_pop_more)
    }

    var popBinding : BrowserPopMoreBinding?=null
    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        popBinding = BrowserPopMoreBinding.bind(contentView)
    }

    fun showTabPop() {
        var tabPop = TabPop(context)
        tabPop.createPop()
        tabPop.setOnDismissListener(object : OnDismissListener(){
            override fun onDismiss() {
            }
        })
    }

    fun createPop(){
        popBinding?.apply {
            ivDelete.setOnClickListener {
                dismiss()
            }
            llNewTab.setOnClickListener {
                showTabPop()
                PointEvent.posePoint(PointEventKey.profile_newtab)
                dismiss()
            }
            llClearData.setOnClickListener {
                clearData()
                PointEvent.posePoint(PointEventKey.profile_cleardate)
                dismiss()
            }
            llHistory.setOnClickListener {
                if (context is BaseActivity<*>){
                    (context as BaseActivity<*>).startActivity(Intent(context,HistoryActivity::class.java))
                }
                PointEvent.posePoint(PointEventKey.profile_history)
                dismiss()
            }
            llWidget.setOnClickListener {
                ShortManager.addWidgetToLaunch(context,true)
            }
            llAbout.setOnClickListener {
                if (context is BaseActivity<*>){
                    (context as BaseActivity<*>).startActivity(Intent(context,AboutActivity::class.java))
                }
                PointEvent.posePoint(PointEventKey.profile_about)
                dismiss()
            }
            llDownload.setOnClickListener {
                if (context is BaseActivity<*>){
                    (context as BaseActivity<*>).startActivity(Intent(context, DownloadActivity::class.java).apply {
                        putExtra("fromPage","home_more_pop")
                    })
                }
                PointEvent.posePoint(PointEventKey.profile_download)
                dismiss()
            }
            updateUI()
        }
        showPopupWindow()
        PointEvent.posePoint(PointEventKey.profile_pop)
    }

    override fun dismiss() {
        super.dismiss()
        PointEvent.posePoint(PointEventKey.profile_close)
    }


    fun clearData(){
        ClearPop(context as BaseActivity<*>).createPop {
            CacheManager.clearAll()
            JumpDataManager.toMain()
        }
    }

    override fun onCreateShowAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withTranslation(TranslationConfig.FROM_BOTTOM)
            .toShow()
    }

    override fun onCreateDismissAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withTranslation(TranslationConfig.TO_BOTTOM)
            .toDismiss()
    }

    fun updateUI() {
        popBinding?.apply {
            var isDefault = BrowserManager.isDefaultBrowser()
            if (APP.instance.clickSetBrowser){
                APP.instance.clickSetBrowser = false
                PointEvent.posePoint(if (isDefault) PointEventKey.default_pop_set_s else PointEventKey.default_pop_set_f)
            }
            if (isDefault){
                llBrowser.visibility = View.GONE
                viewLine.visibility = View.GONE
            }else{
                llBrowser.visibility = View.VISIBLE
                viewLine.visibility = View.VISIBLE
                switchBrowser.setChecked(isDefault)
                switchBrowser.isClickable = false
                llBrowser.setOnClickListener {
                    PointEvent.posePoint(PointEventKey.profile_setdefault)
                    if (isDefault.not()){
                        var pop = DefaultPop(context)
                        pop.createPop()
                    }
                }
            }
        }
    }

}