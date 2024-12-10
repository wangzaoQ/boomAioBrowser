package com.boom.aiobrowser.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.BrowserActivityLocationAddBinding
import com.boom.aiobrowser.model.LocationViewModel
import com.boom.aiobrowser.net.NetController
import com.boom.aiobrowser.net.NetRequest
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.ui.adapter.CityAlreadyAddAdapter
import com.boom.aiobrowser.ui.adapter.CityRecommendAdapter
import com.boom.aiobrowser.ui.pop.LoadingPop
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocationAddActivity: BaseActivity<BrowserActivityLocationAddBinding>() {
    override fun getBinding(inflater: LayoutInflater): BrowserActivityLocationAddBinding {
        return BrowserActivityLocationAddBinding.inflate(layoutInflater)
    }

    private val viewModel by lazy {
        viewModels<LocationViewModel>()
    }


    override fun setListener() {
        acBinding.apply {
            ivBack.setOneClick {
                finish()
            }
            llRoot.setOneClick {
                jumpActivity<LocationSettingActivity>(Bundle().apply {
                    putInt(PointValueKey.from_type,1)
                })
            }
            tvSearch.setOneClick {
                jumpActivity<LocationSettingActivity>(Bundle().apply {
                    putInt(PointValueKey.from_type,1)
                })
            }
        }
        viewModel.value.recommendLiveData.observe(this){
            recommendCityAdapter.submitList(it)
        }
        APP.locationListUpdateLiveData.observe(this){
            if (it == 1){
                addCityAdapter.submitList(CacheManager.alreadyAddCityList)
                viewModel.value.getRecommendAddList()
                updateSize = true
            }
        }
    }

    var updateSize = false

    val addCityAdapter by lazy {
        CityAlreadyAddAdapter()
    }
    val recommendCityAdapter by lazy {
        CityRecommendAdapter()
    }

    override fun onResume() {
        super.onResume()

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
        acBinding.rvFollow.apply {
            layoutManager = LinearLayoutManager(this@LocationAddActivity,
                LinearLayoutManager.VERTICAL,false)
            adapter = addCityAdapter
            addCityAdapter.setOnDebouncedItemClick{adapter, view, position ->
                if (position>addCityAdapter.items.size-1)return@setOnDebouncedItemClick
                var data = addCityAdapter.items.get(position)
                CacheManager.removeAlreadyAddCity(data)
                addCityAdapter.submitList(CacheManager.alreadyAddCityList)
                viewModel.value.getRecommendAddList()
                updateSize = true
            }
            addCityAdapter.submitList(CacheManager.alreadyAddCityList)
        }
        acBinding.rvRecommend.apply {
            layoutManager = LinearLayoutManager(this@LocationAddActivity,
                LinearLayoutManager.VERTICAL,false)
            adapter = recommendCityAdapter
            recommendCityAdapter.setOnDebouncedItemClick{adapter, view, position ->
                if (position>recommendCityAdapter.items.size-1)return@setOnDebouncedItemClick
                var data = recommendCityAdapter.items.get(position)
                showPop()
                addLaunch(success = {
                    NetRequest.request { NetController.getLocation(locationData!!.longitude, locationData!!.latitude) }.data?.apply {
                        if (asilve.isNullOrEmpty()){
                            if (acoat!=null && acoat!!.asilve.isNotEmpty()){
                                data!!.locationArea = acoat!!.asilve
                            }
                        }else{
                            data!!.locationArea = asilve
                        }
                        CacheManager.addAlreadyAddCity(data)
                        withContext(Dispatchers.Main){
                            loadingPop?.dismiss()
                            addCityAdapter.submitList(CacheManager.alreadyAddCityList)
                            viewModel.value.getRecommendAddList()
                            updateSize = true
                        }
                    }
                }, failBack = {
                    hidePop()
                })
            }
        }
        viewModel.value.getRecommendAddList()
    }

    override fun onDestroy() {
        APP.locationListUpdateLiveData.removeObservers(this)
        if (updateSize){
            APP.locationListUpdateLiveData.postValue(0)
        }
        super.onDestroy()
    }
}