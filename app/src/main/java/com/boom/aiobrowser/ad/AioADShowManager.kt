package com.boom.aiobrowser.ad

import com.boom.aiobrowser.APP
import com.boom.aiobrowser.ad.AioADDataManager.addADClick
import com.boom.aiobrowser.ad.AioADDataManager.setADDismissTime
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.ADResultData
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.TimeManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.rewarded.RewardedAd

class AioADShowManager(
    private val activity: BaseActivity<*>,
    var adEnum: ADEnum,
    var tag: String,
    var result: (type: String) -> Unit
) {

    fun showScreenAD() {
        var data = AioADDataManager.getCacheAD(adEnum)
        if (data == null || activity == null || activity.getActivityStatus().not()) {
            loadComplete(type = AioADDataManager.AD_SHOW_TYPE_FAILED, tag = "activity 状态异常")
            return
        }
        initScreenAdmob(data, adEnum)
    }

    private fun initScreenAdmob(adResultData: ADResultData, adEnum: ADEnum) {
        when (adResultData.adRequestData?.tybxumpn) {
            AioADDataManager.AD_PLATFORM_ADMOB -> {
                val callback = object : FullScreenContentCallback() {
                    override fun onAdShowedFullScreenContent() {
                        AppLogs.dLog(
                            AioADDataManager.TAG,
                            "admob 广告展示:${adEnum.adName}-id:${adResultData.adRequestData?.ktygzdzn}"
                        )
                        adShowFullScreen(adResultData, adEnum, tag = "admob 广告展示")
                    }

                    override fun onAdClicked() {
                        addADClick(adEnum.adName)
                    }

                    override fun onAdDismissedFullScreenContent() {
                        AppLogs.dLog(
                            AioADDataManager.TAG,
                            "admob 广告关闭:${adEnum.adName}-id:${adResultData.adRequestData?.ktygzdzn}"
                        )
                        adDismissFullScreen(adEnum, tag = "admob 广告关闭")
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        AppLogs.dLog(
                            AioADDataManager.TAG,
                            "admob 广告展示失败:${adEnum.adName}-id:${adResultData.adRequestData?.ktygzdzn}"
                        )
                        adShowFailed(adEnum, "admob 广告展示失败")
                    }
                }
                when (adResultData.adRequestData?.pxdtzgho ?: "") {
                    AioADDataManager.AD_TYPE_OPEN -> (adResultData.adAny as AppOpenAd).run {
                        fullScreenContentCallback = callback
                        show(activity!!)
                    }
                }
            }
        }
        AioADDataManager.adCache.remove(adEnum)
//        activity.aLife.destoryList.add{
//            if(it!=0)return@add
//            when (data.nowAdValue) {
//                is AppOpenAd -> {
//                    (data.nowAdValue as AppOpenAd).fullScreenContentCallback = null
//                }
//                is InterstitialAd ->{
//                    (data.nowAdValue as InterstitialAd).fullScreenContentCallback = null
//                }
//                is RewardedAd ->{
//                    (data.nowAdValue as RewardedAd).fullScreenContentCallback = null
//                }
//                else -> {}
//            }
//            data.nowAdValue = null
//        }
    }

    private fun adShowFailed(adEnum: ADEnum, tag: String) {
        if (adEnum == ADEnum.LAUNCH) {
            loadComplete(AioADDataManager.AD_SHOW_TYPE_FAILED, tag)
        }
    }

    private fun adDismissFullScreen(adEnum: ADEnum, tag: String) {
        AppLogs.dLog(AioADDataManager.TAG, "tag:${tag} 位置:${adEnum.adName}")
        if (adEnum == ADEnum.LAUNCH) {
            loadComplete(type = AioADDataManager.AD_SHOW_TYPE_SUCCESS, tag)
        }
        setADDismissTime()
    }

    private fun adShowFullScreen(adResultData: ADResultData, adEnum: ADEnum, tag: String) {
        AppLogs.dLog(AioADDataManager.TAG, "tag:${tag} 位置:${adEnum.adName}")
        AioADDataManager.addShowNumber(tag)
        AioADDataManager.preloadAD(adEnum,"广告展示时")
        if (adEnum == ADEnum.LAUNCH){
            APP.instance.lifecycleApp.adScreenType = 0
        }
    }

    private fun loadComplete(type: String, tag: String) {
        AppLogs.dLog(AioADDataManager.TAG, "tag:${tag} result 开始回调 type:${type}")
        result?.invoke(type)
    }

}