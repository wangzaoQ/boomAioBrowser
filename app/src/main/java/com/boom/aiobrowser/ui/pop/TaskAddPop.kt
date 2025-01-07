package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import com.boom.aiobrowser.APP
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
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.BatteryUtil
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.download.DownloadCacheManager
import com.boom.aiobrowser.tools.jobCancel
import com.boom.aiobrowser.ui.activity.DownloadActivity
import com.boom.aiobrowser.ui.adapter.DownloadAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
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

    var job: Job?=null

    var isComplete = false

    fun createPop(downloadVideoIdList:MutableList<String>){
        defaultBinding?.apply {
            if (AioADDataManager.adFilter1().not()) {
                PointEvent.posePoint(PointEventKey.aobws_ad_chance, Bundle().apply {
                    putString(PointValueKey.ad_pos_id, AD_POINT.aobws_task_add)
                })
                val data = AioADDataManager.getCacheAD(ADEnum.BANNER_AD_NEWS_DETAILS)
                data?.apply {
                    AioADShowManager(context as BaseActivity<*>, ADEnum.BANNER_AD_NEWS_DETAILS,"添加任务过渡弹窗 原生/banner"){
                    }.showNativeAD(defaultBinding!!.flRoot,AD_POINT.aobws_task_add)
                }
            }
            llRoot.setOnClickListener {
                if (isComplete){
                    PointEvent.posePoint(PointEventKey.download_complete_view)
                }else{
                    PointEvent.posePoint(PointEventKey.download_task_view)
                }
                clickOther = false
                APP.downloadPageLiveData.postValue(if (isComplete.not()) "webpage_download_task_pop" else "webpage_download_task_pop_complete")
//                context.startActivity(Intent(context, DownloadActivity::class.java).apply {
//                    putExtra("fromPage", "webpage_download_task_pop")
//                    putExtra("jumpType", if (isComplete) 1 else 0)
//                })
                tips3?.dismiss()
                dismiss()
            }
        }
        setBackground(R.color.color_70_black)
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
        var first = true
        job = (context as BaseActivity<*>).addLaunch(success = {
            while (true){
                delay(1000)
                var list = DownloadCacheManager.queryDownloadModelDone()
                var name = ""
                var completeCount = 0
                list?.forEach {
                    for (i in 0 until downloadVideoIdList.size ){
                        if (it.videoId == downloadVideoIdList.get(i)){
                            if (name.isNullOrEmpty()){
                                name = it.fileName?:""
                            }
                            completeCount++
                        }
                    }
                }
                if (completeCount>0){
                    withContext(Dispatchers.Main){
                        defaultBinding?.apply {
                            ivDownload.setImageResource(R.mipmap.nf_video_download_success)
                            taskTitle.text = context.getString(R.string.app_download_complete)
                            taskContent.visibility = View.VISIBLE
                            taskContent.text = name
                        }
                        isComplete = true
                        if (first){
                            first = false
                            PointEvent.posePoint(PointEventKey.download_complete)
                        }
                    }
                }
            }
        }, failBack = {
            AppLogs.eLog("TaskAddPop",it)
        })
    }


    override fun dismiss() {
        tips3?.dismiss()
        job?.jobCancel()
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