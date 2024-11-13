package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.view.View
import android.view.animation.Animation
import com.boom.aiobrowser.R
import com.boom.aiobrowser.databinding.BrowserPopHomeGuideBinding
import pop.basepopup.BasePopupWindow
import pop.util.animation.AnimationHelper
import pop.util.animation.TranslationConfig

class HomeGuidePop(context: Context) : BasePopupWindow(context) {

    init {
        setContentView(R.layout.browser_pop_home_guide)
    }

    var defaultBinding: BrowserPopHomeGuideBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = BrowserPopHomeGuideBinding.bind(contentView)
    }

    fun createPop(){
        defaultBinding?.apply {
            tvConfirm.setOnClickListener {
                DownloadVideoGuidePop(context).createPop {  }
                dismiss()
            }
            ivClose.setOnClickListener {
                dismiss()
            }
        }
        showPopupWindow()
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