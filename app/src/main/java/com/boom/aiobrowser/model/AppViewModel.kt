package com.boom.aiobrowser.model

import com.boom.aiobrowser.APP
import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.TopicBean
import com.boom.aiobrowser.data.WebConfigData
import com.boom.aiobrowser.net.NetController
import com.boom.aiobrowser.nf.NFManager
import com.boom.aiobrowser.nf.NFShow
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.getTopicDataLan
import com.boom.aiobrowser.other.TopicConfig
import com.ironsource.ol
import kotlinx.coroutines.delay
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

    fun getTopic() {
        var list = CacheManager.defaultTopicList
        if (list.isNullOrEmpty()) {
//            var topString = mutableListOf<String>(
//                TopicConfig.TOPIC_FOR_YOU,
//                TopicConfig.TOPIC_LOCAL,
//                TopicConfig.TOPIC_POLITICS,
//                TopicConfig.TOPIC_ENTERTAINMENT,
//                TopicConfig.TOPIC_PUBLIC_SAFETY
//            )
            loadData(loadBack = {
//                var data = NetController.getTopic().data
//                topString.forEachIndexed { index, s ->
//                    data?.forEachIndexed { index, nowTopicData ->
//                        if (s == nowTopicData.nsand) {
//                            list.add(TopicBean().apply {
//                                topic = getTopicDataLan(nowTopicData)
//                                id = nowTopicData.nsand ?: ""
//                            })
//                        }
//                    }
//                }
                list.add(getTopicByLanguage(TopicConfig.TOPIC_FOR_YOU))
                list.add(getTopicByLanguage(TopicConfig.TOPIC_LOCAL))
                list.add(getTopicByLanguage(TopicConfig.TOPIC_POLITICS))
                list.add(getTopicByLanguage(TopicConfig.TOPIC_ENTERTAINMENT))
                list.add(getTopicByLanguage(TopicConfig.TOPIC_PUBLIC_SAFETY))

                CacheManager.defaultTopicList = list
                APP.topicLiveData.postValue(list)
            }, failBack = {
            }, 1)
        } else {
            APP.topicLiveData.postValue(list)
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

    fun getTrendsNews(){
        loadData(loadBack = {
            getTrendsNewsData()
            if (APP.isDebug){
                NFShow.showNewsNFFilter(NFEnum.NF_TREND)
            }
            APP.trendNewsComplete.postValue(0)
        }, failBack = {
            AppLogs.eLog(TAG, it.stackTraceToString())
        }, 1)
    }

   suspend fun getTrendsNewsData():MutableList<NewsData>{
        var startTime = CacheManager.trendNewsTime
        if (APP.isDebug){
            if (System.currentTimeMillis()-startTime>10*1000){
                CacheManager.trendNews = mutableListOf()
            }
        }else{
            if (System.currentTimeMillis()-startTime>4*60*60*1000){
                CacheManager.trendNews = mutableListOf()
            }
        }
        var oldList = CacheManager.trendNews
        if (oldList.isNullOrEmpty()){
            var list = NetController.getTrendNews("GTR-4").data?: mutableListOf()
            var index0 = 0
            var index1 = 0
            var index2 = 0
            var endInx = list!!.size
            if (list.size>3){
                if (endInx>15){
                    endInx = 15
                }
                var count = 0
                while (index0 == index1 || index0 == index2 || index1 == index2){
                    count++
                    index0 = Random.nextInt(3,endInx)
                    delay(50)
                    index1 = Random.nextInt(3, endInx)
                    delay(50)
                    index2 = Random.nextInt(3, endInx)
                }
                AppLogs.dLog(APP.instance.TAG,"取随机数 count:${count} index0:${index0} index1:${index1} index2:${index2}")
                for (i in 0 until list.size){
                    if (i == 0 || i == 1 || i == 2)continue
                    if (i == index0 || i == index1 || i == index2 ){
                        list.get(i).isTrendTop = true
                    }
                }
            }
            CacheManager.trendNews = list
            CacheManager.trendNewsTime = System.currentTimeMillis()
            return list
        }else{
            return oldList
        }
    }
}