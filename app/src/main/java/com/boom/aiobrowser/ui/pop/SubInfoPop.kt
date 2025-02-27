package com.boom.aiobrowser.ui.pop

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.R
import com.boom.aiobrowser.databinding.BrowserPopSubBinding
import com.boom.aiobrowser.databinding.BrowserPopSubInfoBinding
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import pop.basepopup.BasePopupWindow
import pop.util.animation.AnimationHelper
import pop.util.animation.TranslationConfig

class SubInfoPop(context: Context) : BasePopupWindow(context) {
    init {
        setContentView(R.layout.browser_pop_sub_info)
    }

    var defaultBinding: BrowserPopSubInfoBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = BrowserPopSubInfoBinding.bind(contentView)
    }

    fun createPop(){
        showPopupWindow()
        defaultBinding?.apply {
            ivClose.setOnClickListener {
                dismiss()
            }
            tvSubInfo.setOnClickListener {
                openPlayStoreAccount()
            }
        }
        PointEvent.posePoint(PointEventKey.subscribe_impression, Bundle().apply {
            putString(PointValueKey.type,"vip")
        })
    }

    private fun openPlayStoreAccount() {
        PointEvent.posePoint(PointEventKey.subscription_management)
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/account/subscriptions")))
        } catch (e: ActivityNotFoundException) {
            ToastUtils.showLong(context.getString(R.string.app_open_error))
            e.printStackTrace()
        }
    }


    override fun onCreateShowAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withTranslation(TranslationConfig.FROM_BOTTOM)
            .toShow()
    }

    override fun onCreateDismissAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withTranslation(TranslationConfig.TO_BOTTOM)
            .toDismiss()
    }
}