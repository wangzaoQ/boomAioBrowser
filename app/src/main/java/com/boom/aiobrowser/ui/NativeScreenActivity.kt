package com.boom.aiobrowser.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.get
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADDataManager
import com.boom.aiobrowser.ad.AioADDataManager.preloadAD
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.BrowserActivityNativeScreenBinding
import com.boom.aiobrowser.tools.AppLogs
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd

class NativeScreenActivity :BaseActivity<BrowserActivityNativeScreenBinding>(){
    override fun getBinding(inflater: LayoutInflater): BrowserActivityNativeScreenBinding {
        return BrowserActivityNativeScreenBinding.inflate(layoutInflater)
    }

    override fun setListener() {
        acBinding.ivClose.setOneClick {
            val intent = Intent()
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun onBackPressed() {
        acBinding.ivClose.performClick()
    }

    override fun setShowView() {
        showNativeAD()
    }

    var nativeAd:NativeAd?=null

    fun showNativeAD() {
        runCatching {
            val data = AioADDataManager.getCacheAD(ADEnum.DEFAULT_AD)
            nativeAd = (data?.adAny as? NativeAd)
            if (data?.adAny == null || nativeAd == null){
                finish()
            }
            AppLogs.dLog(
                AioADDataManager.TAG,
                "admob 广告展示:${ADEnum.DEFAULT_AD}-id:${data?.adRequestData?.ktygzdzn} type:图片池"
            )

            nativeAd?.let {
                acBinding.nativeADView?.run {
                    findViewById<TextView>(R.id.is_headline)?.run {
                        text = it.headline
                        headlineView = this
                    }
                    findViewById<TextView>(R.id.is_body)?.run {
                        text = it.body
                        bodyView = this
                    }
                    findViewById<TextView>(R.id.is_call)?.run {
                        if (it.callToAction == null) {
                            visibility = View.INVISIBLE
                        } else {
                            text = it.callToAction
                            callToActionView = this
                        }
                    }
                    findViewById<MediaView>(R.id.is_media)?.run {
                        it.mediaContent?.let {
                            mediaContent = it
                            setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                            mediaView = this
                        }
                    }
                    findViewById<ImageView>(R.id.is_icon)?.run {
                        it.icon?.let {
                            setImageDrawable(it.drawable)
                            iconView = this
                        }
                    }
                    setNativeAd(it)
                }
            }
            AioADDataManager.adCache.remove(ADEnum.DEFAULT_AD)
            preloadAD(ADEnum.DEFAULT_AD,"全屏原生")
        }.onFailure {
            finish()
        }
        life.destoryList.add {
            if(it!=0)return@add
            destoryNative()
        }
    }

    private fun destoryNative() {
        AppLogs.dLog(AioADDataManager.TAG,"destoryNative")
        runCatching {
            nativeAd?.destroy()
            if (acBinding.nativeADView?.parent != null) {
                (acBinding.nativeADView?.parent as? ViewGroup)?.removeView(acBinding.nativeADView)
            }
            acBinding.nativeADView?.destroy()
        }.onFailure {

        }

    }
}