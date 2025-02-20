package com.boom.aiobrowser.ui.fragment

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.databinding.BrowserFragmentSearchBinding
import com.boom.aiobrowser.model.SearchViewModel
import com.boom.aiobrowser.net.SearchNet
import com.boom.aiobrowser.other.JumpConfig
import com.boom.aiobrowser.other.ParamsConfig
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.tools.jobCancel
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.activity.WebDetailsActivity
import com.boom.aiobrowser.ui.adapter.RecentSearchAdapter
import com.boom.aiobrowser.ui.adapter.SearchResultAdapter
import com.boom.base.adapter4.QuickAdapterHelper
import com.boom.base.adapter4.util.addOnDebouncedChildClick
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import com.boom.indicator.ViewPagerHelper
import com.boom.indicator.buildins.UIUtil
import com.boom.indicator.buildins.commonnavigator.CommonNavigator
import com.boom.indicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import com.boom.indicator.buildins.commonnavigator.abs.IPagerIndicator
import com.boom.indicator.buildins.commonnavigator.abs.IPagerTitleView
import com.boom.indicator.buildins.commonnavigator.indicators.LinePagerIndicator
import com.boom.indicator.buildins.commonnavigator.titles.SimplePagerTitleView
import kotlinx.coroutines.Job

class SearchFragment : BaseFragment<BrowserFragmentSearchBinding>() {

    private val viewModel by viewModels<SearchViewModel>()

    var jumpData:JumpData?=null

    companion object{
        /**
         * search download home nf widget
         */
        fun newInstance(fromType:String,jsonData: String): SearchFragment{
            val args = Bundle()
            args.putString(ParamsConfig.JSON_PARAMS, jsonData)
            args.putString(PointValueKey.from_type, fromType)
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
            fBinding.searchRv.visibility = View.VISIBLE
            fBinding.searchEmptyRoot.visibility = View.GONE
            //进入就有数据
            viewModel.searchResult(title,fromType)
        }else{
            fBinding.searchRv.visibility = View.GONE
            fBinding.searchEmptyRoot.visibility = View.VISIBLE
            var searchist = CacheManager.getRecentSearchDataList(fromType)
            if (searchist.isNotEmpty()){
                fBinding.recentSearchRoot.visibility = View.VISIBLE
                //默认展示小布局
                if (recentListType == 0){
                    fBinding.searchShrinkRoot.visibility = View.VISIBLE
                    fBinding.searchExpendRv.visibility = View.GONE
                    addSearchShrink(searchist,false)
                }else if (recentListType == 1){
                    fBinding.searchShrinkRoot.visibility = View.GONE
                    fBinding.searchExpendRv.visibility = View.VISIBLE
                }
                if (cleanType == 0){
                    fBinding.llDefaultRoot.visibility = View.VISIBLE
                    fBinding.llClearRoot.visibility = View.GONE
                }else{
                    fBinding.llDefaultRoot.visibility = View.GONE
                    fBinding.llClearRoot.visibility = View.VISIBLE
                }
            }else{
                fBinding.recentSearchRoot.visibility = View.GONE
            }
        }
        var trendNews = CacheManager.trendNews
        if (trendNews.isNullOrEmpty()){
            fBinding.tvGuessTitle.visibility = View.GONE
        }else{
            fBinding.tvGuessTitle.visibility = View.VISIBLE
            fBinding.guessShrinkRoot.removeAllViews()
            fBinding.guessShrinkRoot.heightLimit = false
            fBinding.guessShrinkRoot.maxLimit = false
            for (i in 0 until trendNews.size){
                if (i == 6)break
                var data = trendNews.get(i)

                if (data.tdetai.isNullOrEmpty().not()){
                    var content = data.tdetai!!.get(0)
                    var recentView = LayoutInflater.from(context).inflate(R.layout.item_guess_recent,null,false)
                    var tv = recentView.findViewById<AppCompatTextView>(R.id.tvRecentHistory)
                    recentView.setOneClick {
                        rootActivity.jumpActivity<WebDetailsActivity>(Bundle().apply {
                            putString(ParamsConfig.JSON_PARAMS, toJson(data))
                        })
                        PointEvent.posePoint(PointEventKey.search_page_gtr,Bundle().apply {
                            putString(PointValueKey.type,content)
                            putString(PointValueKey.news_id,data.itackl)
                        })
                    }
                    tv.text = "#${content}"
                    fBinding.guessShrinkRoot.addView(recentView)
                }
            }
        }
        APP.instance.appModel.getTrendsNews()
    }


