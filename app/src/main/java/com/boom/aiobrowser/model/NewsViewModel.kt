package com.boom.aiobrowser.model

import androidx.lifecycle.MutableLiveData
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.net.NetController
import com.boom.aiobrowser.net.NetParams
import com.boom.aiobrowser.net.NetRequest
import com.boom.aiobrowser.other.NewsConfig
import com.boom.aiobrowser.tools.CacheManager

class NewsViewModel : BaseDataModel() {
    var newsLiveData = MutableLiveData<List<NewsData>>()
    var newsDetailsLiveData = MutableLiveData<MutableList<NewsData>>()



    fun getNewsData(topic:String,page:Int,refresh:Boolean=false) {
        var middleTime = System.currentTimeMillis()-CacheManager.newsSaveTime
        if (middleTime>5*60*1000){
            CacheManager.newsList = mutableListOf()
        }
        var newsList = CacheManager.newsList
        if (newsList.isNotEmpty() && refresh.not()){
            newsLiveData.postValue(addHomeData(topic,page,newsList))
        }else{
            loadData(loadBack = {
                var list :MutableList<NewsData>
                if (topic =="${NewsConfig.TOPIC_TAG}${APP.instance.getString(R.string.app_trending_today)}"){
                    list = NetRequest.request(HashMap<String, Any>().apply {
                        put("sessionKey", APP.instance.getString(R.string.app_trending_today))
                    }) { NetController.getHotNewsList(HashMap()) }.data
                        ?: mutableListOf()
                    list.forEach {
                        it.dataType = NewsData.TYPE_DETAILS_NEWS_SEARCH
                    }
                }else{
                    list = NetRequest.request(HashMap<String, Any>().apply {
                        put("sessionKey",topic )
                    }) { NetController.getNewsList(NetParams.getParamsMap(topic,page)) }.data?: mutableListOf()
                    if (topic == "${NewsConfig.TOPIC_TAG}${APP.instance.getString(R.string.app_local_brief)}"){
                        list.forEach {
                            it.dataType = NewsData.TYPE_DETAILS_NEWS_SEARCH
                        }
                    }else if (topic == "${NewsConfig.TOPIC_TAG}${APP.instance.getString(R.string.app_movie)}"){
                        list.forEach {
                            it.dataType = NewsData.TYPE_DETAILS_NEWS_SEARCH_FILM
                        }
                    }
                }
                newsLiveData.postValue(addHomeData(topic,page,list))
            }, failBack = {

            }, 1)
        }
    }

    private fun addHomeData(topic:String,page:Int,list:MutableList<NewsData>) :MutableList<NewsData>{
        if (NetParams.FOR_YOU == topic && page == 1){
            list.add(0,NewsData().apply {
                dataType = NewsData.TYPE_HOME_NEWS_TRENDING
                var trendNews = CacheManager.trendNews
                if (trendNews.isNullOrEmpty()){
                    trendList = mutableListOf<NewsData>().apply {
                        add(NewsData().apply {
                            isLoading = true
                        })
                        add(NewsData().apply {
                            isLoading = true
                        })
                        add(NewsData().apply {
                            isLoading = true
                        })
                    }
                }else{
                    if (trendNews.size>3){
                        trendNews = trendNews.subList(0,3)
                    }
                    trendList = trendNews
                }
            })
            list.add(0,NewsData().apply {
                dataType = NewsData.TYPE_HOME_NEWS_TOP
            })
        }
        return list
    }


    fun getNewsDetails(newData: NewsData) {
        loadData(loadBack = {
            NetRequest.request<NewsData> { NetController.getNewsDetails(newData.itackl?:"") }.apply {
                var detailsData = this.data ?: return@apply
                var newsList = mutableListOf<NewsData>()
                var firstShowMedia = false
                // video or img
                if (newData.vbreas.isNullOrEmpty().not()){
                    newsList.add(NewsData().apply {
                        dataType = NewsData.TYPE_DETAILS_NEWS_TOP_VIDEO
                        vbreas = detailsData.vbreas
                        iassum = newData.iassum
                    })
                    firstShowMedia = true
                }else if (newData.iassum.isNullOrEmpty().not()){
                    newsList.add(NewsData().apply {
                        dataType = NewsData.TYPE_DETAILS_NEWS_TOP_IMG
                        iassum = newData.iassum
                    })
                    firstShowMedia = true
                }

                // title
                newsList.add(NewsData().apply {
                    dataType = NewsData.TYPE_DETAILS_NEWS_TITLE
                    tconsi = detailsData.tconsi
                    sfindi = detailsData.sfindi
                    sschem = detailsData.sschem
                    pphilo = detailsData.pphilo
                })
                //content
                var size = detailsData?.cvehic?.size ?: 0
                var textSize = 0
                if (size > 0) {
                    var textLength = 0
                    for (i in 0 until size) {
                        var bean = detailsData?.cvehic?.get(i) ?: continue
                        var contentType = -1
                        if (bean.tmouth == "img" && (firstShowMedia.not() || textSize>0)) {
                            contentType = NewsData.TYPE_DETAILS_NEWS_IMG
                        } else if (bean.tmouth == "text") {
                            contentType = NewsData.TYPE_DETAILS_NEWS_TEXT
                            textLength += (bean.dgas ?: "").length
                            textSize++
                        }
                        if (contentType != -1) {
                            newsList.add(NewsData().apply {
                                dataType = contentType
                                if (contentType == NewsData.TYPE_DETAILS_NEWS_IMG) {
                                    iassum = bean.dgas
                                } else if (contentType == NewsData.TYPE_DETAILS_NEWS_TEXT) {
                                    tconsi = bean.dgas ?: ""
                                    lcousi = bean.lcousi
//                                    links = mutableListOf<LinkData>().apply {
//                                        add(LinkData().apply {
//                                            sfunny = mutableListOf<Int>().apply {
//                                                add(3)
//                                                add(8)
//                                            }
//                                            lhate = "https://www.baidu.com"
//                                        })
//                                    }
                                }
                            })
                        }
                    }
                }

                // readSource
                newsList.add(NewsData().apply {
                    dataType = NewsData.TYPE_DETAILS_NEWS_READ_SOURCE
                    uweek = newData.uweek
                })
                newsDetailsLiveData.postValue(newsList)
        }}, failBack = {},1)
    }
}