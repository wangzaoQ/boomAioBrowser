package com.boom.aiobrowser

//import com.fast.newsnow.activity.NowStartActivity
// // import com.fast.newsnow.utils.logsi
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.boom.aiobrowser.tools.AppLogs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
//import org.greenrobot.eventbus.EventBus
import java.util.Stack

class BrowserLifeCycle : Application.ActivityLifecycleCallbacks {

    val stack = Stack<Activity>()
    var listAppActivity = Stack<Activity>()

    var TAG = "BrowserLifeCycle:"
//    var TAG = NowNewsADDataManager.TAG

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        AppLogs.dLog(TAG, "onActivityCreated() activity=" + activity + " listAppActivity.size=" + listAppActivity.size)
        stack.add(activity)
    }

    var startTime = 0L
    override fun onActivityStarted(activity: Activity) {
        AppLogs.dLog(TAG, "onActivityStarted() activity=" + activity + " listAppActivity.size=" + listAppActivity.size)
        if (listAppActivity.contains(activity).not()) {
            listAppActivity.add(activity)
        }
        if (startTime == 0L){
            startTime = System.currentTimeMillis()
        }
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {
        listAppActivity.remove(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        stack.remove(activity)
    }

}