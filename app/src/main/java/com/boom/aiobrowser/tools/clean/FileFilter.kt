package com.boom.aiobrowser.tools.clean

import com.boom.aiobrowser.tools.clean.CleanConfig.appInstalledPkgList
import java.io.File

object FileFilter {

    fun isResidual(file: File): Boolean {
        return null != file.parentFile
                && null != file.parentFile?.parentFile
                && "data" == file.parentFile?.name
                && "Android" == file.parentFile?.parentFile?.name
                && ".nomedia" != file.name
                && !isInstalled(file.name)
    }

    fun isDownloadApks(file: File): Boolean {
        return file.isFile && file.absolutePath.endsWith(".apk", true)
    }

    fun isJunkFile(file: File): Boolean {
        return isLogFile(file) || isTmpFile(file)
    }

    fun isADFile(file: File): Boolean {
        if (file.isFile && "ad" == file.parentFile?.name){
            return true
        }
        return false
    }

    fun isLogFile(file: File): Boolean = file.absolutePath.endsWith(".log", true)

    fun isTmpFile(file: File): Boolean = file.absolutePath.endsWith(".tmp", true)


    fun isInstalled(fileName: String): Boolean = appInstalledPkgList.contains(fileName)

}