    var allowRecentDelete = false

    /**
     * 小布局
     */
    private fun addSearchShrink(dataList: MutableList<JumpData>,showDelete:Boolean,maxLimit:Boolean = true) {
        fBinding.searchShrinkRoot.removeAllViews()
        fBinding.searchShrinkRoot.heightLimit = false
        fBinding.searchShrinkRoot.maxLimit = maxLimit
//        var tempList = mutableListOf<JumpData>()
        for (i in 0 until dataList.size){
            var data = dataList.get(i)
//            tempList.add(data)
            var recentView = LayoutInflater.from(context).inflate(R.layout.item_search_recent,null,false)
            var tv = recentView.findViewById<AppCompatTextView>(R.id.tvRecentHistory)
            var iv = recentView.findViewById<AppCompatImageView>(R.id.ivRecentDelete)
            iv.visibility = if (showDelete) View.VISIBLE else View.GONE
            tv.text = data.jumpTitle
            recentView.setOneClick {
                if (allowRecentDelete){
                    clickDelete(i)
                    addSearchShrink(CacheManager.getRecentSearchDataList(fromType),true,false)
                }else{
                    var url = SearchNet.getSearchUrl(data.jumpTitle)
                    var jumpData = JumpDataManager.getCurrentJumpData(tag = "searchFragment 搜索").apply {
                        jumpType = JumpConfig.JUMP_WEB
                        jumpTitle = tv.text.toString()
                        jumpUrl = url
                    }
                    clickHistory(jumpData)
                }
            }
            fBinding.searchShrinkRoot.addView(recentView)
            if (fBinding.searchShrinkRoot.heightLimit){
                break
            }
        }
    }

    // 0 小布局 1 展开
    var recentListType = 0

    // 0 默认 1 clean 状态
    var cleanType = 0

    override fun setListener() {
        APP.engineLiveData.observe(this){
            fBinding.topRoot.updateEngine(it)
        }
        fBinding.ivBack.setOneClick {
            rootActivity.finish()
        }
        fBinding.ivClean.setOneClick {
            cleanType = 1
            fBinding.llDefaultRoot.visibility = View.GONE
            fBinding.llClearRoot.visibility = View.VISIBLE
            if (recentListType == 0){
                allowRecentDelete = true
                addSearchShrink(CacheManager.getRecentSearchDataList(fromType),true,false)
            }else if (recentListType == 1){
                searchRecentAdapter.allowDelete = true
                searchRecentAdapter.notifyDataSetChanged()
            }
        }
        fBinding.tvClearAll.setOneClick {
            var builder =  AlertDialog.Builder(rootActivity)
            builder.setMessage(R.string.app_delete_msg)
            builder.setCancelable(true);
            builder.setNegativeButton(rootActivity.getString(R.string.app_yes)) { dialog, which ->
                CacheManager.saveRecentSearchDataList(fromType,mutableListOf())
                startLoadData()
                dialog.dismiss()
            }
            builder.setNeutralButton(rootActivity.getString(R.string.app_no)) { dialog, which ->
                dialog.dismiss()
            }
            var dialog = builder.create()
            dialog!!.show()
        }
        fBinding.tvFinish.setOneClick {
            cleanType = 0
            fBinding.llDefaultRoot.visibility = View.VISIBLE
            fBinding.llClearRoot.visibility = View.GONE
            if (recentListType == 0){
                allowRecentDelete = false
                addSearchShrink(CacheManager.getRecentSearchDataList(fromType),false)
            }else if (recentListType == 1){
                searchRecentAdapter.allowDelete = false
                searchRecentAdapter.notifyDataSetChanged()
            }
        }
        fBinding.ivExpend.setOneClick {
            if (recentListType == 0){
                recentListType = 1
                fBinding.searchShrinkRoot.visibility = View.GONE
                fBinding.searchExpendRv.visibility = View.VISIBLE
                searchRecentAdapter.submitList(CacheManager.getRecentSearchDataList(fromType))
            }else{
                recentListType = 0
                fBinding.searchShrinkRoot.visibility = View.VISIBLE
                fBinding.searchExpendRv.visibility = View.GONE
                addSearchShrink(CacheManager.getRecentSearchDataList(fromType),false)
            }
        }
        viewModel.searchViewModel.observe(this){
            startLoadData()
            searchResultAdapter.content = fBinding.topRoot.binding.etToolBarSearch.text.toString()
            searchResultAdapter.submitList(it)
        }
    }

