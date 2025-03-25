package com.boom.aiobrowser.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.VideoActivityDownloadBinding
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.FragmentManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.ui.fragment.HomePageDownloadFragment

class DownloadActivity : BaseActivity<VideoActivityDownloadBinding>() {

    val fManager by lazy {
        FragmentManager()
    }

    override fun getBinding(inflater: LayoutInflater): VideoActivityDownloadBinding {
        return VideoActivityDownloadBinding.inflate(layoutInflater)
    }


    override fun setListener() {

    }


    override fun setShowView() {
        fManager.addFragment(supportFragmentManager, HomePageDownloadFragment.newInstance(intent.getStringExtra("fromType")?:"",intent.getBooleanExtra("backToDownload",false)),
            R.id.flSearch)
        CacheManager.isFirstToDownload = false
    }

    companion object{
        fun startActivity(context:BaseActivity<*>,fromType:String,backToDownload:Boolean = false){
            context.jumpActivity<DownloadActivity>(Bundle().apply {
                putString("fromType",fromType)
                putBoolean("backToDownload",backToDownload)
            })
        }
    }

    override fun onBackPressed() {
        runCatching {
            var homePageDownloadFragment = supportFragmentManager.findFragmentById(R.id.flSearch)
            if (homePageDownloadFragment!=null){
                (homePageDownloadFragment as HomePageDownloadFragment).fBinding.ivBack.performClick()
            }
        }.onFailure {
            super.onBackPressed()
        }
    }
}