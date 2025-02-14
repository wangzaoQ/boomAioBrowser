package com.boom.aiobrowser.ui.adapter.item

import android.content.Intent
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.base.BaseViewHolder
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.NewsDetailsItemTextBinding
import com.boom.aiobrowser.ui.activity.WebActivity
import com.boom.base.adapter4.BaseQuickAdapter

internal class TextItem(parent: ViewGroup) : BaseViewHolder<NewsDetailsItemTextBinding>(
    NewsDetailsItemTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) {

    fun bind(item: NewsData, fragment: BaseFragment<*>?, position:Int, adapter: BaseQuickAdapter<*, *>) {
        viewBinding?.apply {
            val stringBuilder = SpannableStringBuilder(item.tconsi?.trim() ?: "")
            runCatching {
                item.lcousi?.apply {
                    forEachIndexed { index, linkData ->
                        var start = linkData.slong!!.get(0)
                        var end = linkData.slong!!.get(1) + 1
                        var clickSpan = object : ClickableSpan() {
                            override fun onClick(widget: View) {
                                adapter.context.startActivity(
                                    Intent(
                                        adapter.context,
                                        WebActivity::class.java
                                    ).putExtra("url", linkData.lsong)
                                )
                            }

                            override fun updateDrawState(ds: TextPaint) {
                                super.updateDrawState(ds)
                                ds.color = ContextCompat.getColor(
                                    adapter.context,
                                    R.color.color_blue_0066FF
                                )
                                ds.isUnderlineText = false;//去掉下划线
                            }

                        }
                        stringBuilder.setSpan(
                            clickSpan,
                            start,
                            end,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }
            }
            tvContent.setText(stringBuilder)
            tvContent.setMovementMethod(LinkMovementMethod.getInstance())
            tvContent.highlightColor = Color.TRANSPARENT
        }
    }
}