package com.boom.aiobrowser.ad

import android.os.Bundle
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.data.ADResultData
import com.boom.aiobrowser.data.AioRequestData
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
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

class AdmobDLoader(
     requestBean: AioRequestData,
     adEnum: ADEnum
) :BaseLoader(requestBean,adEnum){

    override fun openAD() {
        var startTime = System.currentTimeMillis()
        CoroutineScope(Dispatchers.IO).launch{
            val build = AdRequest.Builder().build()
            withContext(Dispatchers.Main){
                AppOpenAd.load(APP.instance,
                    requestBean.ktygzdzn,
                    build,
                    object : AppOpenAd.AppOpenAdLoadCallback() {
                        override fun onAdLoaded(appOpenAd: AppOpenAd) {
                            successCallBack(ADResultData().apply {
                                adRequestData = requestBean
                                adAny = appOpenAd
                                adType = requestBean.pxdtzgho
                                adRequestTime = (System.currentTimeMillis() - startTime) / 1000
                            })
                            if (APP.isDebug){
//                                PointEvent.adPoint(AdValue.zza(1*100000,"",1*100000),appOpenAd,requestBean,adEnum)
                            }
                            appOpenAd.setOnPaidEventListener {
                                PointEvent.adPoint(it,appOpenAd,requestBean,adEnum)
                            }
                        }

                        override fun onAdFailedToLoad(error: LoadAdError) {
                            loadFailed(error.message, failedCallBack)
                        }
                    })
            }
        }
    }

    override fun intAD(){
        var startTime = System.currentTimeMillis()
        CoroutineScope(Dispatchers.IO).launch{
            val build = AdRequest.Builder().build()
            withContext(Dispatchers.Main){
                InterstitialAd.load(
                    APP.instance,
                    requestBean.ktygzdzn,
                    build,
                    object : InterstitialAdLoadCallback() {

                        override fun onAdLoaded(inAd: InterstitialAd) {
                            successCallBack(ADResultData().apply {
                                adRequestData = requestBean
                                adAny = inAd
                                adType = requestBean.pxdtzgho
                                adRequestTime = (System.currentTimeMillis() - startTime) / 1000
                                if (adEnum == ADEnum.DEFAULT_AD){
                                    adShowType = 1
                                }
                            })
                            if (APP.isDebug){
//                                PointEvent.adPoint(AdValue.zza(1*100000,"",1*100000),inAd,requestBean,adEnum)
                            }
                            inAd.setOnPaidEventListener {
                                PointEvent.adPoint(it,inAd,requestBean,adEnum)
                            }
                            initADPoint(startTime)
                        }

                        override fun onAdFailedToLoad(error: LoadAdError) {
                            loadFailed(error.message, failedCallBack)
                        }
                    })
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
        var startTime = System.currentTimeMillis()
        CoroutineScope(Dispatchers.IO).launch{
            val request = AdRequest.Builder().build()
            val build =
                AdLoader.Builder(APP.instance,
                    requestBean.ktygzdzn)
                    .forNativeAd { nativeAd ->
                        nativeAd.setOnPaidEventListener {
                            PointEvent.adPoint(it,nativeAd,requestBean,adEnum)
                        }
                        successCallBack(ADResultData().apply {
                            adRequestData = requestBean
                            adAny = nativeAd
                            adType = requestBean.pxdtzgho
                            adRequestTime = (System.currentTimeMillis() - startTime) / 1000
                            if (adEnum == ADEnum.DEFAULT_AD){
                                adShowType = 2
                            }
                        })
                        nativePoint(startTime)
                    }.withAdListener(object : AdListener() {
                        override fun onAdImpression() {
//                            AioADDataManager.preloadAD(adEnum)
                            AioADDataManager.preloadAD(adEnum,"admob showNativeAD onAdImpression")
                        }

                        override fun onAdClicked() {
                        }

                        override fun onAdFailedToLoad(error: LoadAdError) =
                            loadFailed(error.message, failedCallBack)
                    }).withNativeAdOptions(NativeAdOptions.Builder().build()).build()
            withContext(Dispatchers.Main){
                build.loadAd(request)
            }
        }
    }



    override fun banner(){
        var startTime = System.currentTimeMillis()
        CoroutineScope(Dispatchers.IO).launch{
            val adView = AdView(APP.instance)

            if (adEnum == ADEnum.NATIVE_AD || adEnum == ADEnum.BANNER_AD_NEWS_DETAILS){
                adView.setAdSize(AdSize.MEDIUM_RECTANGLE)
            }else{
                adView.setAdSize(AdSize.BANNER)
            }
//                val adSize = AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(NewsAPP.singleApp.appLifecycle.stack.get(NewsAPP.singleApp.appLifecycle.stack.size-1), 320)
//                adView.setAdSize(adSize)
            adView.adUnitId = requestBean.ktygzdzn
            adView.adListener = object : AdListener() {
                override fun onAdClicked() {
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    loadFailed(error.message, failedCallBack)
                }


                override fun onAdLoaded() {
                    successCallBack(ADResultData().apply {
                        adRequestData = requestBean
                        adAny = adView
                        adType = requestBean.pxdtzgho
                        adRequestTime = (System.currentTimeMillis() - startTime) / 1000
                    })
                    adView.setOnPaidEventListener {
                        PointEvent.adPoint(it,adView,requestBean,adEnum)
                    }
                    bannerPoint(startTime)
                }
            }
            withContext(Dispatchers.Main){
                adView.loadAd(AdRequest.Builder().build())
            }
        }
    }


}