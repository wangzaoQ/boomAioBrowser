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
        WorkManager.getInstance(APP.instance).cancelAllWork();
        var time = if (APP.isDebug){
           60*1000
        }else{
            (FirebaseConfig.pushData?.time_interval?:360)*60*1000
        }
        start(APP.instance,NormalNewsWork::class.java,NFEnum.NF_NEWS.menuName,time.toLong(),time.toLong())

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