package com.boom.aiobrowser.nf

import android.content.Context
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.tools.AppLogs

class NormalNewsWork(appContext: Context, params: WorkerParameters) : BaseNotifyWork(appContext, params) {
    override suspend fun doWork(): Result {
        AppLogs.dLog(NFManager.TAG,"NormalNewsWork doWork")
        if (System.currentTimeMillis() > getStartTime() && System.currentTimeMillis() < getEndTime()) {
            NFShow.showNewsNFFilter(NFEnum.NF_NEWS)
        }
        return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return super.getForegroundInfo()
    }

}