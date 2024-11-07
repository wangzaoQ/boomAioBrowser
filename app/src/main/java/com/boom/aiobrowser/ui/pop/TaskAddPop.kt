package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADDataManager
import com.boom.aiobrowser.ad.AioADShowManager
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.VideoPopDownloadBinding
import com.boom.aiobrowser.databinding.VideoPopTaskAddBinding
import com.boom.aiobrowser.point.AD_POINT
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.BatteryUtil
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.ui.activity.DownloadActivity
import com.boom.aiobrowser.ui.adapter.DownloadAdapter
import pop.basepopup.BasePopupWindow
import pop.util.animation.AnimationHelper
import pop.util.animation.TranslationConfig
import java.lang.ref.WeakReference

class TaskAddPop (context: Context) : BasePopupWindow(context){

    init {
        setContentView(R.layout.video_pop_task_add)
    }

    var defaultBinding: VideoPopTaskAddBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = VideoPopTaskAddBinding.bind(contentView)
    }

    var clickOther = true

    fun createPop(){
        defaultBinding?.apply {
            if (AioADDataManager.adFilter1().not()) {
                PointEvent.posePoint(PointEventKey.aobws_ad_chance, Bundle().apply {
                    putString(PointValueKey.ad_pos_id, AD_POINT.aobws_task_add)
                })
                val data = AioADDataManager.getCacheAD(ADEnum.BANNER_AD)
                data?.apply {
                    AioADShowManager(context as BaseActivity<*>, ADEnum.BANNER_AD,"添加任务过渡弹窗 banner"){
                    }.showADBanner(defaultBinding!!.flRoot,this,AD_POINT.aobws_task_add)
                }
            }
            llRoot.setOnClickListener {
                clickOther = false
                PointEvent.posePoint(PointEventKey.download_task_view)
                context.startActivity(Intent(context, DownloadActivity::class.java).apply {
                    putExtra("fromPage", "webpage_download_task_pop")
                })
                tips3?.dismiss()
                dismiss()
            }
        }
        setBackground(R.color.tran)
        showPopupWindow()
        PointEvent.posePoint(PointEventKey.download_task)
        defaultBinding?.apply {
            root.postDelayed({
                if (CacheManager.isFirstDownloadTips3){
                    CacheManager.isFirstDownloadTips3 = false
                    tips3 = FirstDownloadTips(context)
                    tips3?.createPop(tvView,3)
                }
            },500)
        }
    }


    override fun dismiss() {
        tips3?.dismiss()
        super.dismiss()
    }

    var tips3 :FirstDownloadTips?=null


    override fun onDismiss() {
        if (clickOther) BatteryUtil(WeakReference(context as BaseActivity<*>)).requestIgnoreBatteryOptimizations()
        super.onDismiss()
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