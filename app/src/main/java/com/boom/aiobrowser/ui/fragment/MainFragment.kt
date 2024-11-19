package com.boom.aiobrowser.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.SizeUtils.dp2px
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADDataManager
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.BrowserFragmentMainBinding
import com.boom.aiobrowser.model.NewsViewModel
import com.boom.aiobrowser.net.NetParams
import com.boom.aiobrowser.other.JumpConfig
import com.boom.aiobrowser.other.ParamsConfig
import com.boom.aiobrowser.other.SearchConfig
import com.boom.aiobrowser.other.ShortManager
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.BigDecimalUtils
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.partitionList
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.activity.MainActivity
import com.boom.aiobrowser.ui.activity.SearchActivity
import com.boom.aiobrowser.ui.activity.WebDetailsActivity
import com.boom.aiobrowser.ui.adapter.HomeTabAdapter
import com.boom.aiobrowser.ui.adapter.NewsMainAdapter
import com.boom.aiobrowser.ui.pop.EngineGuidePop
import com.boom.aiobrowser.ui.pop.SearchPop
import com.boom.base.adapter4.QuickAdapterHelper
import com.boom.base.adapter4.loadState.LoadState
import com.boom.base.adapter4.loadState.trailing.TrailingLoadStateAdapter
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import com.google.android.material.appbar.AppBarLayout
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle
import java.lang.ref.WeakReference


class MainFragment : BaseFragment<BrowserFragmentMainBinding>()  {

