package com.boom.aiobrowser.tools.clean

import android.os.Environment
import com.boom.aiobrowser.tools.clean.CleanConfig.apkExtension
import com.boom.aiobrowser.tools.clean.CleanConfig.appInstalledPkgList
import com.boom.aiobrowser.tools.clean.CleanConfig.audioExtension
import com.boom.aiobrowser.tools.clean.CleanConfig.docExtension
import com.boom.aiobrowser.tools.clean.CleanConfig.imgExtension
import com.boom.aiobrowser.tools.clean.CleanConfig.videoExtension
import com.boom.aiobrowser.tools.clean.CleanConfig.zipExtension
import java.io.File
import java.util.Locale

object FileFilter {

    fun isResidual(file: File): Boolean {
        return null != file.parentFile
                && null != file.parentFile?.parentFile
                && "data" == file.parentFile?.name
                && "Android" == file.parentFile?.parentFile?.name
                && ".nomedia" != file.name
                && !isInstalled(file.name)
    }

    fun isApks(file: File): Boolean {
        return file.absolutePath.isApk()
    }

    fun isADFile(file: File): Boolean {
        if (file.isFile && "ad" == file.parentFile?.name){
            return true
        }
        return false
    }

    fun isLogFile(file: File): Boolean = file.absolutePath.endsWith(".log", true)
    fun isTxtFile(file: File): Boolean = file.absolutePath.endsWith(".txt", true)
    fun isLogCatFile(file: File): Boolean = file.absolutePath.endsWith(".logcat", true)

    fun isTmpFile(file: File): Boolean = file.absolutePath.endsWith(".tmp", true)
    fun isTemporaryFile(file: File): Boolean {
        val fileName = file.name.lowercase(Locale.getDefault())
        return fileName.endsWith(".tmp") || fileName.endsWith(".temp") || fileName.endsWith(".swp") ||
                fileName.endsWith("~") || fileName.endsWith(".bak")
    }

    fun isLargeFile(file: File): Boolean {
        if (file.isFile && file.length()>5*1024*1024){
            return true
        }
        return false
    }

    fun isImagesFile(file: File): Boolean {
        if (file.absolutePath.isImage()){
            return true
        }
        return false
    }

    fun isVideoFile(file: File): Boolean {
        if (file.absolutePath.isVideo()){
            return true
        }
        return false
    }
    fun isAudioFile(file: File): Boolean {
        if (file.absolutePath.isAudio()){
            return true
        }
        return false
    }
    fun isZipFile(file: File): Boolean {
        if (file.absolutePath.isZip()){
            return true
        }
        return false
    }
    fun isDocFile(file: File): Boolean {
        if (file.absolutePath.isDoc()){
            return true
        }
        return false
    }
    fun isInstalled(fileName: String): Boolean = appInstalledPkgList.contains(fileName)


    fun String.isImage() = imgExtension.any { endsWith(it, true) }
    fun String.isVideo() = videoExtension.any { endsWith(it, true) }
    fun String.isAudio() = audioExtension.any { endsWith(it, true) }
    fun String.isDoc() = docExtension.any { endsWith(it, true) }
    fun String.isZip() = zipExtension.any { endsWith(it, true) }
    fun String.isApk(): Boolean {
        return apkExtension.any { endsWith(it, true) }
    }
}