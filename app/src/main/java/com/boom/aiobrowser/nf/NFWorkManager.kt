package com.boom.aiobrowser.nf

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.firebase.FirebaseConfig
import com.boom.aiobrowser.tools.AppLogs
import java.util.concurrent.TimeUnit

object NFWorkManager {

    fun startNF(){
        AppLogs.dLog(NFManager.TAG,"WorkManager start")
        WorkManager.getInstance(APP.instance).cancelAllWork()
        if (APP.isDebug){
            start(APP.instance,NormalNewsWork::class.java,NFEnum.NF_NEWS.menuName,1*60*1000,15*60*1000)
            start(APP.instance,EditorNewsWork::class.java,NFEnum.NF_EDITOR.menuName,2*60*1000,15*60*1000)
            start(APP.instance,LocalNewsWork::class.java,NFEnum.NF_LOCAL.menuName,3*60*1000,15*60*1000)
            start(APP.instance,HotNewsWork::class.java,NFEnum.NF_HOT.menuName,30*60*1000,15*60*1000)
            start(APP.instance,TrendNewsWork::class.java,NFEnum.NF_TREND.menuName,30*60*1000,15*60*1000)
            start(APP.instance,NewUserNewsWork::class.java,NFEnum.NF_NEW_USER.menuName,5*60*1000,15*60*1000)
        }else{
            start(APP.instance,NormalNewsWork::class.java,NFEnum.NF_NEWS.menuName,15*60*1000,15*60*1000)
            start(APP.instance,EditorNewsWork::class.java,NFEnum.NF_EDITOR.menuName,10*60*1000,60*60*1000)
            start(APP.instance,LocalNewsWork::class.java,NFEnum.NF_LOCAL.menuName,20*60*1000,60*60*1000)
            start(APP.instance,HotNewsWork::class.java,NFEnum.NF_HOT.menuName,30*60*1000,3*60*60*1000)
            start(APP.instance,TrendNewsWork::class.java,NFEnum.NF_TREND.menuName,30*60*1000,60*60*1000)
            start(APP.instance,NewUserNewsWork::class.java,NFEnum.NF_NEW_USER.menuName,40*60*1000,60*60*1000)
        }
    }

    fun start(context: Context, clazz: Class<out BaseNotifyWork>, workTag:String, delayMilli: Long, time:Long) {
        val constraints: Constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workUniqueName ="${workTag}_Name"

        val workRequest =
            PeriodicWorkRequest.Builder(clazz, time, TimeUnit.MILLISECONDS)
                //设置重试退避策略
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .setInitialDelay(delayMilli, TimeUnit.MILLISECONDS) //延迟10秒执行0
                .setConstraints(constraints) //设置触发条件
//            .setInputData(inputData) //传输数据
                .addTag(workTag) //设置tag标签
                .build()

        //开始执行任务
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(workUniqueName, ExistingPeriodicWorkPolicy.KEEP, workRequest);
    }
}