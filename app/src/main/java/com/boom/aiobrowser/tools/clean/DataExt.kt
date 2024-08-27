package com.boom.aiobrowser.tools.clean

import android.os.Build
import com.boom.aiobrowser.R
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


