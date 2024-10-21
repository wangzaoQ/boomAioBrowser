package com.boom.aiobrowser.point

import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADDataManager
import com.boom.aiobrowser.data.AioRequestData
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.ResponseInfo
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import java.math.BigDecimal

class ADPointManager(var adValue: Any, var ad: Any, var requestBean: AioRequestData, var adEnum: ADEnum) {
    var valueMicros =0L
    var detailValueMicros = 0.0
    var ad_sdk_ver = ""
    init {
        // 未处理的
        if (requestBean.tybxumpn == AioADDataManager.AD_PLATFORM_ADMOB){
            valueMicros = (adValue as AdValue).valueMicros
            detailValueMicros  = BigDecimal(valueMicros).divide(
                BigDecimal(1_000_000),10,
                BigDecimal.ROUND_HALF_UP).toDouble()
            ad_sdk_ver = MobileAds.getVersion().toString()
        }
    }

    fun getSDKVersion():String{
        return ad_sdk_ver
    }

    fun getADEcpm():Long{
        return valueMicros
    }

    fun getADValue():Double{
        return detailValueMicros
    }

    fun getADSDK():String{
        if (requestBean.tybxumpn == AioADDataManager.AD_PLATFORM_ADMOB){
            return "admob"
        }
        return "Unknown"
    }

    fun getADID(): String {
        return requestBean.ktygzdzn
    }

    fun getADPosition(): String {
        return adEnum.adName
    }

    fun getADFill():String{
        if (requestBean.tybxumpn == AioADDataManager.AD_PLATFORM_ADMOB){
            return when (ad) {
                is AppOpenAd ->{
                    getADFillByAdmob((ad as AppOpenAd).responseInfo)
                }
                is InterstitialAd ->{
                    getADFillByAdmob((ad as InterstitialAd).responseInfo)
                }
                is NativeAd ->{
                    getADFillByAdmob((ad as NativeAd).responseInfo)
                }
                is AdView ->{
                    getADFillByAdmob((ad as AdView).responseInfo)
                }
                else -> "Unknown"
            }
        }
        return "Unknown"
    }

    fun getAdFormat(): String {
        runCatching {
            when (requestBean.tybxumpn) {
                AioADDataManager.AD_PLATFORM_ADMOB -> {
                    return when (ad) {
                        is AppOpenAd -> "AppOpenAd"
                        is InterstitialAd -> "InterstitialAd"
                        is NativeAd -> "NativeAd"
                        else -> "Unknown"
                    }
                }
//                AdPaltm.NSN_PLATFORM_MAX ->{
//                    return when (ad) {
//                        is MaxAppOpenAd -> "AppOpenAd"
//                        is MaxInterstitialAd -> "InterstitialAd"
//                        is MaxNativeAdLoader -> "NativeAd"
//                        else -> "Unknown"
//                    }
//                }
//                AdPaltm.NSN_PLATFORM_TOP_ON ->{
//                    return when (ad) {
//                        is ATSplashAd -> "AppOpenAd"
//                        is ATInterstitial -> "InterstitialAd"
//                        is ATNative -> "NativeAd"
//                        else -> "Unknown"
//                    }
//                }
//                AdPaltm.NSN_PLATFORM_TRADPLUS->{
//                    return when (ad) {
//                        is TPSplash -> "AppOpenAd"
//                        is TPInterstitial -> "InterstitialAd"
//                        is TPNative -> "NativeAd"
//                        else -> "Unknown"
//                    }
//                }
                else -> { return "Unknown"}
            }
        }
        return "Unknown"
    }

    private fun getADFillByAdmob(info: ResponseInfo?): String {
        val adapterClassName = info?.mediationAdapterClassName ?: "admob"
        return if ("com.google.ads.mediation.admob.AdMobAdapter" == adapterClassName) "admob"
        else if (adapterClassName.contains("facebook", true)) "facebook"
        else if (adapterClassName.contains("pangle", true)) "pangle"
        else if (adapterClassName.contains("applovin", true)) "applovin"
        else adapterClassName
    }


}