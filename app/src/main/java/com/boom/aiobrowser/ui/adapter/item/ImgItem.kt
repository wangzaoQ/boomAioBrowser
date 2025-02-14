package com.boom.aiobrowser.ui.adapter.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.base.BaseViewHolder
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.NewsDetailsItemImgBinding
import com.boom.aiobrowser.tools.GlideManager
import com.boom.base.adapter4.BaseQuickAdapter

internal class ImgItem(parent: ViewGroup) : BaseViewHolder<NewsDetailsItemImgBinding>(
    NewsDetailsItemImgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) {

    fun bind(item: NewsData, fragment: BaseFragment<*>?, position:Int, adapter: BaseQuickAdapter<*, *>) {
        viewBinding?.apply {
            GlideManager.loadImg(
                fragment = null,
                iv = ivImg,
                url = item.iassum,
                loadId = R.mipmap.ic_default_nf,
                R.mipmap.ic_default_nf
            )
        }
    }
}