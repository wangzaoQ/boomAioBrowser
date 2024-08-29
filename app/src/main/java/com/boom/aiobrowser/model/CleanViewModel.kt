package com.boom.aiobrowser.model

import android.os.Environment
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.FileUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.TimeManager
import com.boom.aiobrowser.tools.clean.CleanConfig
import com.boom.aiobrowser.tools.clean.CleanConfig.adFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.apkFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.audioFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.cacheFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.documentsFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.downloadFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.imageFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.junkFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.largeFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.recentFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.residualFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.videoFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.zipFiles
import com.boom.aiobrowser.tools.clean.CleanToolsManager.getCacheSize
import com.boom.aiobrowser.tools.clean.FileFilter.isADFile
import com.boom.aiobrowser.tools.clean.FileFilter.isApk
import com.boom.aiobrowser.tools.clean.FileFilter.isApks
import com.boom.aiobrowser.tools.clean.FileFilter.isAudioFile
import com.boom.aiobrowser.tools.clean.FileFilter.isDoc
import com.boom.aiobrowser.tools.clean.FileFilter.isDocFile
import com.boom.aiobrowser.tools.clean.FileFilter.isImage
import com.boom.aiobrowser.tools.clean.FileFilter.isImagesFile
import com.boom.aiobrowser.tools.clean.FileFilter.isLargeFile
import com.boom.aiobrowser.tools.clean.FileFilter.isLogCatFile
import com.boom.aiobrowser.tools.clean.FileFilter.isLogFile
import com.boom.aiobrowser.tools.clean.FileFilter.isResidual
import com.boom.aiobrowser.tools.clean.FileFilter.isTmpFile
import com.boom.aiobrowser.tools.clean.FileFilter.isVideo
import com.boom.aiobrowser.tools.clean.FileFilter.isVideoFile
import com.boom.aiobrowser.tools.clean.FileFilter.isZip
import com.boom.aiobrowser.tools.clean.FileFilter.isZipFile
import com.boom.aiobrowser.tools.clean.Scan1
import com.boom.aiobrowser.tools.clean.formatSize
import com.boom.aiobrowser.ui.isAndroid12
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

class CleanViewModel : BaseDataModel() {
    var currentPathLiveData = MutableLiveData<String>()
    var currentSizeLiveData = MutableLiveData<String>()


    var recentListLiveData = MutableLiveData<MutableList<FilesData>>()

    var allFiles = mutableListOf<FilesData>()
    var allFilesLength = 0L

    /**
     * 扫描内存
     */
    fun startScanCache(
        onComplete: () -> Unit = {},
        onScanPath: (size:Long) -> Unit = {}
    ) {
        loadData(loadBack = {
            delay(100)
            if (isAndroid12()){
                cacheFiles = getCacheSize(onScanPath)
                onComplete.invoke()
            }
        }, failBack = {},1)
    }

    /**
     * 扫描数据
     */
    fun startScan(
        parentDir: File,
        onScanPath: (file: File) -> Unit = {},
        onComplete: () -> Unit = {},
        waitTime:Long = 0
    ) {
        allFiles.clear()
        CleanConfig.clearCleanConfig()
        CleanConfig.clearFileConfig()
        loadData(loadBack = {
            Scan1(parentDir,waitTime, onProgress = {
                detailMemoryFile(it, onScanPath)
            }, onComplete = {
                var start = System.currentTimeMillis()
                AppLogs.eLog(TAG,"onComplete 开始")
                allFiles.sortByDescending { it.fileTime }
                largeFiles.sortByDescending { it.fileSize }
                onComplete.invoke()
                APP.instance.cleanComplete = true
                APP.scanCompleteLiveData.postValue(0)
                var recentList = mutableListOf<FilesData>()
                for (i in 0 until allFiles.size){
                    var data = allFiles.get(i)
                    if (TimeManager.isWithin30Days(System.currentTimeMillis(),data.fileTime)){
                        recentList.add(data)
                        recentFiles.add(data)
                    }else{
                        break
                    }
                }
                recentListLiveData.postValue(recentList)
                AppLogs.eLog(TAG,"onComplete 结束时间 :${System.currentTimeMillis()-start}")
            }).scanStart()
        }, failBack = {}, 1)
    }

