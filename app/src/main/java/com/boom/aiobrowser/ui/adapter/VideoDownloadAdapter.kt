package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.databinding.ItemVideoDownloadBinding
import com.boom.aiobrowser.tools.GlideManager
import com.boom.aiobrowser.tools.clean.formatLength
import com.boom.aiobrowser.tools.clean.formatSize
import com.boom.base.adapter4.BaseQuickAdapter

class VideoDownloadAdapter: BaseQuickAdapter<VideoDownloadData, VideoDownloadAdapter.VH>() {
    class VH(
        parent: ViewGroup,
        val viewBinding: ItemVideoDownloadBinding = ItemVideoDownloadBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: VideoDownloadData?) {
        if (item == null) return
        holder.viewBinding.apply {
            GlideManager.loadImg(null,ivVideo,item.url,0,0,0)
            tvName.text = item.fileName
            if(item.downloadType == VideoDownloadData.DOWNLOAD_NOT){
                progress.visibility = View.GONE
                llPlayRoot.visibility = View.GONE
                ivDownload.visibility = View.VISIBLE
                tvContent.text = item.size?.formatLength()
            }else{
                progress.visibility = View.VISIBLE
                llPlayRoot.visibility = View.VISIBLE
                ivDownload.visibility = View.GONE
            }
        }
    }

}