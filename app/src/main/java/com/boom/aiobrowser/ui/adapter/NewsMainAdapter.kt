package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADDataManager
import com.boom.aiobrowser.ad.AioADShowManager
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.ScanData
import com.boom.aiobrowser.data.ViewItem
import com.boom.aiobrowser.databinding.BrowserItemHomeAdBinding
import com.boom.aiobrowser.databinding.BrowserItemMainNewsBinding
import com.boom.aiobrowser.databinding.NewsDetailsItemImgBinding
import com.boom.aiobrowser.databinding.NewsDetailsItemReadSourceBinding
import com.boom.aiobrowser.databinding.NewsDetailsItemTextBinding
import com.boom.aiobrowser.databinding.NewsDetailsItemTitleBinding
import com.boom.aiobrowser.databinding.NewsDetailsItemTopImgBinding
import com.boom.aiobrowser.databinding.NewsDetailsItemTopVideoBinding
import com.boom.aiobrowser.point.AD_POINT
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.GlideManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.JumpDataManager.getCurrentJumpData
import com.boom.aiobrowser.tools.JumpDataManager.updateCurrentJumpData
import com.boom.aiobrowser.tools.TimeManager
import com.boom.aiobrowser.tools.getUrlIcon
import com.boom.aiobrowser.tools.getUrlSource
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.UrlConfig
import com.boom.aiobrowser.ui.activity.WebActivity
import com.boom.base.adapter4.BaseMultiItemAdapter
import com.boom.video.GSYVideoManager
import com.boom.video.builder.GSYVideoOptionBuilder
import com.boom.video.listener.GSYSampleCallBack
import com.boom.video.utils.OrientationUtils
import java.io.File
import java.lang.ref.WeakReference

class NewsMainAdapter(var fragmet: BaseFragment<*>? = null) : BaseMultiItemAdapter<NewsData>() {

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

