package com.boom.aiobrowser.db

import androidx.room.*
import com.boom.aiobrowser.data.model.DownloadModel

@Dao
interface DownloadDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDownloadModel(repo: DownloadModel)

}