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
import com.boom.aiobrowser.tools.getNewsTopic
import com.boom.aiobrowser.ui.adapter.NewsMainAdapter
import com.boom.base.adapter4.util.addOnDebouncedChildClick
import com.boom.video.GSYVideoManager

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

    override fun setListener() {
        acBinding.apply {
            ivBack.setOneClick {
                var manager = AioADShowManager(this@WebDetailsActivity, ADEnum.INT_AD, tag = "新闻详情插屏") {
                    finish()
                }
                manager.showScreenAD(AD_POINT.aobws_news_return_int)
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
                manager.showScreenAD(AD_POINT.aobws_news_return_int)
            }
        }
        viewModel.value.newsDetailsLiveData.observe(this){
            newsAdapter.submitList(it)
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
        newData = getBeanByGson(
            intent.getStringExtra(ParamsConfig.JSON_PARAMS),
            NewsData::class.java
        )
        newData?.apply {
            viewModel.value.getNewsDetails(this)
        }
        PointEvent.posePoint(PointEventKey.news_page, Bundle().apply {
            putString(PointValueKey.news_id,newData?.itackl)
            putString(PointValueKey.news_topic,newData?.tdetai?.getNewsTopic())
        })
        CacheManager.newsReadCount += 1
        var readCount = CacheManager.newsReadCount
        if (readCount == 2 || readCount == 5 || readCount == 10 || readCount == 20){
            allowShowRate = true
        }
    }

    override fun onDestroy() {
        if (allowShowRate){
            APP.showRateLiveData.postValue(0)
        }
        GSYVideoManager.releaseAllVideos()
        super.onDestroy()
    }
}