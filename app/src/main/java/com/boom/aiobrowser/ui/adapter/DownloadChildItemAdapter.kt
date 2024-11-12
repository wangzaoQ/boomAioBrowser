package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.databinding.BrowserItemVideoDownloadChildBinding
import com.boom.aiobrowser.tools.GlideManager
import com.boom.aiobrowser.tools.clean.formatLength
import com.boom.base.adapter4.BaseQuickAdapter

class DownloadChildItemAdapter(): BaseQuickAdapter<VideoDownloadData, DownloadChildItemAdapter.VH>() {
    class VH(
        parent: ViewGroup,
        val viewBinding: BrowserItemVideoDownloadChildBinding = BrowserItemVideoDownloadChildBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, data: VideoDownloadData?) {
        if (data == null)return
        holder.viewBinding.apply{
            tvText.text = data.resolution
            if (data.downloadType == VideoDownloadData.DOWNLOAD_SUCCESS){
                ivSelected.visibility = View.INVISIBLE
            }else{
                ivSelected.visibility = View.VISIBLE
                if (data.videoChecked){
                    ivSelected.setImageResource(R.mipmap.ic_video_selected)
                    tvSize.setTextColor(ContextCompat.getColor(context,R.color.black_33))
                }else{
                    ivSelected.setImageResource(R.drawable.shape_white_oval)
                    tvSize.setTextColor(ContextCompat.getColor(context,R.color.gray_AC))
                }
            }
            if (data.size == 0L) {
                tvSize.text = ""
            }else{
                tvSize.text = data.size?.formatLength()
            }
        }
    }

}