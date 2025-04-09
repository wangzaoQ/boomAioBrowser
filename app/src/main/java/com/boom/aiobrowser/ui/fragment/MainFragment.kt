package com.boom.aiobrowser.ui.fragment

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADDataManager
import com.boom.aiobrowser.ad.AioADShowManager
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.BrowserFragmentMainBinding
import com.boom.aiobrowser.model.NewsViewModel
import com.boom.aiobrowser.net.NetParams
import com.boom.aiobrowser.other.JumpConfig
import com.boom.aiobrowser.other.ParamsConfig
import com.boom.aiobrowser.other.ShortManager
import com.boom.aiobrowser.point.AD_POINT
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.LocationManager
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.activity.LocationSettingActivity
import com.boom.aiobrowser.ui.activity.MainActivity
import com.boom.aiobrowser.ui.activity.SearchActivity
import com.boom.aiobrowser.ui.activity.TrendingNewsListActivity
import com.boom.aiobrowser.ui.activity.WebDetailsActivity
import com.boom.aiobrowser.ui.adapter.NewsMainAdapter
import com.boom.aiobrowser.ui.pop.LoadingPop
import com.boom.aiobrowser.ui.pop.SearchPop
import com.boom.aiobrowser.ui.pop.TabPop
import com.boom.base.adapter4.QuickAdapterHelper
import com.boom.base.adapter4.loadState.LoadState
import com.boom.base.adapter4.loadState.trailing.TrailingLoadStateAdapter
import com.boom.base.adapter4.util.addOnDebouncedChildClick
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pop.basepopup.BasePopupWindow.OnDismissListener
import java.lang.ref.WeakReference


class MainFragment : BaseFragment<BrowserFragmentMainBinding>()  {

    private val viewModel by lazy {
        viewModels<NewsViewModel>()
    }


    override fun startLoadData() {

    }

    var absVerticalOffset = 0

    var firstLoad = true

    @Volatile
    var page = 1

    var loadingPop:LoadingPop?=null

    override fun setListener() {
        APP.engineLiveData.observe(this){
            updateEngine(it)
            fBinding.topSearch.updateEngine(it)
        }
        APP.jumpResumeData.observe(this){
            jump(it == 1)
        }
        APP.homeTabLiveData.observe(this){
            updateTopTab()
        }
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
                newsAdapter.notifyItemChanged(index)
            }
        }
        APP.trendNewsComplete.observe(this){
            if (newsAdapter.mutableItems.isNullOrEmpty())return@observe
            for (i in 0 until newsAdapter.mutableItems.size){
                if (newsAdapter.mutableItems.get(i).dataType == NewsData.TYPE_HOME_NEWS_TRENDING){
                    var trendNews = CacheManager.trendNews
                    if (trendNews.size>3){
                        trendNews = trendNews.subList(0,3)
                    }
                    newsAdapter.mutableItems.get(i).trendList = trendNews
                    newsAdapter.notifyItemChanged(i)
                    break
                }
            }

        }

