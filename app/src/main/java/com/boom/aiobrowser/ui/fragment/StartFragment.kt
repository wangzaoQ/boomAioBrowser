package com.boom.aiobrowser.ui.fragment

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADDataManager
import com.boom.aiobrowser.ad.AioADShowManager
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.databinding.BrowserFragmentStartBinding
import com.boom.aiobrowser.nf.NFManager
import com.boom.aiobrowser.nf.NFShow
import com.boom.aiobrowser.point.AD_POINT
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.other.UrlConfig
import com.boom.aiobrowser.tools.PointsManager
import com.boom.aiobrowser.tools.SubscribeManager
import com.boom.aiobrowser.tools.UIManager
import com.boom.aiobrowser.tools.jobCancel
import com.boom.aiobrowser.ui.activity.MainActivity
import com.boom.aiobrowser.ui.activity.WebActivity
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class StartFragment :BaseFragment<BrowserFragmentStartBinding>() {
    override fun startLoadData() {

    }

    var clickBtn = false

    override fun setListener() {
        fBinding.btnBrowser.setOneClick {
            APP.instance.isClickStart = true
            PointEvent.posePoint(PointEventKey.launch_page_start)
            clickBtn = true
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
    @Volatile
    var currentTime = 0L

    var job :Job?=null
    private fun toMain(tag: String) {
        AppLogs.dLog(fragmentTAG,tag)
        if (isAdded.not())return
        toLoading()
    }

    private fun toLoading() {
        var isFirst = CacheManager.isFirstStart
        fBinding.btnBrowser.visibility = View.GONE
        fBinding.root.postDelayed({
            fBinding.llLoadingRoot.visibility = View.VISIBLE
            job?.jobCancel()
            job = rootActivity.addLaunch(success = {
                while (true){
                    delay(1000)
                    currentTime+=1000
                }
            }, failBack = {})
            if (currentTime>11000){
                showEnd()
            }else{
                var isSubscribeMember = UIManager.isSpecialUsers()
                startPb(fBinding.progress.progress, 100, if (isSubscribeMember) 1000 else (11000-currentTime), update = {
                    if (isSubscribeMember){
                        fBinding.progress.progress = it
                    }else{
                        if (AioADDataManager.adAllowShowScreen()){
                            if (AioADDataManager.getCacheAD(ADEnum.LAUNCH_AD)== null){
                                var defaultAd = AioADDataManager.getCacheAD(ADEnum.DEFAULT_AD)
                                if (defaultAd!=null){
                                    showEnd()
                                }else{
                                    fBinding.progress.progress = it
                                }
                            }else{
                                showEnd()
                            }
                        }else{
                            var defaultAd = AioADDataManager.getCacheAD(ADEnum.DEFAULT_AD)
                            if (defaultAd!=null){
                                showEnd()
                            }else{
                                fBinding.progress.progress = it
                            }
                        }
                    }
                }, complete = {
                    AppLogs.dLog(fragmentTAG, "10秒内没拿到ad")
                    adLoadComplete(AioADDataManager.AD_SHOW_TYPE_FAILED)
                })
            }
        },if (isFirst) 0 else 1000)
    }

    private fun adLoadComplete(loadStatus:String) {
        AppLogs.dLog(fragmentTAG,"adLoadComplete 广告展示状态:${loadStatus}")
        APP.instance.allowShowStart = true
        cancelPb()
        if (isAdded.not())return
        var count = 0
        for ( i in 0 until APP.instance.lifecycleApp.stack.size){
            var activity = APP.instance.lifecycleApp.stack.get(i)
            if (activity is MainActivity && activity.isFinishing.not() && activity.isDestroyed.not()){
                count++
            }
        }
        val params = ConsentRequestParameters
            .Builder()
            .build()
        var consentInformation = UserMessagingPlatform.getConsentInformation(activity)
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    loadAD()
                }
            },
            { requestConsentError ->
                loadAD()
                AppLogs.eLog(AioADDataManager.TAG,"${requestConsentError.errorCode}: ${requestConsentError.message}")
            })

        if (count>1){
            AppLogs.dLog("testAPP","启动页 关闭")
            (rootActivity as MainActivity).hideStart(false)
        }else{
            (rootActivity as MainActivity).apply {
                (rootActivity as MainActivity).hideStart(true)
                AppLogs.dLog("testAPP","启动页 隐藏")
            }
        }
        cancelPb()
        runCatching {
            fBinding.animalStart.cancelAnimation()
        }
    }

    private fun loadAD() {
        AioADDataManager.preloadAD(ADEnum.INT_AD,"app启动")
        AioADDataManager.preloadAD(ADEnum.NATIVE_AD,"首页展示时")
        AioADDataManager.preloadAD(ADEnum.BANNER_AD_NEWS_DETAILS_TOP,"首页展示时")
        AioADDataManager.preloadAD(ADEnum.BANNER_AD_NEWS_DETAILS,"首页展示时")
    }

    var showEnd = false

    private fun showEnd() {
        fBinding.progress.visibility = View.VISIBLE
        endProgress(end = {
            showEnd = true
            rootActivity.addLaunch(success = {
                while (!rootActivity.getActivityStatus()) {
                    delay(100)
                }
                AppLogs.dLog(fragmentTAG, "开始加载广告逻辑")
                withContext(Dispatchers.Main) {
                    AppLogs.dLog(fragmentTAG, "条件1点击和展示是否超过限制:${AioADDataManager.adFilter1()}")
                    if (AioADDataManager.adFilter1().not()) {
                        var manager = AioADShowManager(rootActivity,ADEnum.LAUNCH_AD, tag = "开屏") {
                            adLoadComplete(it)
                        }
                        manager.showScreenAD(if (CacheManager.isFirstStart) AD_POINT.aobws_launch else AD_POINT.aobws_launch_other)
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
        showEnd = false
        APP.instance.firstInsertHomeAD = true
        APP.instance.isAllowNFPreload = false
        fBinding.btnBrowser.isEnabled = fBinding.btnCheck.isChecked
        fBinding.btnCheck.setOnCheckedChangeListener { compoundButton, b ->
            fBinding.btnBrowser.isEnabled = fBinding.btnCheck.isChecked
        }

        if (CacheManager.isFirstStart){
            fBinding.rlStart.visibility = View.VISIBLE
            fBinding.llLoadingRoot.visibility = View.GONE
            if (APP.isDebug){
//                ConfigPop(rootActivity).createPop()
            }
            CacheManager.firstTime = System.currentTimeMillis()
            APP.instance.isClickStart = false
        }else{
            fBinding.rlStart.visibility = View.GONE
            APP.instance.isClickStart = true
            clickBtn = true
            toMain("非首次")
        }
        ADEnum.values().forEach {
            it.adLoadStatus = AioADDataManager.LOAD_STATUS_START
        }
        AioADDataManager.getCacheAD(ADEnum.LAUNCH_AD)
        AioADDataManager.preloadAD(ADEnum.LAUNCH_AD,"app启动")
        PointEvent.session()
        PointEvent.posePoint(PointEventKey.nn_session)
        APP.instance.getWebConfig()
        NFManager.requestNotifyPermission(WeakReference((context as BaseActivity<*>)), onSuccess = {
            NFShow.showForegroundNF()
        }, onFail = {})

        if (CacheManager.isSubscribeMember){
            var subManager = SubscribeManager(successBack = {

            }, failBack = {

            })
            subManager.queryShop()
        }
        fBinding.animalStart.apply {
            setAnimation("start_data.json")
            playAnimation()
            addAnimatorListener(object : Animator.AnimatorListener{
                override fun onAnimationStart(p0: Animator) {
                    fBinding.rlRoot.setBackgroundColor(Color.BLACK)

                }

                override fun onAnimationEnd(p0: Animator) {
                }

                override fun onAnimationCancel(p0: Animator) {
                }

                override fun onAnimationRepeat(p0: Animator) {
                }

            })
        }
        fBinding.rlRoot.postDelayed({
            PointsManager.login()
            PointsManager.resetTempVip{}
        },0)
    }

    var dataIntent :Intent?=null


    fun updateUI(intent: Intent){
        dataIntent = intent
        APP.instance.allowShowStart = false
    }

    override fun onPause() {
        super.onPause()
        if (clickBtn.not())return
        if (isAdded&&isVisible && showEnd.not()){
            cancelPb()
        }
    }

    override fun onResume() {
        super.onResume()
        if (clickBtn.not())return
        if (isAdded&&isVisible && showEnd.not()){
            toLoading()
        }
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BrowserFragmentStartBinding {
        return BrowserFragmentStartBinding.inflate(inflater)
    }


    var pbAnimal: ValueAnimator? = null

    fun cancelPb() {
        job.jobCancel()
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