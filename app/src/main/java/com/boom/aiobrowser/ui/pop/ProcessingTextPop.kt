package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.view.View
import android.view.animation.Animation
import com.boom.aiobrowser.R
import com.boom.aiobrowser.databinding.BrowserPopProcessingTextBinding
import com.boom.aiobrowser.databinding.VideoPopRenameBinding
import pop.basepopup.BasePopupWindow
import pop.util.animation.AnimationHelper
import pop.util.animation.TranslationConfig

class ProcessingTextPop (context: Context)  : BasePopupWindow(context) {

    init {
        setContentView(R.layout.browser_pop_processing_text)
    }

    var defaultBinding: BrowserPopProcessingTextBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = BrowserPopProcessingTextBinding.bind(contentView)
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

    fun createPop(text:String,callBack: () -> Unit){
        defaultBinding?.apply {
            tvShareText.text = text
            btnOk.setOnClickListener {
                callBack.invoke()
                dismiss()
            }
        }
        setOutSideDismiss(true)
        showPopupWindow()
    }
}