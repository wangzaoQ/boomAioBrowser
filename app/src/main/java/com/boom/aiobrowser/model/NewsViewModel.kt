package com.boom.aiobrowser.model

import androidx.lifecycle.MutableLiveData
import com.boom.aiobrowser.data.LocationData
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.net.Net
import com.boom.aiobrowser.net.NetController
import com.boom.aiobrowser.net.NetRequest
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class NewsViewModel : BaseDataModel() {
    var newsLiveData = MutableLiveData<List<NewsData>>()

    suspend fun getLocationByIp(): LocationData? {
        var locationData: LocationData? = null
        runCatching {
            var request = Request.Builder().get().url("https://ipinfo.io/json").build()
            val response = OkHttpClient.Builder().build().newCall(request)?.execute()
            val bodyStr = response?.body?.string() ?: ""
            JSONObject(bodyStr).apply {
                var city = getString("city")
//                var region = getString("region")
                var country = getString("country")
                var loc = getString("loc").split(",")
                var lat = loc[0].toDouble()
                var lon = loc[1].toDouble()
                if (city.isNullOrEmpty().not() && country.isNullOrEmpty().not()) {
                    locationData = LocationData.createDataByIp(city,country,lon,lat)
                }
            }
        }.onFailure {
            AppLogs.eLog(TAG, it.stackTraceToString())
        }
        if (locationData!=null){
            runCatching {
                NetRequest.request { NetController.getLocation(locationData!!.longitude, locationData!!.latitude) }.data?.apply {
                    if (asilve.isNullOrEmpty()){
                        if (acoat!=null && acoat!!.asilve.isNotEmpty()){
                            locationData!!.locationArea = acoat!!.asilve
                        }
                    }else{
                        locationData!!.locationArea = asilve
                    }
                }
            }
        }
        if (locationData!=null){
            CacheManager.locationData = locationData
        }
        return locationData
    }

    fun getNewsData() {
        var middleTime = System.currentTimeMillis()-CacheManager.newsSaveTime
        if (middleTime>5*60*1000){
            CacheManager.newsList = mutableListOf()
        }
        var newsList = CacheManager.newsList
        if (newsList.isNotEmpty()){
            newsLiveData.postValue(newsList)
        }else{
            var map = HashMap<String, String>()
            loadData(loadBack = {
                var locationData = CacheManager.locationData
                if (locationData == null){
                    locationData = getLocationByIp()
                }
                if (locationData != null) {
                    if (locationData.locationCity.isNullOrEmpty().not()) {
                        map.put("csuck", locationData.locationCity)
                    }
                    if (locationData.locationArea.isNullOrEmpty().not()) {
                        map.put("asilve", locationData.locationArea)
                    }
                }
                var session = CacheManager.getSession("forYou")
                map.put("smessa", "CS")
                if (session.isNotEmpty()){
                    map.put("sstop", session)
                }
                var list = NetRequest.request(HashMap<String, Any>().apply {
                    put("sessionKey", "forYou")
                }) { NetController.getNewsList(map) }.data?: mutableListOf()
                newsLiveData.postValue(list)
            }, failBack = {

            }, 1)
        }
    }
}