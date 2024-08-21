package com.boom.aiobrowser.tools.clean

import com.boom.aiobrowser.APP
import com.boom.aiobrowser.BuildConfig
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.getRegexForFile

object CleanConfig {
    //垃圾文件
    var junkFiles: MutableList<FilesData> = mutableListOf()
    //    volatile
    var downloadApks: MutableList<FilesData> = mutableListOf()
       //残留文件
    var residualFiles: MutableList<FilesData> = mutableListOf()
    var adFiles: MutableList<FilesData> = mutableListOf()
    val appInstalledPkgList = mutableListOf<String>()

    val filters = mutableListOf<String>()

    const val DATA_TYPE_JUNK = 0
    const val DATA_TYPE_APK = 1
    const val DATA_TYPE_RESIDUAL = 2
    const val DATA_TYPE_AD = 3


    fun getAllFiles():String{
        return ""
    }

    val genericFilterFolders: Array<String>
        get() = arrayOf(
            "Logs",
            "logs",
            "temp",
            "Temporary",
            "temporary"
        )

    val aggressiveFilterFolders: Array<String>
        get() = arrayOf(
            "supersonicads",
            "cache",
            "Analytics",
            "MiPushLog",
            "thumbnails?",
            "mobvista",
            "UnityAdsVideoCache",
            "LOST.DIR",
            ".Trash",
            "desktop.ini",
            "leakcanary",
            ".DS_Store",
            ".spotlight-V100",
            "fseventsd",
            "Bugreport",
            "bugreports",
            ".cache",
            "debug_log",
            "splashad",
        )

    fun initCleanConfig(){
        runCatching {
            filters.clear()
            val folders = mutableListOf<String>().apply {
                addAll(genericFilterFolders)
                addAll(aggressiveFilterFolders)
            }
            filters.add(".apk".getRegexForFile())
        }.onFailure {
            AppLogs.eLog(APP.instance.TAG,it.stackTraceToString())
        }
        initAppInstalledPkgList()
    }


    private fun initAppInstalledPkgList() {
        runCatching {
            appInstalledPkgList.clear()
            val applicationId = BuildConfig.APPLICATION_ID
            APP.instance.packageManager.getInstalledPackages(0).let { packageInfoList ->
                appInstalledPkgList.addAll(packageInfoList.map { it.packageName ?: "" }
                    .filter { it.isNotEmpty() && it != applicationId })
            }
        }.onFailure {
            AppLogs.eLog(APP.instance.TAG,it.printStackTrace().toString())
        }
    }

    fun clearAll(){
        junkFiles.clear()
        junkFiles.add(FilesData().apply {
            fileName = APP.instance.getString(R.string.app_log)
            imgId = R.mipmap.ic_clean_log
            tempList = mutableListOf()
        })
        junkFiles.add(FilesData().apply {
            fileName = APP.instance.getString(R.string.app_temp)
            imgId = R.mipmap.ic_clean_temp
            tempList = mutableListOf()
        })
        downloadApks.clear()
        residualFiles.clear()
        adFiles.clear()
    }
}