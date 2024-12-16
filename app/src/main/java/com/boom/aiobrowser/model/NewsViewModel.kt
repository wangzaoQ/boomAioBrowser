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
import com.boom.aiobrowser.tools.video.VideoPreloadManager

class NewsViewModel : BaseDataModel() {
    var newsLiveData = MutableLiveData<List<NewsData>>()
    var newsDetailsLiveData = MutableLiveData<MutableList<NewsData>>()
    var newsRelatedLiveData = MutableLiveData<MutableList<NewsData>>()
    var newsRecommendLiveData = MutableLiveData<MutableList<NewsData>>()
    var newsVideoLiveData = MutableLiveData<MutableList<NewsData>>()



    fun getNewsData(topic:String,page:Int,refresh:Boolean=false) {
        var middleTime = System.currentTimeMillis()-CacheManager.newsSaveTime
        if (middleTime>5*60*1000){
            CacheManager.newsList = mutableListOf()
        }
        var newsList = CacheManager.newsList
        loadData(loadBack = {
            if (newsList.isNotEmpty() && refresh.not()){
                newsLiveData.postValue(addHomeData(topic,page,newsList))
            }else{
                var list :MutableList<NewsData>
                if (topic =="${NewsConfig.TOPIC_TAG}${APP.instance.getString(R.string.app_trending_today)}"){
                    list = CacheManager.trendNews
                    if (list.isNullOrEmpty()){
                        list = NetController.getTrendNews("GTR-4").data?: mutableListOf()
                    }
                    CacheManager.trendNews = list
                    list.forEach {
                        it.dataType = NewsData.TYPE_DETAILS_NEWS_SEARCH
                    }
                }else if(topic == "${NewsConfig.TOPIC_TAG}${APP.instance.getString(R.string.app_local_brief)}"){
                    list = NetRequest.request(HashMap<String, Any>().apply {
                        put("sessionKey",topic)
                    }) { NetController.getNewsList(NetParams.getParamsMap(topic, currentPage = page)) }.data
                        ?: mutableListOf()
                    list.forEach {
                        it.dataType = NewsData.TYPE_DETAILS_NEWS_SEARCH
                    }
                }else{
                    list = NetRequest.request(HashMap<String, Any>().apply {
                        put("sessionKey",topic )
                    }) { NetController.getNewsList(NetParams.getParamsMap(topic,page)) }.data?: mutableListOf()
                     if (topic == "${NewsConfig.TOPIC_TAG}${APP.instance.getString(R.string.app_movie)}"){
                        list.forEach {
                            it.dataType = NewsData.TYPE_DETAILS_NEWS_SEARCH_FILM
                        }
                    }
                }
                newsLiveData.postValue(addHomeData(topic,page,list))
            }
        }, failBack = {

        }, 1)
    }

    fun getNewsVideoList(){
        loadData(loadBack = {
            var url = NetParams.videoMapToUrl(NetParams.getParamsMap(NetParams.NEWS_HOME_VIDEO))
            var list = NetRequest.request(HashMap<String, Any>().apply { put("sessionKey", NetParams.NEWS_HOME_VIDEO) }){
                NetController.getNewsList(url)
            }.data ?: mutableListOf()
            newsVideoLiveData.postValue(list)
            CacheManager.videoList = list
            for (i in 0 until list.size){
                VideoPreloadManager.serialList(1, mutableListOf<NewsData>().apply {
                    add(list.get(i))
                })
            }
        }, failBack = {},1)
    }

    private suspend fun addHomeData(topic:String, page:Int, list:MutableList<NewsData>) :MutableList<NewsData>{
        if ((NetParams.MAIN == topic || topic == NetParams.FOR_YOU) && page == 1){
            if (NetParams.MAIN == topic){
                list.add(0,NewsData().apply {
                    dataType = NewsData.TYPE_HOME_NEWS_TRENDING
                    var trendNews = APP.instance.appModel.getTrendsNewsData()
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

            var isSuccess = CacheManager.locationData?.locationSuccess?:false
            var insertIndex = -1
            if (isSuccess.not()){
                var newsCount = 0
                for (i in 0 until list.size){
                    if (list.get(i).dataType == NewsData.TYPE_NEWS){
                        newsCount++
                    }
                    if (newsCount == 3){
                        insertIndex = i
                        break
                    }
                }

                if (insertIndex>=0){
                    list.add(insertIndex,NewsData().apply {
                        dataType = NewsData.TYPE_HOME_NEWS_LOCAL
                    })
                }
            }
            if (NetParams.MAIN == topic){
                list.add(insertIndex,NewsData().apply {
                    dataType = NewsData.TYPE_HOME_NEWS_VIDEO
                    var tempList = CacheManager.videoList
                    if (tempList.isNullOrEmpty()){
                        var list = mutableListOf<NewsData>()
                        for (i in 0 until 10){
                            list.add(NewsData().apply {
                                isLoading = true
                            })
                        }
                        this.videoList = list
                    }else{
                        this.videoList = tempList
                    }
                })
            }
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

                // topic
                if (detailsData.tdetai.isNullOrEmpty().not()){
                    newsList.add(NewsData().apply {
                        dataType = NewsData.TYPE_DETAILS_NEWS_TOPIC
                        tdetai = newData.tdetai
                    })
                }

                // related news
                var relatedList = mutableListOf<NewsData>()
                for (i in 0 until 10){
                    relatedList.add(NewsData().apply {
                        isLoading = true
                    })
                }
                newsList.add(NewsData().apply {
                    dataType = NewsData.TYPE_DETAILS_NEWS_RELATED
                    this.relatedList = relatedList
                })
                newsDetailsLiveData.postValue(newsList)
        }}, failBack = {},1)
    }
    // 相关新闻
    fun getNewsRelated(newData: NewsData?) {
        if (newData == null)return
        loadData(loadBack = {
            var list = NetRequest.request(HashMap<String, Any>().apply { put("sessionKey", NetParams.NEWS_RELATED) })
            {
                var map = NetParams.getParamsMap(NetParams.NEWS_RELATED)
                map.put("itackl",newData.itackl?:"")
                NetController.getRelatedNews(map)
            }.data ?: mutableListOf()
            newsRelatedLiveData.postValue(list)
        }, failBack = {},1)
    }

    //推荐新闻
    fun getNewsLike() {
        loadData(loadBack = {
            var url = NetParams.likeMapToUrl(NetParams.getParamsMap(NetParams.NEWS_RECOMMEND))
            var list = NetRequest.request(HashMap<String, Any>().apply { put("sessionKey", NetParams.NEWS_RECOMMEND) }){
                NetController.getNewsList(url)
            }.data ?: mutableListOf()
            newsRecommendLiveData.postValue(list)
        }, failBack = {},1)
    }
}