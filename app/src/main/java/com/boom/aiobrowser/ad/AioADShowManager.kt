package com.boom.aiobrowser.ad

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.get
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.AioADDataManager.addADClick
import com.boom.aiobrowser.ad.AioADDataManager.setADDismissTime
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.ADResultData
import com.boom.aiobrowser.databinding.BrowserAdNativeBinding
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class AioADShowManager(
    private val activity: BaseActivity<*>,
    var adEnum: ADEnum,
    var tag: String,
    var result: (type: String) -> Unit
) {

    private fun showAD(tTag: String) {
        var data = AioADDataManager.getCacheAD(adEnum)
        if (data == null || activity == null || activity.getActivityStatus().not()) {
            loadComplete(type = AioADDataManager.AD_SHOW_TYPE_FAILED, tag = "activity 状态异常")
            return
        }
        initScreenAdmob(data, adEnum,tTag)
    }



    fun showScreenAD(tTag: String) {
        if (activity == null){
            result?.invoke(AioADDataManager.AD_SHOW_TYPE_FAILED)
            return
        }
        if (AioADDataManager.adAllowShowScreen()){
            //有展示机会
            PointEvent.posePoint(PointEventKey.aobws_ad_chance, Bundle().apply {
                putString(PointValueKey.ad_pos_id,tTag)
            })
        }

        if (AioADDataManager.getCacheAD(adEnum)!=null && AioADDataManager.adAllowShowScreen()) { // 插屏广告 冷却设置
            showAD(tTag)
        } else {
            result?.invoke(AioADDataManager.AD_SHOW_TYPE_FAILED)
        }
    }

    private fun initScreenAdmob(adResultData: ADResultData, adEnum: ADEnum,tTag: String) {
        when (adResultData.adRequestData?.tybxumpn) {
            AioADDataManager.AD_PLATFORM_ADMOB -> {
                val callback = object : FullScreenContentCallback() {
                    override fun onAdShowedFullScreenContent() {
                        AppLogs.dLog(
                            AioADDataManager.TAG,
                            "admob 广告展示:${adEnum.adName}-id:${adResultData.adRequestData?.ktygzdzn}"
                        )
                        adShowFullScreen(adResultData, adEnum, tag = "admob 广告展示")
                    }

                    override fun onAdClicked() {
                        addADClick(adEnum.adName)
                    }

                    override fun onAdDismissedFullScreenContent() {
                        AppLogs.dLog(
                            AioADDataManager.TAG,
                            "admob 广告关闭:${adEnum.adName}-id:${adResultData.adRequestData?.ktygzdzn}"
                        )
                        adDismissFullScreen(adEnum, tag = "admob 广告关闭")
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        AppLogs.dLog(
                            AioADDataManager.TAG,
                            "admob 广告展示失败:${adEnum.adName}-id:${adResultData.adRequestData?.ktygzdzn}"
                        )
                        adShowFailed(adEnum, "admob 广告展示失败")
                    }
                }
                when (adResultData.adRequestData?.pxdtzgho ?: "") {
                    AioADDataManager.AD_TYPE_OPEN -> {
                        (adResultData.adAny as AppOpenAd).run {
                            fullScreenContentCallback = callback
                            show(activity!!)
                        }
                    }
                    AioADDataManager.AD_TYPE_INT ->{
                        (adResultData.adAny as InterstitialAd).run {
                            fullScreenContentCallback = callback
                            if (adEnum != ADEnum.LAUNCH_AD){
//                                AioADDataManager.isShowAD = true
//                                activity.addLaunch(success = {
//                                    delay(1000)
//                                    withContext(Dispatchers.Main){
//                                        loadComplete(type = AioADDataManager.AD_SHOW_TYPE_SUCCESS, tag)
//                                    }
//                                }, failBack = {})
                                loadComplete(type = AioADDataManager.AD_SHOW_TYPE_SUCCESS, tag)
                            }
                            show(activity!!)
                        }
                    }
                }
            }
        }
        AioADDataManager.adCache.remove(adEnum)
//        activity.aLife.destoryList.add{
//            if(it!=0)return@add
//            when (data.nowAdValue) {
//                is AppOpenAd -> {
//                    (data.nowAdValue as AppOpenAd).fullScreenContentCallback = null
//                }
//                is InterstitialAd ->{
//                    (data.nowAdValue as InterstitialAd).fullScreenContentCallback = null
//                }
//                is RewardedAd ->{
//                    (data.nowAdValue as RewardedAd).fullScreenContentCallback = null
//                }
//                else -> {}
//            }
//            data.nowAdValue = null
//        }
    }

    fun showNativeAD(
        flRoot: FrameLayout,
        tTag:String
    ) {

        val data = AioADDataManager.getCacheAD(adEnum)
        var status = activity?.getActivityStatus()?.not()?:true
        if (adEnum == ADEnum.NATIVE_AD || adEnum == ADEnum.NATIVE_DOWNLOAD_AD){
            status = false
        }
        if (activity == null || status || data == null) {
            loadComplete(type = AioADDataManager.AD_SHOW_TYPE_FAILED, tag)
            return
        }
        if (data.adRequestData?.pxdtzgho?:"" == "ban"){
            showADBanner(flRoot,data,tTag)
        }else{
            initNativeAdmob(data, flRoot,tTag)
//            when (data.adRequestData?.tybxumpn) {
//                AioADDataManager.AD_TYPE_OPEN -> initNativeAdmob(data, flRoot, showType)
//            }
        }
    }

    fun showADBanner(parent:ViewGroup, data: ADResultData,tTag:String) {
        if (data?.adAny == null) {
            loadComplete(type = AioADDataManager.AD_SHOW_TYPE_FAILED, tag)
            return
        }
        var view:View? = data.adAny as AdView
        val currentParent: ViewGroup? = view?.parent as? ViewGroup
        currentParent?.removeView(view)
        parent.apply {
            visibility = View.VISIBLE
            addView(view)
        }
        PointEvent.posePoint(PointEventKey.aobws_ad_impression,Bundle().apply {
            putString(PointValueKey.ad_pos_id,tTag)
        })
        activity.life.destoryList.add {
            if(it!=0)return@add
            runCatching {
                (data?.adAny as? AdView)?.apply {
                    destroy()
                }
                parent?.removeView(view)
                data?.adAny = null
                view = null
            }.onFailure {
                it.stackTraceToString()
            }
        }
    }

    private fun initNativeAdmob(data: ADResultData, bFRoot: FrameLayout, tTag: String) {
        var nativeAd = (data.adAny as NativeAd)
        var nativeAdView: NativeAdView
        if (bFRoot.childCount == 0 || (bFRoot.get(0) is NativeAdView).not()) {
            val binding: BrowserAdNativeBinding = BrowserAdNativeBinding.inflate(activity.layoutInflater)
            nativeAdView = binding.root
            bFRoot.removeAllViews()
            bFRoot.addView(nativeAdView)
        } else {
            nativeAdView = bFRoot[0] as NativeAdView
        }
        nativeAd?.let {
            nativeAdView?.run {
                findViewById<TextView>(R.id.is_headline)?.run {
                    text = it.headline
                    headlineView = this
                }
                findViewById<TextView>(R.id.is_body)?.run {
                    text = it.body
                    bodyView = this
                }
                findViewById<TextView>(R.id.is_call)?.run {
                    if (it.callToAction == null) {
                        visibility = View.INVISIBLE
                    } else {
                        text = it.callToAction
                        callToActionView = this
                    }
                }
                findViewById<MediaView>(R.id.is_media)?.run {
                    it.mediaContent?.let {
                        mediaContent = it
                        setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                        mediaView = this
                    }
                }
                findViewById<ImageView>(R.id.is_icon)?.run {
                    it.icon?.let {
                        setImageDrawable(it.drawable)
                        iconView = this
                    }
                }
                setNativeAd(it)
            }
        }
        PointEvent.posePoint(PointEventKey.aobws_ad_impression,Bundle().apply {
            putString(PointValueKey.ad_pos_id,tTag)
        })
    }


    private fun adShowFailed(adEnum: ADEnum, tag: String) {
        if (adEnum == ADEnum.LAUNCH_AD) {
            loadComplete(AioADDataManager.AD_SHOW_TYPE_FAILED, tag)
        }
    }

    private fun adDismissFullScreen(adEnum: ADEnum, tag: String) {
        AppLogs.dLog(AioADDataManager.TAG, "tag:${tag} 位置:${adEnum.adName}")
        if (adEnum == ADEnum.LAUNCH_AD) {
            loadComplete(type = AioADDataManager.AD_SHOW_TYPE_SUCCESS, tag)
        }
        setADDismissTime()
    }

    private fun adShowFullScreen(adResultData: ADResultData, adEnum: ADEnum, tag: String) {
        AppLogs.dLog(AioADDataManager.TAG, "tag:${tag} 位置:${adEnum.adName}")
        AioADDataManager.addShowNumber(tag)
        AioADDataManager.preloadAD(adEnum,"广告展示时")
        APP.instance.lifecycleApp.adScreenType = 0
        PointEvent.posePoint(PointEventKey.aobws_ad_impression,Bundle().apply {
            putString(PointValueKey.ad_pos_id,adEnum.adName)
        })
    }

    private fun loadComplete(type: String, tag: String) {
        AppLogs.dLog(AioADDataManager.TAG, "tag:${tag} result 开始回调 type:${type}")
        result?.invoke(type)
    }


}