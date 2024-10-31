package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.databinding.ItemVideoDownload2Binding
import com.boom.aiobrowser.databinding.ItemVideoDownloadBinding
import com.boom.aiobrowser.tools.BigDecimalUtils
import com.boom.aiobrowser.tools.GlideManager
import com.boom.aiobrowser.tools.clean.formatLength
import com.boom.base.adapter4.BaseQuickAdapter

class DownloadAdapter(var isPop:Boolean = false): BaseQuickAdapter<VideoDownloadData, DownloadAdapter.VH>() {
    class VH(
        parent: ViewGroup,
        val viewBinding: ItemVideoDownload2Binding = ItemVideoDownload2Binding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(
        holder: VH,
        position: Int,
        item: VideoDownloadData?,
        payloads: List<Any>
    ) {
        if (payloads.isNullOrEmpty()){
            onBindViewHolder(holder,position,item)
        }else{
            val payload = payloads[0].toString()
            if (payload == "updateStatus"){
                if (item == null)return
                holder.viewBinding.apply {
                    updateItem(item,holder,payload)
                }
            }else if (payload == "updateLoading"){
                if (item == null)return
                holder.viewBinding.apply{
                    if (item.videoChecked){
                        ivSelected.setImageResource(R.mipmap.ic_video_selected)
                    }else{
                        ivSelected.setImageResource(R.drawable.shape_white_oval)
                    }
                }
            }
        }
    }

    private fun updateItem(item: VideoDownloadData, holder: VH,payload:String): Boolean {
        holder.viewBinding.apply {
            tvName.text = item.fileName
            when (item.downloadType) {
                VideoDownloadData.DOWNLOAD_SUCCESS->{
                    tvContent.text = "${item.size?.formatLength()}"
                    tvName.text = item.downloadFileName
                    if (item.videoType == VideoDownloadData.TYPE_M3U8){
                        GlideManager.loadImg(null,ivVideo,item.imageUrl,0,R.mipmap.ic_default_download,0)
                    }else{
                        GlideManager.loadImg(null,ivVideo,item.imageUrl,0,R.mipmap.ic_default_download,0)
                    }
                    ivPlay.visibility = View.VISIBLE
                    ivSelected.visibility = View.GONE
                }
                VideoDownloadData.DOWNLOAD_ERROR -> {
                    ivSelected.setImageResource(R.mipmap.ic_video_error)
                    tvContent.text = context.getString(R.string.app_download_error)
                    tvContent.setTextColor(context.getColor(R.color.red_FF3B30))
                    GlideManager.loadImg(null,ivVideo,item.imageUrl,0,R.mipmap.ic_default_download,0)
                    ivPlay.visibility = View.VISIBLE
                    ivSelected.visibility = View.GONE
                }

                else -> {
                    if (item.size == 0L) {
                        tvContent.text = ""
                    }else{
                        tvContent.text = item.size?.formatLength()
                    }
                    ivSelected.visibility = View.VISIBLE
                    if (item.videoChecked){
                        ivSelected.setImageResource(R.mipmap.ic_video_selected)
                    }else{
                        ivSelected.setImageResource(R.drawable.shape_white_oval)
                    }
                    GlideManager.loadImg(null,ivVideo,item.imageUrl,0,R.mipmap.ic_default_download,0)
                }
            }
        }

        return false
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: VideoDownloadData?) {
        if (item == null) return
        holder.viewBinding.apply {
            updateItem(item, holder,"")
        }
    }

}