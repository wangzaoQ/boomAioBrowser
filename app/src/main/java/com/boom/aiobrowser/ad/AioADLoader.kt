package com.boom.aiobrowser.ad

import com.boom.aiobrowser.APP
import com.boom.aiobrowser.data.ADResultData
import com.boom.aiobrowser.data.AioRequestData
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd

class AioADLoader(
    val requestBean: AioRequestData,
    val successCallBack: (ADResultData) -> Unit,
    val failedCallBack: (String) -> Unit
) {
    private fun loadFailed(msg: String, failed: (String) -> Unit) {
        failed(msg)
    }
    init {
        when (requestBean.pxdtzgho) {
            AioADDataManager.AD_TYPE_OPEN -> openAD()
        }
    }

    private fun openAD() {
        val build = AdRequest.Builder().build()
        var startTime = System.currentTimeMillis()
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
//                    appOpenAd.setOnPaidEventListener {
//                        FirebaseAnalysis.go(it, appOpenAd, requestBean, adEnum)
//                    }
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    loadFailed(error.message, failedCallBack)
                }
            })
    }


}