    fun startScanDownload(waitTime:Long = 0,onComplete: (list:MutableList<FilesData>) -> Unit = {}){
        var fileList = mutableListOf<FilesData>()
        loadData(loadBack = {
            Scan1(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),waitTime, onProgress = {
                var file = isRealFile(it) ?: return@Scan1
                if (file.absolutePath.isVideo() || file.absolutePath.isImage() || file.absolutePath.isDoc()||file.absolutePath.isApk()||file.absolutePath.isZip()){
                    val fileName = file.name
                    val filePath = file.absolutePath
                    val fileSize = file.length()
                    fileList.add(FilesData.createDefault(file))
                }
            }, onComplete = {
                downloadFiles.clear()
                downloadFiles.addAll(fileList)
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

        if (isLargeFile(file)){
            runCatching {
                synchronized(largeFiles) {
                    var data = FilesData.createDefault(file)
                    largeFiles.add(data)
                    allFiles.add(data)
                }
                return true
            }.onFailure {
                AppLogs.eLog(TAG,"isLargeFile:${it.stackTraceToString()}")
            }
        }
        if (isImagesFile(file)){
            runCatching {
                synchronized(imageFiles) {
                    var data = FilesData.createDefault(file)
                    imageFiles.add(data)
                    allFiles.add(data)
                }
                return true
            }.onFailure {
                AppLogs.eLog(TAG,"isImagesFile:${it.stackTraceToString()}")
            }
        }else if (isVideoFile(file)){
            runCatching {
                synchronized(videoFiles) {
                    var data = FilesData.createDefault(file)
                    videoFiles.add(data)
                    allFiles.add(data)
                }
                return true
            }.onFailure {
                AppLogs.eLog(TAG,"isVideoFile:${it.stackTraceToString()}")
            }
        }else if (isAudioFile(file)){
            runCatching {
                synchronized(audioFiles) {
                    var data = FilesData.createDefault(file)
                    audioFiles.add(data)
                    allFiles.add(data)
                }
                return true
            }.onFailure {
                AppLogs.eLog(TAG,"isAudioFile:${it.stackTraceToString()}")
            }
        }else if (isZipFile(file)){
            runCatching {
                synchronized(zipFiles) {
                    var data = FilesData.createDefault(file)
                    zipFiles.add(data)
                    allFiles.add(data)
                }
                return true
            }.onFailure {
                AppLogs.eLog(TAG,"isZipFile:${it.stackTraceToString()}")
            }
        }else if (isDocFile(file)){
            runCatching {
                synchronized(documentsFiles) {
                    var data = FilesData.createDefault(file)
                    documentsFiles.add(data)
                    allFiles.add(data)
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
                    var data = FilesData.createDefault(file)
                    residualFiles.add(data)
                }
                return true
            }.onFailure {
                AppLogs.eLog(TAG,"isResidual:${it.stackTraceToString()}")
            }
        } else if (isApks(file)) {
            runCatching {
                synchronized(apkFiles) {
                    var data = FilesData.createDefault(file)
                    apkFiles.add(data)
                }
            }.onFailure {
                AppLogs.eLog(TAG,"isDownloadApks:${it.stackTraceToString()}")
            }

            return true
        } else if (isLogFile(file) || isLogCatFile(file)) {
            runCatching {
                synchronized(junkFiles) {
                    var data = FilesData.createDefault(file)
                    junkFiles.get(0).tempList?.add(data)
                }
            }.onFailure {
                AppLogs.eLog(TAG,"isLogFile:${it.stackTraceToString()}")
            }
            return true
        } else if (isTmpFile(file)) {
            runCatching {
                synchronized(junkFiles) {
                    var data = FilesData.createDefault(file)
                    junkFiles.get(1).tempList?.add(data)
                }
            }.onFailure {
                AppLogs.eLog(TAG,"isTmpFile:${it.stackTraceToString()}")
            }
            return true
        } else if (isADFile(file)) {
            runCatching {
                synchronized(adFiles) {
                    var data = FilesData.createDefault(file)
                    adFiles.add(data)
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
            allFilesLength += file.length()
            currentSizeLiveData.postValue(allFilesLength.formatSize())
        }
    }
}