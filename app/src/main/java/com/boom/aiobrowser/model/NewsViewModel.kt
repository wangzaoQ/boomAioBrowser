package com.boom.aiobrowser.model

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADDataManager
import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.NewsData.Companion.TYPE_DOWNLOAD_VIDEO
import com.boom.aiobrowser.firebase.FirebaseConfig
import com.boom.aiobrowser.net.Net
import com.boom.aiobrowser.net.NetController
import com.boom.aiobrowser.net.NetParams
import com.boom.aiobrowser.net.NetRequest
import com.boom.aiobrowser.other.NewsConfig
import com.boom.aiobrowser.other.NewsConfig.NO_NF_VIDEO_TAG
import com.boom.aiobrowser.other.NewsConfig.NO_SESSION_TAG
import com.boom.aiobrowser.other.NewsConfig.TOPIC_TAG
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.video.VideoPreloadManager

class NewsViewModel : BaseDataModel() {
    var newsLiveData = MutableLiveData<List<NewsData>>()
    var newsDetailsLiveData = MutableLiveData<MutableList<NewsData>>()
    var newsRelatedLiveData = MutableLiveData<MutableList<NewsData>>()
    var newsRecommendLiveData = MutableLiveData<MutableList<NewsData>>()
    var newsVideoLiveData = MutableLiveData<MutableList<NewsData>>()
    var newsTopicListLiveData = MutableLiveData<HashMap<Int,MutableList<NewsData>>>()
    var newsDownloadVideoLiveData = MutableLiveData<MutableList<NewsData>>()
    var newsHotVideoLiveData = MutableLiveData<MutableList<NewsData>>()


    fun getDownloadVideo(dataList:MutableList<NewsData>,page:Int,refresh:Boolean=false) {
        var newsList:MutableList<NewsData>?=null
        if (page == 1){
            newsList = CacheManager.getNewsSaveList("recommendVideo")
        }
        if (newsList.isNullOrEmpty() || refresh){
            loadData(loadBack = {
                var list = NetRequest.request(HashMap<String, Any>().apply {
                    put("sessionKey", NetParams.DOWNLOAD_UNLOCK_PUSH)
                }) { NetController.getNewsList(NetParams.getParamsMap(NetParams.DOWNLOAD_UNLOCK_PUSH)) }.data
                    ?: mutableListOf()
                list = detailHistoryVideo(dataList?: mutableListOf(),list)
                if (list.isNullOrEmpty()){
                    var url = NetParams.videoMapToUrl(NetParams.getParamsMap(NetParams.NEWS_HOME_VIDEO))
                    list = NetRequest.request(HashMap<String, Any>().apply { put("sessionKey", NetParams.NEWS_HOME_VIDEO) }){
                        NetController.getNewsList(url)
                    }.data ?: mutableListOf()
                    list = detailHistoryVideo(dataList?: mutableListOf(),list)
                }
                list.forEach {
                    it.dataType = NewsData.TYPE_DOWNLOAD_VIDEO
                }
                newsDownloadVideoLiveData.postValue(list)
            }, failBack = {})
        }
    }

    fun getNewsData(dataList:MutableList<NewsData>,topic:String,page:Int,refresh:Boolean=false) {
        var newsList:MutableList<NewsData>?=null
        if (page == 1){
            var middleTime = System.currentTimeMillis()-CacheManager.getNewsSaveTime(topic)
            if (middleTime>5*60*1000){
                CacheManager.saveNewsSaveList(topic, mutableListOf())
            }
            newsList = CacheManager.getNewsSaveList(topic)
        }
        loadData(loadBack = {
            if (newsList.isNullOrEmpty().not() && refresh.not()){
                newsList = detailHistory(dataList,newsList!!)
                newsLiveData.postValue(addHomeData(dataList,topic,page,newsList!!))
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
                list = detailHistory(dataList,list)
                if (topic == "${NewsConfig.TOPIC_TAG}${NetParams.PUBLIC_SAFETY}" && list.isNullOrEmpty()){
                    list = NetRequest.request(HashMap<String, Any>().apply {
                        put("sessionKey",topic)
                    }) { NetController.getNewsList(NetParams.getParamsMap(topic, currentPage = page,specialKey="noLocation")) }.data
                        ?: mutableListOf()
                }
                list = detailHistory(dataList,list)
                newsLiveData.postValue(addHomeData(dataList,topic,page,list))
            }
        }, failBack = {

        }, 1)
    }


