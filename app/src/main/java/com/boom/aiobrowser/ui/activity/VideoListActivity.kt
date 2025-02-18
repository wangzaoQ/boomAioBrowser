package com.boom.aiobrowser.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.NewsActivityVideoListBinding
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.FragmentManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.tools.video.VideoPreloadManager
import com.boom.aiobrowser.ui.fragment.video.NewsVideoFragment
import com.boom.drag.EasyFloat
import com.boom.video.GSYVideoManager

class VideoListActivity : BaseActivity<NewsActivityVideoListBinding>() {
    override fun getBinding(inflater: LayoutInflater): NewsActivityVideoListBinding {
        return NewsActivityVideoListBinding.inflate(layoutInflater)
    }

    override fun setListener() {
    }
    var enumName = ""
    override fun setShowView() {
        var manager = FragmentManager()
        var jsonString = intent.getStringExtra("data")?:""
        var index = intent.getIntExtra("index",0)
        enumName = intent.getStringExtra("enumName")?:""
        var fromType = intent.getStringExtra(PointValueKey.from_type)
        manager.addFragment(supportFragmentManager, NewsVideoFragment.newInstance(index,jsonString,enumName,fromType?:""),
            R.id.flRoot)

    }

    companion object{
        /**
         * enumName 有值则是从通知进入
         */
        fun startVideoListActivity(activity: BaseActivity<*>,index:Int,list: MutableList<NewsData>?,enumName:String,fromType:String){
            activity.jumpActivity<VideoListActivity>(Bundle().apply {
                putString("data", toJson(list))
                putInt("index", index)
                putString("enumName", enumName)
                putString(PointValueKey.from_type, fromType)
            })
        }
    }

    override fun onDestroy() {
        GSYVideoManager.releaseAllVideos()
        if (enumName.isNullOrEmpty().not()){
            APP.homeJumpLiveData.postValue(2)
        }
        EasyFloat.dismiss(tag = "download")
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