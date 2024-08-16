package com.boom.aiobrowser.firebase

import android.app.Application
import android.util.Base64
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.ad.AioADDataManager
import com.boom.aiobrowser.ad.AioADDataManager.adRootBean
import com.boom.aiobrowser.data.AioADData
import com.boom.aiobrowser.data.AioRequestData
import com.boom.aiobrowser.firebase.FirebaseConfig.AD_DEFAULT_JSON
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.getBeanByGson
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object FirebaseManager {

//    val firebaseAnalytics: FirebaseAnalytics by lazy {
//        FirebaseAnalytics.getInstance(APP.instance)
//    }

    var firebaseRemoteConfig: FirebaseRemoteConfig?=null

    fun Application.initFirebase() {
        runCatching {
            AppLogs.dLog(APP.instance.TAG,"firebase 初始化")
            FirebaseApp.initializeApp(this)
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
                        }
                    }
                    delay(60*60*1000)
                }
            }
        }.onFailure {
            it.printStackTrace()
        }
    }


    private fun initFirebaseConfig(tag: String) {
        AppLogs.dLog(APP.instance.TAG,"tag:${tag}")
        getADConfig()
        runCatching {
            FirebaseConfig.AD_CD_ALL = firebaseRemoteConfig?.getString("aobws_cd")?.toInt()?:if (APP.isDebug)10 else 60
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

}