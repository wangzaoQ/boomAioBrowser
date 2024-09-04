package com.boom.aiobrowser.data

import android.os.Build
import com.boom.aiobrowser.R
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes

class FilesData :ViewItem(){

    var fileName = ""
    var filePath = ""
    var itemChecked = false
    var fileSize :Long?= 0L
    var fileTime :Long?= 0L
    var imgId :Any = R.mipmap.ic_file

    var scanType = -1

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
                this.fileTime = getFileCreationTime(file)
                this.dataType = TYPE_CHILD
            }
        }

    }

    fun getFileCreationTime(file: File): Long {
        runCatching {
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
                    return 0
                }
            }
        }
        return 0
    }

}