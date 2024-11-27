package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.data.WebSourceData
import com.boom.aiobrowser.databinding.BrowserItemWebCategoryBinding
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.ui.pop.SearchPop
import com.boom.base.adapter4.BaseMultiItemAdapter
import com.boom.base.adapter4.BaseQuickAdapter
import com.boom.base.adapter4.util.addOnDebouncedChildClick
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import java.lang.ref.WeakReference

class WebSourceAdapter : BaseQuickAdapter<WebSourceData, WebSourceAdapter.VH>() {

    class VH(
        parent: ViewGroup,
        val viewBinding: BrowserItemWebCategoryBinding = BrowserItemWebCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: WebSourceData?) {
        if (item == null) return
        holder.viewBinding.apply {
            item.titleRes?.apply {
                tvTitle.text = context.getString(this)
            }
            var tag = childRv.getTag(R.id.childRv) as?MutableList<JumpData>
            if (item.sourceList != tag && item.sourceList!=null){
                var childAdapter = WebSourceChildAdapter()
                childRv.apply {
                    layoutManager = GridLayoutManager(context,2)
                    adapter = childAdapter
                }
                childAdapter.submitList(item.sourceList)
                childAdapter.setOnDebouncedItemClick{adapter, view, position ->
                    if (position>item.sourceList!!.size-1)return@setOnDebouncedItemClick
                    var data = item.sourceList!!.get(position)
                    APP.jumpLiveData.postValue(JumpDataManager.addTabToOtherWeb(data.jumpUrl,"webStore"))
                    (context as BaseActivity<*>).finish()
                }

                childAdapter.addOnDebouncedChildClick(R.id.flAdd) { adapter, view, position ->
                    if (position>item.sourceList!!.size-1)return@addOnDebouncedChildClick
                    var data = item.sourceList!!.get(position)
                    data.isCurrent = data.isCurrent.not()
                    childAdapter.notifyItemChanged(position,"updateCheck")
                    if (data.isCurrent){
                        CacheManager.addHomeTab(data)
                    }else{
                        CacheManager.removeHomeTab(data)
                    }
                    PointEvent.posePoint(PointEventKey.web_store_add, Bundle().apply {
                        putString(PointValueKey.type,data.jumpTitle)
                    })
                    APP.homeTabLiveData.postValue(CacheManager.homeTabList)
                }
                childRv.setTag(R.id.childRv,item.sourceList)
            }
        }
    }
}