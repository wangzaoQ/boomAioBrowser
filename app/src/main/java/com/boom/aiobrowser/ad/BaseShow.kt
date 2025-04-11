package com.boom.aiobrowser.ad

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.ad.AioADDataManager.setADDismissTime
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.ADResultData
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs

abstract class BaseShow(var activity: BaseActivity<*>, var adEnum: ADEnum,var  tag: String,var result: (type: String) -> Unit) {

    abstract fun showScreenAd(adResultData: ADResultData, pointTag: String)
    abstract fun showNativeAD(flRoot: FrameLayout, pointTag:String)
    abstract fun showADBanner(parent: ViewGroup, data: ADResultData, tTag:String)


    fun adShowFailed(adEnum: ADEnum, tag: String) {
        if (adEnum == ADEnum.LAUNCH_AD) {
            loadComplete(AioADDataManager.AD_SHOW_TYPE_FAILED, tag)
        }
    }

    fun adDismissFullScreen(adEnum: ADEnum, tag: String) {
        AppLogs.dLog(AioADDataManager.TAG, "tag:${tag} 位置:${adEnum.adName}")
        if (adEnum == ADEnum.LAUNCH_AD || adEnum == ADEnum.REWARD_AD) {
            loadComplete(type = AioADDataManager.AD_SHOW_TYPE_SUCCESS, tag)
        }
        setADDismissTime()
    }

    fun adShowFullScreen(adEnum: ADEnum, tag: String, adResultData: ADResultData, pointTag: String) {
        AppLogs.dLog(AioADDataManager.TAG, "tag:${tag} 位置:${adEnum.adName}")
        AioADDataManager.addShowNumber(tag)
        if (adResultData.adShowType == 1){
            AioADDataManager.preloadAD(ADEnum.DEFAULT_AD,"fullScreen 广告展示时")
        }else{
            AioADDataManager.preloadAD(adEnum,"fullScreen 广告展示时")
        }
        APP.instance.lifecycleApp.adScreenType = 0
        PointEvent.posePoint(PointEventKey.aobws_ad_impression, Bundle().apply {
            putString(PointValueKey.ad_pos_id,pointTag)
        })
    }

    fun loadComplete(type: String, tag: String) {
        AppLogs.dLog(AioADDataManager.TAG, "tag:${tag} result 开始回调 type:${type}")
        result?.invoke(type)
    }


    fun getTypeContent(type:Int):String{
        return if (type == 0 ) "广告池 视频池 " else "广告池 图片池"
    }

}