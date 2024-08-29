package com.boom.aiobrowser.data

import android.os.Build
import com.boom.aiobrowser.R
import com.boom.aiobrowser.tools.clean.CleanConfig.DATA_TYPE_JUNK
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes

class FilesData :ViewItem(){

    var fileName = ""
    var filePath = ""
    var itemChecked = false
    var fileSize = 0L
    var fileTime = 0L
    var imgId :Any = R.mipmap.ic_file

    var scanType = -1

    override var dataType=1

    var tempList:MutableList<FilesData>?=null

    companion object {
        fun createDefault(file:File):FilesData{
            val fileName = file.name
            val filePath = file.absolutePath
            val fileSize = file.length()
            return FilesData().apply {
                this.fileName = fileName
                this.filePath = filePath
                this.fileSize = fileSize
                this.fileTime = getFileCreationTime(file)?:0L
                this.dataType = 1
            }
        }

    }

    fun getFileCreationTime(file: File): Long? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                // 获取文件的基本属性
                val attrs: BasicFileAttributes = Files.readAttributes(
                    file.toPath(),
                    BasicFileAttributes::class.java
                )
                // 获取文件创建时间
                return attrs.creationTime().toMillis()
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
        return null
    }

}