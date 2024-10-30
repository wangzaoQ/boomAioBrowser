package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.AppInfo
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.databinding.BrowserItemRateBinding
import com.boom.aiobrowser.databinding.CleanProcessItemBinding
import com.boom.aiobrowser.databinding.FileItemPhotoBinding
import com.boom.aiobrowser.tools.GlideManager
import com.boom.aiobrowser.tools.clean.formatSize
import com.boom.base.adapter4.BaseQuickAdapter

class RateAdapter() : BaseQuickAdapter<Int, RateAdapter.VH>() {
    class VH(parent: ViewGroup, val viewBinding: BrowserItemRateBinding = BrowserItemRateBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: Int?) {
        if (item == null)return
        if (position >= items.size) return
        holder.viewBinding.apply {
            if (position>index){
                ivPic.setImageResource(R.mipmap.ic_rate_unselected)
            }else{
                ivPic.setImageResource(R.mipmap.ic_rate_selected)
            }
        }
    }


    var index=-1
    fun updateIndex(index :Int){
        this.index = index
        notifyDataSetChanged()
    }

}