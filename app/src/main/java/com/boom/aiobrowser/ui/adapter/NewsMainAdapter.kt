package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.SizeUtils.dp2px
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADDataManager
import com.boom.aiobrowser.ad.AioADShowManager
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.ViewItem
import com.boom.aiobrowser.databinding.BrowserItemFilmNewsBinding
import com.boom.aiobrowser.databinding.BrowserItemHomeAdBinding
import com.boom.aiobrowser.databinding.BrowserItemMainNewsBinding
import com.boom.aiobrowser.databinding.BrowserItemSearchNewsBinding
import com.boom.aiobrowser.databinding.NewsDetailsItemImgBinding
import com.boom.aiobrowser.databinding.NewsDetailsItemReadSourceBinding
import com.boom.aiobrowser.databinding.NewsDetailsItemTextBinding
import com.boom.aiobrowser.databinding.NewsDetailsItemTitleBinding
import com.boom.aiobrowser.databinding.NewsDetailsItemTopImgBinding
import com.boom.aiobrowser.databinding.NewsDetailsItemTopVideoBinding
import com.boom.aiobrowser.databinding.NewsItemHomeTopBinding
import com.boom.aiobrowser.databinding.NewsItemTrendingBinding
import com.boom.aiobrowser.other.JumpConfig
import com.boom.aiobrowser.other.ParamsConfig
import com.boom.aiobrowser.other.SearchConfig
import com.boom.aiobrowser.point.AD_POINT
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.GlideManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.TimeManager
import com.boom.aiobrowser.tools.partitionList
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.activity.WebActivity
import com.boom.aiobrowser.ui.activity.WebDetailsActivity
import com.boom.base.adapter4.BaseMultiItemAdapter
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import com.boom.drag.utils.DisplayUtils
import com.boom.video.GSYVideoManager
import com.boom.video.builder.GSYVideoOptionBuilder
import com.boom.video.listener.GSYSampleCallBack
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle
import java.text.SimpleDateFormat
import java.util.Date


