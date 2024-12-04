package com.boom.aiobrowser.ad

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.boom.aiobrowser.ad.AioADDataManager.addADClick
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.ADResultData
import com.boom.aiobrowser.tools.AppLogs
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.nativead.NativeAd

class MaxShow(
    activity: BaseActivity<*>,
    adEnum: ADEnum,
    tag: String,
    result: (type: String) -> Unit
) : BaseShow(activity, adEnum, tag, result) {
    override fun showScreenAd(adResultData: ADResultData, pointTag: String) {
        when ((adResultData.adRequestData?.pxdtzgho ?: "")) {
            AioADDataManager.AD_TYPE_INT -> {
                (adResultData.adAny as MaxInterstitialAd).run {
                    setListener(object : MaxAdListener {
                        override fun onAdLoaded(p0: MaxAd) {

                        }

                        override fun onAdDisplayed(p0: MaxAd) {
                            AppLogs.dLog(
                                AioADDataManager.TAG,
                                "max 广告展示:${adEnum.adName}-id:${adResultData.adRequestData?.ktygzdzn}"
                            )
                            adShowFullScreen(adEnum, tag = "admob 广告展示", pointTag)
                        }

                        override fun onAdHidden(p0: MaxAd) {
                            AppLogs.dLog(
                                AioADDataManager.TAG,
                                "admob 广告关闭:${adEnum.adName}-id:${adResultData.adRequestData?.ktygzdzn}"
                            )
                            adDismissFullScreen(adEnum, tag = "max 广告关闭")
                        }

                        override fun onAdClicked(p0: MaxAd) {
                            addADClick(adEnum.adName)
                        }

                        override fun onAdLoadFailed(p0: String, p1: MaxError) {
                        }

                        override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                            AppLogs.dLog(
                                AioADDataManager.TAG,
                                "admob 广告展示失败:${adEnum.adName}-id:${adResultData.adRequestData?.ktygzdzn}"
                            )
                            adShowFailed(adEnum, "admob 广告展示失败")
                        }
                    })
                    if (adEnum != ADEnum.LAUNCH_AD) {
                        loadComplete(type = AioADDataManager.AD_SHOW_TYPE_SUCCESS, tag)
                    }
                    showAd()
                }
            }

            else -> {}
        }
    }

    override fun showNativeAD(flRoot: FrameLayout, pointTag: String) {
        val data = AioADDataManager.getCacheAD(adEnum)
        (data!!.adAny as MaxNativeAdView).apply {
            val currentParent: ViewGroup? = parent as? ViewGroup
            currentParent?.removeView(this)
            flRoot.removeAllViews()
            flRoot.addView(this)
        }

    }

    override fun showADBanner(parent: ViewGroup, data: ADResultData, tTag: String) {
        if (data?.adAny == null) {
            loadComplete(type = AioADDataManager.AD_SHOW_TYPE_FAILED, tag)
            return
        }
        var view: View? = data.adAny as MaxAdView
        val currentParent: ViewGroup? = view?.parent as? ViewGroup
        currentParent?.removeView(view)
        parent.apply {
            visibility = View.VISIBLE
            addView(view)
        }
        activity.life.destoryList.add {
            if(it!=0)return@add
            runCatching {
                (data?.adAny as? MaxAdView)?.apply {
                    destroy()
                }
                parent?.removeView(view)
                data?.adAny = null
                view = null
            }.onFailure {
                it.stackTraceToString()
            }
        }
    }
}