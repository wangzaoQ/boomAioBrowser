package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.view.View
import com.boom.aiobrowser.R
import com.boom.aiobrowser.databinding.BrowserPopRewardedBinding
import pop.basepopup.BasePopupWindow

class RewardedPop(context: Context,var callBack: (type:Int) -> Unit) : BasePopupWindow(context)  {

    init {
        setContentView(R.layout.browser_pop_rewarded)
    }

    var defaultBinding: BrowserPopRewardedBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = BrowserPopRewardedBinding.bind(contentView)
    }

    fun createPop(){
        defaultBinding?.apply {
            ivClose.setOnClickListener {
                callBack.invoke(0)
                dismiss()
            }
            tvConfirm.setOnClickListener {
                callBack.invoke(1)
                dismiss()
            }
        }
        showPopupWindow()
    }

}