package com.boom.aiobrowser.ui.fragment

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.databinding.BrowserFragmentMainBinding
import com.boom.aiobrowser.model.NewsViewModel
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.BigDecimalUtils
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.StoragePermissionManager
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.SearchConfig
import com.boom.aiobrowser.ui.activity.clean.CleanScanActivity
import com.boom.aiobrowser.ui.activity.MainActivity
import com.boom.aiobrowser.ui.activity.clean.ProcessActivity
import com.boom.aiobrowser.ui.activity.clean.load.ProcessLoadActivity
import com.boom.aiobrowser.ui.adapter.NewsMainAdapter
import com.boom.aiobrowser.ui.pop.EngineGuidePop
import com.boom.aiobrowser.ui.pop.SearchPop
import com.boom.base.adapter4.QuickAdapterHelper
import com.boom.base.adapter4.loadState.LoadState
import com.boom.base.adapter4.loadState.trailing.TrailingLoadStateAdapter
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import com.google.android.material.appbar.AppBarLayout
import java.lang.ref.WeakReference


class MainFragment : BaseFragment<BrowserFragmentMainBinding>()  {

    private val viewModel by lazy {
        rootActivity.viewModels<NewsViewModel>()
    }

    val permissionManager by lazy {
        StoragePermissionManager(WeakReference(rootActivity), onGranted = {
            rootActivity.startActivity(Intent(rootActivity, CleanScanActivity::class.java))
        }, onDenied = {
            toSetting()
        })
    }

    private fun toSetting() {
        permissionManager.toOtherSetting()
    }

    override fun startLoadData() {

    }

    var absVerticalOffset = 0

    var firstLoad = true

    var page = 1

