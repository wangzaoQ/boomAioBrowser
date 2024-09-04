package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.view.View
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.BrowserPopCacheBinding
import com.boom.aiobrowser.databinding.BrowserPopClearBinding
import com.boom.aiobrowser.databinding.BrowserPopStorageBinding
import pop.basepopup.BasePopupWindow

class CachePop(context: Context) : BasePopupWindow(context) {

    init {
        setContentView(R.layout.browser_pop_cache)
    }

    var defaultBinding: BrowserPopCacheBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = BrowserPopCacheBinding.bind(contentView)
    }

    fun createPop(callBack: (type:Int) -> Unit){
        defaultBinding?.apply {
            tvCancel.setOnClickListener {
                callBack.invoke(1)
                dismiss()
            }
            tvConfirm.setOnClickListener {
                callBack.invoke(0)
                dismiss()
            }
        }
        showPopupWindow()
    }
}