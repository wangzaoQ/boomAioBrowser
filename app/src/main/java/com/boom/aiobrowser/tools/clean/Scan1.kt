package com.boom.aiobrowser.tools.clean

import com.boom.aiobrowser.APP
import com.boom.aiobrowser.base.ScanInterface
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.Logger
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

class Scan1(var parentDirectory: File,var onProgress: (file:File) -> Unit,var onComplete: () -> Unit = {}) :ScanInterface {
    var TAG = "Scan1"
    var scanTime = 0L

    override fun scanProgress(file: File) {

    }

    override suspend fun scanStart() {
        scanTime = System.currentTimeMillis()
        AppLogs.dLog(TAG,"开始扫描")
        val files = scanDirectory(parentDirectory)
        withContext(Dispatchers.Main){
            AppLogs.dLog(TAG,"扫描耗时:${(System.currentTimeMillis()-scanTime)}")
            var middleTime = System.currentTimeMillis()-scanTime
            if (middleTime<5000){
                delay(5000-middleTime)
            }
            scanComplete(files)
        }
    }


    suspend fun scanDirectory(dir: File): MutableList<File> = coroutineScope {
        val deferredResults = mutableListOf<Deferred<MutableList<File>>>()

        val files = mutableListOf<File>()
        dir.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                deferredResults.add(async {
                    scanDirectory(file)
                })
            } else {
                AppLogs.dLog(TAG,"扫描出的文件:${file.absolutePath}")
                Logger.writeLog(APP.instance,"扫描出的文件:${file.absolutePath}")
                files.add(file)
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
        onComplete.invoke()
    }
}