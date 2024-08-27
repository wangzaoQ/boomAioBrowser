package com.boom.aiobrowser.model

import android.os.Environment
import androidx.lifecycle.MutableLiveData
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.clean.CleanConfig
import com.boom.aiobrowser.tools.clean.CleanConfig.adFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.apkFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.audioFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.documentsFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.imageFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.junkFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.largeFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.residualFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.videoFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.zipFiles
import com.boom.aiobrowser.tools.clean.FileFilter.isADFile
import com.boom.aiobrowser.tools.clean.FileFilter.isApks
import com.boom.aiobrowser.tools.clean.FileFilter.isAudioFile
import com.boom.aiobrowser.tools.clean.FileFilter.isDocFile
import com.boom.aiobrowser.tools.clean.FileFilter.isImagesFile
import com.boom.aiobrowser.tools.clean.FileFilter.isLargeFile
import com.boom.aiobrowser.tools.clean.FileFilter.isLogCatFile
import com.boom.aiobrowser.tools.clean.FileFilter.isLogFile
import com.boom.aiobrowser.tools.clean.FileFilter.isResidual
import com.boom.aiobrowser.tools.clean.FileFilter.isTmpFile
import com.boom.aiobrowser.tools.clean.FileFilter.isVideoFile
import com.boom.aiobrowser.tools.clean.FileFilter.isZipFile
import com.boom.aiobrowser.tools.clean.Scan1
import com.boom.aiobrowser.tools.clean.formatSize
import kotlinx.coroutines.InternalCoroutinesApi
import java.io.File

class CleanViewModel : BaseDataModel() {
    var currentPathLiveData = MutableLiveData<String>()
    var currentSizeLiveData = MutableLiveData<String>()
    var downloadListLiveData = MutableLiveData<FilesData>()
    var completeListLiveData = MutableLiveData<Int>()

    var allFiles = mutableListOf<File>()
    var allFilesLength = 0L

    /**
     * 扫描内存
     */
    fun startScan(
        parentDir: File,
        onScanPath: (file: File) -> Unit = {},
        onComplete: () -> Unit = {}
    ) {
        CleanConfig.clearCleanConfig()
        CleanConfig.clearFileConfig()
        loadData(loadBack = {
            Scan1(parentDir, onProgress = {
                detailMemoryFile(it, onScanPath)
            }, onComplete = {
                onComplete.invoke()
                APP.instance.cleanComplete = true
                completeListLiveData.postValue(0)
            }).scanStart()
        }, failBack = {}, 1)
    }

