package com.boom.aiobrowser.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.BrowserActivityAboutBinding
import com.boom.aiobrowser.databinding.BrowserActivityLocationSettingBinding
import com.boom.aiobrowser.model.LocationViewModel
import com.boom.aiobrowser.other.ParamsConfig
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.LocationManager
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.adapter.CityAdapter
import com.boom.aiobrowser.ui.pop.LoadingPop
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class LocationSettingActivity: BaseActivity<BrowserActivityLocationSettingBinding>() {
    override fun getBinding(inflater: LayoutInflater): BrowserActivityLocationSettingBinding {
        return BrowserActivityLocationSettingBinding.inflate(layoutInflater)
    }

    private val viewModel by lazy {
        viewModels<LocationViewModel>()
    }

    var loadingPop:LoadingPop?=null

    override fun setListener() {
        acBinding.apply {
            ivBack.setOneClick {
                finish()
            }
            ivGo.setOneClick {
                LocationManager.requestGPSPermission(WeakReference(this@LocationSettingActivity), onSuccess = {
                    showPop()
                    this@LocationSettingActivity.addLaunch(success = {
                        var area = LocationManager.getAreaByGPS()
                        if (area == null){
                            loadingPop?.dismiss()
                        }else{
                            CacheManager.addCityList(area)
                            viewModel.value.completeLiveData.postValue(area)
                        }
                    }, failBack = {
                        loadingPop?.dismiss()
                    })
                }, onFail = {
                    loadingPop?.dismiss()
                })
            }
        }
        viewModel.value.failLiveData.observe(this){
            loadingPop?.dismiss()
        }
        viewModel.value.completeLiveData.observe(this){
            loadingPop?.dismiss()
            APP.locationListUpdateLiveData.postValue(CacheManager.cityList)
            finish()
        }
        viewModel.value.cityLiveData.observe(this){
            it.forEach {
                it.locationSuccess = acBinding.tvArea.text == it.locationCity
            }
            cityAdapter.submitList(it)
        }
    }

    fun showPop(){
        var isShowing = loadingPop?.isShowing?:false
        if (isShowing.not()){
            loadingPop = LoadingPop(this@LocationSettingActivity)
            loadingPop!!.createPop()
        }
    }

    val cityAdapter by lazy {
        CityAdapter()
    }

    override fun setShowView() {
        var locationData = CacheManager.locationData
        locationData?.apply {
            var builder = StringBuilder()
            builder.append(locationData.locationCity)
            if (locationData.locationCountryShort.isNullOrEmpty().not()){
                builder.append(",${locationData.locationCountryShort}")
            }
            if (locationData.code.isNullOrEmpty().not()){
                builder.append(",${locationData.code}")
            }
            acBinding.tvArea.text = builder.toString()
        }
        viewModel.value.getRecommendList()
        acBinding.rv.apply {
            layoutManager = LinearLayoutManager(this@LocationSettingActivity,LinearLayoutManager.VERTICAL,false)
            adapter = cityAdapter
            cityAdapter.setOnDebouncedItemClick{adapter, view, position ->
                if (position>cityAdapter.items.size-1)return@setOnDebouncedItemClick
                showPop()
                var data = cityAdapter.items.get(position)
                CacheManager.locationData = data
                viewModel.value.getAreaData(data,true)
            }
        }
    }
}