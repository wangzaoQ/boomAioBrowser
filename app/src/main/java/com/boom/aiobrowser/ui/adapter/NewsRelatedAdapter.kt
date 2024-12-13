package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.NewsItemRelatedBinding
import com.boom.aiobrowser.tools.GlideManager
import com.boom.base.adapter4.BaseQuickAdapter

class NewsRelatedAdapter () : BaseQuickAdapter<NewsData, NewsRelatedAdapter.VH>() {
    class VH(parent: ViewGroup, val viewBinding: NewsItemRelatedBinding = NewsItemRelatedBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: NewsData?) {
        if (item == null)return
        holder.viewBinding?.apply {
            GlideManager.loadImg(
                null,
                ivPic,
                item.iassum,
                loadId = R.mipmap.ic_default_nf,
                R.mipmap.ic_default_nf
            )
            if(item.isLoading){
                rlTemp.visibility = View.VISIBLE
            }else{
                rlTemp.visibility = View.GONE
                tvContent.text = item.tconsi
            }
        }
    }
}