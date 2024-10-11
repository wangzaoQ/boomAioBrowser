package com.boom.aiobrowser.ui.activity

import android.content.pm.ActivityInfo
import android.view.LayoutInflater
import android.view.View
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.VideoActivityPreviewBinding
import com.boom.aiobrowser.ui.view.CustomVideoView
import com.boom.video.GSYVideoManager
import com.boom.video.builder.GSYVideoOptionBuilder
import com.boom.video.listener.GSYSampleCallBack
import com.boom.video.utils.OrientationUtils
import java.io.File

class VideoPreActivity :BaseActivity<VideoActivityPreviewBinding>(){
    override fun getBinding(inflater: LayoutInflater): VideoActivityPreviewBinding {
        return VideoActivityPreviewBinding.inflate(layoutInflater)
    }

    override fun setListener() {
    }
    var gsyVideoOptionBuilder: GSYVideoOptionBuilder? = GSYVideoOptionBuilder()
    var gsyVideoPlayer: CustomVideoView? = null

    var orientationUtils: OrientationUtils? = null

    override fun setShowView() {
       var videoPath =  intent.getStringExtra("video_path")
        gsyVideoPlayer = acBinding.videoItemPlayer
        var file = File(videoPath)
        gsyVideoOptionBuilder!!
            .setIsTouchWiget(false) //.setThumbImageView(imageView)
            .setUrl(videoPath)
            .setVideoTitle(file.name)
//            .setCacheWithPlay(true)
            .setRotateViewAuto(false)
            .setLockLand(false)
            .setPlayTag("videoplay")
//            .setMapHeadData(header)
            .setShowFullAnimation(true)
            .setNeedLockFull(true)
            .setCacheWithPlay(false)
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

                override fun onAutoComplete(url: String?, vararg objects: Any?) {
                    super.onAutoComplete(url, *objects)
                    gsyVideoPlayer?.startPlayLogic()
                }
            })
            .build(gsyVideoPlayer)
        //设置返回按键功能
        gsyVideoPlayer?.getBackButton()?.setOnClickListener(View.OnClickListener { onBackPressed() })


        //设置旋转
        orientationUtils = OrientationUtils(this, gsyVideoPlayer)

        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        gsyVideoPlayer?.getFullscreenButton()?.setOnClickListener(View.OnClickListener { // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
                // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
                orientationUtils?.resolveByClick();
            })

        //增加title
        gsyVideoPlayer!!.getTitleTextView().setVisibility(View.GONE)


        //设置返回键
//        gsyVideoPlayer!!.getBackButton().setVisibility(View.GONE)
        gsyVideoPlayer!!.loadCoverImage(file.absolutePath)
        gsyVideoPlayer?.startPlayLogic()

    }


    override fun onBackPressed() {
///       不需要回归竖屏
        if (orientationUtils?.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            gsyVideoPlayer?.getFullscreenButton()?.performClick();
            return;
        }else{
            //释放所有
            gsyVideoPlayer?.setVideoAllCallBack(null)
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        orientationUtils?.releaseListener()
        GSYVideoManager.releaseAllVideos()
        super.onDestroy()
    }
}