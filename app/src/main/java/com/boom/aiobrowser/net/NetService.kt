package com.boom.aiobrowser.net

import com.boom.aiobrowser.data.AreaData
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.NewsDetailsData
import com.boom.aiobrowser.data.TopicData
import com.boom.aiobrowser.data.UserData
import com.boom.aiobrowser.data.WebData
import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface NetService {

    /**
     * 新闻列表
     */
    @GET("api/nemplo")
    suspend fun getNewsList(@QueryMap map: Map<String, String>): NetResponse<MutableList<NewsData>>

    /**
     * 人工推送新闻列表
     */
    @GET("api/nemplo/mguy")
    suspend fun getEditorNewsList(@QueryMap map: Map<String, String>): NetResponse<MutableList<NewsData>>


    /**
     * 热榜新闻列表
     */
    @GET("api/nemplo/tpossi")
    suspend fun getHotNewsList(@QueryMap map: Map<String, String>): NetResponse<MutableList<NewsData>>


    /**
     * 新闻列表
     */
    @GET("api/areas/search")
    suspend fun getLocation(@Query("loutsi") loutsi: Double,
                            @Query("linstr") linstr: Double): NetResponse<AreaData>

    /**
     * 新闻列表
     */
    @GET("api/ssuck/gholid")
    suspend fun getWebConfig(): NetResponse<MutableList<WebData>>

    /**
     * 新闻列表
     */
    @GET("api/ssuck")
    suspend fun getWebDetail(@Query("dsurpr") dsurpr: String,@Query("kdepen") kdepen: String): NetResponse<String>

    /**
     * 新闻详情
     */
    @GET("api/nemplo/dtie")
    suspend fun getNewDetails(@Query("itackl") id: String): NetResponse<NewsData>

    /**
     * topic
     */
    @GET("api/nemplo/tdetai")
    suspend fun getTopics(@Query("tmouth") tmouth:String): NetResponse<MutableList<TopicData>>


    /**
     * 创建用户
     */
    @Headers("Content-Type: application/json")
    @POST("api/uconve/tsafet")
    suspend fun createUser(@Body body: JsonObject): NetResponse<UserData>
}