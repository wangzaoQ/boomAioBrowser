package com.boom.aiobrowser.ad

import android.os.Bundle
import com.boom.aiobrowser.data.ADResultData
import com.boom.aiobrowser.data.AioRequestData
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey

abstract class BaseLoader(
    val requestBean: AioRequestData,
    val adEnum: ADEnum
) {
    fun loadFailed(msg: String, failed: (String) -> Unit) {
        failed(msg)
    }

     lateinit var failedCallBack: (String) -> Unit
     lateinit var successCallBack: (ADResultData) -> Unit


    fun setSuccessCall(successCallBack: (ADResultData) -> Unit){
        this.successCallBack = successCallBack
    }

    fun setFailedCall(failedCallBack: (String) -> Unit){
        this.failedCallBack = failedCallBack
    }

    fun startLoadAD(){
        when (requestBean.pxdtzgho) {
            AioADDataManager.AD_TYPE_OPEN -> openAD()
            AioADDataManager.AD_TYPE_INT -> intAD()
            AioADDataManager.AD_TYPE_NATIVE -> nativeAD()
            AioADDataManager.AD_TYPE_BANNER -> banner()
            AioADDataManager.AD_TYPE_RV -> rewarded()
        }
    }


    abstract fun openAD()
    abstract fun intAD()
    abstract fun nativeAD()
    abstract fun banner()
    abstract fun rewarded()

    fun bannerPoint(startTime: Long) {
        PointEvent.posePoint(PointEventKey.aobws_ad_load, Bundle().apply {
            putString(PointValueKey.ad_pos_id,adEnum.adName)
            putString(PointValueKey.ad_key,requestBean.ktygzdzn)
            putLong(PointValueKey.ad_time, (System.currentTimeMillis() - startTime) / 1000)
        })
    }

    fun nativePoint(startTime: Long,enum:ADEnum) {
        PointEvent.posePoint(PointEventKey.aobws_ad_load, Bundle().apply {
            putString(PointValueKey.ad_pos_id,enum.adName)
            putString(PointValueKey.ad_key,requestBean.ktygzdzn)
            putLong(PointValueKey.ad_time, (System.currentTimeMillis() - startTime) / 1000)
        })
    }
}