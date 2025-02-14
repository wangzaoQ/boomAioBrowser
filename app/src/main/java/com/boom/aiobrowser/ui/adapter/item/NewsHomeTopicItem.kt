package com.boom.aiobrowser.ui.adapter.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.base.BaseViewHolder
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.NewsItemHomeTopicBinding
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.ui.fragment.MainFragment
import com.boom.aiobrowser.ui.fragment.NewsFragment
import com.boom.base.adapter4.BaseQuickAdapter


internal class NewsHomeTopicItem(parent: ViewGroup) : BaseViewHolder<NewsItemHomeTopicBinding>(
    NewsItemHomeTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) {

    fun bind(item: NewsData, fragment: BaseFragment<*>?, position:Int, adapter: BaseQuickAdapter<*, *>) {
        viewBinding?.topicRoot?.apply {
            var tag = getTag(R.id.topicRoot) as? MutableList<String>
            if (tag == null || tag != item.topicList) {
                removeAllViews()
                heightLimit = false
                maxLimit = false
                for (i in 0 until item.topicList!!.size) {
                    var content =
                        if (item.isLoading) context.getString(R.string.app_loading_content) else item.topicList!!.get(
                            i
                        ).topic
                    var topicView = LayoutInflater.from(context)
                        .inflate(R.layout.news_item_child_topic, null, false)
                    var tv = topicView.findViewById<AppCompatTextView>(R.id.tvTopic)
                    topicView.setOnClickListener {
                        if (item.isLoading) {
                            return@setOnClickListener
                        } else {
                            APP.topicJumpData.postValue(item.topicList!!.get(i))
                        }
                        PointEvent.posePoint(
                            PointEventKey.topics_click,
                            Bundle().apply {
                                if (fragment != null) {
                                    if (fragment is MainFragment) {
                                        putString(PointValueKey.from_type, "home")
                                    } else if (fragment is NewsFragment) {
                                        putString(
                                            PointValueKey.from_type,
                                            "for_you"
                                        )
                                    }
                                }
                            })
                    }
                    tv.text = "#${content}"
                    addView(topicView)
                }
                setTag(R.id.topicRoot, item.topicList!!)
            }
        }

    }
}