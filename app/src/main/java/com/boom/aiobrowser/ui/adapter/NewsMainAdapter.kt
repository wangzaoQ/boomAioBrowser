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
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.SizeUtils.dp2px
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADDataManager
import com.boom.aiobrowser.ad.AioADShowManager
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.TopicBean
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
import com.boom.aiobrowser.databinding.NewsItemDetailsRelatedBinding
import com.boom.aiobrowser.databinding.NewsItemHomeTopBinding
import com.boom.aiobrowser.databinding.NewsItemHomeTopicBinding
import com.boom.aiobrowser.databinding.NewsItemHomeVideoBinding
import com.boom.aiobrowser.databinding.NewsItemLocalBinding
import com.boom.aiobrowser.databinding.NewsItemTopicBinding
import com.boom.aiobrowser.databinding.NewsItemTopicHeaderBinding
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
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.TimeManager
import com.boom.aiobrowser.tools.partitionList
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.activity.TopicListActivity
import com.boom.aiobrowser.ui.activity.VideoListActivity
import com.boom.aiobrowser.ui.activity.WebActivity
import com.boom.aiobrowser.ui.activity.WebDetailsActivity
import com.boom.aiobrowser.ui.fragment.MainFragment
import com.boom.aiobrowser.ui.fragment.NewsFragment
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

    //home foryou新闻列表local
    internal class HomeLocalItem(viewBinding: NewsItemLocalBinding) :
        RecyclerView.ViewHolder(viewBinding.getRoot()) {
        var viewBinding: NewsItemLocalBinding? = null

        init {
            this.viewBinding = viewBinding
        }

        constructor(parent: ViewGroup) : this(
            NewsItemLocalBinding.inflate(
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

    internal class NewsHomeVideoItem(viewBinding: NewsItemHomeVideoBinding) :
        RecyclerView.ViewHolder(viewBinding.getRoot()) {
        var viewBinding: NewsItemHomeVideoBinding? = null

        init {
            this.viewBinding = viewBinding
        }

        constructor(parent: ViewGroup) : this(
            NewsItemHomeVideoBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    internal class NewsHomeTopicItem(viewBinding: NewsItemHomeTopicBinding) :
        RecyclerView.ViewHolder(viewBinding.getRoot()) {
        var viewBinding: NewsItemHomeTopicBinding? = null

        init {
            this.viewBinding = viewBinding
        }

        constructor(parent: ViewGroup) : this(
            NewsItemHomeTopicBinding.inflate(
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

    /**
     * 详情
     */
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


    internal class TopicItem(viewBinding: NewsItemTopicBinding) :
        RecyclerView.ViewHolder(viewBinding.getRoot()) {
        var viewBinding: NewsItemTopicBinding? = null

        init {
            this.viewBinding = viewBinding
        }

        constructor(parent: ViewGroup) : this(
            NewsItemTopicBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    //topic header
    internal class NewsTopicHeaderItem(viewBinding: NewsItemTopicHeaderBinding) :
        RecyclerView.ViewHolder(viewBinding.getRoot()) {
        var viewBinding: NewsItemTopicHeaderBinding? = null

        init {
            this.viewBinding = viewBinding
        }

        constructor(parent: ViewGroup) : this(
            NewsItemTopicHeaderBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    /**
     * 详情推荐新闻
     */
    internal class DetailsRelatedItem(viewBinding: NewsItemDetailsRelatedBinding) :
        RecyclerView.ViewHolder(viewBinding.getRoot()) {
        var viewBinding: NewsItemDetailsRelatedBinding? = null

        init {
            this.viewBinding = viewBinding
        }

        constructor(parent: ViewGroup) : this(
            NewsItemDetailsRelatedBinding.inflate(
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
                            params.topMargin = dp2px(11f)
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
                            params.topMargin = dp2px(12f)
                            var paramsIv = (ivImg.layoutParams as ConstraintLayout.LayoutParams)
                            if (context is WebDetailsActivity){
                                if (position>0 && mutableItems.get(position-1).dataType == NewsData.TYPE_DETAILS_NEWS_RELATED){
                                    paramsIv.topMargin = dp2px(0f)
                                }else{
                                    paramsIv.topMargin = dp2px(14f)
                                }
                            }else{
                                if ((position == 2 && (fragmet!=null && fragmet is MainFragment))){
                                    paramsIv.topMargin = dp2px(0f)
                                }else{
                                    paramsIv.topMargin = dp2px(14f)
                                }
                            }
                        }
                        tvNewsTitle.text = item.tconsi
                        GlideManager.loadImg(fragmet, ivSource, item.sschem)
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
                NewsData.TYPE_HOME_NEWS_LOCAL,
                object : OnMultiItemAdapterListener<NewsData, HomeLocalItem> {

                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ): HomeLocalItem {
                        return HomeLocalItem(
                            parent
                        );
                    }

                    override fun onBind(holder: HomeLocalItem, position: Int, item: NewsData?) {
                        if (item == null) return
                        holder.viewBinding?.apply {
                            var locationCity = CacheManager.locationData?.locationCity?:""
                            tvTitle.text = "${context.getString(R.string.app_location_title)} ${locationCity}?"
                            var s = SpannableStringBuilder(tvTitle.text)
                            var index = tvTitle.text.toString().indexOf(locationCity, ignoreCase = true)
                            if (index >=0){
                                s.setSpan(
                                    ForegroundColorSpan(context.getColor(R.color.color_blue_4442E7)),
                                    index,
                                    index+locationCity.length,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                                tvTitle.setText(s)
                            }
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
                        if (item == null || item.trendList.isNullOrEmpty()) return
                        holder.viewBinding?.apply {
                            var tag = rvList.getTag(R.id.rvList) as? MutableList<MutableList<NewsData>>
                            if (tag == null || tag != item.trendList){
                                rvList.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
                                var newsAdapter = TrendingNewsAdapter()
                                rvList.adapter = newsAdapter
                                newsAdapter.submitList(item.trendList)
                                newsAdapter.setOnDebouncedItemClick{adapter, view, position ->
                                    var data = newsAdapter.items.get(position)
                                    (context as BaseActivity<*>).jumpActivity<WebDetailsActivity>(Bundle().apply {
                                        putString(ParamsConfig.JSON_PARAMS, toJson(data))
                                    })
                                    PointEvent.posePoint(PointEventKey.trend_news, Bundle().apply {
                                        putString(PointValueKey.from_type,"home_page")
                                        putString(PointValueKey.news_id,data.itackl)
                                    })
                                }
                                rvList.setTag(R.id.rvList,item.trendList)
                            }
                        }
                    }
                })
            .addItemType(
                NewsData.TYPE_HOME_NEWS_VIDEO,
                object : OnMultiItemAdapterListener<NewsData, NewsHomeVideoItem> {

                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ): NewsHomeVideoItem {
                        return NewsHomeVideoItem(
                            parent
                        );
                    }

                    override fun onBind(holder: NewsHomeVideoItem, position: Int, item: NewsData?) {
                        if (item == null) return
                        holder.viewBinding?.apply {
                            var tag = clRoot.getTag(R.id.clRoot) as? MutableList<MutableList<NewsData>>
                            if (tag == null || tag != item.videoList){
                                rvVideo.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
                                var newsAdapter = HomeVideoAdapter(fragmet)
                                rvVideo.adapter = newsAdapter
                                newsAdapter.submitList(item.videoList?: mutableListOf())
                                if (rvVideo.itemDecorationCount==0){
                                    rvVideo.addItemDecoration(object : RecyclerView.ItemDecoration() {

                                        override fun getItemOffsets(
                                            outRect: Rect,
                                            view: View,
                                            parent: RecyclerView,
                                            state: RecyclerView.State
                                        ) {
                                            super.getItemOffsets(outRect, view, parent, state)
                                            val itemPosition = parent.getChildAdapterPosition(view)
                                            outRect.right = DisplayUtils.dp2px(
                                                context,
                                                8f
                                            )
                                            var endInx = item.videoList?.size?:0
                                            if ( endInx > 0){
                                                endInx = endInx-1
                                            }
                                            if (itemPosition == endInx){
                                                outRect.right = DisplayUtils.dp2px(
                                                    context,
                                                    13f
                                                )
                                            }else if (itemPosition == 0){
                                                outRect.left = DisplayUtils.dp2px(context, 13f)
                                            }
                                        }
                                    })
                                }

                                newsAdapter.setOnDebouncedItemClick{adapter, view, position ->
                                    var data = newsAdapter.items.get(position)
                                    if (fragmet!= null){
                                        if (fragmet!=null){
                                            if (fragmet is MainFragment){
                                                data.fromType = "home"
                                            }else if (fragmet is NewsFragment){
                                                data.fromType = "for_you"
                                            }
                                        }
                                    }
                                    VideoListActivity.startVideoListActivity(context as BaseActivity<*>,position,item.videoList?: mutableListOf())
                                }
                                clRoot.setTag(R.id.clRoot,item.videoList)
                            }
                        }
                    }
                })

            .addItemType(
                NewsData.TYPE_HOME_NEWS_TOPIC,
                object : OnMultiItemAdapterListener<NewsData, NewsHomeTopicItem> {

                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ): NewsHomeTopicItem {
                        return NewsHomeTopicItem(
                            parent
                        );
                    }

                    override fun onBind(holder: NewsHomeTopicItem, position: Int, item: NewsData?) {
                        if (item == null) return
                        holder.viewBinding?.topicRoot?.apply {
                            var tag = getTag(R.id.topicRoot) as? MutableList<String>
                            if (tag == null || tag!=item.topicList){
                                removeAllViews()
                                heightLimit = false
                                maxLimit = false
                                for (i in 0 until item.topicList!!.size){
                                    var content = if(item.isLoading) context.getString(R.string.app_loading_content) else item.topicList!!.get(i).topic
                                    var topicView = LayoutInflater.from(context).inflate(R.layout.news_item_child_topic,null,false)
                                    var tv = topicView.findViewById<AppCompatTextView>(R.id.tvTopic)
                                    topicView.setOnClickListener {
                                        if (item.isLoading){
                                            return@setOnClickListener
                                        }else{
                                            APP.topicJumpData.postValue(item.topicList!!.get(i))
                                        }
                                        PointEvent.posePoint(PointEventKey.topics_click,Bundle().apply{
                                            if (fragmet!=null){
                                                if (fragmet is MainFragment){
                                                    putString(PointValueKey.from_type,"home")
                                                }else if (fragmet is NewsFragment){
                                                    putString(PointValueKey.from_type,"for_you")
                                                }
                                            }
                                        })
                                    }
                                    tv.text = "#${content}"
                                    addView(topicView)
                                }
                                setTag(R.id.topicRoot,item.topicList!!)
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
                        if (item == null)return
                        holder.viewBinding?.apply {
                            var adPosId = AD_POINT.aobws_news_one
                            var adEnum = ADEnum.NATIVE_AD
                            if (item.adTag == ADEnum.BANNER_AD_NEWS_DETAILS.adName){
                                adPosId = ADEnum.BANNER_AD_NEWS_DETAILS.adName
                                adEnum = ADEnum.BANNER_AD_NEWS_DETAILS
                                line.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
                            }else{
                                line.setBackgroundColor(ContextCompat.getColor(context,R.color.color_black_F7F7F9))
                            }
                            if (AioADDataManager.adFilter1().not()) {
                                PointEvent.posePoint(PointEventKey.aobws_ad_chance, Bundle().apply {
                                    putString(PointValueKey.ad_pos_id, adPosId)
                                })
                            }
                            AioADShowManager(if (fragmet == null)(context as BaseActivity<*>) else fragmet!!.rootActivity,adEnum , "原生") {

                            }.showNativeAD(flRoot, adPosId)
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
                            if (tag== null || tag != item) {
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
                                        fullscreenButton.visibility = View.GONE
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
                            if(item.areaTag.isNotEmpty()){
                                llLocation.visibility = View.VISIBLE
                                tvLocation.text = item.areaTag
                            }else{
                                llLocation.visibility = View.GONE
                            }
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

                    }
                })
            .addItemType(
                NewsData.TYPE_DETAILS_NEWS_TOPIC,
                object : OnMultiItemAdapterListener<NewsData, TopicItem> {

                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ): TopicItem {
                        return TopicItem(
                            parent
                        );
                    }

                    override fun onBind(holder: TopicItem, position: Int, item: NewsData?) {
                        if (item == null || item.tdetai.isNullOrEmpty()) return
                        holder.viewBinding?.topicRoot?.apply {
                            var tag = getTag(R.id.topicRoot) as? MutableList<String>
                            if (tag == null || tag!=item.tdetai){
                                removeAllViews()
                                heightLimit = false
                                maxLimit = false
                                for (i in 0 until item.tdetai!!.size){
                                    var content = item.tdetai!!.get(i)
                                    var topicView = LayoutInflater.from(context).inflate(R.layout.news_item_child_topic,null,false)
                                    var tv = topicView.findViewById<AppCompatTextView>(R.id.tvTopic)
                                    topicView.setOnClickListener {
                                        var allTopicList = CacheManager.allTopicList
                                        var index = -1
                                        for(i in 0 until allTopicList.size){
                                            if (content == allTopicList.get(i).topic){
                                                index = i
                                                break
                                            }
                                        }
                                        if (index>=0){
                                            (context as BaseActivity<*>).jumpActivity<TopicListActivity>(Bundle().apply {
                                                putString("topic", toJson(allTopicList.get(i)))
                                            })
                                        }else{
                                            (context as BaseActivity<*>).jumpActivity<TopicListActivity>(Bundle().apply {
                                                putString("topic", toJson(TopicBean().apply {
                                                    id = content
                                                    topic = content
                                                }))
                                            })
                                        }
                                        PointEvent.posePoint(PointEventKey.topics_click,Bundle().apply{
                                            putString(PointValueKey.from_type,"news_page")
                                        })
                                    }
                                    tv.text = "#${content}"
                                    addView(topicView)
                                }
                                setTag(R.id.topicRoot,item.tdetai!!)
                            }
                        }
                    }
                })
            .addItemType(
                NewsData.TYPE_DETAILS_NEWS_RELATED,
                object : OnMultiItemAdapterListener<NewsData, DetailsRelatedItem> {

                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ): DetailsRelatedItem {
                        return DetailsRelatedItem(
                            parent
                        );
                    }

                    override fun onBind(holder: DetailsRelatedItem, position: Int, item: NewsData?) {
                        if (item == null || item.relatedList.isNullOrEmpty()) return
                        holder.viewBinding?.apply {
                            var tag = rvRecommend.getTag(R.id.rvRecommend) as? MutableList<NewsData>
                            if (tag.isNullOrEmpty() || tag!=item.relatedList){
                                var recommendAdapter = NewsRelatedAdapter()
                                rvRecommend.apply {
                                    layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
                                    adapter = recommendAdapter
                                    recommendAdapter.submitList(item.relatedList)
                                    addItemDecoration(object : RecyclerView.ItemDecoration() {

                                        override fun getItemOffsets(
                                            outRect: Rect,
                                            view: View,
                                            parent: RecyclerView,
                                            state: RecyclerView.State
                                        ) {
                                            super.getItemOffsets(outRect, view, parent, state)
                                            val itemPosition = parent.getChildAdapterPosition(view)
                                            if (itemPosition == item.relatedList!!.size-1){
                                                outRect.right = DisplayUtils.dp2px(
                                                    context,
                                                    14f
                                                )
                                            }
                                            outRect.left = DisplayUtils.dp2px(context, 14f)
                                        }
                                    })
                                    recommendAdapter.setOnDebouncedItemClick{adapter, view, position ->
                                        var data = recommendAdapter.items.get(position)
                                        if (data.dataType == NewsData.TYPE_NEWS){
                                            (context as BaseActivity<*>).jumpActivity<WebDetailsActivity>(Bundle().apply {
                                                putString(ParamsConfig.JSON_PARAMS, toJson(data))
                                            })
                                        }
                                    }
                                }
                                rvRecommend.setTag(R.id.rvRecommend,item.relatedList)
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
//                        super.onBind(holder, position, item, payloads)
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
                                if (item == null) return
                                holder.viewBinding?.apply{
                                    var count = JumpDataManager.getBrowserTabList(CacheManager.browserStatus,tag ="mainAdapter 获取当前tab数量").size
                                    if (count>0){
                                        tvTab.text = "${count}"
                                        tvTab.visibility = View.VISIBLE
                                    }else{
                                        tvTab.visibility = View.GONE
                                    }
                                }
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
                            var count = JumpDataManager.getBrowserTabList(CacheManager.browserStatus,tag ="mainAdapter 获取当前tab数量").size
                            if (count>0){
                                tvTab.text = "${count}"
                                tvTab.visibility = View.VISIBLE
                            }else{
                                tvTab.visibility = View.GONE
                            }
                        }
                    }
                })
            .addItemType(
                NewsData.TYPE_TOPIC_HEADER,
                object : OnMultiItemAdapterListener<NewsData, NewsTopicHeaderItem> {

                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ): NewsTopicHeaderItem {
                        return NewsTopicHeaderItem(
                            parent
                        );
                    }

                    override fun onBind(holder: NewsTopicHeaderItem, position: Int, item: NewsData?) {
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