package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.databinding.ItemVideoDownloadBinding
import com.boom.aiobrowser.tools.BigDecimalUtils
import com.boom.aiobrowser.tools.GlideManager
import com.boom.aiobrowser.tools.clean.formatLength
import com.boom.base.adapter4.BaseQuickAdapter

class VideoDownloadAdapter(var isProgress:Boolean = true): BaseQuickAdapter<VideoDownloadData, VideoDownloadAdapter.VH>() {
    class VH(
        parent: ViewGroup,
        val viewBinding: ItemVideoDownloadBinding = ItemVideoDownloadBinding.inflate(
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
                    updateItem(item,holder,payload)
                }
            }
        }
    }

    private fun updateItem(item: VideoDownloadData, holder: VH,payload:String): Boolean {
        holder.viewBinding.apply {
            tvName.text = item.fileName
            when (item.downloadType) {
                VideoDownloadData.DOWNLOAD_LOADING, VideoDownloadData.DOWNLOAD_PAUSE,VideoDownloadData.DOWNLOAD_PREPARE -> {
                    llPlayRoot.visibility = View.VISIBLE
                    progress.visibility = View.VISIBLE
                    ivDownload.visibility = View.GONE
                    ivMore.visibility = View.GONE
                    var allSize = item.size ?: 0
                    if (allSize == 0L) return true
                    var fileProgress = BigDecimalUtils.mul(
                        100.0,
                        BigDecimalUtils.div((item.downloadSize ?: 0).toDouble(), allSize.toDouble())
                    ).toInt()
                    progress.progress = fileProgress
                    if (item.downloadType == VideoDownloadData.DOWNLOAD_LOADING || item.downloadType == VideoDownloadData.DOWNLOAD_PREPARE) {
                        ivVideoStatus.setImageResource(R.mipmap.ic_video_pause)
                    } else {
                        ivVideoStatus.setImageResource(R.mipmap.ic_video_play)
                    }
                    if (item.downloadType == VideoDownloadData.DOWNLOAD_PREPARE){
                        tvContent.text = context.getString(R.string.app_download_prepare)
                    }else{
                        tvContent.text = "${item.downloadSize?.formatLength()}/${item.size?.formatLength()}"
                        tvContent.setTextColor(context.getColor(R.color.gray))
                    }
                    if(payload.isNullOrEmpty()) GlideManager.loadImg(null,ivVideo,item.imageUrl,0,R.mipmap.ic_default_download,0)
                }
                VideoDownloadData.DOWNLOAD_SUCCESS->{
                    llPlayRoot.visibility = View.GONE
                    ivDownload.visibility = View.GONE
                    ivMore.visibility = View.VISIBLE
                    progress.visibility = View.GONE
                    tvContent.text = "${item.size?.formatLength()}"
                    tvName.text = item.downloadFileName
                    if (item.videoType == VideoDownloadData.TYPE_M3U8){
                        GlideManager.loadImg(null,ivVideo,item.imageUrl,0,R.mipmap.ic_default_download,0)
                    }else{
                        GlideManager.loadImg(null,ivVideo,item.downloadFilePath,0,R.mipmap.ic_default_download,0)
                    }
                }
                VideoDownloadData.DOWNLOAD_ERROR -> {
                    progress.visibility = View.GONE
                    llPlayRoot.visibility = View.VISIBLE
                    ivDownload.visibility = View.GONE
                    ivMore.visibility = View.GONE
                    ivVideoStatus.setImageResource(R.mipmap.ic_video_error)
                    tvContent.text = context.getString(R.string.app_download_error)
                    tvContent.setTextColor(context.getColor(R.color.red_FF3B30))
                    GlideManager.loadImg(null,ivVideo,item.imageUrl,0,R.mipmap.ic_default_download,0)
                }

                VideoDownloadData.DOWNLOAD_NOT ->{
                    progress.visibility = View.GONE
                    llPlayRoot.visibility = View.GONE
                    ivMore.visibility = View.GONE
                    ivDownload.visibility = View.VISIBLE
                    tvContent.text = item.size?.formatLength()
                    GlideManager.loadImg(null,ivVideo,item.imageUrl,0,R.mipmap.ic_default_download,0)
                }

                else -> {}
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