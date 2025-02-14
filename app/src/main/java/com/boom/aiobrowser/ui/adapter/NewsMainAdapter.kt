package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.view.ViewGroup
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.ViewItem
import com.boom.aiobrowser.ui.adapter.item.ADItem
import com.boom.aiobrowser.ui.adapter.item.DetailsRelatedItem
import com.boom.aiobrowser.ui.adapter.item.DownloadVideoItem
import com.boom.aiobrowser.ui.adapter.item.HomeLocalItem
import com.boom.aiobrowser.ui.adapter.item.ImgItem
import com.boom.aiobrowser.ui.adapter.item.NewsHomeItem
import com.boom.aiobrowser.ui.adapter.item.NewsHomeTopicItem
import com.boom.aiobrowser.ui.adapter.item.NewsHomeVideoItem
import com.boom.aiobrowser.ui.adapter.item.NewsItem
import com.boom.aiobrowser.ui.adapter.item.NewsItemFilm
import com.boom.aiobrowser.ui.adapter.item.NewsItemSearch
import com.boom.aiobrowser.ui.adapter.item.NewsTopicHeaderItem
import com.boom.aiobrowser.ui.adapter.item.NewsTrendingItem
import com.boom.aiobrowser.ui.adapter.item.ReadSourceItem
import com.boom.aiobrowser.ui.adapter.item.TextItem
import com.boom.aiobrowser.ui.adapter.item.TitleItem
import com.boom.aiobrowser.ui.adapter.item.TopImgItem
import com.boom.aiobrowser.ui.adapter.item.TopVideoItem
import com.boom.aiobrowser.ui.adapter.item.TopicItem
import com.boom.base.adapter4.BaseMultiItemAdapter


class NewsMainAdapter(var fragmet: BaseFragment<*>? = null) : BaseMultiItemAdapter<NewsData>() {

    init {
        addItemType(
            ViewItem.TYPE_PARENT, object : OnMultiItemAdapterListener<NewsData, NewsItem> {

                override fun onCreate(
                    context: Context,
                    parent: ViewGroup,
                    viewType: Int
                ): NewsItem {
                    return NewsItem(parent)
                }

                override fun onBind(holder: NewsItem, position: Int, item: NewsData?) {
                    item?.let { holder.bind(it, fragmet, position, this@NewsMainAdapter) }
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
                        return NewsItemSearch(parent)
                    }

                    override fun onBind(holder: NewsItemSearch, position: Int, item: NewsData?) {
                        item?.let { holder.bind(it, fragmet) }
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
                        return HomeLocalItem(parent)
                    }

                    override fun onBind(holder: HomeLocalItem, position: Int, item: NewsData?) {
                        item?.let { holder.bind(it, fragmet, position, this@NewsMainAdapter) }
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
                        item?.let { holder.bind(it, fragmet, position, this@NewsMainAdapter) }
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
                        item?.let { holder.bind(it, fragmet, position, this@NewsMainAdapter) }
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
                        item?.let { holder.bind(it, fragmet, position, this@NewsMainAdapter) }
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
                        item?.let { holder.bind(it, fragmet, position, this@NewsMainAdapter) }
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
                        item?.let { holder.bind(it, fragmet, position, this@NewsMainAdapter) }
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
                        item?.let { holder.bind(it, fragmet, position, this@NewsMainAdapter) }
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
                        item?.let { holder.bind(it, fragmet, position, this@NewsMainAdapter) }
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
                        item?.let { holder.bind(it, fragmet, position, this@NewsMainAdapter) }
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
                        item?.let { holder.bind(it, fragmet, position, this@NewsMainAdapter) }
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
                        item?.let { holder.bind(it, fragmet, position, this@NewsMainAdapter) }
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
                        item?.let { holder.bind(it, fragmet, position, this@NewsMainAdapter) }
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

                    override fun onBind(
                        holder: DetailsRelatedItem,
                        position: Int,
                        item: NewsData?
                    ) {
                        if (item == null || item.relatedList.isNullOrEmpty()) return
                        item?.let { holder.bind(it, fragmet, position, this@NewsMainAdapter) }
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
                            item?.let { holder.bind(it, fragmet, payloads[0].toString()) }
                        }
                    }


                    override fun onBind(holder: NewsHomeItem, position: Int, item: NewsData?) {
                        item?.let { holder.bind(it, fragmet) }
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

                    override fun onBind(
                        holder: NewsTopicHeaderItem,
                        position: Int,
                        item: NewsData?
                    ) {
                    }
                })
            .addItemType(
                NewsData.TYPE_DOWNLOAD_VIDEO,
                object : OnMultiItemAdapterListener<NewsData, DownloadVideoItem> {

                    override fun onCreate(
                        context: Context,
                        parent: ViewGroup,
                        viewType: Int
                    ): DownloadVideoItem {
                        return DownloadVideoItem(
                            parent
                        );
                    }

                    override fun onBind(
                        holder: DownloadVideoItem,
                        position: Int,
                        item: NewsData?
                    ) {
                        item?.let { holder.bind(it, fragmet,position,this@NewsMainAdapter) }
                    }
                })

            .onItemViewType { position, list -> list.get(position).dataType }
    }


}