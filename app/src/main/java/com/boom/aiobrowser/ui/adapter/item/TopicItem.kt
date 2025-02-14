package com.boom.aiobrowser.ui.adapter.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.base.BaseViewHolder
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.TopicBean
import com.boom.aiobrowser.databinding.NewsDetailsItemReadSourceBinding
import com.boom.aiobrowser.databinding.NewsItemTopicBinding
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.activity.TopicListActivity
import com.boom.base.adapter4.BaseQuickAdapter


internal class TopicItem(parent: ViewGroup) : BaseViewHolder<NewsItemTopicBinding>(
    NewsItemTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) {

    fun bind(item: NewsData, fragment: BaseFragment<*>?, position:Int, adapter: BaseQuickAdapter<*, *>) {
        viewBinding?.topicRoot?.apply {
            var tag = getTag(R.id.topicRoot) as? MutableList<String>
            if (tag == null || tag != item.tdetai) {
                removeAllViews()
                heightLimit = false
                maxLimit = false
                for (i in 0 until item.tdetai!!.size) {
                    var content = item.tdetai!!.get(i)
                    var topicView = LayoutInflater.from(context)
                        .inflate(R.layout.news_item_child_topic, null, false)
                    var tv = topicView.findViewById<AppCompatTextView>(R.id.tvTopic)
                    topicView.setOnClickListener {
                        var allTopicList = CacheManager.allTopicList
                        var index = -1
                        for (i in 0 until allTopicList.size) {
                            if (content == allTopicList.get(i).topic) {
                                index = i
                                break
                            }
                        }
                        if (index >= 0) {
                            (context as BaseActivity<*>).jumpActivity<TopicListActivity>(
                                Bundle().apply {
                                    putString("topic", toJson(allTopicList.get(i)))
                                })
                        } else {
                            (context as BaseActivity<*>).jumpActivity<TopicListActivity>(
                                Bundle().apply {
                                    putString("topic", toJson(TopicBean().apply {
                                        id = content
                                        topic = content
                                    }))
                                })
                        }
                        PointEvent.posePoint(
                            PointEventKey.topics_click,
                            Bundle().apply {
                                putString(PointValueKey.from_type, "news_page")
                            })
                    }
                    tv.text = "#${content}"
                    addView(topicView)
                }
                setTag(R.id.topicRoot, item.tdetai!!)
            }
        }

    }
}