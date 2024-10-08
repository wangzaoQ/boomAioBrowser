package com.boom.aiobrowser.tools.clean

import com.blankj.utilcode.util.FileUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.FileManageData.Companion.FILE_TYPE_APKS
import com.boom.aiobrowser.data.FileManageData.Companion.FILE_TYPE_DOCUMENTS
import com.boom.aiobrowser.data.FileManageData.Companion.FILE_TYPE_DOWNLOADS
import com.boom.aiobrowser.data.FileManageData.Companion.FILE_TYPE_IMAGES
import com.boom.aiobrowser.data.FileManageData.Companion.FILE_TYPE_LARGE_FILE
import com.boom.aiobrowser.data.FileManageData.Companion.FILE_TYPE_MUSIC
import com.boom.aiobrowser.data.FileManageData.Companion.FILE_TYPE_OTHER
import com.boom.aiobrowser.data.FileManageData.Companion.FILE_TYPE_VIDEOS
import com.boom.aiobrowser.data.FileManageData.Companion.FILE_TYPE_ZIP
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.tools.BigDecimalUtils
import com.boom.aiobrowser.tools.clean.CleanConfig.apkFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.audioFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.documentsFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.downloadFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.imageFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.largeFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.recentFiles
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
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.log10


fun Long.formatSize(): String {
    // 定义单位数组，从 Bytes 到 PB（Petabytes）
    val units = arrayOf("B", "KB", "MB", "GB", "TB", "PB")

    // 如果大小为0，直接返回 "0 Bytes"
    if (this < 1024) return "$this B"

    // 将字节大小转换为 BigDecimal
    val size = BigDecimal(this)

    // 计算需要使用的单位级别（digitGroups）
    val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()

    // 计算转换后的值（使用 BigDecimal 计算以保持精度）
//    BigDecimalUtils.div()
    val divisor = BigDecimal(1024).pow(digitGroups)
    val sizeInUnit = size.divide(divisor, 2, RoundingMode.HALF_UP)

    // 格式化输出，附加相应的单位
    return "${sizeInUnit.toPlainString()} ${units[digitGroups]}"
}

fun Long.getFileSize(time:Int):Long{
   var data =  this*time*1000
   return BigDecimalUtils.div("${data}","8").toLong()
}


fun Long.formatLength(): String {
    // 定义单位数组，从 Bytes 到 PB（Petabytes）
    val units = arrayOf("B", "KB", "MB", "GB", "TB", "PB")

    // 如果大小为0，直接返回 "0 Bytes"
    if (this < 1024) return "$this B"

    // 将字节大小转换为 BigDecimal
    val size = BigDecimal(this)

    // 计算需要使用的单位级别（digitGroups）
    val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()

    // 计算转换后的值（使用 BigDecimal 计算以保持精度）
//    BigDecimalUtils.div()
    val divisor = BigDecimal(1024).pow(digitGroups)
    val sizeInUnit = size.divide(divisor, 2, RoundingMode.HALF_UP)

    return "${sizeInUnit.toPlainString()} ${units[digitGroups]}"
//    // 格式化输出，附加相应的单位
//    return if (sizeInUnit.toInt() == 0){
//        APP.instance.getString(R.string.app_loading_content)
//    }else{
//        if (needUnit) "${sizeInUnit.toPlainString()} ${units[digitGroups]}" else "${sizeInUnit.toPlainString()}"
//    }
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

fun getSizeByType(type: Int):Long{
    var allLength = 0L
    var list = getListByType(type)
    list.forEach {
        allLength+=it.fileSize?:0L
    }
    return allLength
}

fun getListByType(type: Int) :MutableList<FilesData>{
    var list :MutableList<FilesData>
    when (type) {
        FILE_TYPE_DOWNLOADS -> {
            list = downloadFiles
        }
        FILE_TYPE_LARGE_FILE -> {
            list = largeFiles
        }
        FILE_TYPE_IMAGES -> {
            list = imageFiles
        }
        FILE_TYPE_VIDEOS -> {
            list = videoFiles
        }
        FILE_TYPE_APKS -> {
            list = apkFiles
        }
        FILE_TYPE_MUSIC -> {
            list = audioFiles
        }
        FILE_TYPE_ZIP -> {
            list = zipFiles
        }
        FILE_TYPE_DOCUMENTS -> {
            list = documentsFiles
        }
        FILE_TYPE_OTHER->{
            list = recentFiles
        }
        else -> {
            list = mutableListOf()
        }
    }
    return list
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


