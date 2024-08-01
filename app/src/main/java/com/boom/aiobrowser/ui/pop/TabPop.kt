package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.view.View
import com.boom.aiobrowser.R
import com.boom.aiobrowser.databinding.BrowserPopSearchBinding
import com.boom.aiobrowser.databinding.BrowserPopTabBinding
import pop.basepopup.BasePopupWindow

class TabPop(context: Context): BasePopupWindow(context)  {
    init {
        setContentView(R.layout.browser_pop_tab)
    }
    var popBinding : BrowserPopTabBinding?=null
    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        popBinding = BrowserPopTabBinding.bind(contentView)
    }


}