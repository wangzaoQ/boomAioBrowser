package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.boom.aiobrowser.APP
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

    var allowRefresh = true

    fun createPop() {
        addPop()
        showPopupWindow()
    }

    private fun addPop() {
        allowRefresh = true
        (context as BaseActivity<*>).addLaunch(success = {
            while (allowRefresh) {
                delay(500)
                if (allowRefresh.not())return@addLaunch
                withContext(Dispatchers.Main){
                    defaultBinding?.llRoot?.apply {
                        removeAllViews()
                        addTest("买量用户状态:${UIManager.isBuyUser()}") {
                        }
                        addTest("cloak:${UIManager.cloakValue}") {
                            if (UIManager.cloakValue == "creamery") {
                                UIManager.cloakValue = "orgasm"
//                            it.text = "orgasm"
                            } else {
                                UIManager.cloakValue = "creamery"
//                            it.text = "creamery"
                            }
                        }
                        addTest("adjust:${CacheManager.adJustFrom}") {
                            if (CacheManager.adJustFrom == "Organic"){
                                CacheManager.adJustFrom = "买量"
                            }else{
                                CacheManager.adJustFrom = "Organic"
                            }
                        }
                        addTest("af:${CacheManager.afFrom}") {
                            if (CacheManager.afFrom == "Organic"){
                                CacheManager.afFrom = "买量"
                            }else{
                                CacheManager.afFrom = "Organic"
                            }
                        }
                        addEdittext("refer:${CacheManager.installRefer}") {
                            CacheManager.installRefer = it.text.toString().trim().substringAfter("refer:")
                        }
                        addEdittext("referConfig:${FirebaseConfig.referConfig}") {
                            FirebaseConfig.referConfig = it.text.toString().trim().substringAfter("referConfig:")
                        }

                        addEdittext("归因结果:${CacheManager.campaignId}") {
                            CacheManager.campaignId = it.text.toString().trim().substringAfter("归因结果:")
//                        it.text = "归因结果:${CacheManager.campaignId}"
                        }
                        addTest("全局视频限制开关 true为限制 不展示下载:${FirebaseConfig.switchOpenFilter1}") {
                            FirebaseConfig.switchOpenFilter1 =
                                FirebaseConfig.switchOpenFilter1.not()
//                        it.text = "全局视频限制开关 true为限制 不展示下载:${FirebaseConfig.switchOpenFilter1}"
                        }
                        addTest("网站限制list 命中则不展示下载:${toJson(FirebaseConfig.switchOpenFilterList)}") {

                        }
                        addTest("默认浏览器开关 true 允许展示pop :${FirebaseConfig.switchDefaultPop}") {
                            FirebaseConfig.switchDefaultPop = FirebaseConfig.switchDefaultPop.not()
//                        it.text = "默认浏览器开关 true 允许展示pop:${FirebaseConfig.switchDefaultPop}"
                        }
                        addTest("下载引导开关 true 允许展示pop:${FirebaseConfig.switchDownloadGuidePop}") {
                            FirebaseConfig.switchDownloadGuidePop =
                                FirebaseConfig.switchDownloadGuidePop.not()
//                        it.text = "下载引导开关 true 允许展示pop:${FirebaseConfig.switchDownloadGuidePop}"
                        }
                    }
                }
            }
        }, failBack = {})
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

    fun ViewGroup.addEdittext(str: String, block: (view: AppCompatEditText) -> Unit) {
        this.addView(AppCompatEditText(context).apply {
            setText(str)
            isAllCaps = false
            background = ContextCompat.getDrawable(APP.instance,R.drawable.shape_allow)
            setOnTouchListener(object :OnTouchListener{
                override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                    allowRefresh = false
                    return false
                }

            })
            setOnKeyListener { v, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                        block(this@apply)
                        addPop()
                        return@setOnKeyListener true
                    }
                    return@setOnKeyListener false
                }
        })
    }
}
