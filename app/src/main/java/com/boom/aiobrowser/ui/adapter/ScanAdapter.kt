//package com.boom.aiobrowser.ui.adapter
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.boom.aiobrowser.R
//import com.boom.aiobrowser.data.FilesData
//import com.boom.aiobrowser.data.ScanData
//import com.boom.aiobrowser.databinding.BrowserItemScanBinding
//import com.boom.aiobrowser.ui.view.CustomLinearLayoutManager
//import com.boom.base.adapter4.BaseQuickAdapter
//import com.boom.base.adapter4.util.setOnDebouncedItemClick
//
//class ScanAdapter(var updateBack:()-> Unit) : BaseQuickAdapter<ScanData, ScanAdapter.VH>() {
//    class VH(parent: ViewGroup, val viewBinding: BrowserItemScanBinding = BrowserItemScanBinding.inflate(
//        LayoutInflater.from(parent.context), parent, false
//    )
//    ) : RecyclerView.ViewHolder(viewBinding.root)
//
//    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
//        return VH(parent)
//    }
//
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
//                    (holder.viewBinding.rvChild.adapter as ScanChildAdapter).notifyItemRangeRemoved(0,holder.viewBinding.rvChild.adapter!!.itemCount)
//                }else{
//                    (holder.viewBinding.rvChild.adapter as ScanChildAdapter).addAll(item.childList)
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
//
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
//
//    override fun onBindViewHolder(holder: ScanAdapter.VH, position: Int, item: ScanData?) {
//        if (item == null)return
//        holder.viewBinding.apply {
//            scanItem.setScanData(item)
//            if (item.childList.isNullOrEmpty() || item.itemExpend.not()){
//                if (position == 0 && item.isLoading.not()){
//                    rlCacheTips.visibility = View.VISIBLE
//                }else{
//                    rlCacheTips.visibility = View.GONE
//                }
//                rvChild.visibility = View.GONE
//            }else{
//                rvChild.visibility = View.VISIBLE
//                var tag = rvChild.getTag(R.id.rvChild)as?MutableList<FilesData>
//                if (tag != item.childList){
//                    updateRv(rvChild, item, position)
//                }
//            }
//        }
//    }
//
//}