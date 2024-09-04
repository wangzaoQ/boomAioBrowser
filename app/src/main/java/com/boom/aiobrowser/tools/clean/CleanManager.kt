package com.boom.aiobrowser.tools.clean

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.blankj.utilcode.util.FileUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.tools.clean.CleanConfig.adFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.apkFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.cacheFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.junkFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.residualFiles
import com.boom.aiobrowser.ui.isAndroid11
import java.io.File


object CleanManager {
    fun deleteFile(){
        deleteCache()
        junkFiles.forEach {
            it.tempList?.forEach {
                if (it.itemChecked) FileUtils.delete(it.filePath)
            }
        }
        apkFiles.forEach {
            if (it.itemChecked)  FileUtils.delete(it.filePath)
        }
        residualFiles.forEach {
            if (it.itemChecked)   FileUtils.delete(it.filePath)
        }
        adFiles.forEach {
            if (it.itemChecked) FileUtils.delete(it.filePath)
        }
    }

    private fun deleteCache() {
        if (isAndroid11()){
            if (cacheFiles.isNotEmpty()){
                cacheFiles.forEach {
                    if (it.itemChecked){
                        val file = File(it.filePath)
                        val uri = Uri.fromFile(file)
                        val documentFile = DocumentFile.fromSingleUri(APP.instance, uri)
                        documentFile?.delete()
                    }
                }
            }
        }else{
            if (cacheFiles.isNotEmpty()){
                cacheFiles.forEach {
                    if (it.itemChecked) FileUtils.delete(it.filePath)
                }
            }
        }
    }
}