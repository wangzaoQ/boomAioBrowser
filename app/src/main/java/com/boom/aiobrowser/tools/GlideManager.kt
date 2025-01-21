package com.boom.aiobrowser.tools

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.SizeUtils.dp2px
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget

object GlideManager {

    fun loadImg(fragment: Fragment?=null, iv: ImageView, url: Any?, loadId:Int?=0, errorId:Int?=0, width: Int=0, height: Int=0) {
        runCatching {
            if (url == null)return
            val builder =if (fragment !=null) GlideApp.with(fragment).load(url) else GlideApp.with(iv).load(url)
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

    fun loadNFBitmap(mContext: Context, url: Any?, width:Int, height: Int, bitmapCall:(Bitmap?)->Unit, callFail:(()->Unit)?=null){
        if (url == null || mContext == null){
            callFail?.invoke()
            return
        }

        GlideApp.with(mContext)
            .load(url)
            .override(width,height)
            .into(object : CustomTarget<Drawable?>() {
                override fun onResourceReady(resource: Drawable, transition: com.bumptech.glide.request.transition.Transition<in Drawable?>?) {
                    runCatching {
                        bitmapCall(resource.toBitmap(width, height, Bitmap.Config.RGB_565))
                    }.onFailure {
                        AppLogs.eLog("GlideManager","通知图片获取失败")
                        callFail?.invoke()
                    }
                }
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    callFail?.invoke()
                }
            })
    }

}