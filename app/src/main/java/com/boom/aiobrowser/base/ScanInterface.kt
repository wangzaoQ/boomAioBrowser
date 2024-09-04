package com.boom.aiobrowser.base

import java.io.File

interface ScanInterface {


    fun  scanProgress(file : File)

    suspend fun scanStart()

    fun scanComplete(files: MutableList<File>)

}