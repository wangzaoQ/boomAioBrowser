package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.view.View
import android.view.animation.Animation
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.BrowserPopDefaultBinding
import com.boom.aiobrowser.databinding.BrowserPopSubBinding
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.SubscribeManager
import pop.basepopup.BasePopupWindow
import pop.util.animation.AnimationHelper
import pop.util.animation.TranslationConfig
import java.lang.ref.WeakReference

class SubPop(context: Context) : BasePopupWindow(context) {
    init {
        setContentView(R.layout.browser_pop_sub)
    }

    var defaultBinding: BrowserPopSubBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = BrowserPopSubBinding.bind(contentView)
    }

    var checkPosition = 0

    fun createPop() {
        defaultBinding?.apply {
            btnConfirm.setOnClickListener {
                when (checkPosition) {
                    0 -> {
                        if (CacheManager.isSubscribeMember.not()) {
                            SubscribeManager.subscribeShop(
                                WeakReference(context as BaseActivity<*>),
                                "vip_weekly"
                            )
                        } else {
                            ToastUtils.showLong("当前已有订阅")
                        }
                    }

                    1 -> {
                        if (CacheManager.isSubscribeMember.not()) {
                            SubscribeManager.subscribeShop(
                                WeakReference(context as BaseActivity<*>),
                                "vip_monthly"
                            )
                        } else {
                            ToastUtils.showLong("当前已有订阅")
                        }
                    }
                    2 -> {
                        if (CacheManager.isSubscribeMember.not()) {
                            SubscribeManager.subscribeShop(
                                WeakReference(context as BaseActivity<*>),
                                "vip_quarterly"
                            )
                        } else {
                            ToastUtils.showLong("当前已有订阅")
                        }
                    }
                    else -> {}
                }
            }
            ivClose.setOnClickListener {
                dismiss()
            }
            llSubWeekly.setOnClickListener {
                checkPosition = 0
                updateUI(llSubWeekly, true)
                updateUI(llSubMonthly, false)
                updateUI(llSubQuarterly, false)
            }
            llSubMonthly.setOnClickListener {
                checkPosition = 1
                updateUI(llSubWeekly, false)
                updateUI(llSubMonthly, true)
                updateUI(llSubQuarterly, false)
            }
            llSubQuarterly.setOnClickListener {
                checkPosition = 2
                updateUI(llSubWeekly, false)
                updateUI(llSubMonthly, false)
                updateUI(llSubQuarterly, true)
            }
            tvTipsMonthly.text =
                "\$3.96/${context.getString(R.string.app_weekly)} 50%${context.getString(R.string.app_off)}"
            tvTipsQuarterly.text =
                "\$15.84/${context.getString(R.string.app_quarterly)} 80%${context.getString(R.string.app_off)}"
            llSubWeekly.performClick()
        }
        showPopupWindow()
    }

    private fun updateUI(llRoot: LinearLayoutCompat, isCheck: Boolean) {
        llRoot.isEnabled = isCheck.not()
        for (i in 0 until llRoot.childCount) {
            var child = (llRoot.getChildAt(i) as AppCompatTextView)
            child.setTextColor(context.getColor(if (isCheck) R.color.color_blue_2926D9 else R.color.color_black_3C3B72))
        }
    }

    override fun onDismiss() {
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