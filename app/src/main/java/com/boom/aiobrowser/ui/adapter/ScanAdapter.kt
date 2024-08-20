package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.data.ScanData
import com.boom.aiobrowser.databinding.BrowserItemScanBinding
import com.boom.aiobrowser.ui.view.CustomLinearLayoutManager
import com.boom.base.adapter4.BaseQuickAdapter
import com.boom.base.adapter4.util.setOnDebouncedItemClick

class ScanAdapter(var updateBack:()-> Unit) : BaseQuickAdapter<ScanData, ScanAdapter.VH>() {
    class VH(parent: ViewGroup, val viewBinding: BrowserItemScanBinding = BrowserItemScanBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: ScanAdapter.VH, position: Int, item: ScanData?) {
        if (item == null)return
        holder.viewBinding.apply {
            scanItem.setScanData(item)
            if (item.childList.isNullOrEmpty() || item.itemExpend.not() ){
                rvChild.visibility = View.GONE
            }else{
                rvChild.visibility = View.VISIBLE
                var tag = rvChild.getTag(R.id.rvChild)as?MutableList<FilesData>
                var childAdapter = ScanChildAdapter()
                if (tag != item.childList){
                    rvChild.apply {
                        layoutManager = CustomLinearLayoutManager(context)
                        // 设置预加载，请调用以下方法
                        adapter = childAdapter
                        setNestedScrollingEnabled(false)
                        childAdapter.setOnDebouncedItemClick{adapter, view, position ->
                            var data: FilesData? = childAdapter.getItem(position)
                            if (data == null)return@setOnDebouncedItemClick
                            data.itemChecked = data.itemChecked.not()
                            childAdapter.notifyItemChanged(position)
                            updateBack.invoke()
                        }
                        childAdapter.submitList(item.childList)
                    }
                    rvChild.setTag(R.id.rvChild,item.childList)
                }
            }
        }
    }

}