    override fun setListener() {
        APP.engineLiveData.observe(this){
            updateEngine(it)
            fBinding.topSearch.updateEngine(it)
        }
        fBinding.topSearch.binding.ivRefresh.visibility = View.GONE
        fBinding.mainAppBar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                absVerticalOffset = Math.abs(verticalOffset) //AppBarLayout竖直方向偏移距离px
                if (absVerticalOffset == 0)return
                val totalScrollRange = appBarLayout!!.totalScrollRange //AppBarLayout总的距离px
                var offset = BigDecimalUtils.mul(BigDecimalUtils.div(255.toDouble(), totalScrollRange.toDouble(),1),absVerticalOffset.toDouble()).toInt()
//                var offset = absVerticalOffset / 2
//                offset = 255 - offset
                AppLogs.dLog("onOffsetChanged", "offset=$offset")
                if (offset > 255) {
                    offset = 255
                } else if (offset <= 0) {
                    offset = 0
                }
                if (offset <10) {
                    fBinding.mainCl.alpha = 1f
                    fBinding.mainToolBar.alpha = 0f
                } else {
                    val div = BigDecimalUtils.div(offset.toDouble(), 255.0, 2)
                    AppLogs.dLog("onOffsetChanged", "div=$div")
                    fBinding.mainToolBar.alpha = div.toFloat()
                    fBinding.mainCl.alpha = 1-div.toFloat()
                }
            }
        })
        for (i in 0 until fBinding.llRoot.childCount){
            fBinding.llRoot.getChildAt(i).setOnClickListener {
                var title = ""
                var url = ""
                when(i){
                    0 ->{
                        title = getString(R.string.app_ytb)
                        url = "https://www.youtube.com/"
                    }
                    1 ->{
                        title = getString(R.string.app_fb)
                        url = "https://www.facebook.com/"
                    }
                    2 ->{
                        title = getString(R.string.app_instagram)
                        url = "https://www.instagram.com/"
                    }
                    3 ->{
                        title = getString(R.string.app_tt)
                        url = "https://www.tiktok.com/"
                    }
                }
                APP.jumpLiveData.postValue(JumpDataManager.getCurrentJumpData(tag = "mainFragment 点击热们功能").apply {
                    jumpType = JumpConfig.JUMP_WEB
                    jumpTitle = title
                    jumpUrl = url
                })
            }
        }
        fBinding.rlSearch.setOneClick {
            APP.jumpLiveData.postValue(JumpDataManager.getCurrentJumpData(isReset = true,tag = "mainFragment 点击搜索").apply {
                jumpType = JumpConfig.JUMP_SEARCH
            })
        }
        fBinding.ivSearchEngine.setOneClick {
            SearchPop.showPop(WeakReference(rootActivity),fBinding.ivSearchEngine)
        }
        fBinding.rlClean.setOneClick {
            permissionManager.requestStoragePermission()
        }
        fBinding.rlProcess.setOneClick {
            rootActivity.jumpActivity<ProcessLoadActivity>()
        }

        viewModel.value.newsLiveData.observe(rootActivity){
            if (page == 1){
                newsAdapter.submitList(it)
                fBinding.rv.scrollToPosition(0)
            }else{
                newsAdapter.addAll(it)
            }
            adapterHelper.trailingLoadState = LoadState.NotLoading(false)
            fBinding.refreshLayout.isRefreshing = false
        }
        viewModel.value.failLiveData.observe(rootActivity){
            adapterHelper.trailingLoadState = LoadState.NotLoading(false)
            fBinding.refreshLayout.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()
        var jumpData:JumpData
        if (firstLoad){
            firstLoad = false
            jumpData = JumpDataManager.getCurrentJumpData(tag = "MainFragment onResume 首次")
            if (jumpData.jumpType == JumpConfig.JUMP_WEB){
                APP.jumpLiveData.postValue(jumpData)
            }else{

            }
        }else{
            jumpData = JumpDataManager.getCurrentJumpData(isReset = true,tag = "MainFragment onResume 非首次")
            JumpDataManager.updateCurrentJumpData(jumpData,"MainFragment onResume 更新 jumpData")
            if (CacheManager.engineGuideFirst){
                CacheManager.engineGuideFirst = false
                EngineGuidePop(rootActivity).createPop(fBinding.ivSearchEngine)
            }
            if (fBinding.refreshLayout.isRefreshing == true){
                fBinding.refreshLayout.isRefreshing = false
            }
            if (jumpData.jumpType != JumpConfig.JUMP_HOME ){
                APP.bottomLiveData.postValue(JumpConfig.JUMP_HOME)
            }else{
                if (rootActivity is MainActivity){
                (rootActivity as MainActivity).apply {
//                    acBinding.llWebControl.visibility = View.GONE
                    acBinding.llMainControl.visibility = View.VISIBLE
                }
            }
            }
        }
        if (CacheManager.isAllowShowCleanTips()){
            fBinding.tips.visibility = View.VISIBLE
        }else{
            fBinding.tips.visibility = View.GONE
        }
    }

    private fun updateEngine(type: Int) {
        when (type) {
            SearchConfig.SEARCH_ENGINE_GOOGLE->{
                fBinding.ivSearchEngine.setImageResource(R.mipmap.ic_search_gg)
            }
            SearchConfig.SEARCH_ENGINE_BING->{
                fBinding.ivSearchEngine.setImageResource(R.mipmap.ic_search_bing)
            }
            SearchConfig.SEARCH_ENGINE_YAHOO->{
                fBinding.ivSearchEngine.setImageResource(R.mipmap.ic_search_yahoo)
            }
            SearchConfig.SEARCH_ENGINE_PERPLEXITY->{
                fBinding.ivSearchEngine.setImageResource(R.mipmap.ic_search_perplexity)
            }
        }
    }

    val newsAdapter by lazy {
        NewsMainAdapter(this)
    }
    val adapterHelper  by lazy {
        QuickAdapterHelper.Builder(newsAdapter)
            .setTrailingLoadStateAdapter(object :
                TrailingLoadStateAdapter.OnTrailingListener {
                override fun onLoad() {
                    AppLogs.dLog(fragmentTAG,"加载更多")
                    page++
                    loadData()
                }

                override fun onFailRetry() {

                }

                override fun isAllowLoading(): Boolean {
                    return fBinding.refreshLayout.isRefreshing.not()
                }

            }).build()
    }

    override fun setShowView() {
        fBinding.refreshLayout.isRefreshing = true
        fBinding.apply {
            adapterHelper.trailingLoadState = LoadState.NotLoading(false)
            rv.apply {
                layoutManager = LinearLayoutManager(rootActivity,LinearLayoutManager.VERTICAL,false)
                // 设置预加载，请调用以下方法
//                 helper.trailingLoadStateAdapter?.preloadSize = 1
                adapter = adapterHelper.adapter
                newsAdapter.setOnDebouncedItemClick{adapter, view, position ->
                    var data = newsAdapter.items.get(position)
                    var jumpData = JumpDataManager.getCurrentJumpData(tag="点击新闻item")
                    jumpData.apply {
                        jumpUrl= data.uweek?:""
                        jumpType = JumpConfig.JUMP_WEB
                        jumpTitle = data.tconsi?:""
                        isJumpClick = true
                    }
                    APP.jumpLiveData.postValue(jumpData)
                }
            }
            refreshLayout.setOnRefreshListener {
                fBinding.refreshLayout.isRefreshing = true
                page = 1
                adapterHelper.trailingLoadState = LoadState.None
                loadData()
            }
        }
        updateEngine(CacheManager.engineType)
        fBinding.topSearch.updateEngine(CacheManager.engineType)
        if (CacheManager.browserStatus == 0){
            fBinding.ivPrivate.visibility = View.GONE
            fBinding.topSearch.binding.ivPrivate.visibility = View.GONE
        }else{
            fBinding.ivPrivate.visibility = View.VISIBLE
            fBinding.topSearch.binding.ivPrivate.visibility = View.VISIBLE
        }
//        APP.bottomLiveData.postValue(0)
    }

    private fun loadData() {
        if (rootActivity is MainActivity){
            (rootActivity as MainActivity).loadNews()
        }
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BrowserFragmentMainBinding {
        return BrowserFragmentMainBinding.inflate(layoutInflater)
    }

    override fun onDestroy() {
        APP.engineLiveData.removeObservers(this)
        APP.bottomLiveData.removeObservers(this)
        super.onDestroy()
    }
}