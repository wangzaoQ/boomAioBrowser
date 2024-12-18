package com.boom.aiobrowser.ui.activity

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.NewsActivityTrendingListBinding
import com.boom.aiobrowser.other.ParamsConfig
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointManager
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.adapter.TrendingNewsAdapter
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import com.boom.drag.utils.DisplayUtils.dp2px

class TrendingNewsListActivity :BaseActivity<NewsActivityTrendingListBinding>() {
    override fun getBinding(inflater: LayoutInflater): NewsActivityTrendingListBinding {
        return NewsActivityTrendingListBinding.inflate(layoutInflater)
    }

    val newsAdapter by lazy {
        TrendingNewsAdapter()
    }

    override fun setListener() {
        acBinding.ivBack.setOneClick {
            finish()
        }
    }

    override fun setShowView() {
        var trendNews = CacheManager.trendNews
        acBinding.rv.apply {
            layoutManager = LinearLayoutManager(this@TrendingNewsListActivity,LinearLayoutManager.VERTICAL,false)
            adapter = newsAdapter
            newsAdapter.submitList(trendNews)
            newsAdapter.setOnDebouncedItemClick{adapter, view, position ->
                var data = newsAdapter.items.get(position)
                jumpActivity<WebDetailsActivity>(Bundle().apply {
                    putString(ParamsConfig.JSON_PARAMS, toJson(data))
                })
            }
            addItemDecoration(object : RecyclerView.ItemDecoration() {

                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    val itemPosition = parent.getChildAdapterPosition(view)
                    if (itemPosition == 0){
                        outRect.top = dp2px(this@TrendingNewsListActivity,10f)
                    }else{
                        outRect.top = dp2px(this@TrendingNewsListActivity,0f)
                    }
                    outRect.bottom = dp2px(this@TrendingNewsListActivity,10f)
                }
            })

        }
        APP.instance.appModel.getTrendsNews()
        PointEvent.posePoint(PointEventKey.trend_today_page)
    }
}