package com.boom.aiobrowser.model

import androidx.lifecycle.MutableLiveData
import com.boom.aiobrowser.data.LocationData
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.net.Net
import com.boom.aiobrowser.net.NetController
import com.boom.aiobrowser.net.NetParams
import com.boom.aiobrowser.net.NetRequest
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class NewsViewModel : BaseDataModel() {
    var newsLiveData = MutableLiveData<List<NewsData>>()



    fun getNewsData() {
        var middleTime = System.currentTimeMillis()-CacheManager.newsSaveTime
        if (middleTime>5*60*1000){
            CacheManager.newsList = mutableListOf()
        }
        var newsList = CacheManager.newsList
        if (newsList.isNotEmpty()){
            newsLiveData.postValue(newsList)
        }else{
            loadData(loadBack = {
                var list = NetRequest.request(HashMap<String, Any>().apply {
                    put("sessionKey", NetParams.FOR_YOU)
                }) { NetController.getNewsList(NetParams.getParamsMap(NetParams.FOR_YOU)) }.data?: mutableListOf()
                newsLiveData.postValue(list)
            }, failBack = {

            }, 1)
        }
    }
}