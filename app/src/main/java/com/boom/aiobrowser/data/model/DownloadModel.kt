package com.boom.aiobrowser.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.tools.toJson

@Entity(tableName = "download_tab")
public class DownloadModel{
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0

    var videoId:String?=""
    var fileName:String? =""
    var url:String?=""
    var imageUrl:String?=""
    var size:Long?=0
    var downloadSize:Long?=0
    var videoType:String?=""
    var paramsMapJson:String?=""
    var downloadType:Int?=0

    var downloadFileName :String = ""
    var downloadFilePath :String = ""

    fun createDownloadModel(downloadData: VideoDownloadData):DownloadModel{
        var model = DownloadModel()
        model.apply {
            videoId = downloadData.videoId
            fileName = downloadData.fileName
            url = downloadData.url
            size = downloadData.size
            videoType = downloadData.videoType?:""
            paramsMapJson = toJson(downloadData.paramsMap)
            downloadType = downloadData.downloadType
            downloadSize = downloadData.downloadSize
            downloadFileName = downloadData.downloadFileName
            downloadFilePath = downloadData.downloadFilePath
            imageUrl = downloadData.imageUrl
        }
        return model
    }

    override fun toString(): String {
        return toJson(this)
    }
}
