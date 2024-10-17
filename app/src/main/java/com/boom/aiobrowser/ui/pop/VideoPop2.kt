package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import com.boom.aiobrowser.R
import com.boom.aiobrowser.databinding.BrowserPopCacheBinding
import com.boom.aiobrowser.databinding.BrowserVideoPopNotDetectedBinding
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValue
import com.boom.aiobrowser.point.PointValueKey
import pop.basepopup.BasePopupWindow

/**
 * 未检测到时的弹窗
 */
class VideoPop2 (context: Context) : BasePopupWindow(context) {

    init {
        setContentView(R.layout.browser_video_pop_not_detected)
    }

    var defaultBinding: BrowserVideoPopNotDetectedBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = BrowserVideoPopNotDetectedBinding.bind(contentView)
    }

    fun createPop(callBack: (type:Int) -> Unit){
        defaultBinding?.apply {
            tvFeedBack.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
            tvFeedBack.getPaint().setAntiAlias(true);//抗锯齿
            tvFeedBack.setOnClickListener {
                FeedbackPop(context).createPop {  }
                dismiss()
            }
            btnOk.setOnClickListener {
                dismiss()
            }
        }
        showPopupWindow()
        PointEvent.posePoint(PointEventKey.webpage_page_pop_nodl)
    }
}