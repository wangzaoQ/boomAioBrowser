package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.View
import android.view.animation.Animation
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.BrowserPopSubBinding
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.SubscribeManager
import kotlinx.coroutines.Dispatchers
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

    fun createPop(successBack: () -> Unit) {
        defaultBinding?.apply {
            btnConfirm.setOnClickListener {
                var subManager = SubscribeManager(successBack = {
                    (context as BaseActivity<*>).addLaunch(success = {
                        successBack.invoke()
                        SubInfoPop(context).createPop()
                        dismiss()
                    }, failBack = {}, Dispatchers.Main)
                }, failBack = {
                    (context as BaseActivity<*>).addLaunch(success = {
                        if (it == "0"){
                            SubFailPop(context).createPop {
                                btnConfirm.performClick()
                            }
                            dismiss()
                        }else{
                            ToastUtils.showShort(context.getString(R.string.app_sub_error))
                        }
                    }, failBack = {}, Dispatchers.Main)
                })
                when (checkPosition) {
                    0 -> {
                        if (tvWeeklyPrice.text.isNullOrEmpty()){
                            subManager.billingComplete()
                            return@setOnClickListener
                        }
                        if (CacheManager.isSubscribeMember.not()) {
                            subManager.subscribeShop(
                                WeakReference(context as BaseActivity<*>),
                                "vip_weekly"
                            )
                        } else {
                            ToastUtils.showShort(context.getString(R.string.app_current_sub))
                        }
                    }

                    1 -> {
                        if (tvMonthlyPrice.text.isNullOrEmpty()){
                            subManager.billingComplete()
                            return@setOnClickListener
                        }
                        if (CacheManager.isSubscribeMember.not()) {
                            subManager.subscribeShop(
                                WeakReference(context as BaseActivity<*>),
                                "vip_monthly"
                            )
                        } else {
                            ToastUtils.showShort(context.getString(R.string.app_current_sub))
                        }
                    }
                    2 -> {
                        if (tvQuarterly.text.isNullOrEmpty()){
                            subManager.billingComplete()
                            return@setOnClickListener
                        }
                        if (CacheManager.isSubscribeMember.not()) {
                            subManager.subscribeShop(
                                WeakReference(context as BaseActivity<*>),
                                "vip_quarterly"
                            )
                        } else {
                            ToastUtils.showShort(context.getString(R.string.app_current_sub))
                        }
                    }
                    else -> {}
                }
            }
            tvRestore.setOnClickListener {
                PointEvent.posePoint(PointEventKey.restore_subscription)
                var subManager = SubscribeManager(successBack = {
                    (context as BaseActivity<*>).addLaunch(success = {
                        successBack.invoke()
                        SubInfoPop(context).createPop()
                        dismiss()
                    }, failBack = {}, Dispatchers.Main)
                }, failBack = {
                    (context as BaseActivity<*>).addLaunch(success = {
                        if (it == "-1"){
                            ToastUtils.showShort(APP.instance.getString(R.string.app_restore_error))
                        }else if (it == "2"){
                            ToastUtils.showShort(context.getString(R.string.app_sub_error))
                        }
                    }, failBack = {
                    }, Dispatchers.Main)

                })
                subManager.queryShop()
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




            llSubWeekly.performClick()
        }
        showPopupWindow()
        PointEvent.posePoint(PointEventKey.subscribe_impression, Bundle().apply {
            putString(PointValueKey.type,"no_vip")
        })
        var subManager = SubscribeManager(successBack = {

        }, failBack = {})
        subManager.getSubPrice{
            (context as BaseActivity<*>).addLaunch(success = {
                defaultBinding?.apply {
                    tvWeeklyPrice.text = it.get("vip_weekly")?:""
                    tvMonthlyPrice.text = it.get("vip_monthly")?:""
                    tvQuarterly.text = it.get("vip_quarterly")?:""

                    tvTipsMonthly.text = "${tvMonthlyPrice.text}${context.getString(R.string.app_monthly)} 50%${context.getString(R.string.app_off)}"
                    var ssb1 = SpannableStringBuilder(tvTipsMonthly.text)
                    ssb1.setSpan(StrikethroughSpan(), 0, "${tvMonthlyPrice.text}${context.getString(R.string.app_monthly)}".length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    tvTipsMonthly.setText(ssb1)

                    tvTipsQuarterly.text =
                        "${tvQuarterly.text}${context.getString(R.string.app_quarterly)} 80%${context.getString(R.string.app_off)}"
                    var ssb2 = SpannableStringBuilder(tvTipsQuarterly.text)
                    ssb2.setSpan(StrikethroughSpan(), 0, "${tvQuarterly.text}${context.getString(R.string.app_quarterly)}".length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    tvTipsQuarterly.setText(ssb2)
                }
            }, failBack = {},Dispatchers.Main)
        }
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