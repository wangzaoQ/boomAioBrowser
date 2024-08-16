package com.boom.aiobrowser.ad

import com.boom.aiobrowser.APP
import com.boom.aiobrowser.data.ADResultData
import com.boom.aiobrowser.data.AioADData
import com.boom.aiobrowser.data.AioRequestData
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.appDataReset
import com.google.android.gms.ads.MobileAds

object AioADDataManager {

    var LOAD_STATUS_START = 100
    var LOAD_STATUS_LOADING = 101
    var AD_LOAD_SUCCESS = 102
    var AD_LOAD_FAIL = 103

    const val AD_PLATFORM_ADMOB = "admob"

    const val AD_TYPE_OPEN = "op"

    const val AD_SHOW_TYPE_SUCCESS = "AD_SHOW_TYPE_SUCCESS"
    const val AD_SHOW_TYPE_FAILED = "AD_SHOW_TYPE_FAILED"


    var TAG = "AioADDataManager"

    var adCache: HashMap<ADEnum, ADResultData> = HashMap()

    var adRootBean: AioADData? = null


    fun initAD(){
        AppLogs.dLog(APP.instance.TAG,"admob初始化")
        MobileAds.initialize(APP.instance)
        AppLogs.dLog(APP.instance.TAG,"admob初始化结束")
    }

    fun initADConfig(bean: AioADData) {
        adRootBean = bean
        ADEnum.values().forEach {
            when (it) {
                ADEnum.LAUNCH -> {
                    it.adRequestList = bean.aobws_launch ?: mutableListOf()
                }
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
        loadAD(enum)
    }

    private fun loadAD(enum: ADEnum) {
        if (enum.adLoadStatus == LOAD_STATUS_LOADING) return
//        NewsAPP.singleApp.showToast("load:${enum}_size:${enum.originalList.size}")
        load(enum, mutableListOf<AioRequestData>().apply { addAll(enum.adRequestList) })
    }

    private fun load(adEnum: ADEnum, list: MutableList<AioRequestData>) {
        val data = list.removeFirstOrNull()
        if (data != null) {
            adEnum.adLoadStatus = LOAD_STATUS_LOADING
            AppLogs.dLog(TAG, "${adEnum}:-id:${data.ktygzdzn}-sort:${data.npxotusg}")
            AioADLoader(data, successCallBack = {
                AppLogs.dLog(
                    TAG,
                    "LoadSuccess:${adEnum}-id:${data.ktygzdzn}-time:${it.adRequestTime}"
                )
                adEnum.adLoadStatus = AD_LOAD_SUCCESS
                saveCacheAD(adEnum, it)
            }, failedCallBack = {
                adEnum.adLoadStatus = AD_LOAD_FAIL
                AppLogs.dLog(TAG, "loadFail:${adEnum}-id:${data.ktygzdzn}-message:${it}")
                load(adEnum, list)
            })
        } else {
            adEnum.adLoadStatus = AD_LOAD_FAIL
        }
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
       return adCache[ADEnum.LAUNCH]
    }
}