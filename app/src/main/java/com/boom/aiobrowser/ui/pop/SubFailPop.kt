package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.view.View
import com.boom.aiobrowser.R
import com.boom.aiobrowser.databinding.BrowserPopSubBinding
import com.boom.aiobrowser.databinding.BrowserPopSubFailBinding
import pop.basepopup.BasePopupWindow

class SubFailPop(context: Context) : BasePopupWindow(context) {

    init {
        setContentView(R.layout.browser_pop_sub_fail)
    }

    var defaultBinding: BrowserPopSubFailBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = BrowserPopSubFailBinding.bind(contentView)
    }

    fun createPop(retryBack: () -> Unit){
        defaultBinding?.apply {
            tvFeedBack.setOnClickListener {
                dismiss()
            }
            tvRetry.setOnClickListener {
                retryBack.invoke()
                dismiss()
            }
        }
        setBackground(R.color.color_70_black)
        showPopupWindow()
    }
}