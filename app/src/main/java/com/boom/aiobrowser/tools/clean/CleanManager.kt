package com.boom.aiobrowser.tools.clean

import com.blankj.utilcode.util.FileUtils
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.tools.clean.CleanConfig.adFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.apkFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.cacheFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.documentCacheFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.junkFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.residualFiles
import com.boom.aiobrowser.ui.isAndroid11

object CleanManager {
    fun deleteFile(){
        deleteCache()
        junkFiles.forEach {
            it.tempList?.forEach {
                FileUtils.delete(it.filePath)
            }
            it.tempList?.clear()
        }
        apkFiles.forEach {
            FileUtils.delete(it.filePath)
        }
        apkFiles.clear()
        residualFiles.forEach {
            FileUtils.delete(it.filePath)
        }
        residualFiles.clear()
        adFiles.forEach {
            FileUtils.delete(it.filePath)
        }
        adFiles.clear()
    }

    private fun deleteCache() {
        if (isAndroid11()){
            if (documentCacheFiles.isNotEmpty()){
                documentCacheFiles.forEach {
                    it.delete()
                }
            }
            documentCacheFiles.clear()
        }else{
            if (cacheFiles.isNotEmpty()){
                cacheFiles.forEach {
                    FileUtils.delete(it.filePath)
                }
            }
            cacheFiles.clear()
        }
    }
}