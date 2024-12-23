package com.boom.aiobrowser.ad

import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkInitializationConfiguration
import com.applovin.sdk.AppLovinSdkSettings
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.ad.ADEnum.BANNER_AD
import com.boom.aiobrowser.ad.ADEnum.INT_AD
import com.boom.aiobrowser.ad.ADEnum.LAUNCH_AD
import com.boom.aiobrowser.ad.ADEnum.NATIVE_AD
import com.boom.aiobrowser.ad.ADEnum.NATIVE_DOWNLOAD_AD
import com.boom.aiobrowser.data.ADResultData
import com.boom.aiobrowser.data.AioADData
import com.boom.aiobrowser.data.AioRequestData
import com.boom.aiobrowser.firebase.FirebaseConfig
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.CacheManager.adLastTime
import com.boom.aiobrowser.tools.TimeManager
import com.boom.aiobrowser.tools.appDataReset
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object AioADDataManager {

    var LOAD_STATUS_START = 100
    var LOAD_STATUS_LOADING = 101
    var AD_LOAD_SUCCESS = 102
    var AD_LOAD_FAIL = 103

    const val AD_PLATFORM_ADMOB = "admob"
    const val AD_PLATFORM_MAX = "max"

    const val AD_TYPE_OPEN = "op"
    const val AD_TYPE_INT = "int"
    const val AD_TYPE_NATIVE = "nat"
    const val AD_TYPE_BANNER = "ban"

    const val AD_SHOW_TYPE_SUCCESS = "AD_SHOW_TYPE_SUCCESS"
    const val AD_SHOW_TYPE_FAILED = "AD_SHOW_TYPE_FAILED"


    var TAG = "AioADDataManager"

    var adCache: HashMap<ADEnum, ADResultData> = HashMap()

    var adRootBean: AioADData? = null

    var isShowAD = false

    @Volatile
    var applovinSdk :AppLovinSdk?=null


    fun initAD(){
        AppLogs.dLog(APP.instance.TAG,"max 初始化")
        MobileAds.initialize(APP.instance){
            AppLogs.dLog(APP.instance.TAG,"admob 初始化结束")
        }
        // Create the initialization configuration
        val initConfig = AppLovinSdkInitializationConfiguration.builder("9Aoo-xD1yqU_6ut1GkUtMgMK3r7KCRfQoVUUO_sdl6idKtF_d1Tz7_Zs4rk0ESL1O31oO8ygDloEzMIMgBbKFT", APP.instance)
            .setMediationProvider(AppLovinMediationProvider.MAX)
            .build()

        // Initialize the SDK with the configuration
        applovinSdk = AppLovinSdk.getInstance(APP.instance)
        applovinSdk!!.initialize(initConfig){
            // Start loading ads
            AppLogs.dLog(APP.instance.TAG,"max 初始化结束")
//            if (APP.isDebug){
//                AppLovinSdk.getInstance(APP.instance).showMediationDebugger()
//            }
        }
    }

    fun initADConfig(bean: AioADData) {
        adRootBean = bean
        ADEnum.values().forEach {
            when (it) {
                LAUNCH_AD -> {
                    it.adRequestList = bean.aobws_launch ?: mutableListOf()
                }
                INT_AD ->{
                    it.adRequestList = bean.aobws_main_one ?: mutableListOf()
                }
                NATIVE_AD ->{
                    it.adRequestList = bean.aobws_detail_bnat ?: mutableListOf()
                }
                NATIVE_DOWNLOAD_AD ->{
                    it.adRequestList = bean.aobws_download_bnat ?: mutableListOf()
                }
                BANNER_AD ->{
                    it.adRequestList = bean.aobws_ban_one ?: mutableListOf()
                }
                else -> {}
            }
            it.adRequestList.sortByDescending { it.npxotusg }
        }
    }

    fun getCacheAD(enum: ADEnum): ADResultData? {
        val adPreloadBean = adCache[enum]
        if (adPreloadBean?.adRequestData == null) {
            adCache.remove(enum)
            return null
        }
        return if ((System.currentTimeMillis() - adPreloadBean!!.adCreateTime) / 1000 > (adPreloadBean!!.adRequestData?.swpuzhhv
                ?: 0)
        ) {
            adCache.remove(enum)
            null
        } else {
            adPreloadBean
        }
    }

    fun saveCacheAD(enum: ADEnum, mcadpBean: ADResultData) {
        AppLogs.dLog(TAG, "putCache enum:${enum} oldSize:${adCache.size}")
        adCache.put(enum, mcadpBean)
        AppLogs.dLog(TAG, "putCache enum:${enum} newSize:${adCache.size}")
    }

    fun adFilter1(
    ): Boolean {
        appDataReset()
        if ((CacheManager.showEveryDay > (adRootBean?.pnvdskmb ?: 0) || CacheManager.clickEveryDay > (adRootBean?.qwmkszbx ?: 0))
        ) {
            AppLogs.dLog(
                TAG,
                "showEveryDay:${CacheManager.showEveryDay}/nsnt:${adRootBean?.pnvdskmb ?: 0}"
            )
            AppLogs.dLog(
                TAG,
                "clickEveryDay:${CacheManager.clickEveryDay}/nsnt:${adRootBean?.qwmkszbx ?: 0}"
            )
            return true
        }
        return false
    }


    fun preloadAD(enum: ADEnum, tag: String = "") {
        AppLogs.dLog(TAG, "预加载位置:${tag} 加载类型:${enum.adName}")
        if (adFilter1()) return
        if (enum != NATIVE_AD){
            if (getCacheAD(enum) !=null)return
        }
        loadAD(enum)
    }

    private fun loadAD(enum: ADEnum) {
        if (enum.adLoadStatus == LOAD_STATUS_LOADING) return
//        NewsAPP.singleApp.showToast("load:${enum}_size:${enum.originalList.size}")
        load(enum, mutableListOf<AioRequestData>().apply { addAll(enum.adRequestList) })
    }


    var platformAdmob = "admob"
    var platformMax = "max"

    private fun load(adEnum: ADEnum, list: MutableList<AioRequestData>) {
        val data = list.removeFirstOrNull()
        if (data != null) {
            AppLogs.dLog(TAG, "开始加载 ${adEnum}:-id:${data?.ktygzdzn}-sort:${data?.npxotusg}")
            adEnum.adLoadStatus = LOAD_STATUS_LOADING
            var adLoader :BaseLoader?=null
            when (data.tybxumpn) {
                platformMax-> {
                    adLoader = MaxDLoader(data,adEnum)
                }
                else -> {
                    adLoader = AdmobDLoader(data,adEnum)
                }
            }
            adLoader?.apply {
                setSuccessCall {
                    AppLogs.dLog(
                        TAG,
                        "adLoader:${adLoader.javaClass.simpleName} LoadSuccess:${adEnum}-id:${data.ktygzdzn}-time:${it.adRequestTime}"
                    )
                    adEnum.adLoadStatus = AD_LOAD_SUCCESS
                    saveCacheAD(adEnum, it)
                }
                setFailedCall {
                    adEnum.adLoadStatus = AD_LOAD_FAIL
                    AppLogs.dLog(TAG, "adLoader:${adLoader.javaClass.simpleName} loadFail:${adEnum}-id:${data.ktygzdzn}-message:${it}")
                    load(adEnum, list)
                }
                startLoadAD()
            }

        } else {
            adEnum.adLoadStatus = AD_LOAD_FAIL
        }
    }



    fun adAllowShowScreen():Boolean{
        appDataReset()
        var allow=true
        var content=""
        var launchMiddle = (System.currentTimeMillis()-adLastTime)/1000
        if (launchMiddle<FirebaseConfig.AD_CD_ALL){
            allow = false
            content = "间隔时间还差 ${(FirebaseConfig.AD_CD_ALL-launchMiddle)} seconds"
        }
        if (adFilter1()){
            allow = false
            content = "adShowMax"
        }
        if (allow.not()){
            AppLogs.dLog(TAG,"全屏广告检测未通过:${content}")
        }
        return allow
    }

    fun setADDismissTime() {
        if (APP.instance.lifecycleApp.isBackstage){
            AppLogs.dLog(AioADDataManager.TAG,"app 在后台 广告销毁不计时")
            return
        }
        if (APP.instance.lifecycleApp.adScreenType == 0){
            CacheManager.adLastTime = System.currentTimeMillis()
            AppLogs.dLog(AioADDataManager.TAG,"setLaunchLastTime:${TimeManager.getADTime()}")
        }
        APP.instance.lifecycleApp.adScreenType = -1
    }

    fun addShowNumber(tag:String) {
        CacheManager.showEveryDay = (CacheManager.showEveryDay+1)
        AppLogs.dLog(TAG,"${tag} 触发广告展示次数增加 当前:${CacheManager.showEveryDay}")
    }


    fun addADClick(tag:String) {
        CacheManager.clickEveryDay = (CacheManager.clickEveryDay+1)
        AppLogs.dLog(TAG,"${tag} 触发广告点击次数增加 当前:${CacheManager.clickEveryDay}")
    }

    fun getLaunchData(): ADResultData? {
       return adCache[LAUNCH_AD]
    }


}