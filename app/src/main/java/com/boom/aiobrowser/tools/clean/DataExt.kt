package com.boom.aiobrowser.tools.clean

import android.os.Build
import com.blankj.utilcode.util.FileUtils
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.tools.clean.CleanConfig.apkFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.audioFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.documentsFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.downloadFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.imageFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.largeFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.videoFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.zipFiles
import com.boom.aiobrowser.tools.clean.FileFilter.isApk
import com.boom.aiobrowser.tools.clean.FileFilter.isAudio
import com.boom.aiobrowser.tools.clean.FileFilter.isDoc
import com.boom.aiobrowser.tools.clean.FileFilter.isImage
import com.boom.aiobrowser.tools.clean.FileFilter.isLargeFile
import com.boom.aiobrowser.tools.clean.FileFilter.isVideo
import com.boom.aiobrowser.tools.clean.FileFilter.isZip
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileTime
import kotlin.math.log10


fun Long.formatSize(): String {
    if (this <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(this.toDouble()) / log10(1024.0)).toInt()
    var size = String.format(
        "%.1f %s",
        this / Math.pow(1024.0, digitGroups.toDouble()),
        units[digitGroups]
    )
    if (size == "0") {
        size = "0 B"
    }
    return size
}

fun String.getRegexForFile(): String = ".+${this.replace(".", "\\.")}$".lowercase()
fun String.hasConstants(list: MutableList<String>) = list.any { contains(it, true) }

fun String.getDocImg(): Int {
    if (this.equals("txt", true)) {
        return R.mipmap.ic_txt
    } else if (this.equals("pdf", true)) {
        return R.mipmap.ic_pdf
    } else if (this.equals("zip", true)) {
        return R.mipmap.ic_zip
    } else {
        return R.mipmap.ic_default
    }
}

fun String.removeDataByFileExt(){
    var extension = FileUtils.getFileExtension(this)
    var list :MutableList<FilesData>?=null
    if (extension.isImage()){
        list = imageFiles
    }else if (extension.isApk()){
        list = apkFiles
    }else if (extension.isZip()){
        list = zipFiles
    }else if (extension.isDoc()){
        list = documentsFiles
    }else if (extension.isVideo()){
        list = videoFiles
    }else if (extension.isAudio()){
        list = audioFiles
    } else {
       var file =  File(this)
        if (isLargeFile(file)){
            list = largeFiles
        }else{
            list = downloadFiles
        }
    }
    var index = -1
    for (i in 0 until list.size){
        var data = list.get(i)
        if (data.filePath == this){
            index = i
            break
        }
    }
    if (index>=0){
        list.removeAt(index)
    }
}


