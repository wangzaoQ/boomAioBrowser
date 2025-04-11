package com.boom.aiobrowser.ad

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.AioADDataManager.addADClick
import com.boom.aiobrowser.ad.AioADDataManager.applovinSdk
import com.boom.aiobrowser.data.ADResultData
import com.boom.aiobrowser.data.AioRequestData
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.drag.utils.DisplayUtils.dp2px
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MaxDLoader(
     requestBean: AioRequestData,
     adEnum: ADEnum
) :BaseLoader(requestBean,adEnum){

    override fun openAD() {

    }

    override fun intAD(){
        if (APP.instance.lifecycleApp.stack.size == 0) {
            loadFailed("noActivity", failedCallBack)
            return
        }
        if (applovinSdk == null){
            loadFailed("applovinSdk == null", failedCallBack)
            return
        }
        var startTime = System.currentTimeMillis()
        CoroutineScope(Dispatchers.IO).launch{
            val maxInterstitialAd = MaxInterstitialAd(requestBean.ktygzdzn ?: "",applovinSdk, APP.instance.lifecycleApp.stack[APP.instance.lifecycleApp.stack.size-1])
            withContext(Dispatchers.Main){
                maxInterstitialAd.setListener(object : MaxAdListener {
                    override fun onAdLoaded(p0: MaxAd) {
                        successCallBack(ADResultData().apply {
                            adRequestData = requestBean
                            adAny = maxInterstitialAd
                            adType = requestBean.pxdtzgho
                            adRequestTime = (System.currentTimeMillis() - startTime) / 1000
                        })
                        initADPoint(startTime)
                        maxInterstitialAd.setRevenueListener {
                            PointEvent.adPoint(it,p0,requestBean,adEnum)
                        }
                    }

                    override fun onAdDisplayed(p0: MaxAd) {
                    }

                    override fun onAdHidden(p0: MaxAd) {
                    }

                    override fun onAdClicked(p0: MaxAd) {
                    }

                    override fun onAdLoadFailed(p0: String, p1: MaxError) {
                        loadFailed(p1.message, failedCallBack)
//                        NewsAPP.singleApp.showToast("loadFailed_max:${adEnum}-id:${requestBean.nsaner}-time:${(System.currentTimeMillis() - startTime) / 1000}message:${p1.message}")
                    }

                    override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                    }

                })
                maxInterstitialAd.loadAd()
            }
        }
    }

    private fun initADPoint(startTime: Long) {
        PointEvent.posePoint(PointEventKey.aobws_ad_load, Bundle().apply {
            putString(PointValueKey.ad_pos_id,adEnum.adName)
            putString(PointValueKey.ad_key,requestBean.ktygzdzn)
            putLong(PointValueKey.ad_time, (System.currentTimeMillis() - startTime) / 1000)
        })
    }

    override fun nativeAD() {
        if (applovinSdk == null){
            loadFailed("applovinSdk == null", failedCallBack)
            return
        }
        var startTime = System.currentTimeMillis()
        val binder : MaxNativeAdViewBinder
        val layoutId = R.layout.layout_ad_max_native
        binder = MaxNativeAdViewBinder.Builder(layoutId)
            .setTitleTextViewId(R.id.is_headline)
            .setBodyTextViewId(R.id.is_body)
            .setIconImageViewId(R.id.is_icon)
            .setMediaContentViewGroupId(R.id.is_media)
            .setCallToActionButtonId(R.id.is_call).build()
        var loader = MaxNativeAdLoader(requestBean.ktygzdzn,applovinSdk,APP.instance)
        loader?.run {
            setNativeAdListener(object : MaxNativeAdListener() {
                override fun onNativeAdLoaded(maxAdView: MaxNativeAdView?, p1: MaxAd) {
                    successCallBack(ADResultData().apply {
                        adRequestData = requestBean
                        adAny = maxAdView
                        adType = requestBean.pxdtzgho
                        adRequestTime = (System.currentTimeMillis() - startTime) / 1000
                    })
                    loader?.setRevenueListener {
                        if (maxAdView!=null){
                            PointEvent.adPoint(it,p1,requestBean,adEnum)
                        }
                    }
                    nativePoint(startTime,adEnum)
                }

                override fun onNativeAdLoadFailed(p0: String, p1: MaxError){
                    loadFailed("max:"+p1.message, failedCallBack)
                }

                override fun onNativeAdExpired(p0: MaxAd) {

                }

                override fun onNativeAdClicked(p0: MaxAd) {
                    addADClick(adEnum.adName)
                }
            })
        }
        loader?.loadAd(MaxNativeAdView(binder, APP.instance))
    }



    override fun banner(){
        var startTime = System.currentTimeMillis()
        var adView = MaxAdView(requestBean.ktygzdzn,applovinSdk, APP.instance)
        adView?.setListener(object : MaxAdViewAdListener {

            override fun onAdLoaded(p0: MaxAd) {
                successCallBack(ADResultData().apply {
                    adRequestData = requestBean
                    adAny = adView
                    adType = requestBean.pxdtzgho
                    adRequestTime = (System.currentTimeMillis() - startTime) / 1000
                })
                adView.setRevenueListener {
                    PointEvent.adPoint(it,adView,requestBean,adEnum)
                }
                bannerPoint(startTime)
            }

            override fun onAdDisplayed(p0: MaxAd) {
            }

            override fun onAdHidden(p0: MaxAd) {
            }

            override fun onAdClicked(p0: MaxAd) {
                addADClick(adEnum.adName)
            }

            override fun onAdLoadFailed(p0: String, p1: MaxError) {
                loadFailed(p1.message, failedCallBack)

            }

            override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
            }

            override fun onAdExpanded(p0: MaxAd) {
            }

            override fun onAdCollapsed(p0: MaxAd) {
            }
        })

        val width = ViewGroup.LayoutParams.MATCH_PARENT
        // Banner height on phones and tablets is 50 and 90, respectively
        val heightPx = dp2px(APP.instance,50f)

        adView?.layoutParams = FrameLayout.LayoutParams(width, heightPx)
        // Set background or background color for banners to be fully functional
//                adView?.setBackgroundColor(...)
        // Load the ad
        adView?.loadAd()
    }

    override fun rewarded() {

    }


}