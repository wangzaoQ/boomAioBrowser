package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.BrowserFragmentMainBinding
import com.boom.aiobrowser.databinding.BrowserItemMainNewsBinding
import com.boom.aiobrowser.tools.GlideManager
import com.boom.aiobrowser.tools.TimeManager
import com.boom.base.adapter4.BaseQuickAdapter

class NewsMainAdapter(var fragmet :BaseFragment<*>?=null) : BaseQuickAdapter<NewsData, NewsMainAdapter.VH>() {
    class VH(
        parent: ViewGroup,
        val viewBinding: BrowserItemMainNewsBinding = BrowserItemMainNewsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: NewsData?) {
        if (item == null)return
        holder.viewBinding.apply {
            if (item.iassum.isNullOrEmpty()){
                ivImg.visibility = View.GONE
                tvNewsContent.visibility = View.VISIBLE
                tvNewsContent.text = item.sissue
            }else{
                ivImg.visibility = View.VISIBLE
                tvNewsContent.visibility = View.GONE
                GlideManager.loadImg(fragmet,ivImg,item.iassum,loadId = R.mipmap.bg_news_default ,R.mipmap.bg_news_default)
            }
            tvNewsTitle.text = item.tconsi
            GlideManager.loadImg(fragmet,ivSource,item.sschem)
            tvSourceName.text = "${item.sfindi}"
            tvNewsTime.text = TimeManager.getNewsTime(item.pphilo?:0)
        }
    }


}