    fun getNewsByTopic(topic: String,page: Int,isSearch:Boolean=false) {
        loadData(loadBack = {
            var dataType = 0
            //topic
            var list:MutableList<NewsData>?=null

            list = NetRequest.request(HashMap<String, Any>().apply {
                put("sessionKey","${NewsConfig.NO_SESSION_TAG}${topic}" )
            }) {
                if (isSearch){
                    NetController.searchNews(topic)
                }else{
                    NetController.getNewsList(NetParams.getParamsMap("${NewsConfig.NO_SESSION_TAG}${topic}",page))
                }
            }.data?: mutableListOf()

            if (list.isNullOrEmpty()){
                dataType = 1
                list = NetRequest.request(HashMap<String, Any>().apply {
                    put("sessionKey","${NewsConfig.NO_SESSION_TAG}${NetParams.PUBLIC_SAFETY}" )
                }) {
                    NetController.getNewsList(NetParams.getParamsMap("${NewsConfig.NO_SESSION_TAG}${NetParams.PUBLIC_SAFETY}",page))
                }.data?: mutableListOf()
            }
            if (list.isNullOrEmpty()){
                dataType =2
                list = NetRequest.request(HashMap<String, Any>().apply {
                    put("sessionKey","${NewsConfig.NO_SESSION_TAG}${topic}" )
                }) {
                    NetController.getEditorNewsList(NetParams.getParamsMap("${NewsConfig.NO_SESSION_TAG}${topic}",page))
                }.data?: mutableListOf()
            }
            newsTopicListLiveData.postValue(HashMap<Int, MutableList<NewsData>>().apply {
                put(dataType,list?: mutableListOf())
            })
        }, failBack = {})
    }

