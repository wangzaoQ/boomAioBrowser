package com.boom.aiobrowser.ui.fragment.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.NewsFragmentVideoListBinding
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.GlideManager
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.tools.getListByGson
import com.boom.aiobrowser.tools.jobCancel
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.tools.video.VideoPreloadManager
import com.boom.aiobrowser.tools.video.VideoPreloadManager.getCachePath
import com.boom.aiobrowser.ui.view.CustomVideoView
import com.boom.downloader.utils.VideoDownloadUtils.computeMD5
import com.boom.video.GSYVideoManager
import com.boom.video.builder.GSYVideoOptionBuilder
import com.boom.video.listener.GSYSampleCallBack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.io.File

class VideoListFragment:  BaseFragment<NewsFragmentVideoListBinding>() {
    override fun startLoadData() {

    }

    override fun setListener() {
        fBinding.ivBack.setOneClick {
            rootActivity.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        AppLogs.dLog(VideoPreloadManager.TAG,"VideoListFragment onResume currentIndex:${index}")
        playVideo()
    }

    override fun onPause() {
        super.onPause()
        AppLogs.dLog(VideoPreloadManager.TAG,"VideoListFragment onPause currentIndex:${index}")
//        stopDownLoad()
    }

    var list:MutableList<NewsData>?=null
    var index = 0
    var gsyVideoPlayer: CustomVideoView? = null
    var gsyVideoOptionBuilder: GSYVideoOptionBuilder? = GSYVideoOptionBuilder()

    override fun setShowView() {
        arguments?.apply {
            list =  getListByGson(getString("data"),NewsData::class.java)
            index = getInt("index")
        }
        var bean = list?.get(index)
        //防止错位，离开释放
        //gsyVideoPlayer.initUIState();
        bean?.apply {
            var file =  File(getCachePath(computeMD5(vbreas?:"")))
//            AppLogs.dLog(VideoCacheUtils.TAG,"播放的url:${grvoxuyD}  播放读取的缓存位置:${file.absolutePath}")
            gsyVideoPlayer = fBinding.videoItemPlayer
            if (gsyVideoPlayer == null) return
            gsyVideoOptionBuilder!!
                .setIsTouchWiget(false) //.setThumbImageView(imageView)
                .setUrl(vbreas)
                .setVideoTitle(tconsi)
                .setCacheWithPlay(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setPlayTag("videoplayer")
//            .setMapHeadData(header)
                .setShowFullAnimation(true)
                .setNeedLockFull(true)
                .setPlayPosition(index)
                .setCachePath(file)
                .setVideoAllCallBack(object : GSYSampleCallBack() {
                    override fun onPrepared(url: String, vararg objects: Any) {
                        super.onPrepared(url, *objects)
                    }

                    override fun onQuitFullscreen(url: String, vararg objects: Any) {
                        super.onQuitFullscreen(url, *objects)
                        //全屏不静音
//                    GSYVideoManager.instance().isNeedMute = true
                    }

                    override fun onEnterFullscreen(url: String, vararg objects: Any) {
                        super.onEnterFullscreen(url, *objects)
                        GSYVideoManager.instance().isNeedMute = false

                    }
                })
                .build(gsyVideoPlayer)
            gsyVideoPlayer?.setVideoAllCallBack(object : GSYSampleCallBack() {
                override fun onAutoComplete(url: String?, vararg objects: Any?) {
                    super.onAutoComplete(url, *objects)
                    gsyVideoPlayer?.startPlayLogic()
                }
            })

            //增加title
            gsyVideoPlayer!!.getTitleTextView().setVisibility(View.GONE)


            //设置返回键
            gsyVideoPlayer!!.getBackButton().setVisibility(View.GONE)
            gsyVideoPlayer!!.loadCoverImage(iassum?:"")
            fBinding.apply {
                tvTitle.text = bean.tconsi
                GlideManager.loadImg(this@VideoListFragment, ivVideoSource, bean.sschem)
                tvSourceName.text = "${bean.sfindi}"
            }
//            AppLogs.dLog(VideoCacheUtils.TAG,"current:${list!!.get(index).mLRqPtKJX}")
        }
    }

    var loadJob : Job?=null


    fun playVideo() {
        if (APP.instance.isHideSplash.not())return
        AppLogs.dLog(VideoPreloadManager.TAG,"VideoListFragment playVideo currentIndex:${index}")
        gsyVideoPlayer?.startPlayLogic()
        if(list.isNullOrEmpty())return
        if (index+1>=list!!.size)return
        loadJob?.jobCancel()
        var cacheList = mutableListOf<NewsData>()
        loadJob = rootActivity.addLaunch(success = {
            while (GSYVideoManager.instance().isPlaying.not()){
                AppLogs.dLog(VideoPreloadManager.TAG,"等待播放中")
                delay(500)
            }
            for (i in index+1 until list!!.size){
                if (i<=index+1){
                    cacheList.add(list!!.get(i))
                }else{
                    break
                }
            }
            VideoPreloadManager.serialList(1,cacheList)
        }, failBack = {
        }, Dispatchers.Main)
    }

    private fun stopDownLoad() {
        AppLogs.dLog(VideoPreloadManager.TAG, "stopDownLoad${index}")
        VideoPreloadManager.releaseAll()
//        cacheHelper.cancel()
    }


    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NewsFragmentVideoListBinding {
        return NewsFragmentVideoListBinding.inflate(layoutInflater)
    }

    companion object {
        fun newInstance(data: MutableList<NewsData>, position: Int): VideoListFragment {
            val args = Bundle()
            args.putString("data", toJson(data))
            args.putInt("index", position)
            val fragment = VideoListFragment()
            fragment.arguments = args
            return fragment
        }

    }
}