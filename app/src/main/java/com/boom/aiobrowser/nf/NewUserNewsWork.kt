package com.boom.aiobrowser.nf

import android.content.Context
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.tools.AppLogs

class NewUserNewsWork(appContext: Context, params: WorkerParameters) : BaseNotifyWork(appContext, params) {
    override suspend fun doWork(): Result {
        AppLogs.dLog(NFManager.TAG,"NewUserNewsWork doWork")
        if (System.currentTimeMillis() > getStartTime() && System.currentTimeMillis() < getEndTime()) {
            NFShow.showNewsNFFilter(NFEnum.NF_NEW_USER,NFManager.FROM_WORK)
        }
        return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return super.getForegroundInfo()
    }

}