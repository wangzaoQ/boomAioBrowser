package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.animation.Animation
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.BrowserPopClearBinding
import com.boom.aiobrowser.databinding.BrowserPopEngineBinding
import com.boom.aiobrowser.ui.activity.AboutActivity
import com.boom.aiobrowser.ui.activity.HistoryActivity
import com.boom.aiobrowser.ui.activity.MainActivity
import pop.basepopup.BasePopupWindow
import pop.util.animation.AnimationHelper
import pop.util.animation.TranslationConfig

class ClearPop(context: Context) : BasePopupWindow(context){
    init {
        setContentView(R.layout.browser_pop_clear)
    }

    var defaultBinding: BrowserPopClearBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = BrowserPopClearBinding.bind(contentView)
    }

    fun createPop(clearCallBack: () -> Unit){
        defaultBinding?.apply {
           llClearAll.setOnClickListener {
               clearCallBack.invoke()
               dismiss()
           }
            llCancel.setOnClickListener {
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