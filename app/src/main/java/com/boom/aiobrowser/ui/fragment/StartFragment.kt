package com.boom.aiobrowser.ui.fragment

import android.animation.ValueAnimator
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADDataManager
import com.boom.aiobrowser.ad.AioADShowManager
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.databinding.BrowserFragmentStartBinding
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.ui.UrlConfig
import com.boom.aiobrowser.ui.activity.MainActivity
import com.boom.aiobrowser.ui.activity.WebActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class StartFragment :BaseFragment<BrowserFragmentStartBinding>() {
    override fun startLoadData() {

    }

    override fun setListener() {
        fBinding.btnBrowser.setOneClick {
            toMain("点击Start")
        }
        fBinding.apply {
            tvPrivate.setOneClick {
                startActivity(Intent(rootActivity, WebActivity::class.java).putExtra("url",
                    UrlConfig.PRIVATE_URL).putExtra("title",getString(R.string.app_private_policy)))
            }
            tvTermsService.setOneClick {
                startActivity(Intent(rootActivity, WebActivity::class.java).putExtra("url",
                    UrlConfig.SERVICE_URL).putExtra("title",getString(R.string.app_terms_of_service)))
            }
        }
    }

    private fun toMain(tag: String) {
        AppLogs.dLog(fragmentTAG,tag)
        if (isAdded.not())return
        fBinding.llLoadingRoot.visibility = View.VISIBLE
        fBinding.rlStart.visibility = View.GONE
        startPb(0, 100, 10000, update = {
            if (AioADDataManager.getLaunchData() == null) {
                fBinding.progress.progress = it
            } else {
                showEnd()
            }
        }, complete = {
            AppLogs.dLog(fragmentTAG, "10秒内没拿到ad")
            adLoadComplete(AioADDataManager.AD_SHOW_TYPE_FAILED)
        })
    }

    private fun adLoadComplete(loadStatus:String) {
        cancelPb()
        if (isAdded.not())return
        if (rootActivity is MainActivity){
            (rootActivity as MainActivity).hideStart()
            CacheManager.isFirstStart = false
        }
    }

    private fun showEnd() {
        fBinding.progress.visibility = View.VISIBLE
        endProgress(end = {
            rootActivity.addLaunch(success = {
                while (!rootActivity.getActivityStatus()) {
                    delay(100)
                }
                AppLogs.dLog(fragmentTAG, "开始加载广告逻辑")
                withContext(Dispatchers.Main) {
                    AppLogs.dLog(fragmentTAG, "条件1点击和展示是否超过限制:${AioADDataManager.adFilter1()}")
                    if (AioADDataManager.adFilter1().not()) {
                        AioADShowManager(rootActivity,ADEnum.LAUNCH, tag = "开屏") {
                            adLoadComplete(it)
                        }.showScreenAD()
                    } else {
                        AppLogs.dLog(fragmentTAG, "不展示广告直接跳转")
                        adLoadComplete(AioADDataManager.AD_SHOW_TYPE_FAILED)
                    }
                }
            }, failBack = {
                AppLogs.dLog(fragmentTAG, "展示广告逻辑异常 error:${it}")
                adLoadComplete(AioADDataManager.AD_SHOW_TYPE_FAILED)
            })
        })
    }

    private fun endProgress(end: () -> Unit = {}) {
        cancelPb()
        val duration = Math.max((100 - fBinding.progress.progress) * 10L, 500L)
        startPb(fBinding.progress.progress, 100, duration, update = {
            fBinding.progress.progress = it
        }, complete = {
            AppLogs.dLog(fragmentTAG, "进度条加载完成")
            end.invoke()
        })
    }

    override fun setShowView() {
        fBinding.btnBrowser.isEnabled = fBinding.btnCheck.isChecked
        fBinding.btnCheck.setOnCheckedChangeListener { compoundButton, b ->
            fBinding.btnBrowser.isEnabled = fBinding.btnCheck.isChecked
        }
        if (CacheManager.isFirstStart){
            fBinding.rlStart.visibility = View.VISIBLE
            fBinding.llLoadingRoot.visibility = View.GONE
        }else{
            toMain("非首次")
        }
        ADEnum.values().forEach {
            it.adLoadStatus = AioADDataManager.LOAD_STATUS_START
        }
        AioADDataManager.preloadAD(ADEnum.LAUNCH,"app启动")
    }

    var dataIntent :Intent?=null


    fun updateUI(intent: Intent){
        dataIntent = intent
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BrowserFragmentStartBinding {
        return BrowserFragmentStartBinding.inflate(inflater)
    }


    var pbAnimal: ValueAnimator? = null

    fun cancelPb() {
        firstComplete = true
        runCatching {
            pbAnimal?.cancel()
            pbAnimal = null
        }.onFailure {
            AppLogs.eLog(fragmentTAG,it.stackTraceToString())
        }
    }

    var firstComplete = true

    fun startPb(cur: Int = 0, max: Int = 100, time: Long, update: (value: Int) -> Unit = {}, complete: () -> Unit = {}) {
        cancelPb()
        runCatching {
            pbAnimal = ValueAnimator.ofInt(cur, max)
            pbAnimal?.run {
                duration = time
                interpolator = LinearInterpolator()
                addUpdateListener {
                    val value = it.animatedValue as Int
                    update.invoke(value)
                    if (value >= max && firstComplete){
                        firstComplete = false
                        AppLogs.dLog(fragmentTAG,"complete")
                        complete.invoke()
                    }
                }
            }
            pbAnimal?.start()
        }.onFailure {
            AppLogs.eLog(fragmentTAG,it.stackTraceToString())
            complete.invoke()
        }
    }
}