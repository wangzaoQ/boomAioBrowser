package com.boom.aiobrowser.model

import android.os.Build
import android.os.Bundle
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.BuildConfig
import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.SourceData
import com.boom.aiobrowser.data.TopicBean
import com.boom.aiobrowser.data.WebConfigData
import com.boom.aiobrowser.net.NetController
import com.boom.aiobrowser.nf.NFShow
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.other.TopicConfig
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointManager
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.encryptECB
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.tools.getTopicDataLan
import com.boom.aiobrowser.tools.toJson
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.util.Locale
import kotlin.random.Random

class AppViewModel : BaseDataModel() {

    fun getWebConfig() {
        loadData(loadBack = {
            var pageList = mutableListOf<WebConfigData>()
            var fetchList = mutableListOf<WebConfigData>()
            NetController.getWebConfig().data?.forEach {
                if (it.kdepen == "FETCH") {
                    fetchList.add(WebConfigData().apply {
                        cType = it.kdepen
                        cUrl = it.dsurpr
                        cDetail = NetController.getWebDetail(it.dsurpr, it.kdepen).data ?: ""
                    })
                } else if (it.kdepen == "PAGE") {
                    pageList.add(WebConfigData().apply {
                        cType = it.kdepen
                        cUrl = it.dsurpr
                        cDetail = NetController.getWebDetail(it.dsurpr, it.kdepen).data ?: ""
                    })
                }
            }
            if (pageList.isNotEmpty()) {
                CacheManager.pageList = pageList
            }
            if (fetchList.isNotEmpty()) {
                CacheManager.fetchList = fetchList
            }
        }, failBack = {
            AppLogs.eLog(TAG, it.stackTraceToString())
        }, 1)
    }

    fun getTopics() {

        var topStringHomeTab = mutableListOf<String>(
            TopicConfig.TOPIC_PUBLIC_SAFETY,
            TopicConfig.TOPIC_WORLD,
            TopicConfig.TOPIC_POLITICS,
            TopicConfig.TOPIC_SPORTS,
            TopicConfig.TOPIC_RELATIONSHIP,
            TopicConfig.TOPIC_SOCIAL_WELFARE,
            TopicConfig.TOPIC_FUNNY
        )

        var topStringNewsTab = mutableListOf<String>(
            TopicConfig.TOPIC_LOCAL,
            TopicConfig.TOPIC_POLITICS,
            TopicConfig.TOPIC_ENTERTAINMENT,
            TopicConfig.TOPIC_PUBLIC_SAFETY
        )
        var homeTopicList = CacheManager.homeTopicList
        var defaultTopicList = CacheManager.defaultTopicList
        var allTopicList = CacheManager.allTopicList
        if (allTopicList.isNullOrEmpty()) {
            homeTopicList.clear()
            defaultTopicList.clear()
            loadData(loadBack = {
                var data = NetController.getTopic().data
                topStringHomeTab.forEachIndexed { index, s ->
                    data?.forEachIndexed { index, nowTopicData ->
                        if (s == nowTopicData.nsand) {
                            homeTopicList.add(TopicBean().apply {
                                topic = getTopicDataLan(nowTopicData)
                                id = nowTopicData.nsand ?: ""
                            })
                        }
                    }
                }
                CacheManager.homeTopicList = homeTopicList
                APP.homeTopicLiveData.postValue(homeTopicList)

                topStringNewsTab.forEachIndexed { index, s ->
                    data?.forEachIndexed { index, nowTopicData ->
                        if (s == nowTopicData.nsand) {
                            defaultTopicList.add(TopicBean().apply {
                                topic = getTopicDataLan(nowTopicData)
                                id = nowTopicData.nsand ?: ""
                            })
                        }
                    }
                }
                defaultTopicList.add(0, getTopicByLanguage(TopicConfig.TOPIC_FOR_YOU))
                defaultTopicList.add(1, getTopicByLanguage(TopicConfig.TOPIC_LOCAL))
                CacheManager.defaultTopicList = defaultTopicList
                APP.topicLiveData.postValue(defaultTopicList)

                data?.forEachIndexed { index, nowTopicData ->
                    allTopicList.add(TopicBean().apply {
                        topic = getTopicDataLan(nowTopicData)
                        id = nowTopicData.nsand ?: ""
                    })
                }
                CacheManager.allTopicList = allTopicList
            }, failBack = {
            }, 1)
        } else {
            CacheManager.homeTopicList = homeTopicList
            APP.homeTopicLiveData.postValue(homeTopicList)
            CacheManager.defaultTopicList = defaultTopicList
            APP.topicLiveData.postValue(defaultTopicList)
        }
    }

