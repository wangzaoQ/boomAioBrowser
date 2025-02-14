package com.boom.aiobrowser.ui.adapter.item// NewsItemSearch.kt
import android.view.LayoutInflater
import android.view.ViewGroup
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.base.BaseViewHolder
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.BrowserItemSearchNewsBinding
import com.bumptech.glide.Glide

internal class NewsItemSearch(parent: ViewGroup) : BaseViewHolder<BrowserItemSearchNewsBinding>(
        BrowserItemSearchNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) {

    fun bind(item: NewsData,fragment:BaseFragment<*>?) {
        if (fragment == null)return
        viewBinding?.apply {
            Glide.with(fragment)
                .load(item.iassum)
                .placeholder(R.mipmap.ic_default_nf_small)
                .into(ivImg)
            tvNewsTitle.text = item.tconsi
            Glide.with(fragment).load(item.sschem).into(ivSource)
            tvSourceName.text = item.sfindi
        }
    }
}
