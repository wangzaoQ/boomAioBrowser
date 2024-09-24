package com.boom.aiobrowser.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "download_tab")
data class DownloadModel(
    @PrimaryKey var downloadId: String = ""
)