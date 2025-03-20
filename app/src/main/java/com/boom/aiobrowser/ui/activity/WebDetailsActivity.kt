package com.boom.aiobrowser.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADDataManager
import com.boom.aiobrowser.ad.AioADShowManager
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.BrowserActivityWebDetailsBinding
import com.boom.aiobrowser.model.NewsViewModel
import com.boom.aiobrowser.point.AD_POINT
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.tools.shareToShop
import com.boom.aiobrowser.other.ParamsConfig
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.PointsManager
import com.boom.aiobrowser.tools.UIManager
import com.boom.aiobrowser.tools.getNewsTopic
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.adapter.NewsMainAdapter
import com.boom.base.adapter4.util.addOnDebouncedChildClick
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import com.boom.video.GSYVideoManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class WebDetailsActivity : BaseActivity<BrowserActivityWebDetailsBinding>() {

    private val viewModel by lazy {
        viewModels<NewsViewModel>()
    }

    override fun getBinding(inflater: LayoutInflater): BrowserActivityWebDetailsBinding {
        return BrowserActivityWebDetailsBinding.inflate(layoutInflater)
    }

    val newsAdapter by lazy {
        NewsMainAdapter()
    }

    var page = 1

    override fun setListener() {
        acBinding.apply {
            ivBack.setOneClick {
                var manager = AioADShowManager(this@WebDetailsActivity, ADEnum.INT_AD, tag = "新闻详情插屏") {
                    finish()
                }
                manager.showScreenAD(AD_POINT.aobws_return_int)
            }
            newsRv.apply {
                layoutManager = LinearLayoutManager(
                    this@WebDetailsActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                adapter = newsAdapter
            }
            ivShare.setOneClick {
                shareToShop(newData?.tconsi?:"")
            }

            newsAdapter.addOnDebouncedChildClick(R.id.tvReadSource) { adapter, view, position ->
                var manager = AioADShowManager(this@WebDetailsActivity, ADEnum.INT_AD, tag = "readSource插屏") {
                    var data = adapter.getItem(position) ?: return@AioADShowManager
                    this@WebDetailsActivity.startActivity(
                        Intent(
                            this@WebDetailsActivity,
                            WebActivity::class.java
                        ).putExtra("url", data.uweek)
                    )
                }
                manager.showScreenAD(AD_POINT.aobws_news_int)
            }
        }
        viewModel.value.newsDetailsLiveData.observe(this){
            newsAdapter.submitList(it)
            viewModel.value.getNewsRelated(newData)
            page = 1
            viewModel.value.getNewsLike()
            acBinding.newsSmart.setEnableLoadMore(true)
            acBinding.newsSmart.finishRefresh()
        }
        viewModel.value.failLiveData.observe(this){
            acBinding.newsSmart.finishRefresh()
            acBinding.newsSmart.finishLoadMore()
        }
        viewModel.value.newsRelatedLiveData.observe(this){
            var index = -1
            for (i in 0 until newsAdapter.mutableItems.size){
                var data = newsAdapter.mutableItems.get(i)
                if (data.dataType == NewsData.TYPE_DETAILS_NEWS_RELATED){
                    index = i
                    break
                }
            }
            if (index>=0){
                var data = newsAdapter.mutableItems.get(index)
                data.relatedList = it
                newsAdapter.notifyItemChanged(index)
            }
        }
        viewModel.value.newsRecommendLiveData.observe(this){
            newsAdapter.addAll(it)
            acBinding.newsSmart.finishLoadMore()
        }
        acBinding.newsSmart.setOnLoadMoreListener {
            page++
            viewModel.value.getNewsLike()
        }
        acBinding.newsSmart.setOnRefreshListener {
            page = 1
            newData?.apply {
                viewModel.value.getNewsDetails(this)
            }
        }

        newsAdapter.setOnDebouncedItemClick{adapter, view, position ->
            if (position>newsAdapter.items.size-1)return@setOnDebouncedItemClick
            var data = newsAdapter.items.get(position)
            if (data.dataType == NewsData.TYPE_NEWS || data.dataType == NewsData.TYPE_DETAILS_NEWS_RELATED){
                var manager = AioADShowManager(this@WebDetailsActivity, ADEnum.INT_AD, tag = "新闻详情相关推荐") {
                    jumpActivity<WebDetailsActivity>(Bundle().apply {
                        putString(ParamsConfig.JSON_PARAMS, toJson(data))
                    })
                }
                manager.showScreenAD(AD_POINT.aobws_newsclick_int)
            }
            if (data.dataType == NewsData.TYPE_NEWS) {
                PointEvent.posePoint(PointEventKey.news_page_like,Bundle().apply {
                    putString(PointValueKey.news_id,data.itackl)
                    putString(PointValueKey.news_topic,newData?.tdetai?.getNewsTopic())
                })
            }else if (data.dataType == NewsData.TYPE_DETAILS_NEWS_RELATED){
                PointEvent.posePoint(PointEventKey.news_page_related,Bundle().apply {
                    putString(PointValueKey.news_id,data.itackl)
                    putString(PointValueKey.news_topic,newData?.tdetai?.getNewsTopic())
                })
            }
        }
    }

    override fun onPause() {
        super.onPause()
        GSYVideoManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        GSYVideoManager.onResume()
    }


    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
//        super.onBackPressed()
        acBinding.ivBack.performClick()
    }

    var newData: NewsData? = null

    var allowShowRate = false

    override fun setShowView() {
        AioADDataManager.preloadAD(ADEnum.BANNER_AD_NEWS_DETAILS_TOP,"新闻详情页展示时")
        AioADDataManager.preloadAD(ADEnum.BANNER_AD_NEWS_DETAILS,"新闻详情页展示时")

        newData = getBeanByGson(
            intent.getStringExtra(ParamsConfig.JSON_PARAMS),
            NewsData::class.java
        )
        acBinding.newsSmart.autoRefresh()
        PointEvent.posePoint(PointEventKey.news_page, Bundle().apply {
            putString(PointValueKey.news_id,newData?.itackl)
            putString(PointValueKey.news_topic,newData?.tdetai?.getNewsTopic())
        })
        CacheManager.newsReadCount += 1
        var readCount = CacheManager.newsReadCount
        if (readCount == 2 || readCount == 5 || readCount == 10 || readCount == 20){
            allowShowRate = true
        }
        addLaunch(success = {
            while (getActivityStatus().not()){
                delay(1000)
            }
            withContext(Dispatchers.Main){
                if (UIManager.isSpecialUsers().not()){
                    PointEvent.posePoint(PointEventKey.aobws_ad_chance, Bundle().apply {
                        putString(PointValueKey.ad_pos_id, ADEnum.BANNER_AD_NEWS_DETAILS_TOP.adName)
                    })
                }
                AioADShowManager(this@WebDetailsActivity,ADEnum.BANNER_AD_NEWS_DETAILS_TOP,"详情页原生/banner"){

                }.showNativeAD(acBinding.flRoot,ADEnum.BANNER_AD_NEWS_DETAILS_TOP.adName)
            }
        }, failBack = {})
        PointsManager.readNews(newData?.itackl?:"")
    }

    override fun onDestroy() {
        if (allowShowRate){
            APP.showRateLiveData.postValue(0)
        }
        GSYVideoManager.releaseAllVideos()
        super.onDestroy()
    }
}