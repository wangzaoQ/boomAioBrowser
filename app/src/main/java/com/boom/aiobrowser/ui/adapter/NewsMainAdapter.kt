package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADDataManager
import com.boom.aiobrowser.ad.AioADShowManager
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.ScanData
import com.boom.aiobrowser.data.ViewItem
import com.boom.aiobrowser.databinding.BrowserItemHomeAdBinding
import com.boom.aiobrowser.databinding.BrowserItemMainNewsBinding
import com.boom.aiobrowser.point.AD_POINT
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.GlideManager
import com.boom.aiobrowser.tools.TimeManager
import com.boom.base.adapter4.BaseMultiItemAdapter

class NewsMainAdapter(var fragmet :BaseFragment<*>?=null) : BaseMultiItemAdapter<NewsData>() {

    internal class NewsItem(viewBinding: BrowserItemMainNewsBinding) :
        RecyclerView.ViewHolder(viewBinding.getRoot()) {
        var viewBinding: BrowserItemMainNewsBinding? = null

        init {
            this.viewBinding = viewBinding
        }

        constructor(parent: ViewGroup) : this(
            BrowserItemMainNewsBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    internal class ADItem(viewBinding: BrowserItemHomeAdBinding) :
        RecyclerView.ViewHolder(viewBinding.getRoot()) {
        var viewBinding: BrowserItemHomeAdBinding? = null

        init {
            this.viewBinding = viewBinding
        }

        constructor(parent: ViewGroup) : this(
            BrowserItemHomeAdBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }



    init {
        addItemType(
            ViewItem.TYPE_PARENT,
            object : OnMultiItemAdapterListener<NewsData, NewsItem> {

                override fun onCreate(
                    context: Context,
                    parent: ViewGroup,
                    viewType: Int
                ): NewsItem {
                    return NewsItem(
                        parent
                    );
                }

                override fun onBind(holder: NewsItem, position: Int, item: NewsData?) {
                    if (item == null) return
                    holder.viewBinding?.apply {
                        if (item.iassum.isNullOrEmpty()){
                            ivImg.visibility = View.GONE
                            tvNewsContent.visibility = View.VISIBLE
                            tvNewsContent.text = item.sissue
                        }else{
                            ivImg.visibility = View.VISIBLE
                            tvNewsContent.visibility = View.GONE
                            GlideManager.loadImg(fragmet,ivImg,item.iassum,loadId = R.mipmap.bg_news_default ,R.mipmap.bg_news_default)
                        }
                        tvNewsTitle.text = item.tconsi
                        GlideManager.loadImg(fragmet,ivSource,item.sschem)
                        tvSourceName.text = "${item.sfindi}"
                        tvNewsTime.text = TimeManager.getNewsTime(item.pphilo?:0)
                    }
                }})
            .addItemType(
                ViewItem.TYPE_CHILD,
                object : OnMultiItemAdapterListener<NewsData, ADItem> {

                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ): ADItem {
                        return ADItem(
                            parent
                        );
                    }

                    override fun onBind(holder: ADItem, position: Int, item: NewsData?) {
                        holder.viewBinding?.apply {
                            if (AioADDataManager.adFilter1().not()) {
                                PointEvent.posePoint(PointEventKey.aobws_ad_chance, Bundle().apply {
                                    putString(PointValueKey.ad_pos_id,AD_POINT.aobws_news_one)
                                })
                            }
                            AioADShowManager(fragmet!!.rootActivity,ADEnum.NATIVE_AD,"原生"){

                            }.showNativeAD(flRoot,AD_POINT.aobws_news_one)
                        }
                    }

                })
            .onItemViewType { position, list -> list.get(position).dataType }
    }


}