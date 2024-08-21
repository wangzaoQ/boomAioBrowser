package com.boom.aiobrowser.tools

import kotlin.math.log10

fun Long.formatSize(): String {
    if (this <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(this.toDouble()) / log10(1024.0)).toInt()
    var size = String.format("%.1f %s", this / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
    if (size == "0"){
        size = "0 B"
    }
    return size
}

fun String.getRegexForFile(): String = ".+${this.replace(".", "\\.")}$".lowercase()
