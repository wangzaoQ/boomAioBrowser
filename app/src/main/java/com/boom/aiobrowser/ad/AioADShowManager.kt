package com.boom.aiobrowser.ad

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.get
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.AioADDataManager.platformMax
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.ADResultData
import com.boom.aiobrowser.databinding.BrowserAdNativeBinding
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
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

    var adResultData:ADResultData?=null
    var adShow:BaseShow?=null

    init {
        adResultData = AioADDataManager.getCacheAD(adEnum)
        when (adResultData?.adRequestData?.tybxumpn) {
            AioADDataManager.platformMax -> {
                adShow = MaxShow(activity,adEnum,tag,result)
            }
            else ->{
                adShow = AdmobShow(activity,adEnum,tag,result)
            }
        }
    }

    fun showScreenAD(pointTag: String) {
        if (adResultData == null || activity == null || activity.getActivityStatus().not()) {
            adShow?.loadComplete(type = AioADDataManager.AD_SHOW_TYPE_FAILED, tag = "activity 状态异常")
            return
        }
        if (AioADDataManager.adAllowShowScreen()){
            //有展示机会
            PointEvent.posePoint(PointEventKey.aobws_ad_chance, Bundle().apply {
                putString(PointValueKey.ad_pos_id,pointTag)
            })
        }

        if (AioADDataManager.getCacheAD(adEnum)!=null && AioADDataManager.adAllowShowScreen()) { // 插屏广告 冷却设置
            adShow?.showScreenAd(adResultData!!,pointTag)
            AioADDataManager.adCache.remove(adEnum)
        } else {
            adShow?.loadComplete(type = AioADDataManager.AD_SHOW_TYPE_FAILED, tag = "无缓存 或不在冷却范围内 ")
        }
    }

    fun showNativeAD(
        flRoot: FrameLayout,
        pointTag:String
    ) {
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
        adShow?.showADBanner(parent,data,pointTag)
        AioADDataManager.adCache.remove(adEnum)
        AioADDataManager.preloadAD(adEnum,"showADBanner 广告展示时")
    }
}