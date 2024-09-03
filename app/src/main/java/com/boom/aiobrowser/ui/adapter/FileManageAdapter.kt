package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.data.FileManageData
import com.boom.aiobrowser.databinding.FileItemFileManagerBinding
import com.boom.aiobrowser.tools.clean.formatSize
import com.boom.aiobrowser.tools.clean.getSizeByType
import com.boom.base.adapter4.BaseQuickAdapter

class FileManageAdapter: BaseQuickAdapter<FileManageData, FileManageAdapter.VH>() {
    class VH(parent: ViewGroup, val viewBinding: FileItemFileManagerBinding = FileItemFileManagerBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    var allowShowSize = false

    override fun onBindViewHolder(holder: FileManageAdapter.VH, position: Int, item: FileManageData?) {
        if (item == null)return
        holder.viewBinding.apply {
            ivImg.setImageResource(item.getImage())
            tvContent.text = context.getString(item.getContent())
            if (allowShowSize){
                tvSize.text = getSizeByType(item.type).formatSize()
                tvSize.visibility = View.VISIBLE
            }else{
                tvSize.visibility = View.GONE
            }
        }
    }

    fun showSize(allowShowSize: Boolean) {
        this.allowShowSize = allowShowSize
        notifyDataSetChanged()
    }

}