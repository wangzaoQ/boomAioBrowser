package com.boom.aiobrowser.tools

import android.annotation.SuppressLint
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Base64
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.LocationData
import com.boom.aiobrowser.firebase.FirebaseConfig
import com.boom.aiobrowser.net.NetController
import com.boom.aiobrowser.net.WebNet
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.lang.ref.WeakReference
import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.Locale
import java.util.function.Consumer

object LocationManager {

    var TAG = "LocationManager"

    fun requestGPSPermission(reference: WeakReference<BaseActivity<*>>,onSuccess: () -> Unit = {}, onFail: () -> Unit = {}) {
        var activity = reference.get()
        if (activity == null){
            onFail.invoke()
            return
        }
        val hasPermission = XXPermissions.isGranted(
            activity!!,
            Permission.ACCESS_FINE_LOCATION,
            Permission.ACCESS_COARSE_LOCATION
        )
        if (hasPermission){
            onSuccess.invoke()
            return
        }
        val xxPermissions = XXPermissions.with(activity!!)
        xxPermissions.permission(Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION)
        runCatching {
            xxPermissions.request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                    AppLogs.dLog(TAG,"onGranted:${allGranted}")
//                    fireLog2Server(PointInfo.gps_req_enable,null)
                    onSuccess.invoke()
                }

                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                    super.onDenied(permissions, doNotAskAgain)
//                    fireLog2Server(PointInfo.gps_req_refuse,null)
                    onFail.invoke()
//                    XXPermissions.startPermissionActivity(activity,Permission.ACCESS_COARSE_LOCATION)
                }
            })
        }.onFailure {
            onFail.invoke()
        }
    }


    suspend fun getAreaByGPS(): LocationData? {
        var areaData: LocationData? = null
        var location = getLocation()
        if (location != null) {
            runCatching {
                var map = HashMap<String, String>().apply {
                    put(
                        "location",
                        "${location.longitude},${location.latitude}"
                    )
                    put("key", URLEncoder.encode("cfbf61df245b453094c4baf9c42017d4", "UTF-8"))
                    put("lang", URLEncoder.encode(Locale.getDefault().language, "UTF-8"))
                }
                var url ="https://geoapi.qweather.com/v2/city/lookup?location=${location.longitude},${location.latitude}" +
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
                    if (locations.length() > 0) {
                        var adm1 = locations.getJSONObject(0).getString("adm1")
                        var adm2 = locations.getJSONObject(0).getString("adm2")
                        areaData = LocationData.createDataByGPS(
                            locations.getJSONObject(0).getString("name"),
                            locations.getJSONObject(0).getString("country"),
                            location.longitude,
                            location.latitude)
                    }
                }
                if (areaData!=null){
                    runCatching {
                        NetController.getLocation(areaData!!.longitude, areaData!!.latitude).data?.apply {
                            if (asilve.isNullOrEmpty()) {
                                if (acoat != null && acoat!!.asilve.isNotEmpty()) {
                                    areaData!!.locationArea = acoat!!.asilve
                                }
                            } else {
                                areaData!!.locationArea = asilve
                            }
                        }
                    }
                }
            }.onFailure {
                AppLogs.eLog(TAG, it.stackTraceToString())
            }
        }
        if (areaData!=null){
            CacheManager.locationData = areaData
        }
        return areaData
    }


    @SuppressLint("MissingPermission")
    suspend fun getLocation(result: (location: Location?) -> Unit = {}): Location? {
        var location: Location? = null
        try {
            if (!isLocationProviderEnabled()) { //未开启GPS或网络定位开关
                AppLogs.dLog(TAG, "getLocation isLocationProviderEnabled == false")
                return null
            }
            var locationManager =
                APP.instance.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()
            if (Build.VERSION.SDK_INT < 31) {
                AppLogs.dLog(TAG, "Build.VERSION.SDK_INT < 31")
                criteria.accuracy = Criteria.ACCURACY_FINE //高精度
                criteria.isAltitudeRequired = true //要求海拔
                criteria.isBearingRequired = true //要求方位
                criteria.isCostAllowed = true //允许有花费
                criteria.powerRequirement = Criteria.POWER_MEDIUM //标准功耗
            } else {
                AppLogs.dLog(TAG, "Build.VERSION.SDK_INT >= 31")
                criteria.accuracy = Criteria.ACCURACY_COARSE //低精度，如果设置为高精度，依然获取不了location。
                criteria.isAltitudeRequired = false //不要求海拔
                criteria.isBearingRequired = false //不要求方位
                criteria.isCostAllowed = true // 允许有花费
                criteria.powerRequirement = Criteria.POWER_LOW //低功耗
            }
            location = getLocationByGps(locationManager, criteria, location)
            //getLocation(location)
            //如果location还为空 去最近一次记录获取的
            if (location == null) {
                val providers = locationManager.getProviders(true)
                for (provider in providers) {
                    val l = locationManager.getLastKnownLocation(provider!!) ?: continue
                    location = l
                }
            }
        } catch (e: Exception) {
            AppLogs.eLog(TAG, "getLocation Exception: " + e.message)
        }
        AppLogs.dLog(TAG, "getLocation location: $location")
        return location
    }

    @SuppressLint("MissingPermission")
    private fun getLocationByGps(
        locationManager: LocationManager,
        criteria: Criteria,
        location: Location?,
    ): Location? {
        var location1 = location
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val locationProvider = locationManager.getBestProvider(criteria, true)
            location1 = locationManager.getLastKnownLocation(locationProvider!!)
            //location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location1 == null) //当GPS信号弱没获取到位置的时候再从网络获取
                location1 = getLocationByNetwork()
        } else { //从网络获取经纬度
            location1 = getLocationByNetwork()
        }
        return location1
    }

    @SuppressLint("MissingPermission")
    private fun getLocationByNetwork(): Location? {
        var location: Location? = null
        val locationManager =
            APP.instance.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, mLocationListener);
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (location == null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        locationManager.getCurrentLocation(
                            LocationManager.NETWORK_PROVIDER,
                            null,
                            APP.instance.getMainExecutor(),
                            object : Consumer<Location?> {
                                override fun accept(loc: Location?) {
                                    location = loc
                                }
                            })
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            AppLogs.eLog(TAG, "getLocationByNetwork Exception: " + e.message)
        }
        AppLogs.dLog(TAG, "getLocationByNetwork location: $location")
        return location
    }

    /**
     * 判断是否开启了GPS或网络定位开关
     *
     * @return
     */
    fun isLocationProviderEnabled(): Boolean {
        var result = false
        val locationManager =
            APP.instance.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                ?: return result
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        ) result = true
        return result
    }

    private fun getLocation(location: Location?) {
        AppLogs.dLog(TAG, "经度：${location?.longitude}纬度：${location?.latitude}")
    }

    fun recommendCity():MutableMap<String, MutableList<LocationData>> {
        return jsonDecode()
    }


    fun jsonDecode(): MutableMap<String, MutableList<LocationData>> {
        val map = mutableMapOf<String, MutableList<LocationData>>()
        val array = JSONArray(FirebaseConfig.ADDRESS_JSON)
        for (i in 0..(array.length() - 1)) {
            val objet = array.getJSONObject(i)
            val area = decodeData(objet.getString("encCountry"),objet.getString("encCountryNo"), objet.getJSONArray("allCity"))
            map.put(objet.getString("tongue"), area)
        }
        return map
    }

    fun decodeData(country:String,countryShort:String, array: JSONArray): MutableList<LocationData> {
        val list = mutableListOf<LocationData>()
        for (i in 0..(array.length() - 1)) {
            val objet = array.getJSONObject(i)
            list.add(LocationData().apply {
                this.locationCountry = country
                this.locationCountryShort = countryShort
                this.locationCity = objet.getString("cncCity")
                this.longitude = objet.getString("lon").toDouble()
                this.latitude = objet.getString("lat").toDouble()
                runCatching {
                    this.admCity = objet.getString("adm")
                }
                runCatching {
                    this.code = objet.getString("code")
                }
            })
        }
        return list
    }



}