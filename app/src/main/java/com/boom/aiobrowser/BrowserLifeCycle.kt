package com.boom.aiobrowser

//import com.fast.newsnow.activity.NowStartActivity
// // import com.fast.newsnow.utils.logsi
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.boom.aiobrowser.ad.AioADDataManager
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.WakeManager
import com.boom.aiobrowser.ui.activity.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
//import org.greenrobot.eventbus.EventBus
import java.util.Stack

class BrowserLifeCycle : Application.ActivityLifecycleCallbacks {

    val stack = Stack<Activity>()

    private var cancelJob: Job? = null
    @Volatile
    var isBackstage = false

    var count = 0

    // 0 开屏广告 1 插屏广告 2 激励广告
    var adScreenType = -1

//    var TAG = NowNewsADDataManager.TAG

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        stack.add(activity)
        AppLogs.dLog(APP.instance.TAG, "onActivityCreated() activity=" + activity + " stack.size=" + stack.size)
    }

    var startTime = 0L
    override fun onActivityStarted(activity: Activity) {
        count++
        if (startTime == 0L){
            startTime = System.currentTimeMillis()
        }
        cancelJob?.cancel()
        var wakeUtils = WakeManager()
        var allowContinue = false
        runCatching {
            allowContinue = wakeUtils.isScreenOn()&& wakeUtils.isDeviceLocked().not()
        }
        if (allowContinue.not()){
            return
        }

        if (isBackstage) {
            AppLogs.dLog(APP.instance.TAG,"onActivityStarted_isBackstage:${isBackstage}")
            isBackstage = false
//            activity.startActivity(Intent(activity, NowStartActivity::class.java))
            if (AioADDataManager.adAllowShowOpen()){
                val temp = mutableListOf<Activity>()
                stack.forEach {
                    if ((it is BaseActivity<*>).not()) {
                        temp.add(it)
                    }
                }
                temp.forEach {
                    it.finish()
                }
                AppLogs.dLog(APP.instance.TAG,"启动开屏")
                if (APP.instance.allowShowStart) activity.startActivity(Intent(activity,MainActivity::class.java))
            }
        }
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {
        count--
        AppLogs.dLog(APP.instance.TAG, "onActivityStopped() activity=" + activity + "count:"+count)
        if (0 >= count) {
            AioADDataManager.setADDismissTime()
            cancelJob?.cancel()
            cancelJob = CoroutineScope(Dispatchers.IO).launch{
                startTime = 0L
                if (AioADDataManager.adFilter1().not()){
                    while (AioADDataManager.adAllowShowOpen().not()){
                        delay(1000)
                    }
                }

                isBackstage = true
                runCatching {
                    val temp = mutableListOf<Activity>()
                    stack.iterator().forEach {
                        if ((it is BaseActivity<*>).not()) {
                            temp.add(it)
                        }
                    }
                    temp.forEach {
                        it.finish()
                    }
                }
            }
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        stack.remove(activity)
        AppLogs.dLog(APP.instance.TAG, "onActivityDestroyed() activity=" + activity + " stack.size=" + stack.size)
    }

}