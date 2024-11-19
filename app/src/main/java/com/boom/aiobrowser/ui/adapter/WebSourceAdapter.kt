package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.data.WebSourceData
import com.boom.aiobrowser.databinding.BrowserItemWebCategoryBinding
import com.boom.base.adapter4.BaseMultiItemAdapter
import com.boom.base.adapter4.BaseQuickAdapter

class WebSourceAdapter : BaseQuickAdapter<WebSourceData, WebSourceAdapter.VH>() {

    class VH(
        parent: ViewGroup,
        val viewBinding: BrowserItemWebCategoryBinding = BrowserItemWebCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: WebSourceData?) {
        if (item == null) return
        holder.viewBinding.apply {
            item.titleRes?.apply {
                tvTitle.text = context.getString(this)
            }
            var tag = childRv.getTag(R.id.childRv) as?MutableList<JumpData>
            if (item.sourceList != tag && item.sourceList!=null){
                var childAdapter = WebSourceChildAdapter()
                childRv.apply {
                    layoutManager = GridLayoutManager(context,2)
                    adapter = childAdapter
                }
                childAdapter.submitList(item.sourceList)
                childRv.setTag(R.id.childRv,item.sourceList)
            }
        }
    }
}