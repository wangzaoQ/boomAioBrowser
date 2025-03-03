package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.databinding.CleanItemFilesBinding
import com.boom.aiobrowser.tools.GlideManager
import com.boom.aiobrowser.tools.clean.CleanToolsManager
import com.boom.aiobrowser.tools.clean.CleanConfig
import com.boom.aiobrowser.tools.clean.formatSize
import com.boom.base.adapter4.BaseQuickAdapter

class ScanChildAdapter(var type:Int) : BaseQuickAdapter<FilesData, ScanChildAdapter.VH>() {
    class VH(parent: ViewGroup, val viewBinding: CleanItemFilesBinding = CleanItemFilesBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }


    override fun onBindViewHolder(
        holder: VH,
        position: Int,
        item: FilesData?,
        payloads: List<Any>
    ) {
        super.onBindViewHolder(holder, position, item, payloads)
        if (payloads.isEmpty()){
            this.onBindViewHolder(holder, position)
        }else{
            if (item == null)return
            val payload = payloads[0].toString()
            if (payload == "updateSelected"){
                holder.viewBinding.apply {
                    ivEnd.setImageResource(if (item.itemChecked) R.mipmap.ic_scan_item_checked else R.mipmap.ic_scan_item_unchecked)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: FilesData?) {
        if (item == null)return
        holder.viewBinding.apply {
            if (type == CleanConfig.DATA_TYPE_APK){
                var label = CleanToolsManager.getApkName(APP.instance,item.filePath)
                ivTag.setImageDrawable(CleanToolsManager.getApkIcon(APP.instance,item.filePath))
                tvName.text = if (label.isNullOrEmpty()) item.fileName else label
            }else{
                GlideManager.loadImg(iv = ivTag,url = item.imgId)
                tvName.text = item.fileName
            }
            if (item.tempList.isNullOrEmpty()){
                tvSize.text = item.fileSize?.formatSize()
            }else{
                var allLength = 0L
                item.tempList?.forEach {
                    allLength+=it.fileSize?:0L
                }
                tvSize.text = allLength.formatSize()
            }
            ivEnd.setImageResource(if (item.itemChecked) R.mipmap.ic_scan_item_checked else R.mipmap.ic_scan_item_unchecked)
        }
    }

}