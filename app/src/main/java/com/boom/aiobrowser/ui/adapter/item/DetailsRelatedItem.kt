package com.boom.aiobrowser.ui.adapter.item

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADShowManager
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.base.BaseViewHolder
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.NewsItemDetailsRelatedBinding
import com.boom.aiobrowser.databinding.NewsItemTopicHeaderBinding
import com.boom.aiobrowser.other.ParamsConfig
import com.boom.aiobrowser.point.AD_POINT
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.activity.WebDetailsActivity
import com.boom.aiobrowser.ui.adapter.NewsRelatedAdapter
import com.boom.base.adapter4.BaseQuickAdapter
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import com.boom.drag.utils.DisplayUtils

/**
 * 详情推荐新闻
 */

internal class DetailsRelatedItem(parent: ViewGroup) : BaseViewHolder<NewsItemDetailsRelatedBinding>(
    NewsItemDetailsRelatedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) {

    fun bind(item: NewsData, fragment: BaseFragment<*>?, position:Int, parentAdapter: BaseQuickAdapter<*, *>) {
        viewBinding?.apply {
            var tag = rvRecommend.getTag(R.id.rvRecommend) as? MutableList<NewsData>
            if (tag.isNullOrEmpty() || tag != item.relatedList) {
                var recommendAdapter = NewsRelatedAdapter()
                rvRecommend.apply {
                    layoutManager = LinearLayoutManager(
                        context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    adapter = recommendAdapter
                    recommendAdapter.submitList(item.relatedList)
                    addItemDecoration(object : RecyclerView.ItemDecoration() {

                        override fun getItemOffsets(
                            outRect: Rect,
                            view: View,
                            parent: RecyclerView,
                            state: RecyclerView.State
                        ) {
                            super.getItemOffsets(outRect, view, parent, state)
                            val itemPosition = parent.getChildAdapterPosition(view)
                            if (itemPosition == item.relatedList!!.size - 1) {
                                outRect.right = DisplayUtils.dp2px(
                                    context,
                                    14f
                                )
                            }
                            outRect.left = DisplayUtils.dp2px(context, 14f)
                        }
                    })
                    recommendAdapter.setOnDebouncedItemClick { adapter, view, position ->
                        var data = recommendAdapter.items.get(position)
                        if (data.dataType == NewsData.TYPE_NEWS) {
                            var manager = AioADShowManager(context as BaseActivity<*>, ADEnum.INT_AD, tag = "新闻详情相关推荐") {
                                (context as BaseActivity<*>).jumpActivity<WebDetailsActivity>(
                                    Bundle().apply {
                                        putString(
                                            ParamsConfig.JSON_PARAMS,
                                            toJson(data)
                                        )
                                    })
                            }
                            manager.showScreenAD(AD_POINT.aobws_newsclick_int)
                        }
                    }
                }
                rvRecommend.setTag(R.id.rvRecommend, item.relatedList)
            }
        }

    }
}