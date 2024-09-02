package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.data.HistoryData
import com.boom.aiobrowser.data.ScanData
import com.boom.aiobrowser.data.ViewItem
import com.boom.aiobrowser.databinding.BrowserItemHistoryBinding
import com.boom.aiobrowser.databinding.BrowserItemHistoryDateBinding
import com.boom.aiobrowser.databinding.BrowserItemScanBinding
import com.boom.aiobrowser.databinding.CleanItemFilesBinding
import com.boom.aiobrowser.tools.GlideManager
import com.boom.aiobrowser.tools.TimeManager
import com.boom.aiobrowser.tools.clean.CleanConfig
import com.boom.aiobrowser.tools.clean.CleanConfig.cacheFiles
import com.boom.aiobrowser.tools.clean.CleanToolsManager
import com.boom.aiobrowser.tools.clean.formatSize
import com.boom.aiobrowser.ui.adapter.HistoryAdapter.HistoryItem
import com.boom.aiobrowser.ui.adapter.HistoryAdapter.HistoryItemTitle
import com.boom.aiobrowser.ui.view.CustomLinearLayoutManager
import com.boom.base.adapter4.BaseMultiItemAdapter
import com.boom.base.adapter4.BaseQuickAdapter
import com.boom.base.adapter4.util.setOnDebouncedItemClick

class ScanAdapter2(var updateBack: () -> Unit) : BaseMultiItemAdapter<ViewItem>() {


