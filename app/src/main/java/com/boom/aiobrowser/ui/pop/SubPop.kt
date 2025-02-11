package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.view.View
import android.view.animation.Animation
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.BuildConfig
import com.boom.aiobrowser.R
import com.boom.aiobrowser.databinding.BrowserPopDefaultBinding
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.tools.BrowserManager
import pop.basepopup.BasePopupWindow
import pop.util.animation.AnimationHelper
import pop.util.animation.TranslationConfig

class SubPop(context: Context) : BasePopupWindow(context){
    init {
        setContentView(R.layout.browser_pop_default)
    }

    var defaultBinding: BrowserPopDefaultBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = BrowserPopDefaultBinding.bind(contentView)
    }

    fun createPop(){
        APP.instance.showPopLevel = 1
        defaultBinding?.rlTabRoot?.addScrollBack {
            if (it>200){
                dismiss()
            }
        }
        defaultBinding?.apply {
            btnConfirm.setOnClickListener {
                PointEvent.posePoint(PointEventKey.default_pop_set)
                APP.instance.clickSetBrowser = true
                BrowserManager.setDefaultBrowser(context,BuildConfig.APPLICATION_ID)
                dismiss()
            }
            ivDelete.setOnClickListener {
                dismiss()
            }
        }
        showPopupWindow()
        PointEvent.posePoint(PointEventKey.default_pop)
    }

    override fun onDismiss() {
        APP.instance.showPopLevel = 0
        PointEvent.posePoint(PointEventKey.default_pop_close)
        super.onDismiss()
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