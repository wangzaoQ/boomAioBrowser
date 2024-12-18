package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.NewsItemHomeTabChildBinding
import com.boom.aiobrowser.databinding.NewsItemHomeVideoChildBinding
import com.boom.aiobrowser.other.JumpConfig
import com.boom.aiobrowser.tools.GlideManager
import com.boom.base.adapter4.BaseQuickAdapter

class HomeVideoAdapter(var fragmet: BaseFragment<*>? = null) : BaseQuickAdapter<NewsData, HomeVideoAdapter.VH>() {
    class VH(
        parent: ViewGroup,
        val viewBinding: NewsItemHomeVideoChildBinding = NewsItemHomeVideoChildBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): HomeVideoAdapter.VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: NewsData?) {
        if (item == null)return
        holder.viewBinding.apply{
           if(item.isLoading){
               clRoot.visibility = View.GONE
               flEmpty.visibility = View.VISIBLE
           }else{
               clRoot.visibility = View.VISIBLE
               flEmpty.visibility = View.GONE
               tvTitle.text = item.tconsi
               tvSourceName.text = item.sfindi
               GlideManager.loadImg(fragmet,ivPic,item.iassum,R.mipmap.bg_video_default,R.mipmap.bg_video_default)
               GlideManager.loadImg(fragmet, ivSource, item.sschem)
           }
        }
    }

}