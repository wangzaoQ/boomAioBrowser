package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import androidx.appcompat.widget.AppCompatImageView
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADDataManager
import com.boom.aiobrowser.ad.AioADShowManager
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.databinding.PopHomeBackBinding
import com.boom.aiobrowser.point.AD_POINT
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager
import pop.basepopup.BasePopupWindow
import pop.util.animation.AnimationHelper
import pop.util.animation.TranslationConfig

class BackPop (context: Context) : BasePopupWindow(context) {

    init {
        setContentView(R.layout.pop_home_back)
    }

    var defaultBinding: PopHomeBackBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = PopHomeBackBinding.bind(contentView)
    }

    var clearHistory = false
    var clearTab = false

    fun createPop(clearCallBack: () -> Unit){
        PointEvent.posePoint(PointEventKey.exit_pop)
        defaultBinding?.apply {
            llClearHistory.setOnClickListener {
                clearHistory = clearHistory.not()
                (llClearHistory.getChildAt(0) as AppCompatImageView).isEnabled = clearHistory.not()
            }
            llClearTabs.setOnClickListener {
                clearTab = clearTab.not()
                (llClearTabs.getChildAt(0) as AppCompatImageView).isEnabled = clearTab.not()
            }
            tvConfirm.setOnClickListener {
                if (clearHistory){
                    CacheManager.saveRecentSearchDataList("", mutableListOf())
                }
                if (clearTab) {
                    JumpDataManager.saveBrowserTabList(0, mutableListOf(),tag = "全局删除")
                    JumpDataManager.saveBrowserTabList(1, mutableListOf(),tag = "全局删除")
                    APP.firstToDownloadLiveData.postValue(2)
                }
                clearCallBack.invoke()
                var type = ""
                if (clearHistory && clearTab){
                    type = "all"
                }else if (clearHistory){
                    type = "history"
                }else if (clearTab){
                    type = "tabs"
                }
                PointEvent.posePoint(PointEventKey.exit_pop_exit,Bundle().apply {
                    if (type.isNotEmpty()){
                        putString("type",type)
                    }
                })
                dismiss()
            }
            if (CacheManager.isVIP().not()){
                PointEvent.posePoint(PointEventKey.aobws_ad_chance, Bundle().apply {
                    putString(PointValueKey.ad_pos_id, AD_POINT.aobws_back)
                })
            }
            val data = AioADDataManager.getCacheAD(ADEnum.BANNER_AD_NEWS_DETAILS)
            data?.apply {
                AioADShowManager(context as BaseActivity<*>, ADEnum.BANNER_AD_NEWS_DETAILS,"back pop 原生/banner"){
                }.showNativeAD(defaultBinding!!.flRoot, AD_POINT.aobws_back)
            }
        }
        showPopupWindow()
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
}