package com.boom.aiobrowser.firebase

import android.app.Application
import android.os.Bundle
import android.util.Base64
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.APP.Companion.instance
import com.boom.aiobrowser.ad.AioADDataManager
import com.boom.aiobrowser.ad.AioADDataManager.adRootBean
import com.boom.aiobrowser.data.AioADData
import com.boom.aiobrowser.data.AioNFData
import com.boom.aiobrowser.data.DefaultUserData
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.PushData
import com.boom.aiobrowser.data.PushRuleData
import com.boom.aiobrowser.firebase.FirebaseConfig.AD_DEFAULT_JSON
import com.boom.aiobrowser.nf.NFManager
import com.boom.aiobrowser.nf.NFManager.defaultNewsList
import com.boom.aiobrowser.nf.NFManager.nfRootBean
import com.boom.aiobrowser.point.Install
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.tools.getListByGson
import com.boom.aiobrowser.tools.toJson
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

object FirebaseManager {

    val firebaseAnalytics: FirebaseAnalytics by lazy {
        FirebaseAnalytics.getInstance(APP.instance)
    }

    var firebaseRemoteConfig: FirebaseRemoteConfig?=null

    fun Application.initFirebase() {
        runCatching {
            AppLogs.dLog(APP.instance.TAG,"firebase 初始化")
            FirebaseApp.initializeApp(this)
            Install.requestRefer(instance,0,{})
            firebaseRemoteConfig = Firebase.remoteConfig.apply {
                setConfigSettingsAsync(remoteConfigSettings {
                    minimumFetchIntervalInSeconds = 3600L
                })
            }
            AppLogs.dLog(APP.instance.TAG,"firebase 初始化结束")
        }.onFailure {
            it.printStackTrace()
        }
        runCatching {
            initFirebaseConfig("firebase 获取默认配置")
            CoroutineScope(Dispatchers.Main).launch{
                while (true){
                    firebaseRemoteConfig?.fetchAndActivate()?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            initFirebaseConfig("firebase 获取最新配置")
                            initFirebaseConfig2()
                        }
                    }
                    delay(60*60*1000)
                }
            }
        }.onFailure {
            it.printStackTrace()
        }
        runCatching {
            var topic = if (APP.isDebug){
                "test~ALL2"
            }else{
                "${matchCountry()}~ALL"
            }
            if (APP.isDebug){
                AppLogs.dLog(NFManager.TAG,"message topic:${topic} country:${Locale.getDefault().country}")
            }
            Firebase.messaging.subscribeToTopic(topic)
                .addOnCompleteListener { task ->
                    var country = "dCountry"
                    var language = "dLanguage"
                    runCatching {
                        country = Locale.getDefault().country
                    }
                    runCatching {
                        language = Locale.getDefault().language
                    }
                    PointEvent.posePoint(PointEventKey.fcm_subscription,Bundle().apply {
                        putString("fcm_conuntry",country)
                        putString("fcm_language",language)
                    })
                    AppLogs.dLog(NFManager.TAG,"message 初始化成功")
                }
        }
    }

    /**
     * 只在成功后初始化
     */
    private fun initFirebaseConfig2() {
        initJumpConfig()
        runCatching {
            FirebaseConfig.jsConfig = firebaseRemoteConfig?.getString("page_js")?:""
        }
        AppLogs.dLog("APP.instance.TAG", "page_js:${FirebaseConfig.jsConfig}")
    }

    private fun initJumpConfig() {
        runCatching {
            FirebaseConfig.defaultUserData = getBeanByGson(firebaseRemoteConfig?.getString("user_flow")?:"",DefaultUserData::class.java)
        }
        AppLogs.dLog(APP.instance.TAG,"user_flow:${ toJson(FirebaseConfig.defaultUserData)}")
        FirebaseConfig.defaultUserData?.apply {
            var splits = download?.split(",")
//            if (splits.isNullOrEmpty() || splits.get(0).isNullOrEmpty()){
//                var configBrowserFrom = FirebaseConfig.jumpBrowserConfig
//                splits = configBrowserFrom.split(",")
//                AppLogs.dLog(APP.instance.TAG,"localConfig start_home_download:${configBrowserFrom}")
//            }
            if (splits.isNullOrEmpty().not()){
                FirebaseConfig.browserJumpList = splits?: mutableListOf()
            }

            var splitsNews = news?.split(",")
//            if (splitsNews.isNullOrEmpty()||splitsNews.get(0).isNullOrEmpty()){
//                var configNewsFrom = FirebaseConfig.jumpNewsConfig
//                splitsNews = configNewsFrom.split(",")
//            }
            if (splitsNews.isNullOrEmpty().not()){
                FirebaseConfig.newsJumpList = splitsNews?: mutableListOf()
            }
        }
    }

    private fun initFirebaseConfig(tag: String) {
        AppLogs.dLog(APP.instance.TAG,"tag:${tag}")
        runCatching {
            FirebaseConfig.referConfig = firebaseRemoteConfig?.getString("refer_config")?:""
        }
        if (FirebaseConfig.referConfig.isNullOrEmpty()){
            FirebaseConfig.referConfig = FirebaseConfig.DEFAULT_REFER_CONFIG
        }
        runCatching {
            var push_rule = firebaseRemoteConfig?.getString("push_rule")?:""
            var data = getBeanByGson(push_rule,PushRuleData::class.java)
            var downloadSplit = (data?.download?:"").split(",")
            var country = matchCountry()
            if (downloadSplit.contains(country)){
                FirebaseConfig.isDownloadConfig = true
            }
        }
//        FirebaseConfig.isDownloadConfig = true
        getADConfig()
        getNFConfig()
        getDefaultConfig()
        runCatching {
            FirebaseConfig.AD_CD_ALL = firebaseRemoteConfig?.getString("aobws_cd")?.toInt()?:if (APP.isDebug)10 else 60
        }
        var show_tutorial = 1
        runCatching {
            show_tutorial = firebaseRemoteConfig?.getString("show_tutorial")?.toInt()?:1
        }
        AppLogs.dLog(APP.instance.TAG,"remoteConfig show_tutorial 1则展示弹窗:${show_tutorial}")
        FirebaseConfig.switchDownloadGuidePop = show_tutorial != 0
        var show_default = 0
        runCatching {
            show_default = firebaseRemoteConfig?.getString("show_default")?.toInt()?:0
        }
        AppLogs.dLog(APP.instance.TAG,"remoteConfig show_default 1则展示弹窗:${show_default}")
        FirebaseConfig.switchDefaultPop = show_default != 0
        runCatching {
            var aioPush = firebaseRemoteConfig?.getString("aio_push")
            FirebaseConfig.pushData = getBeanByGson(aioPush,PushData::class.java)
            PointEvent.posePoint(PointEventKey.aio_push, Bundle().apply {
                putInt(PointValueKey.type,FirebaseConfig.pushData?.time_interval?:0)
            })
        }
//        var config_filter1 = 1
//        runCatching {
//            config_filter1 = firebaseRemoteConfig?.getString("alldownload_switch")?.toInt()?:1
//        }
//        AppLogs.dLog(APP.instance.TAG,"remoteConfig alldownload_switch 1则限制下载:${config_filter1}")
//        FirebaseConfig.switchOpenFilter1=config_filter1!=0
        var config_filter2 = ""
        runCatching {
            config_filter2 = firebaseRemoteConfig?.getString("config_filter")?:""
        }
        AppLogs.dLog(APP.instance.TAG,"remoteConfig config_filter 命中就限制下载:${config_filter2}")
        var splits = config_filter2.split(",")
        if (config_filter2.isNullOrEmpty()||splits.isNullOrEmpty()||splits.get(0).isNullOrEmpty()){
            config_filter2 = FirebaseConfig.FILTER_DEFAULT_WEB
            splits = config_filter2.split(",")
            AppLogs.dLog(APP.instance.TAG,"localConfig config_filter 命中就限制下载:${config_filter2}")
        }
        if (splits.isNullOrEmpty().not() && splits.get(0).isNullOrEmpty().not()){
            FirebaseConfig.switchOpenFilterList.clear()
            FirebaseConfig.switchOpenFilterList.addAll(splits)
        }
        runCatching {
            FirebaseConfig.ltvConfig = firebaseRemoteConfig?.getString("Aio_AdLTV_Percent")?:""
        }
        if (FirebaseConfig.ltvConfig.isNullOrEmpty()){
            FirebaseConfig.ltvConfig = FirebaseConfig.LTV_DEFAULT
        }
    }

    private fun getDefaultConfig() {
        runCatching {
            var newsJson = ""
            runCatching {
                //ad
                newsJson = firebaseRemoteConfig?.getString("aobws_default_news_config")?:""
            }
            defaultNewsList = getListByGson(newsJson, NewsData::class.java)
//            if (defaultNewsList.isNullOrEmpty()){
//                defaultNewsList = getListByGson(FirebaseConfig.DEFAULT_NEWS_JSON, NewsData::class.java)
//            }
        }
    }

    private fun getNFConfig() {
        runCatching {
            var nfJson = ""
            runCatching {
                //ad
                nfJson = firebaseRemoteConfig?.getString("aobws_nf_config")?:""
            }
            nfRootBean = getBeanByGson(nfJson, AioNFData::class.java)
            if (nfRootBean == null){
                nfRootBean =getBeanByGson(FirebaseConfig.NF_JSON, AioNFData::class.java)
            }
        }
    }

    fun getADConfig() {
        runCatching {
            var adJson = ""
            runCatching {
                //ad
                if (CacheManager.isBUser){
                    adJson = firebaseRemoteConfig?.getString("aobws_refer_ad_config")?:""
                }else{
                    adJson = firebaseRemoteConfig?.getString("aobws_new_ad_config")?:""
                }
            }
            adJson = Base64.decode(adJson!!.toByteArray(), Base64.DEFAULT).decodeToString()
            adRootBean = getBeanByGson(adJson, AioADData::class.java)
            if (adRootBean == null){
                adJson = Base64.decode(AD_DEFAULT_JSON, Base64.DEFAULT).decodeToString()
                adRootBean =getBeanByGson(adJson, AioADData::class.java)
                AppLogs.dLog(AioADDataManager.TAG,"走本地配置 adconfig:${toJson(adRootBean)}")
            }else{
                AppLogs.dLog(AioADDataManager.TAG,"走远程配置 adconfig:${toJson(adRootBean)}")
            }
            adRootBean?.apply {
                AioADDataManager.initADConfig(this)
            }
        }
    }

    fun matchCountry(): String {
        var language = ""
        var country = ""
        runCatching {
            language = Locale.getDefault().language
        }
        runCatching {
            country = Locale.getDefault().country
        }
        AppLogs.dLog(APP.instance.TAG,"matchCountry language:${language} country:${country}")
        return when (language) {
            "pt" -> {
                "BR"
            }

            "ja" -> {
                "JP"
            }

            "in" -> {
                "ID"
            }

            "ko" -> {
                "KR"
            }

            "es" ->{
                "MX"
            }

            else -> {
                if (country.isNullOrEmpty() || country == "CN"){
                    "US"
                }else{
                    country
                }
            }
        }
    }

}