package com.boom.aiobrowser.ui.adapter.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.blankj.utilcode.util.SizeUtils.dp2px
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.base.BaseViewHolder
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.BrowserItemMainNewsBinding
import com.boom.aiobrowser.tools.GlideManager
import com.boom.aiobrowser.tools.TimeManager
import com.boom.aiobrowser.ui.activity.WebDetailsActivity
import com.boom.aiobrowser.ui.fragment.MainFragment
import com.boom.base.adapter4.BaseQuickAdapter

//home 新闻列表
internal class NewsItem(parent: ViewGroup) : BaseViewHolder<BrowserItemMainNewsBinding>(
    BrowserItemMainNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) {

    fun bind(item: NewsData, fragment: BaseFragment<*>?,position:Int,adapter:BaseQuickAdapter<*,*>) {
        viewBinding?.apply {
            if (item.iassum.isNullOrEmpty()) {
                ivImg.visibility = View.GONE
                tvNewsContent.visibility = View.VISIBLE
                tvNewsContent.text = item.sissue
                var params = (tvNewsTitle.layoutParams as ConstraintLayout.LayoutParams)
                params.topMargin = dp2px(11f)
            } else {
                ivImg.visibility = View.VISIBLE
                tvNewsContent.visibility = View.GONE
                GlideManager.loadImg(
                    fragment,
                    ivImg,
                    item.iassum,
                    loadId = R.mipmap.ic_default_nf,
                    R.mipmap.ic_default_nf
                )
                var params = (tvNewsTitle.layoutParams as ConstraintLayout.LayoutParams)
                params.topMargin = dp2px(12f)
                var paramsIv = (ivImg.layoutParams as ConstraintLayout.LayoutParams)
                if (adapter.context is WebDetailsActivity){
                    if (position>0 && (adapter.mutableItems.get(position-1) as NewsData).dataType == NewsData.TYPE_DETAILS_NEWS_RELATED){
                        paramsIv.topMargin = dp2px(0f)
                    }else{
                        paramsIv.topMargin = dp2px(14f)
                    }
                }else{
                    if ((position == 2 && (fragment!=null && fragment is MainFragment))){
                        paramsIv.topMargin = dp2px(0f)
                    }else{
                        paramsIv.topMargin = dp2px(14f)
                    }
                }
            }
            tvNewsTitle.text = item.tconsi
            GlideManager.loadImg(fragment, ivSource, item.sschem)
            tvSourceName.text = "${item.sfindi}"
            tvNewsTime.text = TimeManager.getNewsTime(item.pphilo ?: 0)
            if(item.areaTag.isNotEmpty()){
                llLocation.visibility = View.VISIBLE
                tvLocation.text = item.areaTag
            }else{
                llLocation.visibility = View.GONE
            }
        }

    }
}
