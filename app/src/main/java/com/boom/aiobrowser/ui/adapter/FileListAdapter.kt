package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.FileUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.AppInfo
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.databinding.CleanProcessItemBinding
import com.boom.aiobrowser.databinding.FileItemListBinding
import com.boom.aiobrowser.databinding.FileItemPhotoBinding
import com.boom.aiobrowser.tools.GlideManager
import com.boom.aiobrowser.tools.clean.CleanToolsManager
import com.boom.aiobrowser.tools.clean.FileFilter.isApk
import com.boom.aiobrowser.tools.clean.FileFilter.isAudio
import com.boom.aiobrowser.tools.clean.FileFilter.isDoc
import com.boom.aiobrowser.tools.clean.FileFilter.isImage
import com.boom.aiobrowser.tools.clean.FileFilter.isVideo
import com.boom.aiobrowser.tools.clean.FileFilter.isZip
import com.boom.aiobrowser.tools.clean.formatSize
import com.boom.aiobrowser.tools.clean.getDocImg
import com.boom.base.adapter4.BaseQuickAdapter

class FileListAdapter() : BaseQuickAdapter<FilesData, FileListAdapter.VH>() {
    class VH(parent: ViewGroup, val viewBinding: FileItemListBinding = FileItemListBinding.inflate(
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
            tvName.text = item.fileName
            var extension = FileUtils.getFileExtension(item.filePath)
            if (extension.isImage() || extension.isVideo()){
                GlideManager.loadImg(iv = ivImg, url = item.filePath)
            }else if (extension.isAudio()){
                GlideManager.loadImg(iv = ivImg, url = R.mipmap.ic_music)
            }else if (extension.isApk()){
                GlideManager.loadImg(iv = ivImg, url = CleanToolsManager.getApkIcon(APP.instance,item.filePath))
            }else{
                GlideManager.loadImg(iv = ivImg, url = extension.getDocImg())
            }
        }
    }

}