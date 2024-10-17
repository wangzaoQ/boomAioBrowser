package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.graphics.Paint
import android.view.View
import com.boom.aiobrowser.R
import com.boom.aiobrowser.databinding.BrowserPopCacheBinding
import com.boom.aiobrowser.databinding.BrowserVideoFeedbackBinding
import com.boom.aiobrowser.databinding.BrowserVideoPopNotDetectedBinding
import pop.basepopup.BasePopupWindow

/**
 * 未检测到时的弹窗
 */
class FeedbackPop (context: Context) : BasePopupWindow(context) {

    init {
        setContentView(R.layout.browser_video_feedback)
    }

    var defaultBinding: BrowserVideoFeedbackBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = BrowserVideoFeedbackBinding.bind(contentView)
    }

    fun createPop(callBack: (type:Int) -> Unit){
        defaultBinding?.apply {
            tvCancel.setOnClickListener {
                dismiss()
            }
            btnOk.setOnClickListener {
                dismiss()
            }
        }
        showPopupWindow()
    }
}