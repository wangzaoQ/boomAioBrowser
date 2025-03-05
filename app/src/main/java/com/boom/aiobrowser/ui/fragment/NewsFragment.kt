package com.boom.aiobrowser.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADShowManager
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.NewsFragmentBinding
import com.boom.aiobrowser.model.NewsViewModel
import com.boom.aiobrowser.net.NetParams
import com.boom.aiobrowser.other.NewsConfig
import com.boom.aiobrowser.other.ParamsConfig
import com.boom.aiobrowser.other.TopicConfig
import com.boom.aiobrowser.point.AD_POINT
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

    private fun loadData(refresh:Boolean) {
        if (topic == TopicConfig.TOPIC_FOR_YOU || topic.startsWith(NewsConfig.LOCAL_TAG)){
            viewModel.value.getNewsData(newsAdapter.mutableItems,topic,page,refresh)
        }else{
            viewModel.value.getNewsData(newsAdapter.mutableItems,"${NewsConfig.TOPIC_TAG}${topic}",page,refresh)
        }
        viewModel.value.getNewsVideoList("")
    }

    override fun setListener() {
//        APP.newsUpdateLiveData.observe(this){
//            if (it == "home"){
//                var middleTime = System.currentTimeMillis()-CacheManager.getNewsSaveTime(topic)
//                if (middleTime>1*60*1000){
//                    loadData(true)
//                }
//            }else if (it == topic){
//                var middleTime = System.currentTimeMillis()-CacheManager.getNewsSaveTime(topic)
//                if (middleTime>1*60*1000){
//                    loadData(true)
//                }
//            }
//        }
        viewModel.value.newsLiveData.observe(this){
            if (page == 1){
                newsAdapter.submitList(it)
                if (newsAdapter.mutableItems.size<5){
                    page++
                    loadData(false)
                }
            }else{
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
            loadData(true)
        }
        fBinding.newsSmart.setOnLoadMoreListener{
            page++
            loadData(false)
        }
        newsAdapter.setOnDebouncedItemClick{adapter, view, position ->
            if (position>newsAdapter.items.size-1)return@setOnDebouncedItemClick
            var manager = AioADShowManager(rootActivity, ADEnum.INT_AD, tag = "新闻列表点击的广告"){
                var data = newsAdapter.items.get(position)
                if (data.dataType == NewsData.TYPE_DETAILS_NEWS_SEARCH_FILM){
                    rootActivity.jumpActivity<WebActivity>(Bundle().apply {
                        putString("url", data.uweek)
                    })
                }else if (data.dataType == NewsData.TYPE_NEWS || data.dataType == NewsData.TYPE_HOME_NEWS_TRENDING || data.dataType == NewsData.TYPE_DETAILS_NEWS_SEARCH){
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
            manager.showScreenAD(AD_POINT.aobws_newsclick_int)
        }
        newsAdapter.addOnDebouncedChildClick(R.id.btnYes) { adapter, view, position ->
            var data = CacheManager.locationData
            data?.locationSuccess = true
            CacheManager.locationData = data
            newsAdapter.removeAt(position)
            APP.locationListUpdateLiveData.postValue(0)
            PointEvent.posePoint(PointEventKey.IP_location_banner,Bundle().apply {
                putString(PointValueKey.from_type,"for you")
                putString(PointValueKey.type,"yes")
            })
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
                            withContext(Dispatchers.Main){
                                page = 1
                                fBinding.newsRv.smoothScrollToPosition(0)
                                loadData(true)
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
            PointEvent.posePoint(PointEventKey.IP_location_banner,Bundle().apply {
                putString(PointValueKey.from_type,"for you")
                putString(PointValueKey.type,"no")
            })
        }
        if (topic == TopicConfig.TOPIC_FOR_YOU){
            APP.locationListUpdateLiveData.observe(this){
                var list = newsAdapter.mutableItems
                var index = -1
                for (i in 0 until list.size){
                    if(list.get(i).dataType == NewsData.TYPE_HOME_NEWS_LOCAL){
                        index = i
                        break
                    }
                }
                if (index>=0){
                    newsAdapter.removeAt(index)
                }
            }
            APP.homeTopicLiveData.observe(this){
                var list = newsAdapter.mutableItems
                var index = -1
                for (i in 0 until list.size){
                    if(list.get(i).dataType == NewsData.TYPE_HOME_NEWS_TOPIC){
                        index = i
                        break
                    }
                }
                if (index>=0){
                    newsAdapter.mutableItems.get(index).topicList = it
                    newsAdapter.mutableItems.get(index).isLoading = false
                    newsAdapter.notifyItemChanged(index)
                }
            }
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

    override fun onDestroy() {
        if (topic == TopicConfig.TOPIC_FOR_YOU){
            APP.locationListUpdateLiveData.removeObservers(this)
            APP.homeTopicLiveData.removeObservers(this)
        }
//        APP.newsUpdateLiveData.removeObservers(this)
        super.onDestroy()
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