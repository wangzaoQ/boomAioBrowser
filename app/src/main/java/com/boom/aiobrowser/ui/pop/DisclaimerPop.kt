package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import com.boom.aiobrowser.R
import com.boom.aiobrowser.databinding.BrowserPopDisclaimerBinding
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import pop.basepopup.BasePopupWindow
import pop.util.animation.AnimationHelper
import pop.util.animation.TranslationConfig

class DisclaimerPop(context: Context) : BasePopupWindow(context){
    init {
        setContentView(R.layout.browser_pop_disclaimer)
    }

    var defaultBinding: BrowserPopDisclaimerBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = BrowserPopDisclaimerBinding.bind(contentView)
    }

    fun createPop(callBack: () -> Unit):DisclaimerPop{
        defaultBinding?.apply {
            ivClose.setOnClickListener {
                dismiss()
            }
            btnCommit.setOnClickListener {
                callBack.invoke()
                dismiss()
            }
        }
        setOutSideDismiss(true)
        showPopupWindow()
        PointEvent.posePoint(PointEventKey.webpage_download_pop_disclaimer)
        return this
    }

//    override fun onCreateShowAnimation(): Animation {
//        return AnimationHelper.asAnimation()
//            .withTranslation(TranslationConfig.)
//            .toShow()
//    }
//
//    override fun onCreateDismissAnimation(): Animation {
//        return AnimationHelper.asAnimation()
//            .withTranslation(TranslationConfig.TO_BOTTOM)
//            .toDismiss()
//    }
}