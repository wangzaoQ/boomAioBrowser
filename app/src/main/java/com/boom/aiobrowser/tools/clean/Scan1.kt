package com.boom.aiobrowser.tools.clean

import com.boom.aiobrowser.APP
import com.boom.aiobrowser.base.ScanInterface
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.Logger
import com.boom.aiobrowser.tools.TimeManager
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

class Scan1(var parentDirectory: File,var waitTime:Long,var onProgress: (file:File) -> Unit,var onComplete: (files: MutableList<File>) -> Unit = {},var tag:String="清理") :ScanInterface {
    var TAG = ""
    var scanTime = 0L
    init {
        TAG = "Scan1:${tag}"
    }

    override fun scanProgress(file: File) {

    }

    override suspend fun scanStart() {
        scanTime = System.currentTimeMillis()
        AppLogs.dLog(TAG,"开始扫描")
        val files = scanDirectory(parentDirectory)
        AppLogs.dLog(TAG,"扫描耗时:${(System.currentTimeMillis()-scanTime)}")
        var middleTime = System.currentTimeMillis()-scanTime
        if (middleTime<waitTime){
            delay(waitTime-middleTime)
        }

        scanComplete(files)
    }

    suspend fun scanDirectory(dir: File): MutableList<File> = coroutineScope {
        val deferredResults = mutableListOf<Deferred<MutableList<File>>>()

        val files = mutableListOf<File>()
        dir.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                deferredResults.add(async {
                    scanDirectory(file)
                })
//                scanDirectory(file)
            } else {
                AppLogs.dLog(TAG,"扫描出的文件:${file.absolutePath} 当前线程:${Thread.currentThread()}")
//                Logger.writeLog(APP.instance,"扫描出的文件:${file.absolutePath}")
                files.add(file)
//                delay(1)
                onProgress.invoke(file)
            }
        }

        deferredResults.forEach {
            files.addAll(it.await())
        }

        return@coroutineScope files
    }

    override fun scanComplete(files: MutableList<File>) {
        AppLogs.dLog(TAG,"结束扫描")
        onComplete.invoke(files)
    }
}