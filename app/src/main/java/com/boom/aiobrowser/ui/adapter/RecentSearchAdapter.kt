package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.RecentSearchData
import com.boom.aiobrowser.databinding.BrowserFragmentMainBinding
import com.boom.aiobrowser.databinding.BrowserItemMainNewsBinding
import com.boom.aiobrowser.databinding.BrowserItemRecentSearchBinding
import com.boom.base.adapter4.BaseQuickAdapter

class RecentSearchAdapter : BaseQuickAdapter<JumpData, RecentSearchAdapter.VH>() {
    class VH(
        parent: ViewGroup,
        val viewBinding: BrowserItemRecentSearchBinding = BrowserItemRecentSearchBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: JumpData?) {
        holder.viewBinding.apply {
            tvRecent.text = item?.jumpTitle?:""
        }
    }


}