package com.boom.aiobrowser.ad

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.get
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.AioADDataManager.platformMax
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.ADResultData
import com.boom.aiobrowser.databinding.BrowserAdNativeBinding
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.ui.NativeScreenActivity
import com.boom.aiobrowser.ui.pop.SubInfoPop
import com.boom.aiobrowser.ui.pop.SubTempPop
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class AioADShowManager(
    private val activity: BaseActivity<*>,
    var adEnum: ADEnum,
    var tag: String,
    var result: (type: String) -> Unit
) {

    var adShow:BaseShow?=null

    init {
        var adResultData = AioADDataManager.getCacheAD(adEnum)
        when (adResultData?.adRequestData?.tybxumpn) {
            AioADDataManager.platformMax -> {
                adShow = MaxShow(activity,adEnum,tag,result)
            }
            else ->{
                adShow = AdmobShow(activity,adEnum,tag,result)
            }
        }
    }

    fun showScreenAD(pointTag: String,allowShowDefaultAD:Boolean=true) {
        if (CacheManager.isSubscribeMember){
            AppLogs.dLog(AioADDataManager.TAG,"已是会员不展示广告")
            adShow?.loadComplete(type = AioADDataManager.AD_SHOW_TYPE_FAILED, tag = "是会员")
            return
        }
        var adResultData = AioADDataManager.getCacheAD(adEnum)
        if (activity == null || activity.getActivityStatus().not()) {
            adShow?.loadComplete(type = AioADDataManager.AD_SHOW_TYPE_FAILED, tag = "activity 状态异常")
            return
        }
        if (AioADDataManager.adAllowShowScreen() || AioADDataManager.getCacheAD(ADEnum.DEFAULT_AD)!=null){
            //有展示机会
            PointEvent.posePoint(PointEventKey.aobws_ad_chance, Bundle().apply {
                putString(PointValueKey.ad_pos_id,pointTag)
            })
        }
        if (AioADDataManager.adAllowShowScreen() && adResultData!=null){
            if (CacheManager.showEveryDay >= 2 && CacheManager.dayShowSubTemp && adEnum != ADEnum.LAUNCH_AD){
//                SubTempPop(activity,showADBack = {
//                    realShowScreenAD(adResultData,pointTag)
//                }).createPop()
                realShowScreenAD(adResultData,pointTag)
            }else{
                realShowScreenAD(adResultData,pointTag)
            }
        }else{
            if (allowShowDefaultAD){
                var defaultAD = AioADDataManager.getCacheAD(ADEnum.DEFAULT_AD)
                if (defaultAD!=null) {
                    if (CacheManager.showEveryDay >= 2 && CacheManager.dayShowSubTemp && adEnum != ADEnum.LAUNCH_AD){
//                        SubTempPop(activity,showADBack = {
//                            realShowScreenAD2(defaultAD,pointTag)
//                        }).createPop()
                        realShowScreenAD2(defaultAD,pointTag)
                    }else{
                        realShowScreenAD2(defaultAD,pointTag)
                    }
                } else {
                    adShow?.loadComplete(type = AioADDataManager.AD_SHOW_TYPE_FAILED, tag = "无缓存 或不在冷却范围内 ")
                }
            }else{
                adShow?.loadComplete(type = AioADDataManager.AD_SHOW_TYPE_FAILED, tag = "无缓存 或不在冷却范围内 ")
            }
        }

    }

    private fun realShowScreenAD(adResultData:ADResultData,pointTag: String) {
        adShow?.showScreenAd(adResultData!!,pointTag)
        AioADDataManager.adCache.remove(adEnum)
    }

    private fun realShowScreenAD2(adResultData:ADResultData,pointTag: String) {
        if (adResultData.adShowType == 2){
            //native
            if (APP.instance.lifecycleApp.stack.size>0 && APP.instance.lifecycleApp.stack.get(APP.instance.lifecycleApp.stack.size-1) is BaseActivity<*>){
                var currentTopActivity = (APP.instance.lifecycleApp.stack.get(APP.instance.lifecycleApp.stack.size-1) as BaseActivity<*>)
                adShow?.loadComplete(type = AioADDataManager.AD_SHOW_TYPE_SUCCESS, tag = "图片池广告加载完毕")
                currentTopActivity.startActivity(Intent(currentTopActivity,NativeScreenActivity::class.java).apply {
                    putExtra("pointTag",pointTag)
                })
            }else{
                adShow?.loadComplete(type = AioADDataManager.AD_SHOW_TYPE_FAILED, tag = "没有有效activity")
            }
        }else{
            //走通用的逻辑
            adShow?.showScreenAd(adResultData,pointTag)
            AioADDataManager.adCache.remove(ADEnum.DEFAULT_AD)
        }
    }

    fun showNativeAD(
        flRoot: FrameLayout,
        pointTag:String
    ) {
        if (CacheManager.isSubscribeMember){
            AppLogs.dLog(AioADDataManager.TAG,"已是会员不展示广告")
            return
        }
        val data = AioADDataManager.getCacheAD(adEnum)
        var status = activity?.getActivityStatus()?.not()?:true
        if (adEnum == ADEnum.NATIVE_AD || adEnum == ADEnum.BANNER_AD_NEWS_DETAILS_TOP){
            status = false
        }
        if (activity == null || data == null) {
            adShow?.loadComplete(type = AioADDataManager.AD_SHOW_TYPE_FAILED, tag)
            return
        }
        if (data.adRequestData?.pxdtzgho?:"" == "ban"){
            showADBanner(flRoot,data,pointTag)
        }else{
            adShow?.showNativeAD(flRoot,pointTag)
            var platform = data.adRequestData?.tybxumpn?:""
            if (platform == platformMax){
                AioADDataManager.preloadAD(adEnum,"showNativeAD 广告展示时")
            }
            PointEvent.posePoint(PointEventKey.aobws_ad_impression,Bundle().apply {
                putString(PointValueKey.ad_pos_id,pointTag)
            })
        }
    }

    fun showADBanner(parent:ViewGroup, data: ADResultData,pointTag:String) {
        if (CacheManager.isSubscribeMember){
            AppLogs.dLog(AioADDataManager.TAG,"已是会员不展示广告")
            return
        }
        adShow?.showADBanner(parent,data,pointTag)
        AioADDataManager.adCache.remove(adEnum)
        AioADDataManager.preloadAD(adEnum,"showADBanner 广告展示时")
    }
}