package com.boom.aiobrowser.net

import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.LocationData
import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.other.NewsConfig
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object NetParams {

    var TAG = "NetParams"

    var FOR_YOU = "For you"

    var WIDGET = "widget"

    var LOCAL = "local"
    var MOVIE = "11370735975268352"
    var PUBLIC_SAFETY = "Public Safety"

    suspend fun getParamsMap(key:String,currentPage:Int=0):HashMap<String,String>{
        var needLocation = false
        var sessionType = 0
        var isPush = false
//        var fallback = true
        var map = HashMap<String,String>()
        var endKey = key
        var sessionKey = key
        var isTopic = false
        var isSource = false
        if (endKey.startsWith(NewsConfig.TOPIC_TAG)){
            endKey = endKey.substringAfter(NewsConfig.TOPIC_TAG)
            if (endKey == APP.instance.getString(R.string.app_local_brief)){
                endKey = LOCAL
            }
            if (endKey == APP.instance.getString(R.string.app_movie)){
                isSource = true
                endKey = MOVIE
            }else{
                isTopic = true
            }
        }
        when (endKey) {
            FOR_YOU,NFEnum.NF_NEWS.menuName,WIDGET -> {
                needLocation = true
                if (endKey == NFEnum.NF_NEWS.menuName ||endKey == WIDGET){
                    map.put("fit","3:AIOPUSH")
                    isPush = true
                }else if (endKey == FOR_YOU){
                    map.put("fit","3:BROWSER")
                }
            }
            NFEnum.NF_EDITOR.menuName->{

            }
            NFEnum.NF_NEW_USER.menuName->{
                isPush = true
                map.put("opop","1800")
            }
            NFEnum.NF_LOCAL.menuName->{
                needLocation = true
                isPush = true
            }
            LOCAL->{
                needLocation = true
            }
            PUBLIC_SAFETY->{
                needLocation = true
            }
            MOVIE->{
                sessionType = 1
            }

            else -> {
            }
        }
        filterLocation(needLocation, map)
        filterSession(sessionType,sessionKey,map,currentPage)
        if (isTopic){
            map.put("tearth",endKey)
        }
        if (isSource){
            map.put("sfindi",endKey)
        }
        if (isPush){
            map.put("cinvit","push")
        }
        return map
    }

    private fun filterSession(sessionType: Int,key:String, map: java.util.HashMap<String, String>,currentPage:Int=0) {
        if (sessionType == 0 || sessionType == 1){
            var session = CacheManager.getSession(key)
            map.put("smessa", "CS")
            if (sessionType == 1 && currentPage == 1){
                map.put("sstop", "")
                CacheManager.saveSession(key,"")
            }else{
                if (session.isNotEmpty()){
                    map.put("sstop", session)
                }
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