    internal class TitleItem(viewBinding: NewsDetailsItemTitleBinding) :
        RecyclerView.ViewHolder(viewBinding.getRoot()) {
        var viewBinding: NewsDetailsItemTitleBinding? = null

        init {
            this.viewBinding = viewBinding
        }

        constructor(parent: ViewGroup) : this(
            NewsDetailsItemTitleBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    internal class TextItem(viewBinding: NewsDetailsItemTextBinding) :
        RecyclerView.ViewHolder(viewBinding.getRoot()) {
        var viewBinding: NewsDetailsItemTextBinding? = null

        init {
            this.viewBinding = viewBinding
        }

        constructor(parent: ViewGroup) : this(
            NewsDetailsItemTextBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    internal class ImgItem(viewBinding: NewsDetailsItemImgBinding) :
        RecyclerView.ViewHolder(viewBinding.getRoot()) {
        var viewBinding: NewsDetailsItemImgBinding? = null

        init {
            this.viewBinding = viewBinding
        }

        constructor(parent: ViewGroup) : this(
            NewsDetailsItemImgBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    internal class TopVideoItem(viewBinding: NewsDetailsItemTopVideoBinding) :
        RecyclerView.ViewHolder(viewBinding.getRoot()) {
        var viewBinding: NewsDetailsItemTopVideoBinding? = null

        init {
            this.viewBinding = viewBinding
        }

        constructor(parent: ViewGroup) : this(
            NewsDetailsItemTopVideoBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }
    internal class TopImgItem(viewBinding: NewsDetailsItemTopImgBinding) :
        RecyclerView.ViewHolder(viewBinding.getRoot()) {
        var viewBinding: NewsDetailsItemTopImgBinding? = null

        init {
            this.viewBinding = viewBinding
        }

        constructor(parent: ViewGroup) : this(
            NewsDetailsItemTopImgBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }
    internal class ReadSourceItem(viewBinding: NewsDetailsItemReadSourceBinding) :
        RecyclerView.ViewHolder(viewBinding.getRoot()) {
        var viewBinding: NewsDetailsItemReadSourceBinding? = null

        init {
            this.viewBinding = viewBinding
        }

        constructor(parent: ViewGroup) : this(
            NewsDetailsItemReadSourceBinding.inflate(
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
                        if (item.iassum.isNullOrEmpty()) {
                            ivImg.visibility = View.GONE
                            tvNewsContent.visibility = View.VISIBLE
                            tvNewsContent.text = item.sissue
                        } else {
                            ivImg.visibility = View.VISIBLE
                            tvNewsContent.visibility = View.GONE
                            GlideManager.loadImg(
                                fragmet,
                                ivImg,
                                item.iassum,
                                loadId = R.mipmap.bg_news_default,
                                R.mipmap.bg_news_default
                            )
                        }
                        tvNewsTitle.text = item.tconsi
                        GlideManager.loadImg(fragmet, ivSource, item.sschem)
                        tvSourceName.text = "${item.sfindi}"
                        tvNewsTime.text = TimeManager.getNewsTime(item.pphilo ?: 0)
                    }
                }
            })
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
                                    putString(PointValueKey.ad_pos_id, AD_POINT.aobws_news_one)
                                })
                            }
                            AioADShowManager(fragmet!!.rootActivity, ADEnum.NATIVE_AD, "原生") {

                            }.showNativeAD(flRoot, AD_POINT.aobws_news_one)
                        }
                    }

                })
            .addItemType(
                NewsData.TYPE_DETAILS_NEWS_TOP_VIDEO,
                object : OnMultiItemAdapterListener<NewsData, TopVideoItem> {

                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ): TopVideoItem {
                        return TopVideoItem(
                            parent
                        );
                    }

                    override fun onBind(holder: TopVideoItem, position: Int, item: NewsData?) {
                        if (item == null) return
                        holder.viewBinding?.apply {
                            runCatching {
                                var gsyVideoOptionBuilder =  GSYVideoOptionBuilder()
                                gsyVideoOptionBuilder!!
                                    .setIsTouchWiget(false) //.setThumbImageView(imageView)
                                    .setUrl(item?.vbreas?:"")
                                    .setVideoTitle("")
                                    .setRotateViewAuto(false)
                                    .setLockLand(false)
                                    .setPlayTag("videoplay")
                                    .setShowFullAnimation(true)
                                    .setNeedLockFull(true)
                                    .setCacheWithPlay(true)
                                    .setVideoAllCallBack(object : GSYSampleCallBack() {
                                        override fun onPrepared(url: String, vararg objects: Any) {
                                            super.onPrepared(url, *objects)
                                        }

                                        override fun onQuitFullscreen(url: String, vararg objects: Any) {
                                            super.onQuitFullscreen(url, *objects)
                                            //全屏不静音
//                    GSYVideoManager.instance().isNeedMute = true
                                        }

                                        override fun onEnterFullscreen(url: String, vararg objects: Any) {
                                            super.onEnterFullscreen(url, *objects)
                                            GSYVideoManager.instance().isNeedMute = false
                                        }

                                        override fun onAutoComplete(url: String?, vararg objects: Any?) {
                                            super.onAutoComplete(url, *objects)
                                            player?.startPlayLogic()
                                        }
                                    })
                                    .build(player)
                                player?.apply {
                                    //设置返回按键功能
//                                    backButton?.setOnClickListener(View.OnClickListener {
////                                        back()
//                                    })
//                                    //设置旋转
//                                    var  orientationUtils = OrientationUtils(context as BaseActivity<*>, player)
//                                    //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
//                                    fullscreenButton?.setOnClickListener(View.OnClickListener { // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
//                                        // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
//                                        orientationUtils?.resolveByClick();
//                                    })
                                    backButton.visibility = View.GONE
                                    titleTextView.visibility = View.GONE
                                    loadCoverImage(if (item.iassum.isNullOrEmpty()) item.vbreas?:"" else item.iassum?:"")
                                    startPlayLogic()
                                }
                                PointEvent.posePoint(PointEventKey.video_playback_page)
                            }
                        }
                    }
                })
            .addItemType(
                NewsData.TYPE_DETAILS_NEWS_TOP_IMG,
                object : OnMultiItemAdapterListener<NewsData, TopImgItem> {

                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ): TopImgItem {
                        return TopImgItem(
                            parent
                        );
                    }

                    override fun onBind(holder: TopImgItem, position: Int, item: NewsData?) {
                        if (item == null) return
                        holder.viewBinding?.apply {
                            GlideManager.loadImg(fragment = null, iv = ivTopImg, url = item.iassum)
                        }
                    }
                })
            .addItemType(
                NewsData.TYPE_DETAILS_NEWS_TITLE,
                object : OnMultiItemAdapterListener<NewsData, TitleItem> {

                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ): TitleItem {
                        return TitleItem(
                            parent
                        );
                    }

                    override fun onBind(holder: TitleItem, position: Int, item: NewsData?) {
                        if (item == null) return
                        holder.viewBinding?.apply {
                            tvTitle.text = item.tconsi
                            tvSource.text = item.sfindi
                            tvTime.text = TimeManager.getNewsTime(item.pphilo ?: 0)
                            GlideManager.loadImg(fragment = null, iv = ivLogo, url = item.sschem)
                        }
                    }
                })
            .addItemType(
                NewsData.TYPE_DETAILS_NEWS_TEXT,
                object : OnMultiItemAdapterListener<NewsData, TextItem> {

                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ): TextItem {
                        return TextItem(
                            parent
                        );
                    }

                    override fun onBind(holder: TextItem, position: Int, item: NewsData?) {
                        if (item == null) return
                        holder.viewBinding?.apply {
                            val stringBuilder = SpannableStringBuilder(item.tconsi?.trim()?:"")
                            runCatching {
                                item.lcousi?.apply {
                                    forEachIndexed { index, linkData ->
                                        var start = linkData.slong!!.get(0)
                                        var end = linkData.slong!!.get(1)+1
                                        var clickSpan = object : ClickableSpan(){
                                            override fun onClick(widget: View) {
                                                context.startActivity(Intent(context, WebActivity::class.java).putExtra("url",linkData.lsong))
                                            }

                                            override fun updateDrawState(ds: TextPaint) {
                                                super.updateDrawState(ds)
                                                ds.color = ContextCompat.getColor(context, R.color.color_blue_0066FF)
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
                })
            .addItemType(
                NewsData.TYPE_DETAILS_NEWS_IMG,
                object : OnMultiItemAdapterListener<NewsData, ImgItem> {

                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ): ImgItem {
                        return ImgItem(
                            parent
                        );
                    }

                    override fun onBind(holder: ImgItem, position: Int, item: NewsData?) {
                        if (item == null) return
                        holder.viewBinding?.apply {
                            GlideManager.loadImg(fragment = null, iv = ivImg, url = item.iassum)
                        }
                    }
                })
            .addItemType(
                NewsData.TYPE_DETAILS_NEWS_READ_SOURCE,
                object : OnMultiItemAdapterListener<NewsData, ReadSourceItem> {

                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ): ReadSourceItem {
                        return ReadSourceItem(
                            parent
                        );
                    }

                    override fun onBind(holder: ReadSourceItem, position: Int, item: NewsData?) {
                        if (item == null) return
                        holder.viewBinding?.apply {
                            tvReadSource.setOnClickListener {
                                context.startActivity(Intent(context, WebActivity::class.java).putExtra("url",item.uweek))
                            }
                        }
                    }
                })
            .onItemViewType { position, list -> list.get(position).dataType }
    }


}