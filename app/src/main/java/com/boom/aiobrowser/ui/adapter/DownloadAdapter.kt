package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.data.VideoUIData
import com.boom.aiobrowser.databinding.ItemVideoDownload2Binding
import com.boom.aiobrowser.tools.GlideManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.clean.formatLength
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.activity.VideoPreActivity
import com.boom.aiobrowser.ui.pop.DownLoadPop
import com.boom.base.adapter4.BaseQuickAdapter
import com.boom.base.adapter4.util.setOnDebouncedItemClick

class DownloadAdapter(): BaseQuickAdapter<VideoUIData, DownloadAdapter.VH>() {
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
        item: VideoUIData?,
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
                    if (item.formatsList.get(0).videoChecked){
                        ivSelected.setImageResource(R.mipmap.ic_video_selected)
                    }else{
                        ivSelected.setImageResource(R.drawable.shape_white_oval)
                    }
                }
            }
        }
    }

    private fun updateItem(item: VideoUIData, holder: VH,payload:String): Boolean {
        holder.viewBinding.apply {
//            tvName.text = item.description
//            GlideManager.loadImg(null,ivVideo,item.thumbnail,0,R.mipmap.ic_default_download,0)
            if(item.formatsList.size == 1){
                rvFormats.visibility = View.GONE
                var data = item.formatsList.get(0)
                when (data.downloadType) {
                    VideoDownloadData.DOWNLOAD_SUCCESS->{
                        tvContent.text = "${data.size?.formatLength()}"
                        tvName.text = if (TextUtils.isEmpty(data.fileName)) data.downloadFileName else data.fileName
                        if (data.videoType == VideoDownloadData.TYPE_M3U8){
                            GlideManager.loadImg(null,ivVideo,data.imageUrl,0,R.mipmap.ic_default_download,0)
                        }else{
                            GlideManager.loadImg(null,ivVideo,data.imageUrl,0,R.mipmap.ic_default_download,0)
                        }
                        ivPlay.visibility = View.VISIBLE
                        ivSelected.visibility = View.GONE
                    }
                    VideoDownloadData.DOWNLOAD_ERROR -> {
                        ivSelected.setImageResource(R.mipmap.ic_video_error)
                        tvContent.text = context.getString(R.string.app_download_error)
                        tvContent.setTextColor(context.getColor(R.color.red_FF3B30))
                        GlideManager.loadImg(null,ivVideo,data.imageUrl,0,R.mipmap.ic_default_download,0)
                        ivPlay.visibility = View.VISIBLE
                        ivSelected.visibility = View.GONE
                        tvName.text = data.fileName
                    }

                    else -> {
                        GlideManager.loadImg(null,ivVideo,data.imageUrl,0,R.mipmap.ic_default_download,0)
                        tvName.text = data.fileName
                        if (data.size == 0L) {
                            tvContent.text = ""
                        }else{
                            tvContent.text = data.size?.formatLength()
                        }
                        ivSelected.visibility = View.VISIBLE
                        if (data.videoChecked){
                            ivSelected.setImageResource(R.mipmap.ic_video_selected)
                        }else{
                            ivSelected.setImageResource(R.drawable.shape_white_oval)
                        }
                    }
                }
            }else{
                tvName.text = item.description
                tvContent.text = ""
                ivSelected.visibility = View.GONE
                rvFormats.visibility = View.VISIBLE
                GlideManager.loadImg(null,ivVideo,item.thumbnail,0,R.mipmap.ic_default_download,0)
                rvFormats.apply {
                    var childAdapter = DownloadChildItemAdapter()
                    layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
                    adapter = childAdapter
                    childAdapter.submitList(item.formatsList)
                    childAdapter.setOnDebouncedItemClick { adapter, view, position ->
                        var data = childAdapter.getItem(position)
                        data?.apply {
                            if (downloadType == VideoDownloadData.DOWNLOAD_SUCCESS) {
                                VideoPreActivity.startVideoPreActivity((context as BaseActivity<*>),data)
                            } else{
                                item.formatsList.forEach {
                                    it.videoChecked = false
                                }
                                videoChecked = true
                                childAdapter.notifyDataSetChanged()
                                downLoadPop?.updateBottomSize()
                            }
                        }
                    }
                }
            }
        }

        return false
    }

    private fun getCurrentCheckSize(list:MutableList<VideoDownloadData>): Int {
        var checkSize = 0
        for (i in 0 until list.size){
            if (list.get(i).videoChecked){
                checkSize++
            }
        }
        return checkSize
    }


    override fun onBindViewHolder(holder: VH, position: Int, item: VideoUIData?) {
        if (item == null) return
        holder.viewBinding.apply {
            updateItem(item, holder,"")
        }
    }

    var downLoadPop:DownLoadPop?=null

    fun setPopContext(downLoadPop: DownLoadPop) {
        this.downLoadPop = downLoadPop
    }

}