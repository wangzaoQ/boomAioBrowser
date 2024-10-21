package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import com.boom.aiobrowser.R
import com.boom.aiobrowser.databinding.BrowserPopProcessingTextBinding
import com.boom.aiobrowser.databinding.VideoPopRenameBinding
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.CacheManager
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

    fun createPop(text:String,fromPage:String,callBack: () -> Unit){
        defaultBinding?.apply {
            tvShareText.text = text
            btnOk.setOnClickListener {
                PointEvent.posePoint(PointEventKey.clipboard_open, Bundle().apply {
                    putString(PointValueKey.from_page,fromPage)
                    putString(PointValueKey.url,text)
                })
                if (CacheManager.isDisclaimerFirst){
                    CacheManager.isDisclaimerFirst = false
                    DisclaimerPop(context).createPop {
                        callBack.invoke()
                        dismiss()
                    }
                }else{
                    callBack.invoke()
                    dismiss()
                }
            }
        }
        setOutSideDismiss(true)
        showPopupWindow()

        PointEvent.posePoint(PointEventKey.clipboard, Bundle().apply {
            putString(PointValueKey.from_page,fromPage)
            putString(PointValueKey.url,text)
        })
    }
}