    private fun getTopicByLanguage(topic: String): TopicBean {
        var topicBean = TopicBean()
        topicBean.id = topic
        topicBean.topic = when (Locale.getDefault().language) {
            "pt" -> {
                when (topic) {
                    TopicConfig.TOPIC_LOCAL -> {
                        "Local"
                    }

                    TopicConfig.TOPIC_POLITICS -> {
                        "Política"
                    }

                    TopicConfig.TOPIC_ENTERTAINMENT -> {
                        "Entretenimento"
                    }

                    TopicConfig.TOPIC_PUBLIC_SAFETY -> {
                        "Segurança Pública"
                    }

                    else -> {
                        "Para Você"
                    }
                }
            }

            else -> {
                when (topic) {
                    TopicConfig.TOPIC_LOCAL -> {
                        TopicConfig.TOPIC_LOCAL
                    }

                    TopicConfig.TOPIC_POLITICS -> {
                        TopicConfig.TOPIC_POLITICS
                    }

                    TopicConfig.TOPIC_ENTERTAINMENT -> {
                        TopicConfig.TOPIC_ENTERTAINMENT
                    }

                    TopicConfig.TOPIC_PUBLIC_SAFETY -> {
                        TopicConfig.TOPIC_PUBLIC_SAFETY
                    }

                    else -> {
                        TopicConfig.TOPIC_FOR_YOU
                    }
                }
            }
        }
        return topicBean
    }

    fun getTrendsNews() {
        loadData(loadBack = {
            getTrendsNewsData()
            if (APP.isDebug) {
                NFShow.showNewsNFFilter(NFEnum.NF_TREND)
            }
            APP.trendNewsComplete.postValue(0)
        }, failBack = {
            AppLogs.eLog(TAG, it.stackTraceToString())
        }, 1)
    }

    suspend fun getTrendsNewsData(): MutableList<NewsData> {
        var startTime = CacheManager.trendNewsTime
        if (APP.isDebug) {
            if (System.currentTimeMillis() - startTime > 10 * 1000) {
                CacheManager.trendNews = mutableListOf()
            }
        } else {
            if (System.currentTimeMillis() - startTime > 4 * 60 * 60 * 1000) {
                CacheManager.trendNews = mutableListOf()
            }
        }
        var oldList = CacheManager.trendNews
        if (oldList.isNullOrEmpty()) {
            var list = NetController.getTrendNews("GTR-4").data ?: mutableListOf()
            var index0 = 0
            var index1 = 0
            var index2 = 0
            var endInx = list!!.size
            if (list.size > 3) {
                if (endInx > 15) {
                    endInx = 15
                }
                var count = 0
                while (index0 == index1 || index0 == index2 || index1 == index2) {
                    count++
                    index0 = Random.nextInt(3, endInx)
                    delay(50)
                    index1 = Random.nextInt(3, endInx)
                    delay(50)
                    index2 = Random.nextInt(3, endInx)
                }
                AppLogs.dLog(
                    APP.instance.TAG,
                    "取随机数 count:${count} index0:${index0} index1:${index1} index2:${index2}"
                )
                for (i in 0 until list.size) {
                    if (i == 0 || i == 1 || i == 2) continue
                    if (i == index0 || i == index1 || i == index2) {
                        list.get(i).isTrendTop = true
                    }
                }
            }
            CacheManager.trendNews = list
            CacheManager.trendNewsTime = System.currentTimeMillis()
            return list
        } else {
            return oldList
        }
    }

    var maxCount = if (APP.isDebug) 4 else 15
    var currentCount = 0
    fun getCampaign(isCurrent:Boolean=true) {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//            return
//        }
        if (CacheManager.campaignId.isNullOrEmpty().not()) {
            return
        }
        var startTime = 0L
        if (isCurrent){
            startTime = System.currentTimeMillis()
            PointEvent.posePoint(PointEventKey.attribution_req)
        }

        loadData(loadBack = {
            currentCount++
            runCatching {
                var json = JSONObject().apply {
                    put(
                        "pkg",
                        if (APP.isDebug) "com.fast.safe.browser" else BuildConfig.APPLICATION_ID
                    )
                    put(
                        "distinct_id",
                        if (APP.isDebug) "bbf7e0edf9806583" else CacheManager.getID()
//                        CacheManager.getID()
                    )
                    put("platform", "android")
                }
                var old = json.toString()
                AppLogs.dLog(APP.instance.TAG, "归因" + "原始数据:${old}")
//
                AppLogs.dLog(
                    APP.instance.TAG,
                    "归因" + "加密后:${old.encryptECB("tmg6UbIp2gY/JcueVU7oYQ==")}"
                )

                var endJson = JSONObject().apply {
                    put("encrypt", old.encryptECB("tmg6UbIp2gY/JcueVU7oYQ=="))
                }
                val requestBody: RequestBody = RequestBody.create(
                    "application/json; charset=utf-8".toMediaTypeOrNull(),
                    endJson.toString()
                )
                var request = Request.Builder().post(requestBody)
                    .url("https://layette.safebrowsers.net/odium/butler/sedulous/remorse").build()
                val response = PointManager.getPonitNet().newCall(request)?.execute()
                val bodyStr = response?.body?.string() ?: ""
                var data = getBeanByGson(bodyStr, SourceData::class.java)
                AppLogs.dLog(APP.instance.TAG, "查询归因:${toJson(data)}")
                if (data?.campaign_id.isNullOrEmpty() && currentCount < maxCount) {
                    delay(500)
                    getCampaign(false)
                    return@loadData
                }
                CacheManager.campaignId = data?.campaign_id?:""
                PointEvent.posePoint(PointEventKey.attribution_suc)
                PointEvent.posePoint(PointEventKey.track_platform, Bundle().apply {
                    putString("from",data?.track_platform?:"")
                    putLong(PointValueKey.load_time,(System.currentTimeMillis()-startTime)/1000)
                })
            }
        }, failBack = {}, 1)

    }

}