//        fBinding.topSearch.binding.ivRefresh.visibility = View.GONE
//        fBinding.mainAppBar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
//            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
//                absVerticalOffset = Math.abs(verticalOffset) //AppBarLayout竖直方向偏移距离px
//                if (absVerticalOffset == 0)return
//                val totalScrollRange = appBarLayout!!.totalScrollRange //AppBarLayout总的距离px
//                val scrollPercentage = absVerticalOffset.toDouble() / totalScrollRange.toDouble()
//                var offset = (scrollPercentage * 255).toInt()
////                if (offset > 255) {
////                    offset = 255
////                } else if (offset < 0) {
////                    offset = 0
////                }
//
////                var offset = BigDecimalUtils.mul(BigDecimalUtils.div(255.toDouble(), totalScrollRange.toDouble(),10),absVerticalOffset.toDouble()).toInt()
////                var offset = absVerticalOffset / 2
////                offset = 255 - offset
//                AppLogs.dLog("onOffsetChanged", "offset=$offset")
//                if (offset > 255) {
//                    offset = 255
//                } else if (offset <= 0) {
//                    offset = 0
//                }
//                if (offset <100) {
//                    fBinding.mainCl.alpha = 1f
//                    fBinding.mainToolBar.alpha = 0f
//                    fBinding.refreshLayout.isEnabled = true
//                } else {
//                    fBinding.refreshLayout.isEnabled = false
//                    var div = (offset.toDouble() / 255.0)
//                    AppLogs.dLog("onOffsetChanged", "div=$div")
//                    if (div<0.3){
//                        div = 0.0
//                    }
//                    fBinding.mainToolBar.alpha = div.toFloat()
//                    fBinding.mainCl.alpha = 1-div.toFloat()
//                }
//            }
//        })
        newsAdapter.addOnDebouncedChildClick(R.id.tvMoreNews) { adapter, view, position ->
            APP.instance.toNewsFrom = 1
            APP.homeJumpLiveData.postValue(1)
        }
        newsAdapter.addOnDebouncedChildClick(R.id.rlMore) { adapter, view, position ->
            rootActivity.jumpActivity<TrendingNewsListActivity>()
        }
        newsAdapter.addOnDebouncedChildClick(R.id.tvTab) { adapter, view, position ->
            showTabPop()
        }
        newsAdapter.addOnDebouncedChildClick(R.id.rlSearch) { adapter, view, position ->
            rootActivity.jumpActivity<SearchActivity>(Bundle().apply {
                putString(PointValueKey.from_type,"home")
            })
            PointEvent.posePoint(PointEventKey.home_page_search)
        }

        newsAdapter.addOnDebouncedChildClick(R.id.ivSearchEngine) { adapter, view, position ->
            SearchPop.showPop(WeakReference(rootActivity),view)
            PointEvent.posePoint(PointEventKey.home_page_searchengine, Bundle().apply {
                putString(PointValueKey.click_source,"home")
            })
        }
        newsAdapter.addOnDebouncedChildClick(R.id.btnYes) { adapter, view, position ->
            var data = CacheManager.locationData
            data?.locationSuccess = true
            CacheManager.locationData = data
            newsAdapter.removeAt(position)
            APP.locationListUpdateLiveData.postValue(0)
            PointEvent.posePoint(PointEventKey.IP_location_banner,Bundle().apply {
                putString(PointValueKey.from_type,"home_page")
                putString(PointValueKey.type,"yes")
            })

//            fBinding.rv.smoothScrollToPosition(0)
//            page = 1
//            loadData()
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
                                fBinding.rv.smoothScrollToPosition(0)
                                loadData()
                                viewModel.value.getNewsData(newsAdapter.mutableItems,NetParams.MAIN, page = page,true)
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
                putString(PointValueKey.from_type,"home_page")
                putString(PointValueKey.type,"no")
            })
        }
        viewModel.value.newsLiveData.observe(rootActivity){
            loadingPop?.dismiss()
            var list = addADData(it)
            if (page == 1){
                newsAdapter.mutableItems.clear()
                newsAdapter.submitList(list)
                fBinding.rv.scrollToPosition(0)
            }else{
                newsAdapter.addAll(list)
            }
            adapterHelper.trailingLoadState = LoadState.NotLoading(false)
            fBinding.refreshLayout.isRefreshing = false
        }
        viewModel.value.newsVideoLiveData.observe(this){
            var index = -1
            for (i in 0 until newsAdapter.mutableItems.size){
                var data = newsAdapter.mutableItems.get(i)
                if (data.dataType == NewsData.TYPE_HOME_NEWS_VIDEO){
                    data.videoList = it
                    index = i
                    break
                }
            }
            if (index>=0){
                newsAdapter.notifyItemChanged(index)
            }
        }
        viewModel.value.failLiveData.observe(rootActivity){
            adapterHelper.trailingLoadState = LoadState.NotLoading(false)
            fBinding.refreshLayout.isRefreshing = false
            ToastUtils.showShort(rootActivity.getString(R.string.net_error))
        }
