package com.boom.aiobrowser.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.SizeUtils.dp2px
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.databinding.BrowserFragmentSearchBinding
import com.boom.aiobrowser.model.SearchViewModel
import com.boom.aiobrowser.net.SearchNet
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.tools.jobCancel
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.ParamsConfig
import com.boom.aiobrowser.ui.activity.WebDetailsActivity
import com.boom.aiobrowser.ui.adapter.HomeGuideAdapter
import com.boom.aiobrowser.ui.adapter.PopGuideAdapter
import com.boom.aiobrowser.ui.adapter.RecentSearchAdapter
import com.boom.aiobrowser.ui.adapter.SearchResultAdapter
import com.boom.base.adapter4.QuickAdapterHelper
import com.boom.base.adapter4.util.addOnDebouncedChildClick
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle
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
            //进入就有数据
            fBinding.searchGuideRoot.visibility = View.GONE
            fBinding.recentSearchRoot.visibility = View.GONE
            fBinding.searchRv.visibility = View.VISIBLE
            viewModel.searchResult(title)
        }else{
            fBinding.searchRv.visibility = View.GONE
            fBinding.recentSearchRoot.visibility = View.VISIBLE
            var searchist = CacheManager.recentSearchDataList
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
                showGuideRoot()
            }
        }
    }


    private fun showGuideRoot() {
        fBinding.searchGuideRoot.visibility = View.VISIBLE
        guideList.clear()
        guideList.add(0)
        guideList.add(1)
        var width = dp2px(7f).toFloat()
        fBinding.indicator.apply {
            setSliderColor(context.getColor(R.color.color_tab_DAE5EC), context.getColor(R.color.color_tab_5755D9))
            setSliderWidth(width)
            setSliderHeight(width)
            setSlideMode(IndicatorSlideMode.SMOOTH)
            setIndicatorStyle(IndicatorStyle.CIRCLE)
            setPageSize(guideList.size)
            notifyDataChanged()
            fBinding.vpGuide.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
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
                    setGuideIvByPosition(position,guideList)
                }
            })
        }
        homeGuideAdapter.submitList(guideList)
    }

    private fun setGuideIvByPosition(position: Int,dataList: MutableList<Int>) {
        fBinding.ivRight.setImageResource(if (position == dataList.size-1) R.mipmap.ic_guide_right1 else R.mipmap.ic_guide_right2)
        fBinding.ivLeft.setImageResource(if (position == 0) R.mipmap.ic_guide_left1 else R.mipmap.ic_guide_left2)
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
                    addSearchShrink(CacheManager.recentSearchDataList,true,false)
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
        for (i in 0 until fBinding.llRoot.childCount){
            fBinding.llRoot.getChildAt(i).setOneClick {
                var title = ""
                var url = ""
                var jumpGuide = false
                when(i){
                    0 ->{
                        jumpGuide = true
                        title = getString(R.string.app_tt)
                        url = "https://www.tiktok.com/"
                    }
                    1 ->{
                        title = getString(R.string.app_vimeo)
                        url = "https://vimeo.com/"
                    }
                    2 ->{
                        title = getString(R.string.app_x)
                        url = "https://x.com/"
                    }
                }
                PointEvent.posePoint(PointEventKey.search_page_qb,Bundle().apply {
                    putString(PointValueKey.type,title)
                })
                toWebDetailsActivity(JumpDataManager.getCurrentJumpData(tag = "searchFragment 点击推荐").apply {
                    jumpType = JumpConfig.JUMP_WEB
                    jumpTitle = title
                    jumpUrl = url
                })
            }
        }
        APP.engineLiveData.observe(this){
            fBinding.topRoot.updateEngine(it)
        }
        fBinding.ivClean.setOneClick {
            cleanType = 1
            fBinding.llDefaultRoot.visibility = View.GONE
            fBinding.llClearRoot.visibility = View.VISIBLE
            if (recentListType == 0){
                allowRecentDelete = true
                addSearchShrink(CacheManager.recentSearchDataList,true,false)
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
                CacheManager.recentSearchDataList = mutableListOf()
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
                addSearchShrink(CacheManager.recentSearchDataList,false)
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
                searchRecentAdapter.submitList(CacheManager.recentSearchDataList)
            }else{
                recentListType = 0
                fBinding.searchShrinkRoot.visibility = View.VISIBLE
                fBinding.searchExpendRv.visibility = View.GONE
                addSearchShrink(CacheManager.recentSearchDataList,false)
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

    val homeGuideAdapter by lazy {
        HomeGuideAdapter()
    }

    val guideList by lazy {
        mutableListOf<Int>()
    }

    var changeJob: Job? = null

    override fun setShowView() {
        jumpData = getBeanByGson(arguments?.getString(ParamsConfig.JSON_PARAMS)?:"",JumpData::class.java)
        fBinding.topRoot.updateTopView(1, searchRecent = {
            changeJob.jobCancel()
            if (it.isNullOrEmpty()){
                startLoadData()
            }else{
                viewModel.searchResult(it)
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
            searchResultAdapter.setOnDebouncedItemClick{ adapter, view, position ->
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
        fBinding.vpGuide.apply {
            setOrientation(ViewPager2.ORIENTATION_HORIZONTAL)
            adapter = homeGuideAdapter
        }

        PointEvent.posePoint(PointEventKey.search_page)
    }

    private fun clickDelete(position: Int) {
        var list = CacheManager.recentSearchDataList
        list.removeAt(position)
        CacheManager.recentSearchDataList = list
        startLoadData()
    }

    private fun clickHistory(data: JumpData) {
        CacheManager.saveRecentSearchData(data)
        toWebDetailsActivity(data)
        PointEvent.posePoint(PointEventKey.search_page_history, Bundle().apply {
            putString(
                PointValueKey.model_type,
                if (CacheManager.browserStatus == 1) "private" else "normal"
            )
        })
    }

    private fun toWebDetailsActivity(data:JumpData){
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