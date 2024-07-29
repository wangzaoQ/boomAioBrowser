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
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.BrowserFragmentMainBinding
import com.boom.aiobrowser.databinding.BrowserItemMainNewsBinding
import com.boom.aiobrowser.databinding.BrowserItemSearchBinding
import com.boom.base.adapter4.BaseQuickAdapter

class SearchEngineAdapter(var clickBack: (position:Int) -> Unit) : BaseQuickAdapter<Int, SearchEngineAdapter.VH>() {
    class VH(
        parent: ViewGroup,
        val viewBinding: BrowserItemSearchBinding = BrowserItemSearchBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: Int?) {
        holder.viewBinding.apply {
            item?.apply {
                when(this){
                    0->{
                        ivSearchEngine.setImageResource(R.mipmap.ic_search_gg)
                        tvSearchEngine.text = context.getString(R.string.app_google)
                    }
                    1->{
                        ivSearchEngine.setImageResource(R.mipmap.ic_search_bing)
                        tvSearchEngine.text = context.getString(R.string.app_bing)
                    }
                    2->{
                        ivSearchEngine.setImageResource(R.mipmap.ic_search_yahoo)
                        tvSearchEngine.text = context.getString(R.string.app_yahoo)
                    }
                    3->{
                        ivSearchEngine.setImageResource(R.mipmap.ic_search_perplexity)
                        tvSearchEngine.text = context.getString(R.string.app_perplexity)
                    }
                }
            }
        }
        holder.viewBinding.llRoot.setOnClickListener {
            clickBack.invoke(position)
        }
    }


}