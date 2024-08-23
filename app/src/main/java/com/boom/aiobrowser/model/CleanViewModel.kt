package com.boom.aiobrowser.model

import androidx.lifecycle.MutableLiveData
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.clean.CleanConfig
import com.boom.aiobrowser.tools.clean.CleanConfig.adFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.downloadApks
import com.boom.aiobrowser.tools.clean.CleanConfig.junkFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.residualFiles
import com.boom.aiobrowser.tools.clean.FileFilter.isADFile
import com.boom.aiobrowser.tools.clean.FileFilter.isDownloadApks
import com.boom.aiobrowser.tools.clean.FileFilter.isLogCatFile
import com.boom.aiobrowser.tools.clean.FileFilter.isLogFile
import com.boom.aiobrowser.tools.clean.FileFilter.isResidual
import com.boom.aiobrowser.tools.clean.FileFilter.isTmpFile
import com.boom.aiobrowser.tools.clean.Scan1
import com.boom.aiobrowser.tools.clean.formatSize
import kotlinx.coroutines.InternalCoroutinesApi
import java.io.File

class CleanViewModel : BaseDataModel() {
    var currentPathLiveData = MutableLiveData<String>()
    var currentSizeLiveData = MutableLiveData<String>()

    var allFiles = mutableListOf<File>()
    var allFilesLength = 0L

    /**
     * 扫描内存
     */
    fun startScan(parentDir: File, onScanPath: (file: File) -> Unit = {}, onComplete: () -> Unit = {}) {
        CleanConfig.clearAll()
        loadData(loadBack={
            Scan1(parentDir, onProgress = {
                detailMemoryFile(it,onScanPath)
            }, onComplete = {
                onComplete.invoke()
            }).scanStart()
        }, failBack = {},1)
    }

    @OptIn(InternalCoroutinesApi::class)
    private fun filter(file: File?): Boolean {
        try {
            if (file == null || file.exists().not()){
                AppLogs.dLog(TAG,"file 错误")
                return false
            }
            val fileName = file.name
            val filePath = file.absolutePath
            val fileSize = file.length()
            if (file.isDirectory && "cache" == fileName.lowercase()){
                AppLogs.dLog(TAG,"file 为文件夹 cache:${"cache" == fileName.lowercase()}")
            }

//            val filtersIterator = filters.iterator()
//            while (filtersIterator.hasNext()){
//                val filterItem = filtersIterator.next()
//                if (filePath.lowercase().matches(filterItem.toRegex())){
                    if (isResidual(file)) {
                        kotlinx.coroutines.internal.synchronized(residualFiles) {
                            residualFiles.add(FilesData().apply {
                                this.fileName = fileName
                                this.filePath = filePath
                                this.fileSize = fileSize
                            })
                        }
                        return true
                    }else if (isDownloadApks(file)){
                        kotlinx.coroutines.internal.synchronized(downloadApks) {
                            downloadApks.add(FilesData().apply {
                                this.fileName = fileName
                                this.filePath = filePath
                                this.fileSize = fileSize
                            })
                        }
                        return true
                    }else if (isLogFile(file) || isLogCatFile(file)){
                        kotlinx.coroutines.internal.synchronized(junkFiles) {
                            junkFiles.get(0).tempList?.add(FilesData().apply {
                                this.fileName = fileName
                                this.filePath = filePath
                                this.fileSize = fileSize
                            })
                        }
                        return true
                    }else if (isTmpFile(file)){
                        kotlinx.coroutines.internal.synchronized(junkFiles) {
                            junkFiles.get(1).tempList?.add(FilesData().apply {
                                this.fileName = fileName
                                this.filePath = filePath
                                this.fileSize = fileSize
                            })
                        }
                        return true
                    }else if (isADFile(file)){
                        kotlinx.coroutines.internal.synchronized(adFiles) {
                            adFiles.add(FilesData().apply {
                                this.fileName = fileName
                                this.filePath = filePath
                                this.fileSize = fileSize
                            })
                        }
                        return true
                    }
//                }
//            }

        } catch (e: Exception) {
            return false
        }
        return false
    }

    private fun detailMemoryFile(file: File?,onScanPath: (file: File) -> Unit = {}) {
        if (null == file) return
        currentPathLiveData.postValue(file.absolutePath)
        if (filter(file)){
            allFiles.add(file)
            allFilesLength+=file.length()
            currentSizeLiveData.postValue(allFilesLength.formatSize())
        }
    }
}