package com.boom.aiobrowser.net

import com.boom.aiobrowser.data.LocationData
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object NetParams {

    var TAG = "NetParams"

    var FOR_YOU = "forYou"
    var FOR_YOU_PUSH = "forYouPush"

    suspend fun getParamsMap(key:String):HashMap<String,String>{
        var needLocation = false
        var needSession = true
        var isPush = false
        var map = HashMap<String,String>()
        when (key) {
            FOR_YOU,FOR_YOU_PUSH -> {
                needLocation = true
                if (key == FOR_YOU_PUSH){
                    isPush = true
                }
            }
            else -> {}
        }
        filterLocation(needLocation, map)
        filterSession(needSession,key,map)
        if (isPush){
            map.put("cinvit","push")
        }
        return map
    }

    private fun filterSession(needSession: Boolean,key:String, map: java.util.HashMap<String, String>) {
        if (needSession){
            var session = CacheManager.getSession(key)
            map.put("smessa", "CS")
            if (session.isNotEmpty()){
                map.put("sstop", session)
            }
        }
    }

    suspend fun filterLocation(need:Boolean,map:HashMap<String,String>){
        if (need){
            var locationData = getLocation()
            if (locationData != null) {
                if (locationData.locationCity.isNullOrEmpty().not()) {
                    map.put("csuck", locationData.locationCity)
                }
                if (locationData.locationArea.isNullOrEmpty().not()) {
                    map.put("asilve", locationData.locationArea)
                }
            }
        }
    }

    suspend fun getLocation(): LocationData? {
        var locationData = CacheManager.locationData
        if (locationData == null){
            return getLocationByIp()
        }else{
            return locationData
        }
    }

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
}