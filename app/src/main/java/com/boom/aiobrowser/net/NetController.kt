package com.boom.aiobrowser.net

import com.boom.aiobrowser.data.AreaData
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.NewsDetailsData
import com.boom.aiobrowser.data.TopicData
import com.boom.aiobrowser.data.UserData
import com.boom.aiobrowser.data.WebData
import com.google.gson.JsonObject

object NetController {
    private val service: NetService by lazy {
        Net(NetService::class.java)
    }

    suspend fun getNewsList(map: HashMap<String, String>): NetResponse<MutableList<NewsData>> {
        return service.getNewsList(map)
    }
    suspend fun getNewsList(url:String): NetResponse<MutableList<NewsData>> {
        return service.getNewsList(url)
    }

    suspend fun getEditorNewsList(map: HashMap<String, String>): NetResponse<MutableList<NewsData>> {
        return service.getEditorNewsList(map)
    }

    suspend fun getHotNewsList(map: HashMap<String, String>): NetResponse<MutableList<NewsData>> {
        return service.getHotNewsList(map)
    }

    suspend fun getRelatedNews(map: HashMap<String, String>): NetResponse<MutableList<NewsData>> {
        return service.getRelatedNewsList(map)
    }

    suspend fun getLocation(lcommu: Double,ldrawi: Double): NetResponse<AreaData> {
        return service.getLocation(lcommu,ldrawi)
    }

    suspend fun getWebConfig(): NetResponse<MutableList<WebData>> {
        return service.getWebConfig()
    }

    suspend fun getWebDetail(dsurpr:String,kdepen:String): NetResponse<String> {
        return service.getWebDetail(dsurpr,kdepen)
    }

    suspend fun getNewsDetails(id:String): NetResponse<NewsData> {
        return service.getNewDetails(id)
    }
    suspend fun getTopic(tmouth:String="exposed"): NetResponse<MutableList<TopicData>> {
        return service.getTopics(tmouth)
    }

    suspend fun searchNews(content:String): NetResponse<MutableList<NewsData>> {
        return service.searchNews(content)
    }

    suspend fun createUser(body: JsonObject): NetResponse<UserData> {
        return service.createUser(body)
    }

    suspend fun getTrendNews(tguest:String): NetResponse<MutableList<NewsData>>{
        return service.getTrendNews(tguest)
    }
}