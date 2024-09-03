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

class Scan1(var parentDirectory: File,var waitTime:Long,var onProgress: (file:File) -> Unit,var onComplete: (files: MutableList<File>) -> Unit = {},var tag:String="Clean") :ScanInterface {
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
            if (file.isDirectory && files.size<10000) {
//                var tempFile = File("/storage/emulated/0/Android/data/air.com.eprize.nylottery.app.NYLotteryApp/cache")
//                if (tempFile.name.equals("cache", ignoreCase = true) ){
//
//                }else{
//
//                }
                if (tag == "Cache"){
                    deferredResults.add(async {
                        scanDirectory(file)
                    })
                }else{
                    if (file.absolutePath.contains("Android/data").not()){
                        deferredResults.add(async {
                            scanDirectory(file)
                        })
                    }
                }
            } else {
//                Logger.writeLog(APP.instance,"扫描出的文件:${file.absolutePath}")
                if (tag == "Cache"){
                    if (file.absolutePath.contains("cache",true)){
                        AppLogs.dLog(TAG,"扫描出的文件夹:${file.absolutePath} 当前线程:${Thread.currentThread()}")
                        onProgress.invoke(file)
                    }
                }else{
                    AppLogs.dLog(TAG,"扫描出的文件:${file.absolutePath} 当前线程:${Thread.currentThread()}")
                    files.add(file)
                    onProgress.invoke(file)
                }
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