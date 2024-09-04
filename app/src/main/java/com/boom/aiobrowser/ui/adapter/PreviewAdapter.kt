package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.data.AppInfo
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.databinding.CleanProcessItemBinding
import com.boom.aiobrowser.databinding.FileItemPhotoBinding
import com.boom.aiobrowser.databinding.FileItemPreviewBinding
import com.boom.aiobrowser.tools.GlideManager
import com.boom.aiobrowser.tools.clean.formatSize
import com.boom.base.adapter4.BaseQuickAdapter

class PreviewAdapter() : BaseQuickAdapter<FilesData, PreviewAdapter.VH>() {
    class VH(parent: ViewGroup, val viewBinding: FileItemPreviewBinding = FileItemPreviewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: FilesData?) {
        if (item == null)return
        holder.viewBinding.apply {
            GlideManager.loadImg(iv=photoView,url = item.filePath)
        }
    }

}