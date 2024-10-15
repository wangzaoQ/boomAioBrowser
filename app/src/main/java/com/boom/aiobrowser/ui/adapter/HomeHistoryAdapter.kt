package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.databinding.BrowserItemHomeHistoryBinding
import com.boom.aiobrowser.tools.GlideManager
import com.boom.aiobrowser.ui.fragment.MainFragment
import com.boom.base.adapter4.BaseQuickAdapter

class HomeHistoryAdapter(var mainFragment: MainFragment) : BaseQuickAdapter<JumpData, HomeHistoryAdapter.VH>() {
    class VH(
        parent: ViewGroup,
        val viewBinding: BrowserItemHomeHistoryBinding = BrowserItemHomeHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): HomeHistoryAdapter.VH {
        return VH(parent)

    }

    override fun onBindViewHolder(holder: HomeHistoryAdapter.VH, position: Int, item: JumpData?) {
        if (item == null)return
        holder.viewBinding.apply{
            if (position == 0){
                leftTemp.visibility = View.VISIBLE
                rightTemp.visibility = View.GONE
            }else if (position == items.size-1){
                leftTemp.visibility = View.GONE
                rightTemp.visibility = View.VISIBLE
            }else{
                leftTemp.visibility = View.GONE
                rightTemp.visibility = View.GONE
            }
            var uri = Uri.parse(item.jumpUrl)
            uri.host
            uri.scheme
//            var iconUrl = "${uri.scheme}://${uri.host}/favicon.ico"
//            GlideManager.loadImg(mainFragment,ivBrowser,iconUrl, errorId = R.mipmap.ic_default_browser_icon)
            if(item.jumpTitle.isNullOrEmpty()){
                tvBrowser.text = item.jumpUrl
            }else{
                tvBrowser.text = item.jumpTitle
            }
        }
    }

}