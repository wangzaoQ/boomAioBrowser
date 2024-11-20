package com.boom.aiobrowser.model

import com.boom.aiobrowser.APP
import com.boom.aiobrowser.data.TopicBean
import com.boom.aiobrowser.data.WebConfigData
import com.boom.aiobrowser.net.NetController
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.getTopicDataLan
import com.boom.aiobrowser.other.TopicConfig
import java.util.Locale

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
}