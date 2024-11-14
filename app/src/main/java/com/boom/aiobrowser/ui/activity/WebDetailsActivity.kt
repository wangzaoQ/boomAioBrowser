package com.boom.aiobrowser.ui.activity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.boom.aiobrowser.ui.adapter.NewsMainAdapter

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
        }
        viewModel.value.newsDetailsLiveData.observe(this){
            newsAdapter.submitList(it)
        }
    }


    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
//        super.onBackPressed()
        acBinding.ivBack.performClick()
    }

    var newData: NewsData? = null


    override fun setShowView() {
        newData = getBeanByGson(
            intent.getStringExtra(ParamsConfig.JSON_PARAMS),
            NewsData::class.java
        )
        newData?.apply {
            viewModel.value.getNewsDetails(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}