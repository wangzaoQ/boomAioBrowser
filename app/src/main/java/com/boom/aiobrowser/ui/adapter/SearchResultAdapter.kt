package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.RecentSearchData
import com.boom.aiobrowser.data.SearchResultData
import com.boom.aiobrowser.databinding.BrowserFragmentMainBinding
import com.boom.aiobrowser.databinding.BrowserItemMainNewsBinding
import com.boom.aiobrowser.databinding.BrowserItemRecentSearchBinding
import com.boom.aiobrowser.databinding.BrowserItemResultSearchBinding
import com.boom.base.adapter4.BaseQuickAdapter

class SearchResultAdapter : BaseQuickAdapter<SearchResultData, SearchResultAdapter.VH>() {

    var content = ""

    class VH(
        parent: ViewGroup,
        val viewBinding: BrowserItemResultSearchBinding = BrowserItemResultSearchBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: SearchResultData?) {
        if (item == null)return
        holder.viewBinding.apply {
            tvSearch.text = item.searchContent
            var s = SpannableStringBuilder(item.searchContent)
            item.searchContent.replace(" ","")
            var index = item.searchContent.indexOf(content, ignoreCase = true)
            if (index >=0){
                s.setSpan(
                    ForegroundColorSpan(context.getColor(R.color.black)),
                    index,
                    index+content.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                tvSearch.setText(s)
            }
            if (item.type == 0){
                ivLeft.setImageResource(R.mipmap.ic_search)
            }else{
                ivLeft.setImageResource(R.mipmap.ic_recent)
            }
        }
    }

}