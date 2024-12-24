package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.BrowserPopConfigBinding
import com.boom.aiobrowser.firebase.FirebaseConfig
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.UIManager
import com.boom.aiobrowser.tools.toJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import pop.basepopup.BasePopupWindow

class ConfigPop(context: Context) : BasePopupWindow(context) {

    init {
        setContentView(R.layout.browser_pop_config)
    }

    var defaultBinding: BrowserPopConfigBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = BrowserPopConfigBinding.bind(contentView)
    }

    fun createPop() {
        (context as BaseActivity<*>).addLaunch(success = {
            while (CacheManager.campaignId.isNullOrEmpty()) {
                delay(1000)
            }
            withContext(Dispatchers.Main){
                defaultBinding?.llRoot?.apply {
                    removeAllViews()
                    addTest("买量用户状态:${UIManager.isBuyUser()}") {
                    }
                    addTest("adjust:${CacheManager.adJustFrom}") {
                    }
                    addTest("af:${CacheManager.afFrom}") {
                    }
                    addTest("refer:${CacheManager.installRefer}") {
                    }
                    addTest("referConfig:${FirebaseConfig.referConfig}") {
                    }
                    addTest("cloak:${CacheManager.cloakValue}") {
                        if (CacheManager.cloakValue == "creamery") {
                            CacheManager.cloakValue = "orgasm"
                            it.text = "orgasm"
                        } else {
                            CacheManager.cloakValue = "creamery"
                            it.text = "creamery"
                        }
                    }
                    addTest("归因结果:${CacheManager.campaignId}") {
                        if (CacheManager.campaignId.isNullOrEmpty())return@addTest
                        if (CacheManager.campaignId == "22019400202") {
                            CacheManager.campaignId = "22008263268"
                        } else {
                            CacheManager.campaignId = "22019400202"
                        }
                        it.text = "归因结果:${CacheManager.campaignId}"
                    }
                    addTest("全局视频限制开关 true为限制 不展示下载:${FirebaseConfig.switchOpenFilter1}") {
                        FirebaseConfig.switchOpenFilter1 =
                            FirebaseConfig.switchOpenFilter1.not()
                        it.text = "全局视频限制开关 true为限制 不展示下载:${FirebaseConfig.switchOpenFilter1}"
                    }
                    addTest("网站限制list 命中则不展示下载:${toJson(FirebaseConfig.switchOpenFilterList)}") {

                    }
                    addTest("默认浏览器开关 true 允许展示pop :${FirebaseConfig.switchDefaultPop}") {
                        FirebaseConfig.switchDefaultPop = FirebaseConfig.switchDefaultPop.not()
                        it.text = "默认浏览器开关 true 允许展示pop:${FirebaseConfig.switchDefaultPop}"
                    }
                    addTest("下载引导开关 true 允许展示pop:${FirebaseConfig.switchDownloadGuidePop}") {
                        FirebaseConfig.switchDownloadGuidePop =
                            FirebaseConfig.switchDownloadGuidePop.not()
                        it.text = "下载引导开关 true 允许展示pop:${FirebaseConfig.switchDownloadGuidePop}"
                    }
                }
            }
        }, failBack = {})
        showPopupWindow()
    }


    fun ViewGroup.addTest(str: String, block: (view: AppCompatButton) -> Unit) {
        this.addView(AppCompatButton(context).apply {
            text = str
            isAllCaps = false
            setOnClickListener {
                block(this)
            }
        })
    }
}
