package com.boom.aiobrowser.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.databinding.NewsFragmentHomeTabBinding
import com.boom.aiobrowser.other.ParamsConfig
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.getListByGson
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.activity.WebDetailsActivity
import com.boom.aiobrowser.ui.activity.WebSourceActivity
import com.boom.aiobrowser.ui.adapter.HomeTabChildAdapter
import com.boom.aiobrowser.ui.fragment.guide.GuideFragment
import com.boom.base.adapter4.util.setOnDebouncedItemClick

class HomeTabFragment : BaseFragment<NewsFragmentHomeTabBinding>(){
    override fun startLoadData() {

    }

    override fun setListener() {
        homeTabChildAdapter.setOnDebouncedItemClick{adapter, view, position ->
            rootActivity.jumpActivity<WebSourceActivity>()
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