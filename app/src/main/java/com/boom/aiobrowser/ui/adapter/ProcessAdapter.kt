package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.data.AppInfo
import com.boom.aiobrowser.databinding.CleanProcessItemBinding
import com.boom.base.adapter4.BaseQuickAdapter

class ProcessAdapter() : BaseQuickAdapter<AppInfo, ProcessAdapter.VH>() {
    class VH(parent: ViewGroup, val viewBinding: CleanProcessItemBinding = CleanProcessItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: AppInfo?) {
        if (item == null)return
        holder.viewBinding.apply {
            ivImg.setImageDrawable(item.icon)
            tvName.text = item.name
        }
    }

}