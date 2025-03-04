package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.graphics.Paint
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.BrowserPopSubBinding
import com.boom.aiobrowser.databinding.BrowserPopSubTempBinding
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.SubscribeManager
import kotlinx.coroutines.Dispatchers
import pop.basepopup.BasePopupWindow
import java.lang.ref.WeakReference

class SubTempPop(context: Context,var showADBack: () -> Unit) : BasePopupWindow(context) {

    init {
        setContentView(R.layout.browser_pop_sub_temp)
    }

    var defaultBinding: BrowserPopSubTempBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = BrowserPopSubTempBinding.bind(contentView)
    }


    fun createPop(){
        CacheManager.dayShowSubTemp = false
        defaultBinding?.apply {
            ivClose.setOnClickListener {
                dismiss()
            }
            tvConfirm.setOnClickListener {
                var subManager= SubscribeManager(successBack = {
                    (context as BaseActivity<*>).addLaunch(success = {
                        SubInfoPop(context).createPop()
                        dismiss()
                    }, failBack = {},Dispatchers.Main)
                }, failBack = {
                    (context as BaseActivity<*>).addLaunch(success = {
                        if (it == "0"){
                            SubFailPop(context).createPop {
                                tvConfirm.performClick()
                            }
                            dismiss()
                        }else{
                            ToastUtils.showShort(context.getString(R.string.app_sub_error))
                        }
                    }, failBack = {},Dispatchers.Main)
                })
                clickBuy = true
                subVIP(subManager)
            }
//            tvTipsMonthly.text =
//                "\$1.5/${context.getString(R.string.app_weekly)} ${context.getString(R.string.app_flash_sale)} 66%${context.getString(R.string.app_off)}"
//            var ssb2 = SpannableStringBuilder(tvTipsMonthly.text)
//
//            ssb2.setSpan(StrikethroughSpan(), 0, "\$1.5/${context.getString(R.string.app_weekly)}".length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//            tvTipsMonthly.setText(ssb2)
        }
        setBackground(R.color.color_70_black)
        showPopupWindow()
        PointEvent.posePoint(PointEventKey.subscribe_pop)
        var subManager = SubscribeManager(successBack = {

        }, failBack = {})
        subManager.getSubPrice{
            (context as BaseActivity<*>).addLaunch(success = {
                defaultBinding?.apply {
                    tvWeeklyPrice.text = it.get("vip_weekly")?:""
                    tvTipsMonthly.text = "${tvWeeklyPrice.text}${context.getString(R.string.app_weekly)} ${context.getString(R.string.app_flash_sale)} 66%${context.getString(R.string.app_off)}"
                    var ssb1 = SpannableStringBuilder(tvTipsMonthly.text)
                    ssb1.setSpan(StrikethroughSpan(), 0, "${tvWeeklyPrice.text}${context.getString(R.string.app_weekly)}".length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    tvTipsMonthly.setText(ssb1)
                }
            }, failBack = {},Dispatchers.Main)
        }
    }

    var clickBuy = false

    override fun dismiss() {
        super.dismiss()
        if (clickBuy.not()){
            showADBack.invoke()
        }
    }

    private fun subVIP(subManager:SubscribeManager) {
        if (CacheManager.isSubscribeMember.not()) {
            subManager.subscribeShop(
                WeakReference(context as BaseActivity<*>),
                "vip_weekly",
                1
            )
        } else {
            ToastUtils.showShort(context.getString(R.string.app_current_sub))
        }
    }
}