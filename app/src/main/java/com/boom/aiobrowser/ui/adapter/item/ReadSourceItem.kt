package com.boom.aiobrowser.ui.adapter.item

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.base.BaseViewHolder
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.NewsDetailsItemReadSourceBinding
import com.boom.aiobrowser.tools.CacheManager
import com.boom.base.adapter4.BaseQuickAdapter


internal class ReadSourceItem(parent: ViewGroup) : BaseViewHolder<NewsDetailsItemReadSourceBinding>(
    NewsDetailsItemReadSourceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) {

    fun bind(item: NewsData, fragment: BaseFragment<*>?, position:Int, adapter: BaseQuickAdapter<*, *>) {

    }
}