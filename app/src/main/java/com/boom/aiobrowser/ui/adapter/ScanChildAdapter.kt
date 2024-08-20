package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.data.ScanData
import com.boom.aiobrowser.databinding.BrowserItemScanBinding
import com.boom.aiobrowser.databinding.CleanItemFilesBinding
import com.boom.aiobrowser.tools.formatSize
import com.boom.base.adapter4.BaseQuickAdapter

class ScanChildAdapter : BaseQuickAdapter<FilesData, ScanChildAdapter.VH>() {
    class VH(parent: ViewGroup, val viewBinding: CleanItemFilesBinding = CleanItemFilesBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: FilesData?) {
        if (item == null)return
        holder.viewBinding.apply {
            ivTag.setImageResource(item.imgId)
            tvName.text = item.fileName
            tvSize.text = item.fileSize.formatSize()
            ivEnd.setImageResource(if (item.itemChecked) R.mipmap.ic_scan_item_checked else R.mipmap.ic_scan_item_unchecked)
        }
    }

}