class NewsMainAdapter(var fragmet: BaseFragment<*>? = null) : BaseMultiItemAdapter<NewsData>() {
    //home 顶部
    internal class NewsHomeItem(viewBinding: NewsItemHomeTopBinding) :
        RecyclerView.ViewHolder(viewBinding.getRoot()) {
        var viewBinding: NewsItemHomeTopBinding? = null

        init {
            this.viewBinding = viewBinding
        }

        constructor(parent: ViewGroup) : this(
            NewsItemHomeTopBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    //home 新闻列表
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

    internal class NewsTrendingItem(viewBinding: NewsItemTrendingBinding) :
        RecyclerView.ViewHolder(viewBinding.getRoot()) {
        var viewBinding: NewsItemTrendingBinding? = null

        init {
            this.viewBinding = viewBinding
        }

        constructor(parent: ViewGroup) : this(
            NewsItemTrendingBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    //search 新闻列表
    internal class NewsItemSearch(viewBinding: BrowserItemSearchNewsBinding) :
        RecyclerView.ViewHolder(viewBinding.getRoot()) {
        var viewBinding: BrowserItemSearchNewsBinding? = null

        init {
            this.viewBinding = viewBinding
        }

        constructor(parent: ViewGroup) : this(
            BrowserItemSearchNewsBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    // search film 新闻列表
    internal class NewsItemFilm(viewBinding: BrowserItemFilmNewsBinding) :
        RecyclerView.ViewHolder(viewBinding.getRoot()) {
        var viewBinding: BrowserItemFilmNewsBinding? = null

        init {
            this.viewBinding = viewBinding
        }

        constructor(parent: ViewGroup) : this(
            BrowserItemFilmNewsBinding.inflate(
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
                            var params = (tvNewsTitle.layoutParams as ConstraintLayout.LayoutParams)
                            params.topMargin = dp2px(0f)
                        } else {
                            ivImg.visibility = View.VISIBLE
                            tvNewsContent.visibility = View.GONE
                            GlideManager.loadImg(
                                fragmet,
                                ivImg,
                                item.iassum,
                                loadId = R.mipmap.ic_default_nf,
                                R.mipmap.ic_default_nf
                            )
                            var params = (tvNewsTitle.layoutParams as ConstraintLayout.LayoutParams)
                            params.topMargin = dp2px(13f)
                        }
                        tvNewsTitle.text = item.tconsi
                        GlideManager.loadImg(fragmet, ivSource, item.sschem)
                        tvSourceName.text = "${item.sfindi}"
                        tvNewsTime.text = TimeManager.getNewsTime(item.pphilo ?: 0)
                    }
                }
            })
            .addItemType(
                NewsData.TYPE_DETAILS_NEWS_SEARCH,
                object : OnMultiItemAdapterListener<NewsData, NewsItemSearch> {

                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ): NewsItemSearch {
                        return NewsItemSearch(
                            parent
                        );
                    }

                    override fun onBind(holder: NewsItemSearch, position: Int, item: NewsData?) {
                        if (item == null) return
                        holder.viewBinding?.apply {
                            GlideManager.loadImg(
                                fragmet,
                                ivImg,
                                item.iassum,
                                loadId = R.mipmap.ic_default_nf_small,
                                R.mipmap.ic_default_nf_small
                            )
                            tvNewsTitle.text = item.tconsi
                            GlideManager.loadImg(fragmet, ivSource, item.sschem)
                            tvSourceName.text = "${item.sfindi}"
                        }
                    }
                })
            .addItemType(
                NewsData.TYPE_HOME_NEWS_TRENDING,
                object : OnMultiItemAdapterListener<NewsData, NewsTrendingItem> {

                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ): NewsTrendingItem {
                        return NewsTrendingItem(
                            parent
                        );
                    }

                    override fun onBind(holder: NewsTrendingItem, position: Int, item: NewsData?) {
                        if (item == null) return
                        holder.viewBinding?.apply {
                            rvList.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
                            var newsAdapter = TrendingNewsAdapter()
                            rvList.adapter = newsAdapter
                            rvList.addItemDecoration(object : RecyclerView.ItemDecoration() {

                                override fun getItemOffsets(
                                    outRect: Rect,
                                    view: View,
                                    parent: RecyclerView,
                                    state: RecyclerView.State
                                ) {
                                    super.getItemOffsets(outRect, view, parent, state)
                                    outRect.bottom =
                                        DisplayUtils.dp2px(context, 8f)
                                }
                            })
                            newsAdapter.submitList(item.trendList)
                            newsAdapter.setOnDebouncedItemClick{adapter, view, position ->
                                var data = newsAdapter.items.get(position)
                                (context as BaseActivity<*>).jumpActivity<WebDetailsActivity>(Bundle().apply {
                                    putString(ParamsConfig.JSON_PARAMS, toJson(data))
                                })
                            }
                        }
                    }
                })
            .addItemType(
                NewsData.TYPE_DETAILS_NEWS_SEARCH_FILM,
                object : OnMultiItemAdapterListener<NewsData, NewsItemFilm> {

                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ): NewsItemFilm {
                        return NewsItemFilm(
                            parent
                        );
                    }

                    override fun onBind(holder: NewsItemFilm, position: Int, item: NewsData?) {
                        if (item == null) return
                        holder.viewBinding?.apply {
                            GlideManager.loadImg(
                                fragmet,
                                ivImg,
                                item.iassum,
                                loadId = R.mipmap.bg_news_default,
                                R.mipmap.bg_news_default
                            )
                            var movieTitle = context.getString(R.string.app_movie_title)
                            tvNewsTitle.text = "${movieTitle}:${item.tconsi}"

                            extracted(movieTitle, tvNewsTitle)

                            var splits = item.sissue?.split("_")
                            var size = splits?.size ?: 0
                            if (size > 0) {
                                var rate = splits!!.get(0)
                                tvRate.text = rate
                            }
                            if (size > 1) {
                                var country = splits!!.get(1)
                                var movieCountry = context.getString(R.string.app_movie_country)
                                tvNewsCountry.text = "${movieCountry}:${country}"
                                extracted(movieCountry, tvNewsCountry)
                            }
                            var releaseDate = context.getString(R.string.app_movie_release_date)

                            tvNewsDate.text = "${releaseDate}:${
                                SimpleDateFormat("yyyy-MM-dd").format(
                                    Date(item.pphilo ?: System.currentTimeMillis())
                                )
                            }"
                            val stringBuilderDate =
                                SpannableStringBuilder(tvNewsDate.text?.trim() ?: "")
                            extracted(releaseDate, tvNewsDate)
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
                            var tag = clRoot.getTag(R.id.clRoot) as? NewsData
                            if (tag != item) {
                                runCatching {
                                    var gsyVideoOptionBuilder = GSYVideoOptionBuilder()
                                    gsyVideoOptionBuilder!!
                                        .setIsTouchWiget(false) //.setThumbImageView(imageView)
                                        .setUrl(item?.vbreas ?: "")
                                        .setVideoTitle("")
                                        .setRotateViewAuto(false)
                                        .setLockLand(false)
                                        .setPlayTag("videoplay")
                                        .setShowFullAnimation(true)
                                        .setNeedLockFull(true)
                                        .setCacheWithPlay(true)
                                        .setVideoAllCallBack(object : GSYSampleCallBack() {
                                            override fun onPrepared(
                                                url: String,
                                                vararg objects: Any
                                            ) {
                                                super.onPrepared(url, *objects)
                                            }

                                            override fun onQuitFullscreen(
                                                url: String,
                                                vararg objects: Any
                                            ) {
                                                super.onQuitFullscreen(url, *objects)
                                                //全屏不静音
//                    GSYVideoManager.instance().isNeedMute = true
                                            }

                                            override fun onEnterFullscreen(
                                                url: String,
                                                vararg objects: Any
                                            ) {
                                                super.onEnterFullscreen(url, *objects)
                                                GSYVideoManager.instance().isNeedMute = false
                                            }

                                            override fun onAutoComplete(
                                                url: String?,
                                                vararg objects: Any?
                                            ) {
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
                                        loadCoverImage(
                                            if (item.iassum.isNullOrEmpty()) item.vbreas
                                                ?: "" else item.iassum ?: ""
                                        )
                                        startPlayLogic()
                                    }
                                    PointEvent.posePoint(PointEventKey.video_playback_page)
                                }
                                clRoot.setTag(R.id.clRoot, item)
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
                            GlideManager.loadImg(
                                fragment = null,
                                iv = ivTopImg,
                                url = item.iassum,
                                loadId = R.mipmap.ic_default_nf,
                                R.mipmap.ic_default_nf
                            )
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
                            val stringBuilder = SpannableStringBuilder(item.tconsi?.trim() ?: "")
                            runCatching {
                                item.lcousi?.apply {
                                    forEachIndexed { index, linkData ->
                                        var start = linkData.slong!!.get(0)
                                        var end = linkData.slong!!.get(1) + 1
                                        var clickSpan = object : ClickableSpan() {
                                            override fun onClick(widget: View) {
                                                context.startActivity(
                                                    Intent(
                                                        context,
                                                        WebActivity::class.java
                                                    ).putExtra("url", linkData.lsong)
                                                )
                                            }

                                            override fun updateDrawState(ds: TextPaint) {
                                                super.updateDrawState(ds)
                                                ds.color = ContextCompat.getColor(
                                                    context,
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
                            GlideManager.loadImg(
                                fragment = null,
                                iv = ivImg,
                                url = item.iassum,
                                loadId = R.mipmap.ic_default_nf,
                                R.mipmap.ic_default_nf
                            )
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
                                context.startActivity(
                                    Intent(
                                        context,
                                        WebActivity::class.java
                                    ).putExtra("url", item.uweek)
                                )
                            }
                        }
                    }
                })
            .addItemType(
                NewsData.TYPE_HOME_NEWS_TOP,
                object : OnMultiItemAdapterListener<NewsData, NewsHomeItem> {

                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ): NewsHomeItem {
                        return NewsHomeItem(
                            parent
                        );
                    }

                    override fun onBind(
                        holder: NewsHomeItem,
                        position: Int,
                        item: NewsData?,
                        payloads: List<Any>
                    ) {
                        super.onBind(holder, position, item, payloads)
                        if (payloads.isNullOrEmpty()) {
                            onBindViewHolder(holder, position, item)
                        } else {
                            val payload = payloads[0].toString()
                            if (payload == "updateEngine") {
                                if (item == null) return
                                holder.viewBinding?.apply {
                                    when (CacheManager.engineType) {
                                        SearchConfig.SEARCH_ENGINE_GOOGLE -> {
                                            ivSearchEngine.setImageResource(R.mipmap.ic_search_gg)
                                        }

                                        SearchConfig.SEARCH_ENGINE_BING -> {
                                            ivSearchEngine.setImageResource(R.mipmap.ic_search_bing)
                                        }

                                        SearchConfig.SEARCH_ENGINE_YAHOO -> {
                                            ivSearchEngine.setImageResource(R.mipmap.ic_search_yahoo)
                                        }

                                        SearchConfig.SEARCH_ENGINE_PERPLEXITY -> {
                                            ivSearchEngine.setImageResource(R.mipmap.ic_search_perplexity)
                                        }
                                    }
                                }
                            } else if (payload == "updateTopTab") {

                            }
                        }
                    }


                    override fun onBind(holder: NewsHomeItem, position: Int, item: NewsData?) {
                        if (item == null) return
                        holder.viewBinding?.apply {
                            var tabList = CacheManager.homeTabList
                            tabList.add(JumpData().apply {
                                jumpType = JumpConfig.JUMP_WEB_TYPE
                                jumpUrl = ""
                                jumpTitle = ""
                            })
                            // 将集合按每 8 个分割
                            val partitionSize = 8
                            val dataList: MutableList<MutableList<JumpData>> = partitionList(
                                tabList,
                                partitionSize
                            ) as MutableList<MutableList<JumpData>>
                            var tag = vp2.getTag(R.id.vp2) as? MutableList<MutableList<JumpData>>
                            if (true) {
                                vp2.apply {
                                    offscreenPageLimit = dataList.size
                                    var homeTabAdapter: HomeTabAdapter? = null
                                    if (adapter == null) {
                                        homeTabAdapter = HomeTabAdapter(context as BaseActivity<*>)
                                        adapter = homeTabAdapter
                                    }
                                    (adapter as HomeTabAdapter)?.update(dataList)
                                }
                                if (dataList.size > 1) {
                                    indicator.visibility = View.VISIBLE
                                    var width = dp2px(7f).toFloat()
                                    indicator.apply {
                                        setSliderColor(
                                            context.getColor(R.color.color_tab_DAE5EC),
                                            context.getColor(R.color.color_tab_5755D9)
                                        )
                                        setSliderWidth(width)
                                        setSliderHeight(width)
                                        setSlideMode(IndicatorSlideMode.SMOOTH)
                                        setIndicatorStyle(IndicatorStyle.CIRCLE)
                                        setPageSize(dataList.size)
                                        notifyDataChanged()
                                        vp2.registerOnPageChangeCallback(object :
                                            ViewPager2.OnPageChangeCallback() {
                                            override fun onPageScrolled(
                                                position: Int,
                                                positionOffset: Float,
                                                positionOffsetPixels: Int
                                            ) {
                                                super.onPageScrolled(
                                                    position,
                                                    positionOffset,
                                                    positionOffsetPixels
                                                )
                                                indicator.onPageScrolled(
                                                    position,
                                                    positionOffset,
                                                    positionOffsetPixels
                                                )
                                            }

                                            override fun onPageSelected(position: Int) {
                                                super.onPageSelected(position)
                                                indicator.onPageSelected(position)
                                            }
                                        })
                                        rlHistory.setPadding(0, dp2px(11f), 0, dp2px(16f))
                                    }

                                } else {
                                    indicator.visibility = View.GONE
                                    rlHistory.setPadding(0, dp2px(11f), 0, dp2px(20f))
                                }
                                vp2.setTag(R.id.vp2, dataList)
                            }
                            if (CacheManager.browserStatus == 0) {
                                ivPrivate.visibility = View.GONE
                            } else {
                                ivPrivate.visibility = View.VISIBLE
                            }
                            when (CacheManager.engineType) {
                                SearchConfig.SEARCH_ENGINE_GOOGLE -> {
                                    ivSearchEngine.setImageResource(R.mipmap.ic_search_gg)
                                }

                                SearchConfig.SEARCH_ENGINE_BING -> {
                                    ivSearchEngine.setImageResource(R.mipmap.ic_search_bing)
                                }

                                SearchConfig.SEARCH_ENGINE_YAHOO -> {
                                    ivSearchEngine.setImageResource(R.mipmap.ic_search_yahoo)
                                }

                                SearchConfig.SEARCH_ENGINE_PERPLEXITY -> {
                                    ivSearchEngine.setImageResource(R.mipmap.ic_search_perplexity)
                                }
                            }
                        }
                    }
                })
            .onItemViewType { position, list -> list.get(position).dataType }
    }

    private fun extracted(movieTitle: String, textView: TextView) {
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