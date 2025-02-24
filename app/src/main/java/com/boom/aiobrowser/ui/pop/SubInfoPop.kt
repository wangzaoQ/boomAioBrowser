package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.view.View
import com.boom.aiobrowser.R
import com.boom.aiobrowser.databinding.BrowserPopSubBinding
import com.boom.aiobrowser.databinding.BrowserPopSubInfoBinding
import pop.basepopup.BasePopupWindow

class SubInfoPop(context: Context) : BasePopupWindow(context) {
    init {
        setContentView(R.layout.browser_pop_sub_info)
    }

    var defaultBinding: BrowserPopSubInfoBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = BrowserPopSubInfoBinding.bind(contentView)
    }

}