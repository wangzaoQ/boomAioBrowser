package com.boom.aiobrowser.ui.adapter.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.base.BaseViewHolder
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.NewsDetailsItemTitleBinding
import com.boom.aiobrowser.tools.GlideManager
import com.boom.aiobrowser.tools.TimeManager
import com.boom.base.adapter4.BaseQuickAdapter


internal class TitleItem(parent: ViewGroup) : BaseViewHolder<NewsDetailsItemTitleBinding>(
    NewsDetailsItemTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) {

    fun bind(item: NewsData, fragment: BaseFragment<*>?, position:Int, adapter: BaseQuickAdapter<*, *>) {
        viewBinding?.apply {
            tvTitle.text = item.tconsi
            tvSource.text = item.sfindi
            tvTime.text = TimeManager.getNewsTime(item.pphilo ?: 0)
            GlideManager.loadImg(fragment = null, iv = ivLogo, url = item.sschem)
            if (item.areaTag.isNotEmpty()) {
                llLocation.visibility = View.VISIBLE
                tvLocation.text = item.areaTag
            } else {
                llLocation.visibility = View.GONE
            }
        }
    }
}