package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.data.AppInfo
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.databinding.CleanProcessItemBinding
import com.boom.aiobrowser.databinding.FileItemPhotoBinding
import com.boom.aiobrowser.tools.GlideManager
import com.boom.aiobrowser.tools.clean.formatSize
import com.boom.base.adapter4.BaseQuickAdapter

class PhotoAdapter() : BaseQuickAdapter<FilesData, PhotoAdapter.VH>() {
    class VH(parent: ViewGroup, val viewBinding: FileItemPhotoBinding = FileItemPhotoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: FilesData?) {
        if (item == null)return
        holder.viewBinding.apply {
            tvSize.text = item.fileSize?.formatSize()
            tvTitle.text = item.fileName
            GlideManager.loadImg(iv = ivImg, url = item.filePath)
        }
    }

}