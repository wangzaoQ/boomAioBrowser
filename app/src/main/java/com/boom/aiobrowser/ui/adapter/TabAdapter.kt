package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.databinding.BrowserItemTabBinding
import com.boom.base.adapter4.BaseQuickAdapter
import com.boom.base.adapter4.dragswipe.listener.DragAndSwipeDataCallback

class TabAdapter : BaseQuickAdapter<JumpData, TabAdapter.VH>(), DragAndSwipeDataCallback {
    class VH(
        parent: ViewGroup,
        val viewBinding: BrowserItemTabBinding = BrowserItemTabBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: JumpData?) {
        if (item == null)return
        holder.viewBinding.apply {
            var content = if (item.jumpUrl.isNullOrEmpty()){
                item.jumpTitle
            }else{
                item.jumpUrl
            }
            tvTab.text = content
            tvTab.setTextColor(ContextCompat.getColor(context,if (item.isCurrent) R.color.black else R.color.gray))
            ivTab.setImageResource(if (item.isCurrent) R.mipmap.ic_tab2 else R.mipmap.ic_tab)
        }
    }

    override fun dataMove(fromPosition: Int, toPosition: Int) {
        move(fromPosition, toPosition)
    }

    override fun dataRemoveAt(position: Int) {
        removeAt(position)
    }


}