    val searchRecentAdapter by lazy {
        RecentSearchAdapter()
    }

    val searchResultAdapter by lazy {
        SearchResultAdapter()
    }

    var changeJob: Job? = null
    var fromType = ""

    override fun setShowView() {
        jumpData = getBeanByGson(arguments?.getString(ParamsConfig.JSON_PARAMS)?:"",JumpData::class.java)
        fromType = arguments?.getString(PointValueKey.from_type)?:""
        fBinding.topRoot.updateTopView(1, searchRecent = {
            changeJob.jobCancel()
            if (it.isNullOrEmpty()){
                startLoadData()
            }else{
                viewModel.searchResult(it,fromType)
            }
        }, searchResult = {
//            viewModel.searchResult(it)
            var url = SearchNet.getSearchUrl(it)

            var jumpData = JumpDataManager.getCurrentJumpData(tag = "searchFragment 搜索").apply {
                jumpType = JumpConfig.JUMP_WEB
                jumpTitle = it
                jumpUrl = url
            }
            toWebDetailsActivity(jumpData)
            PointEvent.posePoint(PointEventKey.search_page_go,Bundle().apply {
                putString(PointValueKey.input_text,it)
            })
        })
        fBinding.topRoot.updateEngine(CacheManager.engineType)
        if (jumpData?.jumpUrl.isNullOrEmpty().not()){
            fBinding.topRoot.binding.etToolBarSearch.setText("${jumpData?.jumpUrl}")
        }
        fBinding.topRoot.binding.etToolBarSearch.selectAll()

        fBinding.searchExpendRv.apply {
            layoutManager = LinearLayoutManager(rootActivity,LinearLayoutManager.VERTICAL,false)
            var helper = QuickAdapterHelper.Builder(searchRecentAdapter)
                .build()
            // 设置预加载，请调用以下方法
            // helper.trailingLoadStateAdapter?.preloadSize = 1
            adapter = helper.adapter
            // 添加子 view 的点击事件(去除点击抖动的扩展方法)
            searchRecentAdapter.addOnDebouncedChildClick(R.id.ivDelete) { adapter, view, position ->
                searchRecentAdapter.removeAt(position)
                clickDelete(position)
            }
            searchRecentAdapter.setOnDebouncedItemClick{ adapter, view, position ->
                var data = searchRecentAdapter.items.get(position)
                clickHistory(data)
            }
        }
        fBinding.searchRv.apply {
            layoutManager = LinearLayoutManager(rootActivity,LinearLayoutManager.VERTICAL,false)
            adapter = searchResultAdapter
            searchResultAdapter.addOnDebouncedChildClick(R.id.rlRoot){ adapter, view, position ->
                var data = searchResultAdapter.items.get(position)
                var jumpData = JumpDataManager.getCurrentJumpData(tag = "searchFragment 点击联想搜索").apply {
                    jumpType = JumpConfig.JUMP_WEB
                    jumpTitle = data.searchContent
                    jumpUrl = SearchNet.getSearchUrl(data.searchContent)
                }
//                JumpDataManager.updateCurrentJumpData(jumpData,tag="searchFragment 点击联想搜索")
                clickHistory(jumpData)
            }
        }
        PointEvent.posePoint(PointEventKey.search_page,Bundle().apply {
            putString(PointValueKey.model_type, if (CacheManager.browserStatus == 1) "private" else "normal")
            putString(PointValueKey.from_type,fromType)
        })
        initVp()
    }

