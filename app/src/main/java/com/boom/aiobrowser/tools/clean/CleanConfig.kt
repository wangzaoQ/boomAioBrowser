package com.boom.aiobrowser.tools.clean

import com.boom.aiobrowser.data.FilesData
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
        filters.clear()
        val folders = mutableListOf<String>().apply {
            addAll(genericFilterFolders)
            addAll(aggressiveFilterFolders)
        }

        filters.add(".apk".getRegexForFile())
    }

    fun clearAll(){
        junkFiles.clear()
        downloadApks.clear()
        residualFiles.clear()
        adFiles.clear()
    }
}