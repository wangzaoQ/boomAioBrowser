package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.view.View
import android.view.animation.Animation
import com.boom.aiobrowser.R
import com.boom.aiobrowser.databinding.BrowserPopSubBinding
import com.boom.aiobrowser.databinding.BrowserPopSubInfoBinding
import com.boom.aiobrowser.databinding.BrowserPopSubManageBinding
import pop.basepopup.BasePopupWindow
import pop.util.animation.AnimationHelper
import pop.util.animation.TranslationConfig

class SubManagePop(context: Context) : BasePopupWindow(context) {
    init {
        setContentView(R.layout.browser_pop_sub_manage)
    }

    var defaultBinding: BrowserPopSubManageBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = BrowserPopSubManageBinding.bind(contentView)
    }

    fun createPop(){
        showPopupWindow()
        defaultBinding?.apply {
            ivClose.setOnClickListener {
                dismiss()
            }
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