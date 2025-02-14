package com.boom.aiobrowser.ui.adapter.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.base.BaseViewHolder
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.NewsDetailsItemReadSourceBinding
import com.boom.aiobrowser.databinding.NewsItemTopicHeaderBinding
import com.boom.base.adapter4.BaseQuickAdapter

//topic header
internal class NewsTopicHeaderItem(parent: ViewGroup) : BaseViewHolder<NewsItemTopicHeaderBinding>(
    NewsItemTopicHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) {

    fun bind(item: NewsData, fragment: BaseFragment<*>?, position:Int, adapter: BaseQuickAdapter<*, *>) {

    }
}