package com.boom.aiobrowser.model

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.FileUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.BuildConfig
import com.boom.aiobrowser.data.AppInfo
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.data.ViewItem
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.TimeManager
import com.boom.aiobrowser.tools.clean.CleanConfig
import com.boom.aiobrowser.tools.clean.CleanConfig.adFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.apkFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.appInstalledPkgList
import com.boom.aiobrowser.tools.clean.CleanConfig.audioFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.cacheFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.documentsFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.downloadFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.imageFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.junkFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.largeFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.recentFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.residualFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.runAPPExtension
import com.boom.aiobrowser.tools.clean.CleanConfig.runningAppInfo
import com.boom.aiobrowser.tools.clean.CleanConfig.videoFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.zipFiles
import com.boom.aiobrowser.tools.clean.CleanToolsManager.getCacheSize
import com.boom.aiobrowser.tools.clean.CleanToolsManager.getInstallTime
import com.boom.aiobrowser.tools.clean.FileFilter.isADFile
import com.boom.aiobrowser.tools.clean.FileFilter.isApk
import com.boom.aiobrowser.tools.clean.FileFilter.isApks
import com.boom.aiobrowser.tools.clean.FileFilter.isAudioFile
import com.boom.aiobrowser.tools.clean.FileFilter.isDoc
import com.boom.aiobrowser.tools.clean.FileFilter.isDocFile
import com.boom.aiobrowser.tools.clean.FileFilter.isDownload
import com.boom.aiobrowser.tools.clean.FileFilter.isImage
import com.boom.aiobrowser.tools.clean.FileFilter.isImagesFile
import com.boom.aiobrowser.tools.clean.FileFilter.isInstalled
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
import com.boom.aiobrowser.tools.clean.UriManager
import com.boom.aiobrowser.tools.clean.UriManager.URI_SEPARATOR
import com.boom.aiobrowser.tools.clean.formatSize
import com.boom.aiobrowser.tools.clean.hasConstants
import com.boom.aiobrowser.ui.isAndroid11
import com.boom.aiobrowser.ui.isAndroid12
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.Objects

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
        forceScan:Boolean,
        onComplete: () -> Unit = {},
        onScanPath: (size:Long) -> Unit = {}
    ) {
        cacheFiles.clear()
        var startTime = System.currentTimeMillis()
        loadData(loadBack = {
            delay(100)
            if (isAndroid12()){
                cacheFiles = getCacheSize(onScanPath)
                cacheFiles.sortByDescending { it.fileSize?:0L }
                toEnd(startTime, onComplete)
            }else if (isAndroid11()){
//                loadData(loadBack = {
//                    // 使用DocumentFile来管理该目录
//                    if (forceScan){
//                        val documentFile = DocumentFile.fromTreeUri(APP.instance, Uri.parse(UriManager.URI_STORAGE_SAVED_ANRROID_DATA))
//                        scanByUri(documentFile, onScanPath)
//                    }else{
//                        val uriPermissions = APP.instance.contentResolver.persistedUriPermissions
//                        AppLogs.dLog("PermissionManager:", "已经授权的uri集合是:$uriPermissions")
//                        //已经授权的uri集合是:[UriPermission {uri=content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata, modeFlags=3, persistedTime=1669980673302}]
//                        var tempUri: String
//                        //遍历并判断请求的uri字符串是否已经被授权
//                        for (uriP in uriPermissions) {
//                            tempUri = uriP.uri.toString()
//                            AppLogs.dLog("PermissionManager:", "tempUri:$tempUri")
//                            //如果父目录已经授权就返回已经授权
//                            val documentFile = DocumentFile.fromTreeUri(APP.instance, Uri.parse(tempUri))
//                            scanByUri(documentFile, onScanPath)
//                        }
//                    }
//                    cacheFiles.sortByDescending { it.fileSize?:0L }
//                    toEnd(startTime, onComplete)
//                }, failBack = {},1)
            }else{
                loadData(loadBack = {
                    //android 10及以下
                    var allLength = 0L
                    val packageManager: PackageManager = APP.instance.getPackageManager()
                    synchronized("startScanCache"){
                        val pm: PackageManager = APP.instance.packageManager
                        for (lp in pm.getInstalledPackages(0)) {
                            if ((ApplicationInfo.FLAG_SYSTEM and lp.applicationInfo.flags) == 0 && (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP and lp.applicationInfo.flags) == 0 && (ApplicationInfo.FLAG_STOPPED and lp.applicationInfo.flags) == 0) {
                                // 第三方正在运行的 app 进程信息  更多信息查询 PackageInfo 类
                                if (lp.packageName.hasConstants(runAPPExtension))continue
                                var filePath = "${Environment.getExternalStorageDirectory().absolutePath}/Android/data/${lp.packageName}/cache"
                                var file = File(filePath)
                                if (lp.packageName.startsWith( "tap.fun.news")){
                                    AppLogs.dLog(TAG,"test com.tap.fun.news大小:${file.length()}")
                                }
                                var appFileName =
                                    packageManager.getApplicationLabel(lp.applicationInfo)
                                        .toString()
                                var imageId = packageManager.getApplicationIcon(lp.applicationInfo)
                                scanDirectoryCache(file,onProgress = {
                                    var oldData = false
                                    //如果cacheFile中有这个App 就统一把数据归到一起
                                    for (i in 0 until cacheFiles.size) {
                                        var data = cacheFiles.get(i)
                                        if (data.fileName == appFileName) {
                                            AppLogs.dLog(TAG,"FilesData后续增加开始:${data.fileName} 原始大小:${data.fileSize}")
                                            data.fileSize = data.fileSize?.plus(it.length())
                                            AppLogs.dLog(TAG,"FilesData后续增加开始:${data.fileName} 增加的大小:${it.length()}")
                                            oldData = true
                                            AppLogs.dLog(TAG,"FilesData后续增加结束:${data.fileName} 增加后总大小:${data.fileSize?.formatSize()}")
                                            break
                                        }
                                    }
                                    if (oldData.not()) {
                                        cacheFiles.add(FilesData().apply {
                                            this.filePath = filePath
                                            fileSize = it.length()
                                            fileName = appFileName
                                            itemChecked = true
                                            dataType = ViewItem.TYPE_CHILD
                                            imgId = imageId
                                            AppLogs.dLog(TAG,"FilesData初始:${fileName} 大小:${fileSize}")
                                        })
                                    }
                                    allLength += it.length()
                                    //                                        AppLogs.dLog("getCacheSize",":${allLength.formatSize()}")
//                                    AppLogs.dLog(TAG,"扫描内存耗时回调:${it.absolutePath}")
                                    onScanPath.invoke(allLength)
                                })
                            }
                        }
                    }
                    cacheFiles.sortByDescending { it.fileSize?:0L }
                    toEnd(startTime, onComplete)
                }, failBack = {},1)
            }

        }, failBack = {
            AppLogs.dLog(TAG,"startScanCache onComplete errot:${it.stackTraceToString()}")
            onComplete.invoke()
        },1)
    }

    private suspend fun toEnd(startTime: Long, onComplete: () -> Unit) {
        var middleTime = (System.currentTimeMillis() - startTime)
        AppLogs.dLog(TAG, "扫描内存耗时:${middleTime}")
        if (middleTime < 3000) {
            delay(3000 - middleTime)
        }
        AppLogs.dLog(TAG, "startScanCache onComplete")
        onComplete.invoke()
    }


    fun scanDirectoryCache(dir: File,onProgress: (file:File) -> Unit){

        dir.listFiles()?.forEach { file ->
            if (file.isDirectory){
                scanDirectoryCache(file,onProgress)
            }else{
                onProgress.invoke(file)
            }
        }
    }


    private suspend fun CleanViewModel.scanByUri(
        documentFile: DocumentFile?,
        onScanPath: (size: Long) -> Unit
    ) {
        if (documentFile != null) {
            // 现在可以访问/Android/data目录
            val children = documentFile?.listFiles()
            var files = mutableListOf<File>()
            children?.forEach { file ->
                files.addAll(scanDirectory(file))
            }
            val packageManager: PackageManager = APP.instance.getPackageManager()
            var allLength = 0L
            if (files.isNullOrEmpty()) {
                return
            }
            val pm: PackageManager = APP.instance.packageManager
            for (lp in pm.getInstalledPackages(0)) {
                if ((ApplicationInfo.FLAG_SYSTEM and lp.applicationInfo.flags) == 0 && (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP and lp.applicationInfo.flags) == 0 && (ApplicationInfo.FLAG_STOPPED and lp.applicationInfo.flags) == 0) {
                    // 第三方正在运行的 app 进程信息  更多信息查询 PackageInfo 类
                    if (lp.packageName.hasConstants(runAPPExtension))continue
                var pkg = lp.packageName
                if (pkg!=BuildConfig.APPLICATION_ID) {
                    //在所有的安装的app中遍历是否有这个文件的包名，有则拿到信息组建数据
                    files.forEach {
                        if (it.absolutePath.contains(pkg)) {
                            var appFileName =
                                packageManager.getApplicationLabel(lp.applicationInfo)
                                    .toString()
                            var oldData = false
                            //如果cacheFile中有这个App 就统一把数据归到一起
                            for (i in 0 until cacheFiles.size) {
                                var data = cacheFiles.get(i)
                                if (data.fileName == appFileName) {
                                    data.fileSize = data.fileSize?.plus(it.length())
                                    oldData = true
                                    break
                                }
                            }
                            if (oldData.not()) {
                                cacheFiles.add(FilesData().apply {
                                    filePath = it.absolutePath
                                    fileSize = it.length()
                                    fileName = appFileName
                                    itemChecked = true
                                    dataType = ViewItem.TYPE_CHILD
                                    imgId =
                                        packageManager.getApplicationIcon(lp.applicationInfo)
                                })
                            }
                            allLength += it.length()
                            //                                        AppLogs.dLog("getCacheSize",":${allLength.formatSize()}")
                            onScanPath.invoke(allLength)
                        }
                    }
                }
                }
            }
        }
    }

    suspend fun scanDirectory(dir: DocumentFile): MutableList<File> = coroutineScope {
        val deferredResults = mutableListOf<Deferred<MutableList<File>>>()
        val files = mutableListOf<File>()
        dir.listFiles()?.forEach { file ->
//
            if (file.isDirectory) {
                if (file.listFiles().size<200){
                    deferredResults.add(async {
                        scanDirectory(file)
                    })
                }
            } else {
                getRealPathFromURI(file.uri)?.apply {
                    var file = File(this)
                    if (file.absolutePath.contains("cache",true)){
                        AppLogs.dLog(TAG,"清理扫描出的cache文件:${file.absolutePath} size:${file.length()}} 当前线程:${Thread.currentThread()}")
                        files.add(file)
                    }
                }
            }
        }
        deferredResults.forEach {
            files.addAll(it.await())
        }

        return@coroutineScope files
    }

    fun getRealPathFromURI(uri: Uri): String? {
        var result: String? = null
        if (DocumentsContract.isDocumentUri(APP.instance, uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":")
            val type = split[0]

            if ("primary".equals(type, ignoreCase = true)) {
                result = "${Environment.getExternalStorageDirectory()}/${split[1]}"
            }
        }
        return result
    }

    /*
     * 扫描数据
     */
    fun startScan(
        parentDir: File,
        onScanPath: (file: File) -> Unit = {},
        onComplete: () -> Unit = {},
        waitTime:Long = 0
    ) {
        allFilesLength = 0
        allFiles.clear()
        CleanConfig.clearCleanConfig()
        CleanConfig.clearFileConfig()
        loadData(loadBack = {
            Scan1(parentDir,waitTime, onProgress = {
                detailMemoryFile(it, onScanPath)
            }, onComplete = {
                var start = System.currentTimeMillis()
                AppLogs.eLog(TAG,"onComplete 开始")
                runCatching {
                    allFiles.sortByDescending { it.fileTime?:0L }
                }
                largeFiles.sortByDescending { it.fileSize?:0L }
                onComplete.invoke()
                APP.instance.cleanComplete = true
                APP.scanCompleteLiveData.postValue(0)
                var recentList = mutableListOf<FilesData>()
                for (i in 0 until allFiles.size){
                    var data = allFiles.get(i)
                    if (TimeManager.isWithin30Days(System.currentTimeMillis(),data.fileTime?:0L)){
                        if ((data.fileSize?:0L)>0L){
                            recentList.add(data)
                            recentFiles.add(data)
                        }
                    }else{
                        break
                    }
                }
                recentListLiveData.postValue(recentList)
                AppLogs.eLog(TAG,"onComplete 结束时间 :${System.currentTimeMillis()-start}")
            }).scanStart()
        }, failBack = {
            onComplete.invoke()
            APP.scanCompleteLiveData.postValue(0)
            AppLogs.dLog(TAG,"startScan onComplete error:${it.stackTraceToString()}")
        }, 1)
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
            },"download").scanStart()
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
        } else if (isDownload(file)){
            runCatching {
                synchronized(downloadFiles) {
                    var data = FilesData.createDefault(file)
                    downloadFiles.add(data)
                    allFiles.add(data)
                }
                return true
            }.onFailure {
                AppLogs.eLog(TAG,"isDownload:${it.stackTraceToString()}")
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
                AppLogs.dLog("filter", "匹配到 isResidual file:${file.absolutePath}")
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
                AppLogs.dLog("filter", "匹配到 isApks file:${file.absolutePath}")
                return true
            }.onFailure {
                AppLogs.eLog(TAG,"isDownloadApks:${it.stackTraceToString()}")
            }

        } else if (isLogFile(file) || isLogCatFile(file)) {
            runCatching {
                synchronized(junkFiles) {
                    var data = FilesData.createDefault(file)
                    junkFiles.get(0).tempList?.add(data)
                }
                AppLogs.dLog("filter", "匹配到 isLogFile isLogCatFile file:${file.absolutePath}")
                return true
            }.onFailure {
                AppLogs.eLog(TAG,"isLogFile:${it.stackTraceToString()}")
            }
        } else if (isTmpFile(file)) {
            runCatching {
                synchronized(junkFiles) {
                    var data = FilesData.createDefault(file)
                    junkFiles.get(1).tempList?.add(data)
                }
                AppLogs.dLog("filter", "匹配到 isTmpFile file:${file.absolutePath}")
                return true
            }.onFailure {
                AppLogs.eLog(TAG,"isTmpFile:${it.stackTraceToString()}")
            }
        }else if (isADFile(file)) {
            runCatching {
                synchronized(adFiles) {
                    var data = FilesData.createDefault(file)
                    adFiles.add(data)
                }
                AppLogs.dLog("filter", "匹配到 isADFile file:${file.absolutePath}")
                return true
            }.onFailure {
                AppLogs.eLog(TAG,"isADFile:${it.stackTraceToString()}")
            }
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