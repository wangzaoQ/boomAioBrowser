package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.os.Build
import android.view.View
import android.view.animation.Animation
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.BuildConfig
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.databinding.BrowserPopDefaultBinding
import com.boom.aiobrowser.databinding.BrowserPopNfGuideBinding
import com.boom.aiobrowser.nf.NFManager
import com.boom.aiobrowser.nf.NFShow
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.tools.BrowserManager
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import pop.basepopup.BasePopupWindow
import pop.util.animation.AnimationHelper
import pop.util.animation.TranslationConfig
import java.lang.ref.WeakReference

class NFGuidePop(context: Context) : BasePopupWindow(context) {
    init {
        setContentView(R.layout.browser_pop_nf_guide)
    }

    var defaultBinding: BrowserPopNfGuideBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = BrowserPopNfGuideBinding.bind(contentView)
    }

    var isComplete = false


    fun createPop(callBack: () -> Unit){
        defaultBinding?.apply {
            btnConfirm.setOnClickListener {
                APP.instance.isGoOther = true
                isComplete = true
                PointEvent.posePoint(PointEventKey.noti_confirm_pop_allow)
                NFManager.requestNotifyPermission(WeakReference((context as BaseActivity<*>)),
                    onSuccess = {
                        PointEvent.posePoint(PointEventKey.noti_confirm_pop_suc)
                        callBack.invoke()
                        dismiss()
                    },
                    onFail = {
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S){
                            XXPermissions.startPermissionActivity(context, Permission.POST_NOTIFICATIONS)
                        }
                        PointEvent.posePoint(PointEventKey.noti_confirm_pop_fail)
                        dismiss()
                    })
            }
            ivClose.setOnClickListener {
                dismiss()
            }
        }
        showPopupWindow()
        PointEvent.posePoint(PointEventKey.noti_confirm_pop)
    }

    override fun onDismiss() {
        if (isComplete.not()){
            PointEvent.posePoint(PointEventKey.noti_confirm_pop_skip)
        }
        super.onDismiss()
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