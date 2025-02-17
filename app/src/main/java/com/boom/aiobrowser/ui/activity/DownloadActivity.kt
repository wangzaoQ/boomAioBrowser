package com.boom.aiobrowser.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.VideoActivityDownloadBinding
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
        fManager.addFragment(supportFragmentManager, HomePageDownloadFragment.newInstance(intent.getStringExtra("fromType")?:""), R.id.flSearch)
    }

    companion object{
        fun startActivity(context:BaseActivity<*>,fromType:String){
            context.jumpActivity<DownloadActivity>(Bundle().apply {
                putString("fromType",fromType)
            })
        }
    }
}