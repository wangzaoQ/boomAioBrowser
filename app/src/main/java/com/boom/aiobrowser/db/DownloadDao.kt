package com.boom.aiobrowser.db

import androidx.room.*
import com.boom.aiobrowser.data.model.DownloadModel

@Dao
interface DownloadDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDownloadModel(repo: DownloadModel)

    // 查询
    @Query("SELECT * FROM download_tab")
    fun queryAllDownload(): MutableList<DownloadModel>


    // 查询
    @Query("SELECT * FROM download_tab WHERE videoId = :videoId")
    fun queryDataById(videoId:String): DownloadModel?


    // 更新某一个数据
    @Update
    fun updateModel(vararg user: DownloadModel)
}