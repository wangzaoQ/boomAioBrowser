package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.databinding.BrowserItemWebCategoryBinding
import com.boom.aiobrowser.databinding.BrowserItemWebCategoryChildBinding
import com.boom.aiobrowser.tools.GlideManager
import com.boom.base.adapter4.BaseQuickAdapter

class WebSourceChildAdapter : BaseQuickAdapter<JumpData, WebSourceChildAdapter.VH>() {

    class VH(
        parent: ViewGroup,
        val viewBinding: BrowserItemWebCategoryChildBinding = BrowserItemWebCategoryChildBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: WebSourceChildAdapter.VH, position: Int, item: JumpData?) {
        if (item == null) return
        holder.viewBinding.apply {
//            ivSource
            tvSource.text = item.jumpTitle
            var uri = Uri.parse(item.jumpUrl)
            var iconUrl = "https://www.google.com/s2/favicons?sz=128&domain=${uri.host}"
            GlideManager.loadImg(fragment = null,ivSource,iconUrl)
        }
    }
}