package com.boom.aiobrowser.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.NewsActivityVideoListBinding
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.FragmentManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.tools.video.VideoPreloadManager
import com.boom.aiobrowser.ui.fragment.video.NewsVideoFragment
import com.boom.video.GSYVideoManager

class VideoListActivity : BaseActivity<NewsActivityVideoListBinding>() {
    override fun getBinding(inflater: LayoutInflater): NewsActivityVideoListBinding {
        return NewsActivityVideoListBinding.inflate(layoutInflater)
    }

    override fun setListener() {
    }

    override fun setShowView() {
        var manager = FragmentManager()
        var jsonString = intent.getStringExtra("data")?:""
        var index = intent.getIntExtra("index",0)
        manager.addFragment(supportFragmentManager, NewsVideoFragment.newInstance(index,jsonString),
            R.id.flRoot)
    }

    companion object{
        fun startVideoListActivity(activity: BaseActivity<*>,index:Int,list: MutableList<NewsData>?){
            activity.jumpActivity<VideoListActivity>(Bundle().apply {
                putString("data", toJson(list))
                putInt("index", index)
            })
        }
    }

    override fun onDestroy() {
        GSYVideoManager.releaseAllVideos()
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        AppLogs.dLog(VideoPreloadManager.TAG,"VideoListActivity onPause")
        GSYVideoManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        AppLogs.dLog(VideoPreloadManager.TAG,"VideoListActivity onResume")
        GSYVideoManager.onResume()
    }
}