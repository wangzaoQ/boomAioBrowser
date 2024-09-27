package com.boom.aiobrowser.tools.download

import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.data.model.DownloadModel

object DataTransformationManager {

    fun downloadTransformation(data: VideoDownloadData):DownloadModel{
        var model = DownloadModel().createDownloadModel(data)
        return model
    }
}