package com.boom.aiobrowser.ui.adapter.item

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.base.BaseViewHolder
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.BrowserItemFilmNewsBinding
import com.boom.aiobrowser.tools.GlideManager
import com.boom.base.adapter4.BaseQuickAdapter
import java.text.SimpleDateFormat
import java.util.Date

// search film 新闻列表
internal class NewsItemFilm(parent: ViewGroup) : BaseViewHolder<BrowserItemFilmNewsBinding>(
    BrowserItemFilmNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) {

    fun bind(item: NewsData, fragment: BaseFragment<*>?, position:Int, adapter: BaseQuickAdapter<*, *>) {
        viewBinding?.apply {
            GlideManager.loadImg(
                fragment,
                ivImg,
                item.iassum,
                loadId = R.mipmap.bg_news_default,
                R.mipmap.bg_news_default
            )
            var movieTitle = adapter.context.getString(R.string.app_movie_title)
            tvNewsTitle.text = "${movieTitle}:${item.tconsi}"

            extracted(movieTitle, tvNewsTitle,adapter.context)

            var splits = item.sissue?.split("_")
            var size = splits?.size ?: 0
            if (size > 0) {
                var rate = splits!!.get(0)
                tvRate.text = rate
            }
            if (size > 1) {
                var country = splits!!.get(1)
                var movieCountry = adapter.context.getString(R.string.app_movie_country)
                tvNewsCountry.text = "${movieCountry}:${country}"
                extracted(movieCountry, tvNewsCountry,adapter.context)
            }
            var releaseDate = adapter.context.getString(R.string.app_movie_release_date)

            tvNewsDate.text = "${releaseDate}:${
                SimpleDateFormat("yyyy-MM-dd").format(
                    Date(item.pphilo ?: System.currentTimeMillis())
                )
            }"
            val stringBuilderDate =
                SpannableStringBuilder(tvNewsDate.text?.trim() ?: "")
            extracted(releaseDate, tvNewsDate,adapter.context)
        }
    }
    private fun extracted(movieTitle: String, textView: TextView,context: Context) {
        val stringBuilderTitle = SpannableStringBuilder(textView.text?.trim() ?: "")
        stringBuilderTitle.setSpan(
            ForegroundColorSpan(context.getColor(R.color.black)),
            movieTitle.length + 1,
            textView.text.trim().length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        stringBuilderTitle.setSpan(
            StyleSpan(Typeface.BOLD),
            movieTitle.length + 1,
            textView.text.trim().length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.setText(stringBuilderTitle)
    }
}