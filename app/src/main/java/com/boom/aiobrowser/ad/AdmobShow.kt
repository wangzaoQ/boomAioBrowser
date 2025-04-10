package com.boom.aiobrowser.ad

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.get
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.AioADDataManager.addADClick
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.ADResultData
import com.boom.aiobrowser.databinding.BrowserAdNative2Binding
import com.boom.aiobrowser.databinding.BrowserAdNative3Binding
import com.boom.aiobrowser.databinding.BrowserAdNative4Binding
import com.boom.aiobrowser.databinding.BrowserAdNativeBinding
import com.boom.aiobrowser.point.AD_POINT
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
import com.google.android.gms.ads.rewarded.RewardedAd

class AdmobShow(activity: BaseActivity<*>, adEnum: ADEnum, tag: String,result: (type: String) -> Unit) :BaseShow(activity, adEnum, tag,result) {
    override fun showScreenAd(adResultData: ADResultData, pointTag: String) {
        val callback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                AppLogs.dLog(
                    AioADDataManager.TAG,
                    "admob 广告展示:${adEnum.adName}-id:${adResultData.adRequestData?.ktygzdzn} type:${getTypeContent(adResultData.adShowType)}"
                )
                adShowFullScreen(adEnum, tag = "admob 广告展示",adResultData,pointTag)
            }

            override fun onAdClicked() {
                addADClick(adEnum.adName)
            }

            override fun onAdDismissedFullScreenContent() {
                AppLogs.dLog(
                    AioADDataManager.TAG,
                    "admob 广告关闭:${adEnum.adName}-id:${adResultData.adRequestData?.ktygzdzn} type:${getTypeContent(adResultData.adShowType)}"
                )
                adDismissFullScreen(adEnum, tag = "admob 广告关闭")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                AppLogs.dLog(
                    AioADDataManager.TAG,
                    "admob 广告展示失败:${adEnum.adName}-id:${adResultData.adRequestData?.ktygzdzn} type:${getTypeContent(adResultData.adShowType)}"
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
        activity.life.destoryList.add{
            if(it!=0)return@add
            when (adResultData.adAny) {
                is AppOpenAd -> {
                    (adResultData.adAny as AppOpenAd).fullScreenContentCallback = null
                }
                is InterstitialAd->{
                    (adResultData.adAny as InterstitialAd).fullScreenContentCallback = null
                }
                is RewardedAd ->{
                    (adResultData.adAny as RewardedAd).fullScreenContentCallback = null
                }
                else -> {}
            }
            adResultData.adAny = null
        }
    }

    override fun showNativeAD(flRoot: FrameLayout, pointTag: String) {
        val data = AioADDataManager.getCacheAD(adEnum)
        nativeAd = (data!!.adAny as NativeAd)
        if (flRoot.childCount == 0 || (flRoot.get(0) is NativeAdView).not()) {
            if (pointTag == AD_POINT.aobws_task_add){
                val binding: BrowserAdNative2Binding = BrowserAdNative2Binding.inflate(activity.layoutInflater)
                nativeAdView = binding.root
            }
//            else if (pointTag == ADEnum.BANNER_AD_NEWS_DETAILS.adName){
//                //新闻正文
//                val binding: BrowserAdNativeBinding = BrowserAdNativeBinding.inflate(activity.layoutInflater)
//                nativeAdView = binding.root
//            }
            else if (pointTag == AD_POINT.aobws_play_bnat){
                // 播放视频
                val binding: BrowserAdNative3Binding = BrowserAdNative3Binding.inflate(activity.layoutInflater)
                nativeAdView = binding.root
            }else{
                // 新闻列表/download 底部下载
                val binding: BrowserAdNative4Binding = BrowserAdNative4Binding.inflate(activity.layoutInflater)
                nativeAdView = binding.root
            }
            flRoot.removeAllViews()
            flRoot.addView(nativeAdView)
        } else {
            nativeAdView = flRoot[0] as NativeAdView
        }
        nativeAd?.let {
            flRoot.visibility = View.VISIBLE
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
        activity!!.life.destoryList.add {
            if(it!=0)return@add
            destoryNative()
        }
    }


    var nativeAd:NativeAd?=null
    var nativeAdView:NativeAdView?=null

    private fun destoryNative() {
        AppLogs.dLog(AioADDataManager.TAG,"destoryNative")
        runCatching {
            nativeAd?.destroy()
            if (nativeAdView?.parent != null) {
                (nativeAdView?.parent as? ViewGroup)?.removeView(nativeAdView)
            }
            nativeAdView?.destroy()
        }.onFailure {
            it.stackTraceToString()
        }

    }


    override fun showADBanner(parent: ViewGroup, data: ADResultData, tTag: String) {
        if (data?.adAny == null) {
            loadComplete(type = AioADDataManager.AD_SHOW_TYPE_FAILED, tag)
            return
        }
        var view: View? = data.adAny as AdView
        val currentParent: ViewGroup? = view?.parent as? ViewGroup
        currentParent?.removeView(view)
        parent.apply {
            visibility = View.VISIBLE
            addView(view)
        }
        PointEvent.posePoint(PointEventKey.aobws_ad_impression, Bundle().apply {
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

}