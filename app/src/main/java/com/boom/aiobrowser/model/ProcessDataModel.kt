package com.boom.aiobrowser.model

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.FileUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.data.AppInfo
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.clean.CleanToolsManager.getInstallTime
import com.boom.aiobrowser.tools.clean.CleanConfig.runAPPExtension
import com.boom.aiobrowser.tools.clean.CleanConfig.runningAppInfo
import com.boom.aiobrowser.tools.clean.hasConstants

class ProcessDataModel : BaseDataModel() {
    var processListLiveData = MutableLiveData<MutableList<AppInfo>>()


    fun getProcessData(){
        loadData(loadBack = {
            getOtherProcess()
            processListLiveData.postValue(runningAppInfo)
        }, failBack = {},1)
    }

    fun getOtherProcess(){
        synchronized("getOtherProcess"){
            runningAppInfo.clear()
            val pm: PackageManager = APP.instance.getPackageManager()
            for (lp in pm.getInstalledPackages(0)) {
                if ((ApplicationInfo.FLAG_SYSTEM and lp.applicationInfo.flags) == 0 && (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP and lp.applicationInfo.flags) == 0 && (ApplicationInfo.FLAG_STOPPED and lp.applicationInfo.flags) == 0) {
                    // 第三方正在运行的 app 进程信息  更多信息查询 PackageInfo 类
                    if (lp.packageName.hasConstants(runAPPExtension))continue
                    AppLogs.dLog(
                        "getOtherProcess", "appName:${APP.instance.packageManager.getApplicationLabel(lp.applicationInfo)}"+"packageName=" + lp.packageName
                                + "  processName=" + lp.applicationInfo.processName
                    )
                    var info = AppInfo().apply {
                        name = APP.instance.packageManager.getApplicationLabel(lp.applicationInfo).toString()
                        pkg = lp.applicationInfo.packageName ?: ""
                        icon = APP.instance.packageManager.getApplicationIcon(lp.packageName)
                        size = FileUtils.getLength(lp.applicationInfo.dataDir) + FileUtils.getLength(lp.applicationInfo.sourceDir)
                        installTime = getInstallTime(lp.applicationInfo.packageName ?: "")
                    }
                    runningAppInfo.add(info)
                }
            }
        }
    }
}