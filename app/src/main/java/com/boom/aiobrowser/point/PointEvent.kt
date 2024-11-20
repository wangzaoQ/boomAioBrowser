package com.boom.aiobrowser.point

import android.os.Build
import android.os.Bundle
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.AdjustEvent
import com.android.installreferrer.api.ReferrerDetails
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.data.AioRequestData
import com.boom.aiobrowser.point.Install.isLoading
import com.boom.aiobrowser.point.PointManager.NET_TAG
import com.boom.aiobrowser.point.PointManager.PointCallback
import com.boom.aiobrowser.point.PointManager.postEvent
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.facebook.appevents.AppEventsLogger
import okhttp3.Response
import org.json.JSONObject
import java.util.Currency

object PointEvent {

    private val fb by lazy { AppEventsLogger.newLogger(APP.instance) }

    fun posePoint(key:String, bundle: Bundle?=null,callback: PointCallback?=null) {
        AppLogs.dLog(NET_TAG,"posePoint tag:${key}")
        val jsonObject = GeneralParams.getGenericParams()
        jsonObject.put("tahoe",key)
        bundle?.keySet()?.forEach {
            jsonObject.put("${it}>bassi",bundle.get(it))
        }
        postEvent(jsonObject,tag = key)
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
    fun adPoint(adValue: Any, ad: Any, requestBean: AioRequestData, adEnum: ADEnum,){
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
            fb.logPurchase((adPointManager.getADValue()).toBigDecimal(), Currency.getInstance("USD"))
        }
    }



}