    var fragmentList = mutableListOf<BaseFragment<*>>()
    var titleList = mutableListOf<String>()

    private fun initVp() {
        titleList.add(getString(R.string.app_local_brief))
        titleList.add(getString(R.string.app_trending_today))
        titleList.add(getString(R.string.app_film))
        fragmentList.add(NewsFragment.newInstance(getString(R.string.app_local_brief)))
        fragmentList.add(NewsFragment.newInstance(getString(R.string.app_trending_today)))
        fragmentList.add(NewsFragment.newInstance(getString(R.string.app_movie)))
        fBinding.vpNews.apply {
            adapter = object : FragmentPagerAdapter(
                childFragmentManager,
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            ) {
                override fun getItem(position: Int): Fragment {
                    return fragmentList[position]
                }

                override fun getCount(): Int {
                    return fragmentList.size
                }
            }
        }
        val commonNavigator7: CommonNavigator = CommonNavigator(rootActivity)
        commonNavigator7.setScrollPivotX(0.65f)
        commonNavigator7.setAdapter(object : CommonNavigatorAdapter() {

            override fun getCount(): Int {
                return fragmentList.size
            }

            override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                val simplePagerTitleView: SimplePagerTitleView = SimplePagerTitleView(context)
                simplePagerTitleView.setText(titleList.get(index))
                simplePagerTitleView.setNormalColor(Color.parseColor("#FF666666"))
                simplePagerTitleView.setSelectedColor(Color.parseColor("#FF000000"))
                simplePagerTitleView.setOnClickListener(View.OnClickListener {
                    fBinding.vpNews.setCurrentItem(
                        index
                    )
                })
                return simplePagerTitleView
            }

            override fun getIndicator(context: Context?): IPagerIndicator {
                val indicator: LinePagerIndicator = LinePagerIndicator(context)
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY)
                indicator.setLineHeight(UIUtil.dip2px(context, 3.3).toFloat())
                indicator.setLineWidth(UIUtil.dip2px(context, 24.0).toFloat())
                indicator.setRoundRadius(UIUtil.dip2px(context, 12.0).toFloat())
                indicator.setStartInterpolator(AccelerateInterpolator())
                indicator.setEndInterpolator(DecelerateInterpolator(2.0f))
                indicator.setColors(Color.parseColor("#FF5B5ADB"))
                return indicator
            }
        })
        fBinding.indicator.setNavigator(commonNavigator7)
        ViewPagerHelper.bind(fBinding.indicator, fBinding.vpNews)

    }

    private fun clickDelete(position: Int) {
        var list = CacheManager.getRecentSearchDataList(fromType)
        list.removeAt(position)
        CacheManager.saveRecentSearchDataList(fromType,list)
        startLoadData()
    }

    private fun clickHistory(data: JumpData) {
        toWebDetailsActivity(data)
        PointEvent.posePoint(PointEventKey.search_page_history, Bundle().apply {
            putString(
                PointValueKey.model_type,
                if (CacheManager.browserStatus == 1) "private" else "normal"
            )
        })
    }

    private fun toWebDetailsActivity(data:JumpData){
//        APP.jumpLiveData.postValue(data)
//        rootActivity.finish()
//        APP.jumpLiveData.postValue(JumpDataManager.addTabToOtherWeb(data.jumpUrl, title = data.jumpTitle,"搜索",false))
        CacheManager.saveRecentSearchData(data,fromType)
        JumpDataManager.updateCurrentJumpData(data,tag = "searchResult")
        APP.jumpLiveData.postValue(data)
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