    fun startScanDownload(
        onComplete: (list:MutableList<FilesData>) -> Unit = {}
    ){
        var fileList = mutableListOf<FilesData>()
        loadData(loadBack = {
            Scan1(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), onProgress = {
                var file = isRealFile(it) ?: return@Scan1
                val fileName = file.name
                val filePath = file.absolutePath
                val fileSize = file.length()
                fileList.add(FilesData().apply {
                    this.fileName = fileName
                    this.filePath = filePath
                    this.fileSize = fileSize
                })
            }, onComplete = {
                onComplete.invoke(fileList)
            }).scanStart()
        }, failBack = {}, 1)
    }

    private fun isRealFile(file: File?):File? {
        if (file == null || file.exists().not()) {
            AppLogs.dLog(TAG, "file 错误")
            return null
        }
        return file
//        if (isTmpFile())
    }


    @OptIn(InternalCoroutinesApi::class)
    private fun filter2(file: File?): Boolean {
        if (file == null || file.exists().not()) {
            AppLogs.dLog(TAG, "file 错误")
            return false
        }
        val fileName = file.name
        val filePath = file.absolutePath
        val fileSize = file.length()
        if (isLargeFile(file)){
            runCatching {
                synchronized(largeFiles) {
                    largeFiles.add(FilesData().apply {
                        this.fileName = fileName
                        this.filePath = filePath
                        this.fileSize = fileSize
                    })
                }
                return true
            }.onFailure {
                AppLogs.eLog(TAG,"isLargeFile:${it.stackTraceToString()}")
            }
        }
        if (isImagesFile(file)){
            runCatching {
                synchronized(imageFiles) {
                    imageFiles.add(FilesData().apply {
                        this.fileName = fileName
                        this.filePath = filePath
                        this.fileSize = fileSize
                    })
                }
                return true
            }.onFailure {
                AppLogs.eLog(TAG,"isImagesFile:${it.stackTraceToString()}")
            }
        }else if (isVideoFile(file)){
            runCatching {
                synchronized(videoFiles) {
                    videoFiles.add(FilesData().apply {
                        this.fileName = fileName
                        this.filePath = filePath
                        this.fileSize = fileSize
                    })
                }
                return true
            }.onFailure {
                AppLogs.eLog(TAG,"isVideoFile:${it.stackTraceToString()}")
            }
        }else if (isAudioFile(file)){
            runCatching {
                synchronized(audioFiles) {
                    audioFiles.add(FilesData().apply {
                        this.fileName = fileName
                        this.filePath = filePath
                        this.fileSize = fileSize
                    })
                }
                return true
            }.onFailure {
                AppLogs.eLog(TAG,"isAudioFile:${it.stackTraceToString()}")
            }
        }else if (isZipFile(file)){
            runCatching {
                synchronized(zipFiles) {
                    zipFiles.add(FilesData().apply {
                        this.fileName = fileName
                        this.filePath = filePath
                        this.fileSize = fileSize
                    })
                }
                return true
            }.onFailure {
                AppLogs.eLog(TAG,"isZipFile:${it.stackTraceToString()}")
            }
        }else if (isDocFile(file)){
            runCatching {
                synchronized(documentsFiles) {
                    documentsFiles.add(FilesData().apply {
                        this.fileName = fileName
                        this.filePath = filePath
                        this.fileSize = fileSize
                    })
                }
                return true
            }.onFailure {
                AppLogs.eLog(TAG,"isDocFile:${it.stackTraceToString()}")
            }
        }
        return false
    }


    @OptIn(InternalCoroutinesApi::class)
    private fun filter(file: File?): Boolean {
        if (file == null || file.exists().not()) {
            AppLogs.dLog(TAG, "file 错误")
            return false
        }
        val fileName = file.name
        val filePath = file.absolutePath
        val fileSize = file.length()
        if (file.isDirectory && "cache" == fileName.lowercase()) {
            AppLogs.dLog(TAG, "file 为文件夹 cache:${"cache" == fileName.lowercase()}")
        }
        if (isResidual(file)) {
            runCatching {
                synchronized(residualFiles) {
                    residualFiles.add(FilesData().apply {
                        this.fileName = fileName
                        this.filePath = filePath
                        this.fileSize = fileSize
                    })
                }
                return true
            }.onFailure {
                AppLogs.eLog(TAG,"isResidual:${it.stackTraceToString()}")
            }
        } else if (isApks(file)) {
            runCatching {
                synchronized(apkFiles) {
                    apkFiles.add(FilesData().apply {
                        this.fileName = fileName
                        this.filePath = filePath
                        this.fileSize = fileSize
                    })
                }
            }.onFailure {
                AppLogs.eLog(TAG,"isDownloadApks:${it.stackTraceToString()}")
            }

            return true
        } else if (isLogFile(file) || isLogCatFile(file)) {
            runCatching {
                synchronized(junkFiles) {
                    junkFiles.get(0).tempList?.add(FilesData().apply {
                        this.fileName = fileName
                        this.filePath = filePath
                        this.fileSize = fileSize
                    })
                }
            }.onFailure {
                AppLogs.eLog(TAG,"isLogFile:${it.stackTraceToString()}")
            }
            return true
        } else if (isTmpFile(file)) {
            runCatching {
                synchronized(junkFiles) {
                    junkFiles.get(1).tempList?.add(FilesData().apply {
                        this.fileName = fileName
                        this.filePath = filePath
                        this.fileSize = fileSize
                    })
                }
            }.onFailure {
                AppLogs.eLog(TAG,"isTmpFile:${it.stackTraceToString()}")
            }
            return true
        } else if (isADFile(file)) {
            runCatching {
                synchronized(adFiles) {
                    adFiles.add(FilesData().apply {
                        this.fileName = fileName
                        this.filePath = filePath
                        this.fileSize = fileSize
                    })
                }
            }.onFailure {
                AppLogs.eLog(TAG,"isADFile:${it.stackTraceToString()}")
            }
            return true
        }
        return false
    }

    private fun detailMemoryFile(file: File?, onScanPath: (file: File) -> Unit = {}) {
        if (null == file) return
        currentPathLiveData.postValue(file.absolutePath)
        filter2(file)
        if (filter(file)) {
            allFiles.add(file)
            allFilesLength += file.length()
            currentSizeLiveData.postValue(allFilesLength.formatSize())
        }
    }
}