package com.boom.aiobrowser.ui.adapter.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.base.BaseViewHolder
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.NewsItemTrendingBinding
import com.boom.aiobrowser.other.ParamsConfig
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.activity.WebDetailsActivity
import com.boom.aiobrowser.ui.adapter.TrendingNewsAdapter
import com.boom.base.adapter4.BaseQuickAdapter
import com.boom.base.adapter4.util.setOnDebouncedItemClick

internal class NewsTrendingItem(parent: ViewGroup) : BaseViewHolder<NewsItemTrendingBinding>(
    NewsItemTrendingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) {

    fun bind(item: NewsData, fragment: BaseFragment<*>?, position:Int, adapter: BaseQuickAdapter<*, *>) {
        viewBinding?.apply {
            var tag =
                rvList.getTag(R.id.rvList) as? MutableList<MutableList<NewsData>>
            if (tag == null || tag != item.trendList) {
                rvList.layoutManager = LinearLayoutManager(
                    adapter.context,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                var newsAdapter = TrendingNewsAdapter()
                rvList.adapter = newsAdapter
                newsAdapter.submitList(item.trendList)
                newsAdapter.setOnDebouncedItemClick { adapter, view, position ->
                    var data = newsAdapter.items.get(position)
                    (adapter.context as BaseActivity<*>).jumpActivity<WebDetailsActivity>(
                        Bundle().apply {
                            putString(ParamsConfig.JSON_PARAMS, toJson(data))
                        })
                    PointEvent.posePoint(PointEventKey.trend_news, Bundle().apply {
                        putString(PointValueKey.from_type, "home_page")
                        putString(PointValueKey.news_id, data.itackl)
                    })
                }
                rvList.setTag(R.id.rvList, item.trendList)
            }
        }

    }
}