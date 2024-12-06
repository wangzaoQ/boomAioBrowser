package com.boom.aiobrowser.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.boom.aiobrowser.data.LocationData
import com.boom.aiobrowser.net.NetController
import com.boom.aiobrowser.net.NetRequest
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.LocationManager
import com.ironsource.lo
import com.mbridge.msdk.dycreator.viewmodel.BaseViewModel
import java.util.Locale

class LocationViewModel:BaseDataModel() {
    var cityLiveData = MutableLiveData<List<LocationData>>()

    var completeLiveData = MutableLiveData<LocationData>()

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
                    CacheManager.addCityList(locationData)
                }
                completeLiveData.postValue(locationData)
            }
        }, failBack = {
            CacheManager.locationData = locationData
            if (addCityList){
                CacheManager.addCityList(locationData)
            }
            completeLiveData.postValue(locationData)
        },1)
    }
}