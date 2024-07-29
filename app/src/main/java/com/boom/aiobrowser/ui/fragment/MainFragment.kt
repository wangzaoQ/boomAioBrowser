package com.boom.aiobrowser.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.BrowserFragmentMainBinding
import com.boom.aiobrowser.databinding.BrowserFragmentStartBinding
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.BigDecimalUtils
import com.boom.aiobrowser.ui.adapter.NewsMainAdapter
import com.boom.base.adapter4.QuickAdapterHelper
import com.boom.base.adapter4.loadState.trailing.TrailingLoadStateAdapter
import com.google.android.material.appbar.AppBarLayout

class MainFragment : BaseFragment<BrowserFragmentMainBinding>()  {
    override fun startLoadData() {

    }

    override fun setListener() {
        fBinding.mainAppBar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                val absVerticalOffset = Math.abs(verticalOffset) //AppBarLayout竖直方向偏移距离px
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
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BrowserFragmentMainBinding {
        return BrowserFragmentMainBinding.inflate(layoutInflater)
    }
}