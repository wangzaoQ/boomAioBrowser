package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.view.View
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.BuildConfig
import com.boom.aiobrowser.R
import com.boom.aiobrowser.databinding.BrowserPopDefaultBinding
import com.boom.aiobrowser.databinding.BrowserPopLoadingBinding
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.tools.BrowserManager
import pop.basepopup.BasePopupWindow

class LoadingPop(context: Context) : BasePopupWindow(context){
    init {
        setContentView(R.layout.browser_pop_loading)
    }

    var defaultBinding: BrowserPopLoadingBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = BrowserPopLoadingBinding.bind(contentView)
    }

    fun createPop(){
        defaultBinding?.apply {
            lottieAnim.apply {
                setAnimation("loading.json")
                playAnimation()
            }
        }
        showPopupWindow()
    }

    override fun onDismiss() {
        defaultBinding?.lottieAnim?.cancelAnimation()
        super.onDismiss()
    }

}