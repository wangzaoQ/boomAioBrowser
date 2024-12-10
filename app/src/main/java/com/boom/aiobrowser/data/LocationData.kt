package com.boom.aiobrowser.data

class LocationData {
    companion object {
        fun createDataByIp(city: String, country: String, lon: Double, lat: Double): LocationData {
            return LocationData().apply {
                longitude = lon
                latitude = lat
                locationCity = city
                locationCountryShort = country
                locationType = 1
            }
        }

        fun createDataByGPS(city: String, country: String, lon: Double, lat: Double): LocationData {
            return LocationData().apply {
                longitude = lon
                latitude = lat
                locationCity = city
                locationCountryShort = country
                locationType = 2
                locationSuccess = true
                locationCheck = true
            }
        }
        fun createDataBySearch(city: String, country: String, lon: Double, lat: Double): LocationData {
            return LocationData().apply {
                longitude = lon
                latitude = lat
                locationCity = city
                locationCountryShort = country
                locationType = -1
            }
        }
    }

    //经度
    var longitude: Double = -1.0

    //纬度
    var latitude: Double = -1.0
    var locationCity: String = ""
    var locationCountryShort: String = ""
    var locationCountry:String?=""
    var locationArea: String = ""

    // json中有
    var admCity:String?=""
    var code:String?=""

    var locationCheck = false

    // -1 临时数据列表展示（不是存储的）0 默认 1 ip 2 gps定位 3 主动选择城市
    var locationType = 0

    //当ip定位用户选择为yes /gps /主动选择城市
    var locationSuccess = false

}