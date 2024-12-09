package com.boom.aiobrowser.ui.activity

import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.BrowserActivityLocationAddBinding
import com.boom.aiobrowser.model.LocationViewModel
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.ui.adapter.CityAlreadyAddAdapter
import com.boom.aiobrowser.ui.adapter.CityRecommendAdapter
import com.boom.base.adapter4.util.setOnDebouncedItemClick

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
                jumpActivity<LocationSettingActivity>()
            }
        }
        viewModel.value.recommendLiveData.observe(this){
            recommendCityAdapter.submitList(it)
        }
    }

    var updateSize = false

    val addCityAdapter by lazy {
        CityAlreadyAddAdapter()
    }
    val recommendCityAdapter by lazy {
        CityRecommendAdapter()
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
                CacheManager.addAlreadyAddCity(data)
                addCityAdapter.submitList(CacheManager.alreadyAddCityList)
                viewModel.value.getRecommendAddList()
                updateSize = true
            }
        }
        viewModel.value.getRecommendAddList()
    }

    override fun onDestroy() {
        if (updateSize) APP.locationListUpdateLiveData.postValue(0)
        super.onDestroy()
    }
}