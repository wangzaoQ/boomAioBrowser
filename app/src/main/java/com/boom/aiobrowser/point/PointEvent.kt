package com.boom.aiobrowser.point

import android.net.Uri
import android.os.Build
import android.os.Bundle
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.android.installreferrer.api.ReferrerDetails
import com.applovin.mediation.MaxAd
import com.appsflyer.adrevenue.AppsFlyerAdRevenue
import com.appsflyer.adrevenue.adnetworks.AppsFlyerAdNetworkEventType
import com.appsflyer.adrevenue.adnetworks.generic.MediationNetwork
import com.appsflyer.adrevenue.adnetworks.generic.Scheme
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADDataManager
import com.boom.aiobrowser.data.AioRequestData
import com.boom.aiobrowser.firebase.FirebaseConfig
import com.boom.aiobrowser.firebase.FirebaseManager.firebaseAnalytics
import com.boom.aiobrowser.point.Install.isLoading
import com.boom.aiobrowser.point.PointManager.PointCallback
import com.boom.aiobrowser.point.PointManager.postEvent
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.firebase.analytics.FirebaseAnalytics
import okhttp3.Response
import org.json.JSONObject
import java.util.Currency
import java.util.Locale


object PointEvent {

    private val fb by lazy { AppEventsLogger.newLogger(APP.instance) }

    fun posePoint(key:String, bundle: Bundle?=null,needFireBase:Boolean=true,callback: PointCallback?=null) {
//        AppLogs.dLog(NET_TAG,"posePoint tag:${key}")
        val jsonObject = GeneralParams.getGenericParams()
        jsonObject.put("tahoe",key)
        bundle?.keySet()?.forEach {
            jsonObject.put("${it}>bassi",bundle.get(it))
        }
        postEvent(jsonObject,tag = key,callback)
        if (needFireBase){
            runCatching {
                firebaseAnalytics.logEvent(key, bundle)
            }.onFailure {
                it.stackTraceToString()
            }
        }
    }


    /**
     * install
     */
    fun install(referrerDetails: ReferrerDetails? = null) {
        if (CacheManager.installRefer.isNullOrEmpty() && isLoading.not()){
            isLoading = true
            val jsonObject = GeneralParams.getGenericParams().apply {
                put("mazda", JSONObject().apply {
                    put("uphold","build/${Build.ID}")
                    put("sargent", referrerDetails?.installReferrer?:"")
                    put("cornwall", APP.instance.webUA)
                    put("sunspot","reject")
                    put("logan", referrerDetails?.referrerClickTimestampSeconds?:0)
                    put("estrange", referrerDetails?.installBeginTimestampSeconds?:0)
                    put("quasi", referrerDetails?.referrerClickTimestampServerSeconds?:0)
                    put("darkle", referrerDetails?.installBeginTimestampServerSeconds?:0)
                    put("nucleic", GeneralParams.getPackageInfo().firstInstallTime)
                    put("taipei", GeneralParams.getPackageInfo().lastUpdateTime)
                })
            }
            PointManager.postEvent(jsonObject,"install",object : PointCallback {
                override fun onSuccess(response: Response) {
                    AppLogs.dLog(Install.TAG,"install打点成功 refer:${referrerDetails?.installReferrer?:""}")
                    CacheManager.installRefer = referrerDetails?.installReferrer?:""
//                    getADConfig(firebaseRemoteConfig,"requestRefer")
                }
            })
        }
    }

    /**
     * session
     */
    fun session(){
        PointManager.postEvent(GeneralParams.getGenericParams().apply {
            put("quackery",JSONObject())
        },"session")
    }

    /**
     * adValue 广告具体的价值
     * ad 广告类型
     */
    fun adPoint(adValue: Any, ad: Any, requestBean: AioRequestData, adEnum: ADEnum){
        var adPointManager = ADPointManager(adValue,ad,requestBean,adEnum)

        PointManager.postEvent(GeneralParams.getGenericParams().apply {
            put("singsong",JSONObject().apply {
                put("praline",adPointManager.getADEcpm())
                put("gird",adPointManager.getADFill())
                put("permit",adPointManager.getADSDK())
                put("careworn",adPointManager.getADID())
                put("gavel",adPointManager.getADPosition())
                put("basilica",adPointManager.getAdFormat())
                put("syringa",adPointManager.getSDKVersion())
            })
        },"adPoint")
        runCatching {
            val adjustAdRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB)
            adjustAdRevenue.setRevenue(adPointManager.getADValue(), "USD")
            adjustAdRevenue.setAdRevenueNetwork(adPointManager.getADFill())
            Adjust.trackAdRevenue(adjustAdRevenue)
        }
        runCatching {
            val value = adPointManager.getADValue()
            val customParams: MutableMap<String, String> = HashMap()
            customParams[Scheme.COUNTRY] = "US"
            customParams[Scheme.AD_UNIT] = requestBean.ktygzdzn
            customParams[Scheme.AD_TYPE] = getAdType(requestBean,ad)
            var from = "admob"
            var netWork = MediationNetwork.googleadmob
            if (requestBean.tybxumpn == AioADDataManager.AD_PLATFORM_MAX){
                from = "max"
                netWork =MediationNetwork.applovinmax
            }
            AppsFlyerAdRevenue.logAdRevenue(
                from,
                netWork,
                Currency.getInstance(Locale.US),
                value,
                customParams
            )
        }.onFailure {
            AppLogs.eLog("AppsFlyerLib", it.stackTraceToString())
        }


