package com.boom.aiobrowser.tools.clean

import com.boom.aiobrowser.APP
import com.boom.aiobrowser.BuildConfig
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.AppInfo
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.tools.AppLogs

object CleanConfig {
    //垃圾文件
    var junkFiles: MutableList<FilesData> = mutableListOf()
    //    volatile
    var apkFiles: MutableList<FilesData> = mutableListOf()
       //残留文件
    var residualFiles: MutableList<FilesData> = mutableListOf()
    var adFiles: MutableList<FilesData> = mutableListOf()
    val appInstalledPkgList = mutableListOf<String>()

    val runAPPExtension by lazy { mutableListOf("xiaomi","miui","huawei","${BuildConfig.APPLICATION_ID}") }
    val imgExtension by lazy { mutableListOf("jpg", "png", "jpeg", "bmp", "webp", "heic", "heif", "gif") }
    val videoExtension by lazy { mutableListOf("mp4", "mkv", "flv", "webm", "avi", "3gp", "mov", "m4v", "3gpp") }
    val audioExtension by lazy { mutableListOf("mp3", "wav", "wma", "ogg", "m4a", "opus", "flac", "aac", "mid") }
    val docExtension by lazy { mutableListOf("txt", "rtf", "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "pptm") }
    val zipExtension by lazy { mutableListOf("zip") }

    val runningAppInfo = mutableListOf<AppInfo>()

    var largeFiles: MutableList<FilesData> = mutableListOf()
    var imageFiles: MutableList<FilesData> = mutableListOf()
    var videoFiles: MutableList<FilesData> = mutableListOf()
    var audioFiles: MutableList<FilesData> = mutableListOf()
    var zipFiles: MutableList<FilesData> = mutableListOf()
    var documentsFiles: MutableList<FilesData> = mutableListOf()



    const val DATA_TYPE_JUNK = 0
    const val DATA_TYPE_APK = 1
    const val DATA_TYPE_RESIDUAL = 2
    const val DATA_TYPE_AD = 3


    fun getAllFiles():String{
        return ""
    }

    fun initCleanConfig(){
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

    fun clearFileConfig(){
        largeFiles.clear()
        imageFiles.clear()
        videoFiles.clear()
        audioFiles.clear()
        zipFiles.clear()
        documentsFiles.clear()
    }

    fun clearCleanConfig(){
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
        apkFiles.clear()
        residualFiles.clear()
        adFiles.clear()
    }
}