package com.boom.aiobrowser.tools

import android.app.Activity
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.boom.aiobrowser.base.BaseActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

object GlideManager {

    fun loadImg(fragment: Fragment?, iv: ImageView, url: Any?, loadId:Int?=0, errorId:Int?=0, width: Int=0, height: Int=0) {
        runCatching {
            if (url == null)return
            val builder =if (fragment !=null) Glide.with(fragment).load(url) else Glide.with(iv).load(url)
//
            if (width!=0 && height!=0){
                builder.override(width,height)
            }
            if (loadId!=0){
                builder.placeholder(loadId!!)
            }
            if (errorId!=0){
                builder.error(errorId!!)
            }
//            builder.transition(DrawableTransitionOptions().crossFade()).into(iv)
            builder.into(iv)
        }.onFailure {
            AppLogs.eLog("GlideManager","loadImg2   :${it.stackTraceToString()}")
        }
    }
}