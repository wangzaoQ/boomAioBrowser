package com.boom.aiobrowser.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADShowManager
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.databinding.NewsFragmentHomeTabBinding
import com.boom.aiobrowser.other.JumpConfig
import com.boom.aiobrowser.other.ParamsConfig
import com.boom.aiobrowser.point.AD_POINT
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.getListByGson
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.activity.WebSourceActivity
import com.boom.aiobrowser.ui.adapter.HomeTabChildAdapter
import com.boom.base.adapter4.util.setOnDebouncedItemClick

class HomeTabFragment : BaseFragment<NewsFragmentHomeTabBinding>(){
    override fun startLoadData() {

    }

    override fun setListener() {
        homeTabChildAdapter.setOnDebouncedItemClick{adapter, view, position ->
            var data = homeTabChildAdapter.items.get(position)
            if (data.jumpType == JumpConfig.JUMP_WEB_TYPE){
                rootActivity.jumpActivity<WebSourceActivity>()
            }else{
                var manager = AioADShowManager(rootActivity, ADEnum.INT_AD, tag = "下载页点击enum"){
                    var jumpData = JumpDataManager.getCurrentJumpData(tag="homeTab点击", updateData = data)
                    jumpData.jumpType = JumpConfig.JUMP_WEB
                    APP.jumpLiveData.postValue(jumpData)
                    PointEvent.posePoint(PointEventKey.home_navigation_click,Bundle().apply {
                        putString(PointValueKey.click_source,data.jumpTitle)
                    })
                }
                manager.showScreenAD(AD_POINT.aobws_downclick_int)
            }
        }
    }

    val homeTabChildAdapter by lazy {
        HomeTabChildAdapter()
    }

    override fun setShowView() {
        var list = getListByGson(arguments?.getString(ParamsConfig.JSON_PARAMS)?:"",JumpData::class.java)

        fBinding.tabRv.apply {
            layoutManager = GridLayoutManager(rootActivity,4)
            adapter = homeTabChildAdapter
            homeTabChildAdapter.submitList(list)
        }
    }


    companion object{
        fun newInstance(list: MutableList<JumpData>): HomeTabFragment {
            val args = Bundle()
            args.putString(ParamsConfig.JSON_PARAMS, toJson(list))
            val fragment = HomeTabFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NewsFragmentHomeTabBinding {
        return NewsFragmentHomeTabBinding.inflate(layoutInflater)
    }
}