//        fBinding.ivDownload.setOneClick {
//            PointEvent.posePoint(PointEventKey.home_page_dl)
//            if (CacheManager.isVideoFirst){
//                DownloadVideoGuidePop(rootActivity).createPop {  }
//            }else{
//                rootActivity.startActivity(Intent(context, DownloadActivity::class.java).apply {
//                    putExtra("fromPage","home_download_pop")
//                })
//            }
//        }
//        fBinding.tvGuide.setOneClick {
//            DownloadVideoGuidePop(rootActivity).createPop {  }
//        }
    }

    private fun toLocationSetting() {
        loadingPop?.dismiss()
        rootActivity.jumpActivity<LocationSettingActivity>()
    }

    override fun onResume() {
        super.onResume()
        AppLogs.dLog(fragmentTAG,"onResume")
        jump()
        fBinding.root.postDelayed({
            updateTopUI()
        },500)
    }


    fun showTabPop() {
        var tabPop = TabPop(rootActivity)
        tabPop.createPop()
        tabPop.setOnDismissListener(object : OnDismissListener(){
            override fun onDismiss() {
                updateTopUI()
            }
        })
        PointEvent.posePoint(PointEventKey.webpage_tag)
    }

    open fun jump(isNfClick:Boolean = false) {
        AppLogs.dLog(fragmentTAG,"jump 触发")
        if (APP.instance.isHideSplash.not())return
        AppLogs.dLog(fragmentTAG,"jump 跳过限制")
        AppLogs.dLog(fragmentTAG,"onResume")
        PointEvent.posePoint(PointEventKey.home_page)
        var jumpData:JumpData
        if (firstLoad && isNfClick.not()){
            firstLoad = false
            jumpData = JumpDataManager.getCurrentJumpData(tag = "MainFragment onResume 首次")
            if (jumpData.jumpType == JumpConfig.JUMP_WEB){
                APP.jumpLiveData.postValue(jumpData)
            }
        }else{
            jumpData = JumpDataManager.getCurrentJumpData(isReset = true,tag = "MainFragment onResume 非首次")
            JumpDataManager.updateCurrentJumpData(jumpData,"MainFragment onResume 更新 jumpData")
//            if (CacheManager.engineGuideFirst){
//                CacheManager.engineGuideFirst = false
//                EngineGuidePop(rootActivity).createPop(fBinding.ivSearchEngine)
//            }
//            if (fBinding.refreshLayout.isRefreshing == true){
//                fBinding.refreshLayout.isRefreshing = false
//            }
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

        fBinding.root.postDelayed(Runnable {
            ShortManager.addWidgetToLaunch(APP.instance)
            ShortManager.addPinShortcut(WeakReference(rootActivity))
        },1000)
    }

    private fun updateTopUI() {
        var count = JumpDataManager.getBrowserTabList(CacheManager.browserStatus,tag ="mainFragment 获取当前tab数量").size
        if (count>0){
            fBinding.topSearch.binding.tvTab.visibility = View.VISIBLE
            fBinding.topSearch.binding.tvTab.text = "${count}"
            fBinding.topSearch.binding.tvTab.setOneClick {
                showTabPop()
            }
        }else{
            fBinding.topSearch.binding.tvTab.visibility = View.GONE
        }
        if (newsAdapter.mutableItems.size>0 && newsAdapter.mutableItems.get(0).dataType == NewsData.TYPE_HOME_NEWS_TOP){
            newsAdapter.notifyItemChanged(0,"updateTopTab")
        }
    }

    private fun updateEngine(type: Int) {
        newsAdapter.notifyItemChanged(0,"updateEngine")
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
                    viewModel.value.getNewsData(newsAdapter.mutableItems,NetParams.MAIN, page = page)
                }

                override fun onFailRetry() {

                }

                override fun isAllowLoading(): Boolean {
                    return fBinding.refreshLayout.isRefreshing.not()
                }

            }).build()
    }

    var isScroll = false
    var lastPosition:Int = -1
    private var nativeADAlive = false
    var nativeInterval = 3
    var firstShowAD = true


    override fun setShowView() {
        fBinding.refreshLayout.isRefreshing = true
        fBinding.refreshLayout.setProgressViewOffset(true,-200,200)
        fBinding.apply {
            updateTopTab()
            adapterHelper.trailingLoadState = LoadState.None
            rv.apply {
                layoutManager = LinearLayoutManager(rootActivity,LinearLayoutManager.VERTICAL,false)
                // 设置预加载，请调用以下方法
//                 helper.trailingLoadStateAdapter?.preloadSize = 1
                adapter = adapterHelper.adapter
                newsAdapter.setOnDebouncedItemClick{adapter, view, position ->
                    if (position>newsAdapter.items.size-1)return@setOnDebouncedItemClick
                    var manager = AioADShowManager(rootActivity, ADEnum.INT_AD, tag = "新闻列表点击的广告"){
                        var data = newsAdapter.items.get(position)
                        if (data.dataType == NewsData.TYPE_NEWS || data.dataType == NewsData.TYPE_HOME_NEWS_TRENDING || data.dataType == NewsData.TYPE_DETAILS_NEWS_SEARCH){
                            rootActivity.jumpActivity<WebDetailsActivity>(Bundle().apply {
                                putString(ParamsConfig.JSON_PARAMS, toJson(data))
                            })
                            PointEvent.posePoint(PointEventKey.home_page_feeds,Bundle().apply {
                                putString(PointValueKey.news_id,data.itackl)
                            })
                        }
                    }
                    manager.showScreenAD(AD_POINT.aobws_newsclick_int)
                }
                addDefault()
                addOnScrollListener(object : RecyclerView.OnScrollListener(){
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if (newState!=RecyclerView.SCROLL_STATE_IDLE){
                            isScroll = true
                        }
                        if (newState == RecyclerView.SCROLL_STATE_IDLE){
                            if(isScroll){
                                isScroll = false
                                PointEvent.posePoint(PointEventKey.home_page_slide)
                            }
//                            if (lastPosition!=-1 &&firstShowAD && nativeADAlive.not() && AioADDataManager.getCacheAD(ADEnum.NATIVE_AD)!=null){
//                                firstShowAD = false
//                                AppLogs.dLog(fragmentTAG,"滑动停止刷新插入广告 刷新位置:${lastPosition}")
//                            }
                            showShareImage()
                        }
                        if (newState !=RecyclerView.SCROLL_STATE_SETTLING){
                            //不是惯性滑动
                            lastPosition = (fBinding.rv.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                            if (lastPosition == -1)return
                            if (nativeADAlive.not() && AioADDataManager.getCacheAD(ADEnum.NATIVE_AD)!=null){
                                if (APP.instance.firstInsertHomeAD && AioADDataManager.adFilter1().not()){
                                    APP.instance.firstInsertHomeAD = false
                                    nativeADAlive = true
                                    if (lastPosition<=newsAdapter.items.size){
                                        newsAdapter.mutableItems.add(lastPosition, NewsData().apply {
                                            dataType = NewsData.TYPE_AD
                                        })
                                        AppLogs.dLog(fragmentTAG,"列表滑动插入广告位置 1:${lastPosition}")
                                        val size = newsAdapter.items.size
                                        for (i in lastPosition until size){
                                            val mod = i%size
                                            if (((mod == nativeInterval+lastPosition || mod == nativeInterval +lastPosition+3))){
                                                newsAdapter.mutableItems.add(i+1,NewsData().apply {
                                                    dataType = NewsData.TYPE_AD
                                                })
                                                AppLogs.dLog(fragmentTAG,"列表滑动插入广告位置 2:${i}")
                                            }
                                        }
                                        newsAdapter.notifyItemInserted(lastPosition)
                                        newsAdapter.notifyItemRangeChanged(lastPosition, newsAdapter.mutableItems.size-lastPosition)
                                    }
                                }
                            }
                            hideShareImage()
                            var firstPosition = (fBinding.rv.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                            if (firstPosition>0 && topSearch.visibility == View.GONE){
                                topSearch.visibility = View.VISIBLE
                                var animator = ObjectAnimator.ofFloat(topSearch, "translationY",-topSearch.height.toFloat(),0f );
                                animator.setDuration(500L)
                                animator.start()
                            }else if (firstPosition == 0 && topSearch.visibility == View.VISIBLE){
                                var animator = ObjectAnimator.ofFloat(topSearch, "translationY",0f,-topSearch.height.toFloat());
                                animator.setDuration(500L)
                                animator.start()
                                topSearch.postDelayed({
                                    topSearch.visibility = View.GONE
                                },500)
                            }
                        }
                    }

                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                    }
                })

//                addItemDecoration(DividerItemDecoration(rootActivity, LinearLayout.VERTICAL))
            }
            refreshLayout.setOnRefreshListener {
                fBinding.refreshLayout.isRefreshing = true
                page = 1
                loadData()
                viewModel.value.getNewsData(newsAdapter.mutableItems,NetParams.MAIN, page = page,true)
            }
        }
        updateEngine(CacheManager.engineType)
        fBinding.topSearch.updateEngine(CacheManager.engineType)
        if (CacheManager.browserStatus == 0){
            fBinding.topSearch.binding.ivPrivate.visibility = View.GONE
        }else{
            fBinding.topSearch.binding.ivPrivate.visibility = View.VISIBLE
        }
        viewModel.value.getNewsData(newsAdapter.mutableItems,NetParams.MAIN, page = page)
        viewModel.value.getNewsVideoList("")
        fBinding.root.postDelayed({
            fBinding.topSearch.visibility = View.GONE
        },0)
//        APP.bottomLiveData.postValue(0)
    }

    private fun addDefault() {
        newsAdapter.submitList(mutableListOf<NewsData>().apply {
            add(NewsData().apply {
                dataType = NewsData.TYPE_HOME_NEWS_TOP
            })
            add(NewsData().apply {
                dataType = NewsData.TYPE_HOME_NEWS_TRENDING
                trendList = mutableListOf<NewsData>().apply {
                    add(NewsData().apply {
                        isLoading = true
                    })
                    add(NewsData().apply {
                        isLoading = true
                    })
                    add(NewsData().apply {
                        isLoading = true
                    })
                }
            })
        })
    }

    private fun updateTopTab() {
        newsAdapter.notifyItemChanged(0)
    }

    fun addADData(newsBeans: List<NewsData>?):List<NewsData> {
        var list = ArrayList<NewsData>()
        if (AioADDataManager.getCacheAD(ADEnum.NATIVE_AD)!=null && AioADDataManager.adFilter1().not()){
            nativeADAlive = true
            newsBeans?.forEachIndexed { index, newsBean ->
                val listADInterval = listOf(nativeInterval, 3)
                val interval = index.mod(listADInterval.sum())
                if (((index != 0 && interval == 0) || interval == nativeInterval || interval == nativeInterval + 3)){
                    list.add(NewsData().apply {
                        dataType = NewsData.TYPE_AD
                    })
                }
                list.add(newsBean)
            }
            return list
        }
        return newsBeans?:ArrayList()
    }

    private fun loadData() {
        if (page>1){
            adapterHelper.trailingLoadState = LoadState.Loading
        }else {
            adapterHelper.trailingLoadState = LoadState.None
            fBinding.refreshLayout.isRefreshing = true
        }
        PointEvent.posePoint(PointEventKey.home_page_refresh,Bundle().apply {
            putString(PointValueKey.refresh_type,if (page == 1)"down" else "up")
        })
    }
    private var isAnimatorEnd = false

    private fun showShareImage() {
//        val translationX: Float = fBinding.flBottom.getTranslationX()
//        val animator: ObjectAnimator = ObjectAnimator.ofFloat(fBinding.flBottom, "translationX", 10f)
//        animator.setDuration(600)
//        if (!isAnimatorEnd) {
//            animator.startDelay = 1200
//        }
//        animator.start()
    }

    private fun hideShareImage() {
//        isAnimatorEnd = false
//        val translationX: Float = fBinding.flBottom.getTranslationX()
//        val animator = ObjectAnimator.ofFloat(
//            fBinding.flBottom, "translationX", dp2px(
//                activity, 70f
//            ).toFloat()
//        )
//        animator.setDuration(600)
//        animator.addListener(object : Animator.AnimatorListener {
//            override fun onAnimationStart(animation: Animator) {
//            }
//
//            override fun onAnimationEnd(animation: Animator) {
//                isAnimatorEnd = true
//            }
//
//            override fun onAnimationCancel(animation: Animator) {
//            }
//
//            override fun onAnimationRepeat(animation: Animator) {
//            }
//        })
//        animator.start()
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
        APP.jumpResumeData.removeObservers(this)
        APP.homeTabLiveData.removeObservers(this)
        APP.locationListUpdateLiveData.removeObservers(this)
        APP.trendNewsComplete.removeObservers(this)
        APP.homeTopicLiveData.removeObservers(this)
        super.onDestroy()
    }
}