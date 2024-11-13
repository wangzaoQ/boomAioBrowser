package com.boom.aiobrowser.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.databinding.BrowserActivityWebDetailsBinding
import com.boom.aiobrowser.model.NewsViewModel
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.tools.shareToShop
import com.boom.aiobrowser.tools.web.WebScan
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.ParamsConfig
import com.boom.aiobrowser.ui.adapter.NewsMainAdapter
import com.boom.aiobrowser.ui.fragment.WebFragment
import com.boom.aiobrowser.ui.pop.ClearPop
import com.boom.aiobrowser.ui.pop.DownLoadPop
import com.boom.aiobrowser.ui.pop.DownloadVideoGuidePop
import com.boom.aiobrowser.ui.pop.TabPop
import com.boom.aiobrowser.ui.pop.TipsPop
import pop.basepopup.BasePopupWindow.OnDismissListener

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
                finish()
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