    private val viewModel by lazy {
        viewModels<NewsViewModel>()
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
        APP.jumpResumeData.observe(this){
            jump()
        }
        fBinding.topSearch.binding.ivRefresh.visibility = View.GONE
        fBinding.mainAppBar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                absVerticalOffset = Math.abs(verticalOffset) //AppBarLayout竖直方向偏移距离px
                if (absVerticalOffset == 0)return
                val totalScrollRange = appBarLayout!!.totalScrollRange //AppBarLayout总的距离px
                var offset = BigDecimalUtils.mul(BigDecimalUtils.div(255.toDouble(), totalScrollRange.toDouble(),10),absVerticalOffset.toDouble()).toInt()
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
                    var div = BigDecimalUtils.div(offset.toDouble(), 255.0, 2)
                    AppLogs.dLog("onOffsetChanged", "div=$div")
                    if (div<0.3){
                        div = 0.0
                    }
                    fBinding.mainToolBar.alpha = div.toFloat()
                    fBinding.mainCl.alpha = 1-div.toFloat()
                }
            }
        })
        fBinding.rlSearch.setOneClick {
            JumpDataManager.getCurrentJumpData(isReset = true,tag = "mainFragment 点击搜索").apply {
                jumpType = JumpConfig.JUMP_SEARCH
            }
            startActivity(Intent(rootActivity,SearchActivity::class.java))
            PointEvent.posePoint(PointEventKey.home_page_search)
        }
        fBinding.ivSearchEngine.setOneClick {
            SearchPop.showPop(WeakReference(rootActivity),fBinding.ivSearchEngine)
            PointEvent.posePoint(PointEventKey.home_page_searchengine, Bundle().apply {
                putString(PointValueKey.click_source,"home")
            })
        }

        viewModel.value.newsLiveData.observe(rootActivity){
            var list = addADData(it)
            if (page == 1){
                newsAdapter.submitList(list)
                fBinding.rv.scrollToPosition(0)
            }else{
                newsAdapter.addAll(list)
            }
            adapterHelper.trailingLoadState = LoadState.NotLoading(false)
            fBinding.refreshLayout.isRefreshing = false
        }
        viewModel.value.failLiveData.observe(rootActivity){
            adapterHelper.trailingLoadState = LoadState.NotLoading(false)
            fBinding.refreshLayout.isRefreshing = false
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

    override fun onResume() {
        super.onResume()
        AppLogs.dLog(fragmentTAG,"onResume")
        jump()
    }

    open fun jump() {
        AppLogs.dLog(fragmentTAG,"jump 触发")
        if (APP.instance.isHideSplash.not())return
        AppLogs.dLog(fragmentTAG,"jump 跳过限制")
        AppLogs.dLog(fragmentTAG,"onResume")
        PointEvent.posePoint(PointEventKey.home_page)
        var jumpData:JumpData
        if (firstLoad){
            firstLoad = false
            jumpData = JumpDataManager.getCurrentJumpData(tag = "MainFragment onResume 首次")
            if (jumpData.jumpType == JumpConfig.JUMP_WEB){
                APP.jumpLiveData.postValue(jumpData)
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

        fBinding.root.postDelayed(Runnable {
            ShortManager.addWidgetToLaunch(APP.instance)
            ShortManager.addPinShortcut(WeakReference(rootActivity))
        },1000)
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

    val tabAdapter by lazy {

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

    var isScroll = false
    var lastPosition:Int = -1
    private var nativeADAlive = false
    var nativeInterval = 3
    var firstShowAD = true


    override fun setShowView() {
        fBinding.refreshLayout.isRefreshing = true
        fBinding.apply {
            updateTopTab()
            adapterHelper.trailingLoadState = LoadState.NotLoading(false)
            rv.apply {
                layoutManager = LinearLayoutManager(rootActivity,LinearLayoutManager.VERTICAL,false)
                // 设置预加载，请调用以下方法
//                 helper.trailingLoadStateAdapter?.preloadSize = 1
                adapter = adapterHelper.adapter
                newsAdapter.setOnDebouncedItemClick{adapter, view, position ->
                    var data = newsAdapter.items.get(position)
                    rootActivity.jumpActivity<WebDetailsActivity>(Bundle().apply {
                        putString(ParamsConfig.JSON_PARAMS, toJson(data))
                    })
//                    var jumpData = JumpDataManager.getCurrentJumpData(tag="点击新闻item")
//                    jumpData.apply {
//                        jumpUrl= data.uweek?:""
//                        jumpType = JumpConfig.JUMP_WEB
//                        jumpTitle = data.tconsi?:""
//                        isJumpClick = true
//                    }
//                    APP.jumpLiveData.postValue(jumpData)
                    PointEvent.posePoint(PointEventKey.home_page_feeds,Bundle().apply {
                        putString(PointValueKey.news_id,data.itackl)
                    })
                }
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
                            if (lastPosition!=-1 &&firstShowAD && nativeADAlive.not()){
                                firstShowAD = false
                                AppLogs.dLog(fragmentTAG,"滑动停止刷新插入广告 刷新位置:${lastPosition}")
                            }
                            showShareImage()
                        }
                        if (newState !=RecyclerView.SCROLL_STATE_SETTLING){
                            //不是惯性滑动
                            lastPosition = (fBinding.rv.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                            if (lastPosition == -1)return
                            if (nativeADAlive.not()){
                                if (APP.instance.firstInsertHomeAD && AioADDataManager.adFilter1().not()){
                                    APP.instance.firstInsertHomeAD = false
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
                        }
                    }
                })
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
        loadNews()
//        APP.bottomLiveData.postValue(0)
    }

    private fun updateTopTab() {
        var tabList = CacheManager.homeTabList

        // 将集合按每 8 个分割
        val partitionSize = 8
        val dataList: MutableList<MutableList<JumpData>> = partitionList(tabList, partitionSize) as MutableList<MutableList<JumpData>>
        var endList = dataList.get(dataList.size-1)
        if (endList.size<8){
            endList.add(JumpData().apply {
                jumpType = JumpConfig.JUMP_WEB_TYPE
                jumpUrl = ""
                jumpTitle = ""
            })
        }
        fBinding.vp2.apply {
            adapter = HomeTabAdapter(dataList,rootActivity)
        }
//        var params = fBinding.rlHistory.layoutParams as ConstraintLayout.LayoutParams
        if (dataList.size>1){
            fBinding.indicator.visibility = View.VISIBLE
            var width = dp2px(7f).toFloat()
            fBinding.indicator.apply {
                setSliderColor(
                    context.getColor(R.color.color_tab_DAE5EC),
                    context.getColor(R.color.color_tab_5755D9)
                )
                setSliderWidth(width)
                setSliderHeight(width)
                setSlideMode(IndicatorSlideMode.SMOOTH)
                setIndicatorStyle(IndicatorStyle.CIRCLE)
                setPageSize(dataList.size)
                notifyDataChanged()
                fBinding.vp2.registerOnPageChangeCallback(object :
                    ViewPager2.OnPageChangeCallback() {
                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {
                        super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                        fBinding.indicator.onPageScrolled(
                            position,
                            positionOffset,
                            positionOffsetPixels
                        )
                    }

                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        fBinding.indicator.onPageSelected(position)
                    }
                })
                fBinding.rlHistory.setPadding(0,dp2px(11f),0, dp2px(16f))
            }

        }else{
            fBinding.indicator.visibility = View.GONE
            fBinding.rlHistory.setPadding(0,dp2px(11f),0, dp2px(20f))
        }

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

    fun loadNews(){
        viewModel.value.getNewsData(NetParams.FOR_YOU)
    }

    private fun loadData() {
        loadNews()
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
        super.onDestroy()
    }
}