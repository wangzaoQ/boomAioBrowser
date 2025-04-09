package com.boom.aiobrowser.ui.adapter.item

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.base.BaseViewHolder
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.NewsItemHomeVideoBinding
import com.boom.aiobrowser.ui.activity.VideoListActivity
import com.boom.aiobrowser.ui.adapter.HomeVideoAdapter
import com.boom.aiobrowser.ui.fragment.MainFragment
import com.boom.aiobrowser.ui.fragment.NewsFragment
import com.boom.base.adapter4.BaseQuickAdapter
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import com.boom.drag.utils.DisplayUtils

internal class NewsHomeVideoItem(parent: ViewGroup) : BaseViewHolder<NewsItemHomeVideoBinding>(
    NewsItemHomeVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) {

    fun bind(
        item: NewsData,
        fragment: BaseFragment<*>?,
        position: Int,
        adapter: BaseQuickAdapter<*, *>
    ) {
        viewBinding?.apply {
            var tag =
                clRoot.getTag(R.id.clRoot) as? MutableList<MutableList<NewsData>>
            if (tag == null || tag != item.videoList) {
                rvVideo.layoutManager = LinearLayoutManager(
                    adapter.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                var newsAdapter = HomeVideoAdapter(fragment)
                rvVideo.adapter = newsAdapter
                newsAdapter.submitList(item.videoList ?: mutableListOf())
                if (rvVideo.itemDecorationCount == 0) {
                    rvVideo.addItemDecoration(object :
                        RecyclerView.ItemDecoration() {

                        override fun getItemOffsets(
                            outRect: Rect,
                            view: View,
                            parent: RecyclerView,
                            state: RecyclerView.State
                        ) {
                            super.getItemOffsets(outRect, view, parent, state)
                            val itemPosition = parent.getChildAdapterPosition(view)
                            outRect.right = DisplayUtils.dp2px(
                                adapter.context,
                                8f
                            )
                            var endInx = item.videoList?.size ?: 0
                            if (endInx > 0) {
                                endInx = endInx - 1
                            }
                            if (itemPosition == endInx) {
                                outRect.right = DisplayUtils.dp2px(
                                    adapter.context,
                                    13f
                                )
                            } else if (itemPosition == 0) {
                                outRect.left = DisplayUtils.dp2px(adapter.context, 13f)
                            }
                        }
                    })
                }

                newsAdapter.setOnDebouncedItemClick { adapter, view, position ->
                    var data = newsAdapter.items.get(position)
                    if (fragment != null) {
                        if (fragment != null) {
                            if (fragment is MainFragment) {
                                data.fromType = "home"
                            } else if (fragment is NewsFragment) {
                                data.fromType = "for_you"
                            }
                        }
                    }
                    VideoListActivity.startVideoListActivity(
                        adapter.context as BaseActivity<*>,
                        position,
                        item.videoList ?: mutableListOf(),
                        "",
                        "home",
                        "新闻视频"
                    )
                }
                clRoot.setTag(R.id.clRoot, item.videoList)
            }
        }


    }
}