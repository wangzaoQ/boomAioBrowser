package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.databinding.NewsItemHomeTabChildBinding
import com.boom.aiobrowser.other.JumpConfig
import com.boom.aiobrowser.tools.GlideManager
import com.boom.base.adapter4.BaseQuickAdapter

class HomeTabChildAdapter() : BaseQuickAdapter<JumpData, HomeTabChildAdapter.VH>() {
    class VH(
        parent: ViewGroup,
        val viewBinding: NewsItemHomeTabChildBinding = NewsItemHomeTabChildBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): HomeTabChildAdapter.VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: HomeTabChildAdapter.VH, position: Int, item: JumpData?) {
        if (item == null)return
        holder.viewBinding.apply{
            tvBrowser.text = item.jumpTitle
            if (item.jumpType == JumpConfig.JUMP_WEB_TYPE){
                tvBrowser.text = context.getString(R.string.app_more)
                ivBrowser.setImageResource(R.mipmap.ic_news_catrgory_more)
            }else if (item.jumpTitle == context.getString(R.string.app_x)){
                ivBrowser.setImageDrawable(ContextCompat.getDrawable(context,R.mipmap.ic_home_tab_x))
            }else if (item.imgRes!=0){
                ivBrowser.setImageDrawable(ContextCompat.getDrawable(context,item.imgRes!!))
            }else{
                var uri = Uri.parse(item.jumpUrl)
                var iconUrl = "https://www.google.com/s2/favicons?sz=128&domain=${uri.host}"
                GlideManager.loadImg(fragment = null,ivBrowser,iconUrl)
            }
        }
    }

}