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
import com.boom.aiobrowser.databinding.NewsItemLocalBinding
import com.boom.aiobrowser.tools.CacheManager
import com.boom.base.adapter4.BaseQuickAdapter


internal class HomeLocalItem(parent: ViewGroup) : BaseViewHolder<NewsItemLocalBinding>(
    NewsItemLocalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) {

    fun bind(item: NewsData, fragment: BaseFragment<*>?,position:Int,adapter: BaseQuickAdapter<*, *>) {
        viewBinding?.apply {
            var locationCity = CacheManager.locationData?.locationCity ?: ""
            tvTitle.text =
                "${adapter.context.getString(R.string.app_location_title)} ${locationCity}?"
            var s = SpannableStringBuilder(tvTitle.text)
            var index =
                tvTitle.text.toString().indexOf(locationCity, ignoreCase = true)
            if (index >= 0) {
                s.setSpan(
                    ForegroundColorSpan(adapter.context.getColor(R.color.color_blue_4442E7)),
                    index,
                    index + locationCity.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                tvTitle.setText(s)
            }
        }
    }
}