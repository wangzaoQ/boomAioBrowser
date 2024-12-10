package com.boom.aiobrowser.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.NewsFragmentBinding
import com.boom.aiobrowser.model.NewsViewModel
import com.boom.aiobrowser.net.NetParams
import com.boom.aiobrowser.other.NewsConfig
import com.boom.aiobrowser.other.ParamsConfig
import com.boom.aiobrowser.other.TopicConfig
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.LocationManager
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.activity.LocationSettingActivity
import com.boom.aiobrowser.ui.activity.WebActivity
import com.boom.aiobrowser.ui.activity.WebDetailsActivity
import com.boom.aiobrowser.ui.adapter.NewsMainAdapter
import com.boom.aiobrowser.ui.pop.LoadingPop
import com.boom.base.adapter4.util.addOnDebouncedChildClick
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class NewsFragment: BaseFragment<NewsFragmentBinding>() {

    var topic:String = ""


    private val viewModel by lazy {
        viewModels<NewsViewModel>()
    }

    val newsAdapter by lazy {
        NewsMainAdapter(this)
    }

    var page = 1

    override fun startLoadData() {
        fBinding.newsSmart.autoRefresh()
    }

    private fun loadData() {
        if (topic == TopicConfig.TOPIC_FOR_YOU || topic.startsWith(NewsConfig.LOCAL_TAG)){
            viewModel.value.getNewsData(topic,page)
        }else{
            viewModel.value.getNewsData("${NewsConfig.TOPIC_TAG}${topic}",page)
        }
    }

    override fun setListener() {
        viewModel.value.newsLiveData.observe(this){
            if (page == 1){
                newsAdapter.submitList(it)
            }else{
                var oldList = newsAdapter.mutableItems
                var removeList = mutableListOf<NewsData>()
                it.forEach {
                    for (j in 0 until newsAdapter.mutableItems.size){
                        if (newsAdapter.mutableItems.get(j).itackl == it.itackl){
                            removeList.add(it)
                            break
                        }
                    }
                }
                (it as MutableList<NewsData>).removeAll(removeList)
                newsAdapter.addAll(it)
            }
            fBinding.newsSmart.finishRefresh()
            fBinding.newsSmart.finishLoadMore()
        }
        viewModel.value.failLiveData.observe(this){
            fBinding.newsSmart.finishRefresh()
            fBinding.newsSmart.finishLoadMore()
        }
        fBinding.newsSmart.setOnRefreshListener{
            page = 1
            loadData()
        }
        fBinding.newsSmart.setOnLoadMoreListener{
            page++
            loadData()
        }
        newsAdapter.setOnDebouncedItemClick{adapter, view, position ->
            if (position>newsAdapter.items.size-1)return@setOnDebouncedItemClick
            var data = newsAdapter.items.get(position)
            if (data.dataType == NewsData.TYPE_DETAILS_NEWS_SEARCH_FILM){
                rootActivity.jumpActivity<WebActivity>(Bundle().apply {
                    putString("url", data.uweek)
                })
            }else{
                rootActivity.jumpActivity<WebDetailsActivity>(Bundle().apply {
                    putString(ParamsConfig.JSON_PARAMS, toJson(data))
                })
            }
            if (topic == rootActivity.getString(R.string.app_movie)){
                PointEvent.posePoint(PointEventKey.search_page_movie, Bundle().apply {
                    putString(PointValueKey.type,data?.tconsi)
                })
            }
        }
        newsAdapter.addOnDebouncedChildClick(R.id.btnYes) { adapter, view, position ->
            var data = CacheManager.locationData
            data?.locationSuccess = true
            CacheManager.locationData = data
            CacheManager.addAlreadyAddCity(data)
            newsAdapter.removeAt(position)
            APP.locationListUpdateLiveData.postValue(0)
        }
        newsAdapter.addOnDebouncedChildClick(R.id.btnNo) { adapter, view, position ->
            LocationManager.requestGPSPermission(WeakReference(rootActivity), onSuccess = {
                var isShowing = loadingPop?.isShowing?:false
                if (isShowing.not()){
                    loadingPop = LoadingPop(rootActivity)
                    loadingPop!!.createPop()
                    rootActivity.addLaunch(success = {
                        var area = LocationManager.getAreaByGPS()
                        if (area == null){
                            withContext(Dispatchers.Main){
                                toLocationSetting()
                            }
                        }else{
                            CacheManager.locationData = area
                            CacheManager.addAlreadyAddCity(area)
                            withContext(Dispatchers.Main){
                                page = 1
                                fBinding.newsRv.smoothScrollToPosition(0)
                                loadData()
                            }
                            APP.locationListUpdateLiveData.postValue(0)
                        }
                    }, failBack = {
                        toLocationSetting()
                    })
                }

            }, onFail = {
                toLocationSetting()
            })
        }
    }

    var loadingPop:LoadingPop?=null
    private fun toLocationSetting() {
        loadingPop?.dismiss()
        rootActivity.jumpActivity<LocationSettingActivity>()
    }

    override fun setShowView() {
        topic =  arguments?.getString("selectedTopic")?:""
        fBinding.newsRv.apply {
            layoutManager = LinearLayoutManager(rootActivity,LinearLayoutManager.VERTICAL,false)
            adapter = newsAdapter
        }
    }

    companion object {
        fun newInstance(topic: String): BaseFragment<*>{
            val args = Bundle()
            args.putString("selectedTopic",topic)
            val fragment = NewsFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): NewsFragmentBinding {
        return NewsFragmentBinding.inflate(layoutInflater)
    }
}