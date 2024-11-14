package com.boom.aiobrowser.net

import com.boom.aiobrowser.data.AreaData
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.NewsDetailsData
import com.boom.aiobrowser.data.TopicData
import com.boom.aiobrowser.data.WebData

object NetController {
    private val service: NetService by lazy {
        Net(NetService::class.java)
    }

    suspend fun getNewsList(map: HashMap<String, String>): NetResponse<MutableList<NewsData>> {
        return service.getNewsList(map)
    }

    suspend fun getEditorNewsList(map: HashMap<String, String>): NetResponse<MutableList<NewsData>> {
        return service.getEditorNewsList(map)
    }

    suspend fun getHotNewsList(map: HashMap<String, String>): NetResponse<MutableList<NewsData>> {
        return service.getHotNewsList(map)
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

}