        runCatching {
            fb.logPurchase((adPointManager.getADValue()).toBigDecimal(), Currency.getInstance("USD"))
        }
//        CacheManager.adValue+=adPointManager.getADValue()

        posePoint("ad_impression_revenue",Bundle().apply {
            putLong("${FirebaseAnalytics.Param.VALUE}",adPointManager.getADEcpm())
            putString("${FirebaseAnalytics.Param.CURRENCY}",adPointManager.getCurrentCode())
        }, needFireBase = false)
        runCatching {
            firebaseAnalytics.logEvent("ad_impression_revenue", Bundle().apply {
                putDouble(FirebaseAnalytics.Param.VALUE, adPointManager.getADValue())
                putString(FirebaseAnalytics.Param.CURRENCY,adPointManager.getCurrentCode())
            })
        }.onFailure {
            it.stackTraceToString()
        }
        //001
        runCatching {
            val newCurrInt = CacheManager.ad001Value + adPointManager.getADValue()
            if (newCurrInt >= 0.01) {
                posePoint("Total_Ads_Revenue_001",Bundle().apply {
                    putDouble("${FirebaseAnalytics.Param.VALUE}",newCurrInt)
                    putString(FirebaseAnalytics.Param.CURRENCY,adPointManager.getCurrentCode())
                })
                CacheManager.ad001Value = 0.0
            } else {
                CacheManager.ad001Value=newCurrInt
            }
        }

        //LTV
        runCatching {
            var dayValue = CacheManager.adDayValue
            var newValue = dayValue+adPointManager.getADValue()
            CacheManager.adDayValue = newValue
            val result = getLTVList()
            result.forEachIndexed { index, value ->
                if ( dayValue< value && newValue >= value) {
                    val name =  getPostName().get(index)
                    name?.let {
                        posePoint(it,Bundle().apply {
                            putDouble(FirebaseAnalytics.Param.VALUE, value)
                            putString(FirebaseAnalytics.Param.CURRENCY,adPointManager.getCurrentCode())
                        })
                    }
                }
            }
        }

    }

    private fun getAdType(requestBean: AioRequestData,ad: Any): String {
        if (requestBean.tybxumpn == AioADDataManager.AD_PLATFORM_ADMOB){
            return when (ad) {
                is AppOpenAd ->{
                    AppsFlyerAdNetworkEventType.APP_OPEN.toString()
                }
                is InterstitialAd ->{
                    AppsFlyerAdNetworkEventType.INTERSTITIAL.toString()
                }
                is NativeAd ->{
                    AppsFlyerAdNetworkEventType.NATIVE.toString()
                }
                is AdView ->{
                    AppsFlyerAdNetworkEventType.BANNER.toString()
                }
                else -> "Unknown"
            }
        }else{
          return  "Unknown"
        }
    }

    fun getLTVList(): MutableList<Double> {
        val ad = mutableListOf<Double>()
        val jsonObject = JSONObject(FirebaseConfig.ltvConfig)
        ad.add(0, jsonObject.getDouble("aio_top50percent"))
        ad.add(1, jsonObject.getDouble("aio_top40percent"))
        ad.add(2, jsonObject.getDouble("aio_top30percent"))
        ad.add(3, jsonObject.getDouble("aio_top20percent"))
        ad.add(4, jsonObject.getDouble("aio_top10percent"))
        return ad
    }

    fun getPostName(): MutableMap<Int, String> {
        val map = mutableMapOf<Int, String>().apply {
            put(0, "Aio_Top50Percent")
            put(1, "Aio_Top40Percent")
            put(2, "Aio_Top30Percent")
            put(3, "Aio_Top20Percent")
            put(4, "Aio_Top20Percent")
        }
        return map
    }

}