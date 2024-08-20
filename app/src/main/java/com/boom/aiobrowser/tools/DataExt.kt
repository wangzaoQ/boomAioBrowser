package com.boom.aiobrowser.tools

import kotlin.math.log10

fun Long.formatSize(): String {
    if (this <= 0) return "0"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(this.toDouble()) / log10(1024.0)).toInt()
    return String.format("%.1f %s", this / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}

fun String.getRegexForFile(): String = ".+${this.replace(".", "\\.")}$".lowercase()
