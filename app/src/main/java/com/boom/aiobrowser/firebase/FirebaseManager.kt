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
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.PushData
import com.boom.aiobrowser.firebase.FirebaseConfig.AD_DEFAULT_JSON
import com.boom.aiobrowser.nf.NFManager
import com.boom.aiobrowser.nf.NFManager.defaultNewsList
import com.boom.aiobrowser.nf.NFManager.nfRootBean
import com.boom.aiobrowser.point.Install
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.tools.getListByGson
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
    }

    private fun initJumpConfig() {
        var configBrowserFrom = ""
        runCatching {
            configBrowserFrom = firebaseRemoteConfig?.getString("start_home_download")?:""
        }
        var splits = configBrowserFrom.split(",")
        if (configBrowserFrom.isNullOrEmpty()||splits.isNullOrEmpty()||splits.get(0).isNullOrEmpty()){
            configBrowserFrom = FirebaseConfig.jumpBrowserConfig
            splits = configBrowserFrom.split(",")
        }
        if (splits.isNullOrEmpty().not()){
            FirebaseConfig.browserJumpList = splits
        }

        var configNewsFrom = ""
        runCatching {
            configNewsFrom = firebaseRemoteConfig?.getString("start_news")?:""
        }
        var splitsNews = configNewsFrom.split(",")
        if (configNewsFrom.isNullOrEmpty()||splitsNews.isNullOrEmpty()||splitsNews.get(0).isNullOrEmpty()){
            configNewsFrom = FirebaseConfig.jumpNewsConfig
            splitsNews = configNewsFrom.split(",")
        }
        if (splitsNews.isNullOrEmpty().not()){
            FirebaseConfig.newsJumpList = splitsNews
        }
    }

    private fun initFirebaseConfig(tag: String) {
        AppLogs.dLog(APP.instance.TAG,"tag:${tag}")
        getADConfig()
        getNFConfig()
        getDefaultConfig()
        runCatching {
            FirebaseConfig.AD_CD_ALL = firebaseRemoteConfig?.getString("aobws_cd")?.toInt()?:if (APP.isDebug)10 else 60
        }
        var show_tutorial = 0
        runCatching {
            show_tutorial = firebaseRemoteConfig?.getString("show_tutorial")?.toInt()?:0
        }
        FirebaseConfig.switchDownloadGuidePop = show_tutorial != 0
        var show_default = 0
        runCatching {
            show_default = firebaseRemoteConfig?.getString("show_default")?.toInt()?:0
        }
        FirebaseConfig.switchDefaultPop = show_default != 0
        runCatching {
            var aioPush = firebaseRemoteConfig?.getString("aio_push")
            FirebaseConfig.pushData = getBeanByGson(aioPush,PushData::class.java)
            PointEvent.posePoint(PointEventKey.aio_push, Bundle().apply {
                putInt(PointValueKey.type,FirebaseConfig.pushData?.time_interval?:0)
            })
        }
        var config_filter1 = 0
        runCatching {
            config_filter1 = firebaseRemoteConfig?.getString("alldownload_switch")?.toInt()?:1
        }
        FirebaseConfig.switchOpenFilter1=config_filter1!=0
        var config_filter2 = ""
        runCatching {
            config_filter2 = firebaseRemoteConfig?.getString("config_filter")?:""
        }
        var splits = config_filter2.split(",")
        if (config_filter2.isNullOrEmpty()||splits.isNullOrEmpty()||splits.get(0).isNullOrEmpty()){
            config_filter2 = FirebaseConfig.FILTER_DEFAULT_WEB
            splits = config_filter2.split(",")
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
            if (defaultNewsList.isNullOrEmpty()){
                defaultNewsList = getListByGson(FirebaseConfig.DEFAULT_NEWS_JSON, NewsData::class.java)
            }
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
                adJson = firebaseRemoteConfig?.getString("aobws_ad_config")?:""
            }
            adJson = Base64.decode(adJson!!.toByteArray(), Base64.DEFAULT).decodeToString()
            adRootBean = getBeanByGson(adJson, AioADData::class.java)
            if (adRootBean == null){
                adJson = Base64.decode(AD_DEFAULT_JSON, Base64.DEFAULT).decodeToString()
                adRootBean =getBeanByGson(adJson, AioADData::class.java)
            }
            adRootBean?.apply {
                AioADDataManager.initADConfig(this)
            }
        }
    }

    fun matchCountry(): String {
        var language = ""
        runCatching {
            language = Locale.getDefault().language
        }
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
                "US"
            }
        }
    }

}