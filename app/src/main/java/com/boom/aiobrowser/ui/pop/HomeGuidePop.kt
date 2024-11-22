package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.view.View
import android.view.animation.Animation
import com.boom.aiobrowser.R
import com.boom.aiobrowser.databinding.BrowserPopHomeGuideBinding
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import pop.basepopup.BasePopupWindow
import pop.util.animation.AnimationHelper
import pop.util.animation.TranslationConfig

class HomeGuidePop(context: Context) : BasePopupWindow(context) {

    init {
        setContentView(R.layout.browser_pop_home_guide)
    }

    var defaultBinding: BrowserPopHomeGuideBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = BrowserPopHomeGuideBinding.bind(contentView)
    }

    var clickConfirm = false

    fun createPop(){
        defaultBinding?.apply {
            tvConfirm.setOnClickListener {
                clickConfirm = true
                PointEvent.posePoint(PointEventKey.guide_view)
                DownloadVideoGuidePop(context).createPop(0) {  }
                dismiss()
            }
            ivClose.setOnClickListener {
                dismiss()
            }
        }
        PointEvent.posePoint(PointEventKey.guide_pop)
        showPopupWindow()
    }

    override fun dismiss() {
        PointEvent.posePoint(PointEventKey.guide_close)
        PointEvent.posePoint(PointEventKey.home_page_first)
        super.dismiss()
    }

    override fun onCreateShowAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withTranslation(TranslationConfig.FROM_BOTTOM)
            .toShow()
    }

    override fun onCreateDismissAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withTranslation(TranslationConfig.TO_BOTTOM)
            .toDismiss()
    }

}