    fun getNewsVideoList(enumName :String,data: NewsData?=null,dataList: MutableList<NewsData>?=null){
        loadData(loadBack = {
            var list:MutableList<NewsData>
            var endEnumName = enumName
            if (endEnumName == ""){
                var url = NetParams.videoMapToUrl(NetParams.getParamsMap(NetParams.NEWS_HOME_VIDEO))
                list = NetRequest.request(HashMap<String, Any>().apply { put("sessionKey", NetParams.NEWS_HOME_VIDEO) }){
                    NetController.getNewsList(url)
                }.data ?: mutableListOf()
            }else{
                //1 取当前访问的视频
                if (FirebaseConfig.isDownloadConfig){
                    if (endEnumName == NFEnum.NF_EDITOR.menuName){
                        endEnumName = NetParams.DOWNLOAD_EDITOR_PUSH
                    }else if (endEnumName == NFEnum.NF_UNLOCK.menuName){
                        endEnumName = NetParams.DOWNLOAD_UNLOCK_PUSH
                    }else if (endEnumName == NFEnum.NF_NEWS.menuName){
                        endEnumName = NetParams.DOWNLOAD_FOR_YOU_PUSH
                    }else if (endEnumName == NFEnum.NF_NEW_USER.menuName){
                        endEnumName = NetParams.DOWNLOAD_NEW_USER_PUSH
                    }
                }

                var url = NetParams.videoMapToUrl(NetParams.getParamsMap("${NO_NF_VIDEO_TAG}${endEnumName}"))
                list = NetRequest.request(HashMap<String, Any>().apply { put("sessionKey", "${endEnumName}") }){
                    NetController.getNewsList(url)
                }.data ?: mutableListOf()
                list = detailHistoryVideo(dataList?: mutableListOf(),list)
                //2.获取当前topic的视频
                if (list.isNullOrEmpty()){
                    var topics = data?.tdetai
                    var session = ""
                    if (topics.isNullOrEmpty().not()){
                        var builder = StringBuilder()
                        topics!!.forEach {
                            session+=it
                            builder.append("tweakn=${Uri.encode(it)}&")
                        }
                        var map = NetParams.getParamsMap(session)
                        map.forEach {
                            builder.append(it.key).append("=").append(Uri.encode(it.value)).append("&")
                        }
                        builder.append("vback=${true}")
                        var url = "${Net.rootUrl}/api/nemplo?${builder.toString()}"
                        AppLogs.dLog(Net.TAG,"getNewsVideoList: ${url}")
                        list = NetRequest.request(HashMap<String, Any>().apply { put("sessionKey", session) }){
                            NetController.getNewsList(url)
                        }.data ?: mutableListOf()
                    }
                    list = detailHistoryVideo(dataList?: mutableListOf(),list)
                }
                //3 拉正常视频
                if (list.isNullOrEmpty()){
                    var url = NetParams.videoMapToUrl(NetParams.getParamsMap(NetParams.NEWS_HOME_VIDEO))
                    list = NetRequest.request(HashMap<String, Any>().apply { put("sessionKey", NetParams.NEWS_HOME_VIDEO) }){
                        NetController.getNewsList(url)
                    }.data ?: mutableListOf()
                    list = detailHistoryVideo(dataList?: mutableListOf(),list)
                }
            }

            newsVideoLiveData.postValue(list)
            if (endEnumName == ""){
                CacheManager.videoList = list
            }
            for (i in 0 until list.size){
                VideoPreloadManager.serialList(1, mutableListOf<NewsData>().apply {
                    add(list.get(i))
                })
            }
        }, failBack = {
            var cacheList = CacheManager.videoList
            if (cacheList.isNotEmpty()){
                newsVideoLiveData.postValue(cacheList)
            }
        },1)
    }

