package com.boom.aiobrowser.ui.fragment

import android.R.attr.duration
import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.BrowserFragmentMainBinding
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.BigDecimalUtils
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.SearchConfig
import com.boom.aiobrowser.ui.adapter.NewsMainAdapter
import com.boom.aiobrowser.ui.pop.SearchPop
import com.boom.base.adapter4.QuickAdapterHelper
import com.boom.base.adapter4.loadState.trailing.TrailingLoadStateAdapter
import com.google.android.material.appbar.AppBarLayout
import java.lang.ref.WeakReference


class MainFragment : BaseFragment<BrowserFragmentMainBinding>()  {
    override fun startLoadData() {

    }

    var absVerticalOffset = 0

    override fun setListener() {
        APP.engineLiveData.observe(this){
            updateEngine(it)
            fBinding.topSearch.updateEngine(it)
        }
        fBinding.mainAppBar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                absVerticalOffset = Math.abs(verticalOffset) //AppBarLayout竖直方向偏移距离px
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
                    else->{
                        title = getString(R.string.app_ytb)
                        url = "https://www.youtube.com/"
                    }
                }
                APP.jumpLiveData.postValue(JumpData().apply {
                    jumpType = JumpConfig.JUMP_WEB
                    jumpTitle = title
                    jumpUrl = url
                })
            }
        }
        fBinding.rlSearch.setOneClick {
            APP.jumpLiveData.postValue(JumpData().apply {
                jumpType = JumpConfig.JUMP_SEARCH
            })
        }
        fBinding.ivSearchEngine.setOnClickListener {
            SearchPop.showPop(WeakReference(rootActivity),fBinding.ivSearchEngine)
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
        NewsMainAdapter()
    }

    override fun setShowView() {
        fBinding.apply {
            rv.apply {
                layoutManager = LinearLayoutManager(rootActivity,LinearLayoutManager.VERTICAL,false)
                var helper = QuickAdapterHelper.Builder(newsAdapter)
                    .setTrailingLoadStateAdapter(object :
                        TrailingLoadStateAdapter.OnTrailingListener {
                        override fun onLoad() {

                        }

                        override fun onFailRetry() {

                        }

                        override fun isAllowLoading(): Boolean {
                            return !fBinding.refreshLayout.isRefreshing
                        }
                    }).build()

                // 设置预加载，请调用以下方法
                // helper.trailingLoadStateAdapter?.preloadSize = 1
                adapter = helper.adapter
            }
            refreshLayout.setOnRefreshListener {

            }
        }
        var list = mutableListOf<NewsData>()
        for (i in 0 until 10){
            list.add(NewsData())
        }
        newsAdapter.submitList(list)
        updateEngine(CacheManager.engineType)
        fBinding.topSearch.updateEngine(CacheManager.engineType)
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BrowserFragmentMainBinding {
        return BrowserFragmentMainBinding.inflate(layoutInflater)
    }

    override fun onDestroy() {
        APP.engineLiveData.removeObservers(this)
        super.onDestroy()
    }
}