    internal class ParentItem(viewBinding: BrowserItemScanBinding) :
        RecyclerView.ViewHolder(viewBinding.getRoot()) {
        var viewBinding: BrowserItemScanBinding? = null

        init {
            this.viewBinding = viewBinding
        }

        constructor(parent: ViewGroup) : this(
            BrowserItemScanBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    internal class ChildItem(viewBinding: CleanItemFilesBinding) :
        RecyclerView.ViewHolder(viewBinding.getRoot()) {
        var viewBinding: CleanItemFilesBinding? = null

        init {
            this.viewBinding = viewBinding
        }

        constructor(parent: ViewGroup) : this(
            CleanItemFilesBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }


    init {
        addItemType(
            ViewItem.TYPE_PARENT,
            object : OnMultiItemAdapterListener<ViewItem, ParentItem> {

                override fun onCreate(
                    context: Context,
                    parent: ViewGroup,
                    viewType: Int
                ): ParentItem {
                    return ParentItem(
                        parent
                    );
                }

                override fun onBind(holder: ParentItem, position: Int, item: ViewItem?) {
                    if (item == null) return
                    item as ScanData
                    holder.viewBinding?.apply {
                        scanItem.setScanData(item)
                        if (position == 0 && item.isLoading.not() && cacheFiles.isNullOrEmpty()) {
                            rlCacheTips.visibility = View.VISIBLE
                        } else {
                            rlCacheTips.visibility = View.GONE
                        }
                    }
                }

                override fun onBind(
                    holder: ParentItem,
                    position: Int,
                    item: ViewItem?,
                    payloads: List<Any>
                ) {
                    if (payloads.isNullOrEmpty()) {
                        onBind(holder, position, item)
                    } else {
                        val payload = payloads[0].toString()
                        if (payload == "updateCheck") {
                            item as ScanData
                            if (item.itemChecked) {
                                holder.viewBinding!!.scanItem.binding.ivEnd.setImageResource(R.mipmap.ic_scan_item_checked)
                            } else {
                                holder.viewBinding!!.scanItem.binding.ivEnd.setImageResource(R.mipmap.ic_scan_item_unchecked)
                            }
                        }else if (payload == "updateCache"){
                            item as ScanData
                            holder.viewBinding!!.scanItem.binding.tvSize.text = item.allLength.formatSize()
                            if (position == 0 && item.isLoading.not() && cacheFiles.isNullOrEmpty()) {
                                holder.viewBinding!!.rlCacheTips.visibility = View.VISIBLE
                            } else {
                                holder.viewBinding!!.rlCacheTips.visibility = View.GONE
                            }
                        }else if (payload == "updateLoad"){
                            holder.viewBinding!!.apply {
                                scanItem.updateScanData(item as ScanData)
                                if (position == 0 && item.isLoading.not() && cacheFiles.isNullOrEmpty()) {
                                    rlCacheTips.visibility = View.VISIBLE
                                } else {
                                    rlCacheTips.visibility = View.GONE
                                }
                            }
                        }
                    }
                }
            })
            .addItemType(
                ViewItem.TYPE_CHILD,
                object : OnMultiItemAdapterListener<ViewItem, ChildItem> {

                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ): ChildItem {
                        return ChildItem(
                            parent
                        );
                    }

                    override fun onBind(holder: ChildItem, position: Int, item: ViewItem?) {
                        if (item == null) return
                        item as FilesData
                        holder.viewBinding?.apply {
                            if (item.scanType == CleanConfig.DATA_TYPE_APK) {
                                var label =
                                    CleanToolsManager.getApkName(APP.instance, item.filePath)
                                ivTag.setImageDrawable(
                                    CleanToolsManager.getApkIcon(
                                        APP.instance,
                                        item.filePath
                                    )
                                )
                                tvName.text = if (label.isNullOrEmpty()) item.fileName else label
                            } else {
                                GlideManager.loadImg(iv = ivTag, url = item.imgId)
                                tvName.text = item.fileName
                            }
                            if (item.tempList.isNullOrEmpty()) {
                                tvSize.text = item.fileSize?.formatSize()
                            } else {
                                var allLength = 0L
                                item.tempList?.forEach {
                                    allLength += it.fileSize?:0L
                                }
                                tvSize.text = allLength.formatSize()
                            }
                            ivEnd.setImageResource(if (item.itemChecked) R.mipmap.ic_scan_item_checked else R.mipmap.ic_scan_item_unchecked)
                        }
                    }

                    override fun onBind(
                        holder: ChildItem,
                        position: Int,
                        item: ViewItem?,
                        payloads: List<Any>
                    ) {
                        super.onBind(holder, position, item, payloads)
                        if (payloads.isNullOrEmpty()) {
                            onBind(holder, position, item)
                        } else {
                            val payload = payloads[0].toString()
                            if (payload == "updateCheck") {
                                item as FilesData
                                if (item.itemChecked) {
                                    holder.viewBinding!!.ivEnd.setImageResource(R.mipmap.ic_scan_item_checked)
                                } else {
                                    holder.viewBinding!!.ivEnd.setImageResource(R.mipmap.ic_scan_item_unchecked)
                                }
                            }else if (payload == "updateLoad"){

                            }
                        }
                    }
                })
            .onItemViewType { position, list -> list.get(position).dataType }
    }

//
//    override fun onBindViewHolder(holder: VH, position: Int, item: ScanData?, payloads: List<Any>) {
//        super.onBindViewHolder(holder, position, item, payloads)
//        if (payloads.isEmpty()){
//            this.onBindViewHolder(holder, position)
//        }else{
//            if (item == null)return
//            val payload = payloads[0].toString()
//            if (payload == "updateSelected"){
//                holder.viewBinding.apply {
//                    scanItem.updateScanData(item)
//                    updateRv(rvChild,item,position)
//                }
//            }else if (payload == "updateExpend"){
//                if (item.childList.isNullOrEmpty() || item.itemExpend.not()){
//                    (holder.viewBinding.rvChild.adapter as ScanChildAdapter).apply {
//                    }
//                }else{
//                    (holder.viewBinding.rvChild.adapter as ScanChildAdapter).submitList(item.childList)
//                }
//            }else if (payload == "updateCheck"){
//                if (item.itemChecked){
//                    holder.viewBinding.scanItem.binding.ivEnd.setImageResource(R.mipmap.ic_scan_item_checked)
//                }else{
//                    holder.viewBinding.scanItem.binding.ivEnd.setImageResource(R.mipmap.ic_scan_item_unchecked)
//                }
//            }else if (payload == "updateCache"){
//                holder.viewBinding.apply {
//                    scanItem.updateScanData(item)
//                }
//                if (position == 0 && item.isLoading.not()){
//                    holder.viewBinding.rlCacheTips.visibility = View.VISIBLE
//                }else{
//                    holder.viewBinding.rlCacheTips.visibility = View.GONE
//                }
//            }
//        }
//    }

//    private fun updateRv(rvChild: RecyclerView, item: ScanData, parentPosition: Int) {
//        var childAdapter = ScanChildAdapter(item.type)
//        rvChild.apply {
//            layoutManager = CustomLinearLayoutManager(context)
//            // 设置预加载，请调用以下方法
//            adapter = childAdapter
//            setNestedScrollingEnabled(false)
//            childAdapter.setOnDebouncedItemClick{adapter, view, position ->
//                var data: FilesData? = childAdapter.getItem(position)
//                if (data == null)return@setOnDebouncedItemClick
//                data.itemChecked = data.itemChecked.not()
//                var allCheck = true
//                for (i in 0 until item.childList.size){
//                    var data = item.childList.get(i)
//                    if (data.itemChecked.not()){
//                        allCheck = false
//                        break
//                    }
//                }
//                if (item.itemChecked != allCheck){
//                    item.itemChecked = allCheck
//                    notifyItemChanged(parentPosition,"updateCheck")
//                }
//                childAdapter.notifyItemChanged(position,"updateSelected")
//                updateBack.invoke()
//            }
//            childAdapter.submitList(item.childList)
//        }
//        rvChild.setTag(R.id.rvChild,item.childList)
//    }


}