package com.boom.aiobrowser.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.data.RecentSearchData
import com.boom.aiobrowser.databinding.BrowserFragmentSearchBinding
import com.boom.aiobrowser.databinding.BrowserFragmentTempBinding
import com.boom.aiobrowser.model.SearchViewModel
import com.boom.aiobrowser.net.SearchNet
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.tools.jobCancel
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.ParamsConfig
import com.boom.aiobrowser.ui.activity.WebDetailsActivity
import com.boom.aiobrowser.ui.adapter.NewsMainAdapter
import com.boom.aiobrowser.ui.adapter.RecentSearchAdapter
import com.boom.base.adapter4.QuickAdapterHelper
import com.boom.base.adapter4.loadState.trailing.TrailingLoadStateAdapter
import com.boom.base.adapter4.util.addOnDebouncedChildClick
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class SearchFragment : BaseFragment<BrowserFragmentSearchBinding>() {

    private val viewModel by viewModels<SearchViewModel>()

    var jumpData:JumpData?=null

    companion object{
        fun newInstance(jsonData: String): SearchFragment{
            val args = Bundle()
            args.putString(ParamsConfig.JSON_PARAMS, jsonData)
            val fragment = SearchFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun startLoadData() {
        var status = CacheManager.browserStatus
        if (status == 1)return
        var title = fBinding.topRoot.binding.etToolBarSearch.text.toString().trim()
        if (title.isNotEmpty()){
            fBinding.rlRecentRoot.visibility = View.GONE
        }
        var searchist = CacheManager.recentSearchDataList
        var dataList = mutableListOf<JumpData>()
        if (searchist.isNotEmpty()){
            fBinding.rlRecentRoot.visibility = View.VISIBLE
            fBinding.rv.visibility = View.VISIBLE
            if (searchist.size>5 && isShowMoreData.not()){
                dataList = searchist.subList(0,5)
                fBinding.llViewMore.visibility = View.VISIBLE
            }else{
                dataList = searchist
            }
            searchAdapter.submitList(dataList)
        }else{
            fBinding.rlRecentRoot.visibility = View.GONE
            fBinding.rv.visibility = View.GONE
            fBinding.llViewMore.visibility = View.GONE
        }
    }

    var isShowMoreData = false

    override fun setListener() {
        fBinding.llViewMore.setOneClick {
            isShowMoreData = true
            startLoadData()
        }
        fBinding.tvClear.setOneClick {
            CacheManager.recentSearchDataList = mutableListOf()
            startLoadData()
        }
        APP.engineLiveData.observe(this){
            fBinding.topRoot.updateEngine(it)
        }
    }

    val searchAdapter by lazy {
        RecentSearchAdapter()
    }

    var changeJob: Job? = null

    override fun setShowView() {
        jumpData = getBeanByGson(arguments?.getString(ParamsConfig.JSON_PARAMS)?:"",JumpData::class.java)
        fBinding.topRoot.updateTopView(1, searchRecent = {
            changeJob.jobCancel()
            if (it.isNullOrEmpty()){
                startLoadData()
            }else{
                changeJob = rootActivity.addLaunch(success = {
                    delay(1000)
                }, failBack = {}, Dispatchers.IO)
            }
        }, searchResult = {
//            viewModel.searchResult(it)
            var url = SearchNet.getSearchUrl(it)

            var jumpData = JumpDataManager.getCurrentJumpData(tag = "searchFragment 搜索").apply {
                jumpType = JumpConfig.JUMP_WEB
                jumpTitle = it
                jumpUrl = url
            }
            if (CacheManager.browserStatus == 0){
                CacheManager.saveRecentSearchData(jumpData)
            }
            toWebDetailsActivity(jumpData)
        })
        fBinding.topRoot.updateEngine(CacheManager.engineType)
        if (jumpData?.jumpUrl.isNullOrEmpty().not()){
            fBinding.topRoot.binding.etToolBarSearch.setText("${jumpData?.jumpUrl}")
        }
        fBinding.topRoot.binding.etToolBarSearch.selectAll()

        fBinding.rv.apply {
            layoutManager = LinearLayoutManager(rootActivity,LinearLayoutManager.VERTICAL,false)
            var helper = QuickAdapterHelper.Builder(searchAdapter)
                .build()
            // 设置预加载，请调用以下方法
            // helper.trailingLoadStateAdapter?.preloadSize = 1
            adapter = helper.adapter
            // 添加子 view 的点击事件(去除点击抖动的扩展方法)
            searchAdapter.addOnDebouncedChildClick(R.id.ivDelete) { adapter, view, position ->
                searchAdapter.removeAt(position)
                var list = CacheManager.recentSearchDataList
                list.removeAt(position)
                CacheManager.recentSearchDataList = list
                startLoadData()
            }
            searchAdapter.setOnDebouncedItemClick{adapter, view, position ->
                var data = searchAdapter.items.get(position)
                CacheManager.saveRecentSearchData(data)
//                CacheManager.getCurrentJumpData(updateTime = true, updateData = data)
                toWebDetailsActivity(data)
            }
        }
    }

    private fun toWebDetailsActivity(data:JumpData){
        var allow = true
        for (i in 0 until APP.instance.lifecycleApp.stack.size){
            var data = APP.instance.lifecycleApp.stack.get(i)
            if (data is WebDetailsActivity){
                allow = false
                break
            }
        }
        if (allow){
            APP.jumpLiveData.postValue(data)
        }else{
            APP.jumpWebLiveData.postValue(data)
        }
        rootActivity.finish()
    }

    override fun onDestroy() {
        APP.engineLiveData.removeObservers(this)
        super.onDestroy()
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BrowserFragmentSearchBinding {
        return BrowserFragmentSearchBinding.inflate(layoutInflater)
    }
}