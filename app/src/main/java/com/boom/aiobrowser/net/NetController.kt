package com.boom.aiobrowser.net

import com.boom.aiobrowser.data.AreaData
import com.boom.aiobrowser.data.NewsData

object NetController {
    private val service: NetService by lazy {
        Net(NetService::class.java)
    }

    suspend fun getNewsList(map: HashMap<String, String>): NetResponse<MutableList<NewsData>> {
        return service.getNewsList(map)
    }

    suspend fun getLocation(lcommu: Double,ldrawi: Double): NetResponse<AreaData> {
        return service.getLocation(lcommu,ldrawi)
    }

}