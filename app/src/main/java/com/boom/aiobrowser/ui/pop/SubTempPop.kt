package com.boom.aiobrowser.ui.pop

import android.content.Context
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
            tvTipsMonthly.text =
                "\$1.5/${context.getString(R.string.app_weekly)} ${context.getString(R.string.app_flash_sale)} 66%${context.getString(R.string.app_off)}"
        }
        setBackground(R.color.color_70_black)
        showPopupWindow()
        PointEvent.posePoint(PointEventKey.subscribe_pop)
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
                "vip_weekly"
            )
        } else {
            ToastUtils.showShort(context.getString(R.string.app_current_sub))
        }
    }
}