package com.boom.aiobrowser.net

import com.boom.aiobrowser.data.AreaData
import com.boom.aiobrowser.data.NewsData
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface NetService {

    /**
     * 新闻列表
     */
    @GET("api/nemplo")
    suspend fun getNewsList(@QueryMap map: Map<String, String>): NetResponse<MutableList<NewsData>>

  /**
     * 新闻列表
     */
    @GET("api/areas/search")
    suspend fun getLocation(@Query("loutsi") loutsi: Double,
                            @Query("linstr") linstr: Double): NetResponse<AreaData>

}