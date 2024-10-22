package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import com.boom.aiobrowser.R
import com.boom.aiobrowser.databinding.BrowserPopCacheBinding
import com.boom.aiobrowser.databinding.BrowserVideoFeedbackBinding
import com.boom.aiobrowser.databinding.BrowserVideoPopNotDetectedBinding
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValue
import com.boom.aiobrowser.point.PointValueKey
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

    fun createPop(url:String,callBack: (type:Int) -> Unit){
        defaultBinding?.apply {
            tvCancel.setOnClickListener {
                PointEvent.posePoint(PointEventKey.webpage_page_pop_cancel)
                dismiss()
            }
            btnOk.setOnClickListener {
                PointEvent.posePoint(PointEventKey.webpage_page_pop_fb, Bundle().apply {
                    putString(PointValueKey.ponit_action, PointValue.click)
                    putString(PointValueKey.url, url)
                })
                dismiss()
            }
        }
        setBackground(R.color.color_70_black)
        showPopupWindow()
        PointEvent.posePoint(PointEventKey.webpage_page_pop_fb, Bundle().apply {
            putString(PointValueKey.ponit_action, PointValue.show)
            putString(PointValueKey.url, url)
        })
    }
}