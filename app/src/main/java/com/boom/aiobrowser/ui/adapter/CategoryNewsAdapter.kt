package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.FileUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.data.WebCategoryData
import com.boom.aiobrowser.databinding.FileItemRecentBinding
import com.boom.aiobrowser.databinding.NewsItemWebCategoryBinding
import com.boom.aiobrowser.tools.GlideManager
import com.boom.aiobrowser.tools.clean.CleanToolsManager
import com.boom.aiobrowser.tools.clean.FileFilter.isApk
import com.boom.aiobrowser.tools.clean.FileFilter.isAudio
import com.boom.aiobrowser.tools.clean.FileFilter.isImage
import com.boom.aiobrowser.tools.clean.FileFilter.isVideo
import com.boom.aiobrowser.tools.clean.getDocImg
import com.boom.base.adapter4.BaseQuickAdapter

class CategoryNewsAdapter: BaseQuickAdapter<WebCategoryData, CategoryNewsAdapter.VH>() {
    class VH(parent: ViewGroup, val viewBinding: NewsItemWebCategoryBinding = NewsItemWebCategoryBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: CategoryNewsAdapter.VH, position: Int, item: WebCategoryData?) {
        if (item == null)return
        holder.viewBinding.apply {
            tvJumpTitle.text = context.getString(item.titleRes)
            if (item.uiCheck){
                tvJumpTitle.setTextColor(ContextCompat.getColor(context,R.color.white))
                llRoot.setBackgroundResource(com.boom.indicator.R.drawable.shape_custom_tab_unable)
                ivSource.setImageResource(item.unCheckRes)
            }else{
                tvJumpTitle.setTextColor(ContextCompat.getColor(context,R.color.color_black_1c274c))
                llRoot.setBackgroundResource(com.boom.indicator.R.drawable.shape_custom_tab_enable)
                ivSource.setImageResource(item.checkRes)
            }

            when (position) {
                0 -> {
                    startTemp.visibility = View.VISIBLE
                    endTemp.visibility = View.GONE
                }
                items.size-1 -> {
                    startTemp.visibility = View.GONE
                    endTemp.visibility = View.VISIBLE
                }
                else -> {
                    startTemp.visibility = View.GONE
                    endTemp.visibility = View.GONE
                }
            }
        }
    }

}