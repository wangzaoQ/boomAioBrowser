package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.data.HistoryData
import com.boom.aiobrowser.databinding.BrowserItemHistoryBinding
import com.boom.aiobrowser.databinding.BrowserItemHistoryDateBinding
import com.boom.aiobrowser.tools.TimeManager
import com.boom.base.adapter4.BaseMultiItemAdapter

class HistoryAdapter : BaseMultiItemAdapter<HistoryData>() {


    internal class HistoryItem(viewBinding: BrowserItemHistoryBinding) :
        RecyclerView.ViewHolder(viewBinding.getRoot()) {
        var viewBinding: BrowserItemHistoryBinding? = null

        init {
            this.viewBinding = viewBinding
        }

        constructor(parent: ViewGroup) : this(
            BrowserItemHistoryBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }


    internal class HistoryItemTitle(viewBinding: BrowserItemHistoryDateBinding) :
        RecyclerView.ViewHolder(viewBinding.getRoot()) {
        var viewBinding: BrowserItemHistoryDateBinding? = null

        init {
            this.viewBinding = viewBinding
        }

        constructor(parent: ViewGroup) : this(
            BrowserItemHistoryDateBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }



    init {
        addItemType(
            HistoryData.HISTORY_ITEM,
            object : OnMultiItemAdapterListener<HistoryData, HistoryAdapter.HistoryItem> {


                override fun onBind(holder: HistoryItem, position: Int, item: HistoryData?) {
                    item?.jumpData?.apply {
                        holder.viewBinding?.apply {
                            var time = item.jumpData?.updateTime?:System.currentTimeMillis()
                            tvTime.text = TimeManager.getTimeHD(time)
                            tvTitle.text = item.jumpData?.jumpUrl
                        }
                    }
                }

                override fun onCreate(
                    context: Context,
                    parent: ViewGroup,
                    viewType: Int
                ): HistoryItem {
                    return HistoryAdapter.HistoryItem(
                        parent
                    );
                }
            })
            .addItemType(HistoryData.HISTORY_TITLE,
                object : OnMultiItemAdapterListener<HistoryData, HistoryAdapter.HistoryItemTitle> {


                    override fun onBind(holder: HistoryItemTitle, position: Int, item: HistoryData?) {
                        holder.viewBinding?.apply {
                            tvTitle.text = item?.title
                        }
                    }

                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ): HistoryItemTitle {
                        return HistoryAdapter.HistoryItemTitle(
                            parent
                        );
                    }
                })
            .onItemViewType { position, list -> list.get(position).type }
    }

}