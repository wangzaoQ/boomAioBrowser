package com.boom.aiobrowser.net

import android.net.Uri
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.LocationData
import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.other.NewsConfig
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import okhttp3.Request
import org.json.JSONObject
import java.lang.StringBuilder

object NetParams {

    var TAG = "NetParams"

    var MAIN = "Main"
    var FOR_YOU = "For You"

    var WIDGET = "widget"

    var LOCAL = "local"
    var NEWS_RELATED = "NEWS_RELATED"
    var NEWS_RECOMMEND = "NEWS_RECOMMEND"
    var NEWS_HOME_VIDEO = "NEWS_HOME_VIDEO"
    var MOVIE = "11370735975268352"
    var PUBLIC_SAFETY = "Public Safety"


    suspend fun likeMapToUrl(map:HashMap<String,String>):String{
        var builder = StringBuilder()
        map.forEach {
            builder.append(it.key).append("=").append(Uri.encode(it.value)).append("&")
        }
        var topicList = CacheManager.defaultTopicList

        topicList.forEachIndexed { index, topicData ->
            if (topicData.id.isNotEmpty()) builder.append("tweakn=${Uri.encode(topicData.id)}&")
        }
        val toString = builder.toString()
        var endParams = ""
        runCatching {
            endParams = toString.substring(0, toString.length - 1)
        }
        var url = "${Net.rootUrl}/api/nemplo?${endParams}"
        AppLogs.dLog(Net.TAG,"getParamsMap2: ${url}")
        return url
    }

    suspend fun videoMapToUrl(map:HashMap<String,String>):String{
        var builder = StringBuilder()
        map.forEach {
            builder.append(it.key).append("=").append(Uri.encode(it.value)).append("&")
        }
        builder.append("vback=${true}")
        val toString = builder.toString()
        var url = "${Net.rootUrl}/api/nemplo?${toString}"
        AppLogs.dLog(Net.TAG,"getParamsMap2: ${url}")
        return url
    }

    suspend fun getParamsMap(key:String,currentPage:Int=0,specialKey:String=""):HashMap<String,String>{
        var needLocation = false
        var sessionType = 0
        var isPush = false
//        var fallback = true
        var map = HashMap<String,String>()
        var endKey = key
        var sessionKey = key
        var isTopic = false
        var isSource = false
        // local 作为topic area 只传当前选中的local 不传自己定位的
        var isLocalTopic = false
        var onlyArea = false
        if (endKey.startsWith(NewsConfig.TOPIC_TAG)){
            endKey = endKey.substringAfter(NewsConfig.TOPIC_TAG)
            if (endKey == APP.instance.getString(R.string.app_local_brief)){
                endKey = LOCAL
            } else if (endKey == APP.instance.getString(R.string.app_movie)){
                isSource = true
                endKey = MOVIE
            }else{
                isTopic = true
            }
        }else if (endKey.startsWith(NewsConfig.LOCAL_TAG)){
            endKey = endKey.substringAfter(NewsConfig.LOCAL_TAG)
            isLocalTopic = true
        }else if (endKey.startsWith(NewsConfig.NO_SESSION_TAG)){
            endKey = endKey.substringAfter(NewsConfig.NO_SESSION_TAG)
            sessionType = 1
            map.put("tearth",endKey)
        }
        when (endKey) {
            MAIN,NFEnum.NF_NEWS.menuName,WIDGET, NEWS_RECOMMEND,FOR_YOU,NEWS_HOME_VIDEO-> {
                needLocation = true
                if (endKey == NFEnum.NF_NEWS.menuName || endKey == WIDGET){
                    map.put("fit","3:AIOPUSH")
                    isPush = true
                }else if (endKey == MAIN || endKey == FOR_YOU){
                    map.put("fit","3:BROWSER")
                }else if (endKey == NEWS_RECOMMEND){
                    map.put("fit","3:USER")
                }else if (endKey == NEWS_HOME_VIDEO){
                    map.put("fit","3:BROWSER_VIDEO")
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
//                onlyArea = true
//                sessionType = 1
            }
            PUBLIC_SAFETY->{
                needLocation = specialKey != "noLocation"
                map.put("tearth",PUBLIC_SAFETY)
            }
            MOVIE->{
                sessionType = 1
            }

            else -> {
            }
        }
        filterLocation(needLocation,isLocalTopic,endKey, map,onlyArea)
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
                CacheManager.saveSession(key,"")
            }else{
                if (session.isNotEmpty()){
                    map.put("sstop", session)
                }
            }
        }
    }

    suspend fun filterLocation(needLocation:Boolean,isLocalTopic:Boolean,endKey:String,map:HashMap<String,String>,onlyArea:Boolean){
        if (isLocalTopic){
            var currentCity = CacheManager.locationData?.locationCity?:""
            if (currentCity == endKey){
                map.put("csuck",CacheManager.locationData?.locationCity?:"")
                if (CacheManager.locationData?.locationArea.isNullOrEmpty().not()) {
                    map.put("asilve", CacheManager.locationData?.locationArea?:"")
                }
            }else{
                var list = CacheManager.alreadyAddCityList
                for (i in 0 until list.size){
                    if (list.get(i).locationCity == endKey){
                        var data = list.get(i)
                        map.put("csuck",data.locationCity)
                        if (data.locationArea.isNullOrEmpty().not()) {
                            map.put("asilve", data.locationArea)
                        }
                        break
                    }
                }
            }
        }else if (needLocation){
            var locationData = getLocation()
            if (locationData != null) {
                if (locationData.locationCity.isNullOrEmpty().not() && onlyArea.not() ) {
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
            val response = WebNet.netClient.newCall(request)?.execute()
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