    private suspend fun addHomeData(oldList:MutableList<NewsData>,topic:String, page:Int, list:MutableList<NewsData>) :MutableList<NewsData>{
        if ((NetParams.MAIN == topic || topic == NetParams.FOR_YOU) && page == 1){
            if (NetParams.MAIN == topic){
                var removeList = mutableListOf<NewsData>()
                for (i in 0 until list.size){
                    if (list.get(i).dataType != 0){
                        removeList.add(list.get(i))
                    }
                }
                list.removeAll(removeList)
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

            addVideo(list)
            addLocation(list)
            addTopic(list)
        }
        if (topic.startsWith(NewsConfig.LOCAL_TAG)){
            var topicNew = topic.substringAfter(NewsConfig.LOCAL_TAG)
            list.forEach {
                it.areaTag = topicNew
            }
        }else {
            var locationData = CacheManager.locationData
            if (locationData!=null&&locationData.locationSuccess){
                list.forEach {
                    var showArea = it.asilve?.contains(locationData.locationArea)?:false
                    if (showArea){
                        it.areaTag = locationData.locationCity
                    }
                }
            }
        }
        CacheManager.saveNewsSaveList(topic, list)
        CacheManager.saveNewsSaveTime(topic)
        return list
    }

    private fun addTopic(list: MutableList<NewsData>) {
        var insertIndex = -1
        var newsCount = 0
        for (i in 0 until list.size) {
            if (list.get(i).dataType == NewsData.TYPE_NEWS) {
                newsCount++
            }
            if (newsCount == 4) {
                insertIndex = i
                break
            }
        }

        if (insertIndex >= 0) {
            list.add(insertIndex, NewsData().apply {
                var topicList = CacheManager.homeTopicList
                if (topicList.isNullOrEmpty()) {
                    isLoading = true
                    this.topicList = mutableListOf()
                } else {
                    this.topicList = topicList
                }
                dataType = NewsData.TYPE_HOME_NEWS_TOPIC
            })
        }
    }

    private fun addVideo(
        list: MutableList<NewsData>
    ) {
        var insertIndex = -1
        var newsCount = 0
        for (i in 0 until list.size) {
            if (list.get(i).dataType == NewsData.TYPE_NEWS) {
                newsCount++
            }
            if (newsCount == 3) {
                insertIndex = i
                break
            }
        }
        list.add(insertIndex, NewsData().apply {
            dataType = NewsData.TYPE_HOME_NEWS_VIDEO
            var tempList = CacheManager.videoList
            if (tempList.isNullOrEmpty()) {
                var list = mutableListOf<NewsData>()
                for (i in 0 until 10) {
                    list.add(NewsData().apply {
                        isLoading = true
                    })
                }
                this.videoList = list
            } else {
                this.videoList = tempList
            }
        })
    }

    private fun addLocation(list: MutableList<NewsData>): Int {
        var isSuccess = CacheManager.locationData?.locationSuccess ?: false
        var insertIndex = -1
        if (isSuccess.not()) {
            var newsCount = 0
            for (i in 0 until list.size) {
                if (list.get(i).dataType == NewsData.TYPE_NEWS) {
                    newsCount++
                }
                if (newsCount == 3) {
                    insertIndex = i
                    break
                }
            }

            if (insertIndex >= 0) {
                list.add(insertIndex, NewsData().apply {
                    dataType = NewsData.TYPE_HOME_NEWS_LOCAL
                })
            }
        }
        return insertIndex
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
                    areaTag = newData.areaTag
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
                            if (newsList.size == 5 && AioADDataManager.getCacheAD(ADEnum.BANNER_AD_NEWS_DETAILS)!=null){
                                newsList.add(NewsData().apply {
                                    dataType = NewsData.TYPE_AD
                                    adTag = ADEnum.BANNER_AD_NEWS_DETAILS.adName
                                })
                            }
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


    fun detailHistory(oldList:MutableList<NewsData>,newsList:MutableList<NewsData>):MutableList<NewsData>{
        AppLogs.dLog(TAG,"请求的数据长度:${newsList.size}")
        if (newsList.isNullOrEmpty())return mutableListOf()
        if (oldList.isNullOrEmpty())return newsList
        var removeList = mutableListOf<NewsData>()
        newsList.forEach {
            for (i in 0 until oldList.size){
                var oldData = oldList.get(i)
                if (oldData.dataType == NewsData.TYPE_NEWS){
                    if (it.itackl == oldData.itackl){
                        removeList.add(it)
                    }
                }
            }
        }
        newsList.removeAll(removeList)
        AppLogs.dLog(TAG,"经过过滤后的数据长度:${newsList.size}  移除了:${removeList.size}")
        return newsList
    }
    fun detailHistoryVideo(oldList:MutableList<NewsData>,newsList:MutableList<NewsData>):MutableList<NewsData>{
        AppLogs.dLog(TAG,"请求的数据长度:${newsList.size}")
        if (newsList.isNullOrEmpty())return mutableListOf()
        if (oldList.isNullOrEmpty())return newsList
        var removeList = mutableListOf<NewsData>()
        newsList.forEach {
            for (i in 0 until oldList.size){
                var oldData = oldList.get(i)
                if (it.itackl == oldData.itackl || it.vbreas.isNullOrEmpty()){
                    removeList.add(it)
                }
            }
        }
        newsList.removeAll(removeList)
        AppLogs.dLog(TAG,"经过过滤后的数据长度:${newsList.size}  移除了:${removeList.size}")
        return newsList
    }


    fun getHotVideos(){
        loadData(loadBack = {
            var list = NetRequest.request(HashMap<String, Any>().apply {
                put("sessionKey", "${TOPIC_TAG}hot dance")
            }) { NetController.getNewsList(NetParams.getParamsMap("${TOPIC_TAG}hot dance")) }.data
                ?: mutableListOf()
            list.forEach {
                it.dataType = TYPE_DOWNLOAD_VIDEO
            }
            newsHotVideoLiveData.postValue(list)
        }, failBack = {})
    }

}