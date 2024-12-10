package com.boom.aiobrowser.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.boom.aiobrowser.data.LocationData
import com.boom.aiobrowser.net.NetController
import com.boom.aiobrowser.net.NetRequest
import com.boom.aiobrowser.net.WebNet
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.LocationManager
import com.ironsource.da
import com.ironsource.lo
import com.mbridge.msdk.dycreator.viewmodel.BaseViewModel
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import java.util.Locale

class LocationViewModel:BaseDataModel() {
    var cityLiveData = MutableLiveData<List<LocationData>>()
    var recommendLiveData = MutableLiveData<List<LocationData>>()

    var completeLiveData = MutableLiveData<LocationData>()

    var searchLiveData = MutableLiveData<List<LocationData>>()

    fun getRecommendList(){
        loadData(loadBack={
            var list: MutableList<LocationData>? = LocationManager.recommendCity().get(Locale.getDefault().language)
            if (list.isNullOrEmpty()) {
                list = LocationManager.recommendCity().get("en")
            }
            list?.let {
                cityLiveData.postValue(it)
            }
        }, failBack = {},1)
    }


    fun getRecommendAddList(){
        loadData(loadBack={
            var list: MutableList<LocationData>? = LocationManager.recommendCity().get(Locale.getDefault().language)
            if (list.isNullOrEmpty()) {
                list = LocationManager.recommendCity().get("en")
            }
            var alreadyList = CacheManager.alreadyAddCityList
            var endList = mutableListOf<LocationData>()
            list?.forEach {
                var data = it
                var index = -1
                for (i in 0 until alreadyList.size){
                    if (data.locationCity == alreadyList.get(i).locationCity){
                        index = i
                        break
                    }
                }
                if (index == -1){
                    endList.add(data)
                }
            }
            recommendLiveData.postValue(endList)
        }, failBack = {},1)
    }

    fun getAreaData(locationData:LocationData,addCityList:Boolean){
        loadData(loadBack={
            NetRequest.request { NetController.getLocation(locationData!!.longitude, locationData!!.latitude) }.data?.apply {
                if (asilve.isNullOrEmpty()){
                    if (acoat!=null && acoat!!.asilve.isNotEmpty()){
                        locationData!!.locationArea = acoat!!.asilve
                    }
                }else{
                    locationData!!.locationArea = asilve
                }
                CacheManager.locationData = locationData
                if (addCityList){
                    CacheManager.addAlreadyAddCity(locationData)
                }
                completeLiveData.postValue(locationData)
            }
        }, failBack = {
            CacheManager.locationData = locationData
            if (addCityList){
                CacheManager.addAlreadyAddCity(locationData)
            }
            completeLiveData.postValue(locationData)
        },1)
    }


    // 和风天气搜索城市
    fun searchCityList(cityName: String) {
        loadData(loadBack = {

            var url ="https://geoapi.qweather.com/v2/city/lookup?location=${cityName}" +
                    "&key=${URLEncoder.encode("cfbf61df245b453094c4baf9c42017d4", "UTF-8")}&lang=${URLEncoder.encode(Locale.getDefault().language, "UTF-8")}"
            var jsonString = ""
            val request: Request = Request.Builder()
                .url(url)
                .build()
            runCatching {
                var call = WebNet.netClient.newCall(request)
                var result = call?.execute()
                jsonString = result?.body?.string() ?: ""
            }
            val pJson = JSONObject(jsonString)
            if ("200".equals(pJson.getString("code"))) {
                val citysList = mutableListOf<LocationData>()
                val locations = pJson.getJSONArray("location")
                for (i in 0..locations.length() - 1) {
                    citysList.add(
                        LocationData.createDataByGPS(
                            locations.getJSONObject(i).getString("name"),
                            locations.getJSONObject(i).getString("country"),
                            locations.getJSONObject(i).getString("lon").toDouble(),
                            locations.getJSONObject(i).getString("lat").toDouble())
                    )
                }
                searchLiveData.postValue(citysList)
            }else{
                failLiveData.postValue("no data")
            }
        }, failBack = {
            failLiveData.postValue(it.message)
        },1)
    }
}