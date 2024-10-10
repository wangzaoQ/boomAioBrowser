package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.view.View
import android.view.animation.Animation
import com.boom.aiobrowser.R
import com.boom.aiobrowser.databinding.BrowserPopTipsBinding
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import pop.basepopup.BasePopupWindow
import pop.util.animation.AnimationHelper
import pop.util.animation.TranslationConfig

class TipsPop (context: Context) : BasePopupWindow(context){
    init {
        setContentView(R.layout.browser_pop_tips)
    }

    var defaultBinding: BrowserPopTipsBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = BrowserPopTipsBinding.bind(contentView)
    }


    fun createPop(callBack: () -> Unit):TipsPop{
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
        return this
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