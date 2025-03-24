package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.view.View
import com.boom.aiobrowser.R
import com.boom.aiobrowser.databinding.BrowserPopNoAdBinding
import com.boom.aiobrowser.databinding.BrowserPopRewardedBinding
import com.boom.aiobrowser.databinding.BrowserPopSignRejoinBinding
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import pop.basepopup.BasePopupWindow

class SignRejoinPop(context: Context, var callBack: (type:Int) -> Unit) : BasePopupWindow(context)  {

    init {
        setContentView(R.layout.browser_pop_sign_rejoin)
    }

    var defaultBinding: BrowserPopSignRejoinBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = BrowserPopSignRejoinBinding.bind(contentView)
    }

    fun createPop(){
        defaultBinding?.apply {
            ivClose.setOnClickListener {
                dismiss()
            }
            tvConfirm.setOnClickListener {
                PointEvent.posePoint(PointEventKey.daily_interrupt_rc)
                callBack.invoke(0)
                dismiss()
            }
        }
        showPopupWindow()
    }

}