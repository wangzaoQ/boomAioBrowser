package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.view.View
import com.boom.aiobrowser.R
import com.boom.aiobrowser.databinding.BrowserPopNoAdBinding
import com.boom.aiobrowser.databinding.BrowserPopRewardedBinding
import pop.basepopup.BasePopupWindow

class TempNoADPop(context: Context, var callBack: (type:Int) -> Unit) : BasePopupWindow(context)  {

    init {
        setContentView(R.layout.browser_pop_no_ad)
    }

    var defaultBinding: BrowserPopNoAdBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = BrowserPopNoAdBinding.bind(contentView)
    }

    fun createPop(){
        defaultBinding?.apply {
            ivClose.setOnClickListener {
                dismiss()
            }
            tvConfirm.setOnClickListener {
                dismiss()
            }
        }
        showPopupWindow()
    }

    override fun onBackPressed(): Boolean {
        callBack.invoke(0)
        return true
    }
}