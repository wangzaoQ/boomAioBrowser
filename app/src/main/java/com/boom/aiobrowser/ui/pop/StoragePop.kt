package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.view.View
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.BrowserPopClearBinding
import com.boom.aiobrowser.databinding.BrowserPopStorageBinding
import pop.basepopup.BasePopupWindow

class StoragePop(context: Context) : BasePopupWindow(context) {

    init {
        setContentView(R.layout.browser_pop_storage)
    }

    var defaultBinding: BrowserPopStorageBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = BrowserPopStorageBinding.bind(contentView)
    }

    fun createPop(callBack: (type:Int) -> Unit){
        defaultBinding?.apply {
            tvCancel.setOnClickListener {
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