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

    // 查询
    @Query("SELECT * FROM download_tab WHERE url = :url")
    fun queryDataByUrl(url:String): DownloadModel?

    // 查询
    @Query("SELECT * FROM download_tab WHERE downloadType = 4")
    fun queryDataDone(): MutableList<DownloadModel>

    // 查询
    @Query("SELECT * FROM download_tab WHERE downloadType != 4")
    fun queryDataOther(): MutableList<DownloadModel>
    // 查询
    @Query("SELECT * FROM download_tab WHERE downloadType == 1")
    fun queryDataLoading(): MutableList<DownloadModel>

    // 更新某一个数据
    @Update
    fun updateModel(vararg model: DownloadModel)

    //删除某一个数据
    @Delete
    fun deleteModel(vararg model: DownloadModel)
}