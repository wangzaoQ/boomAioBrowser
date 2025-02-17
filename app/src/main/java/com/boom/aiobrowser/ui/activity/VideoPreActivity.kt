package com.boom.aiobrowser.ui.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADDataManager
import com.boom.aiobrowser.ad.AioADShowManager
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.databinding.VideoActivityPreviewBinding
import com.boom.aiobrowser.point.AD_POINT
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.JumpDataManager.getCurrentJumpData
import com.boom.aiobrowser.tools.JumpDataManager.updateCurrentJumpData
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.tools.getUrlIcon
import com.boom.aiobrowser.tools.getUrlSource
import com.boom.aiobrowser.other.JumpConfig
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.view.CustomVideoView
import com.boom.video.GSYVideoManager
import com.boom.video.builder.GSYVideoOptionBuilder
import com.boom.video.listener.GSYSampleCallBack
import com.boom.video.listener.GSYStateUiListener
import com.boom.video.utils.OrientationUtils
import com.boom.video.video.base.GSYVideoView.CURRENT_STATE_PAUSE
import java.io.File

class VideoPreActivity :BaseActivity<VideoActivityPreviewBinding>(){


    companion object{
        fun startVideoPreActivity(context:BaseActivity<*>,data:VideoDownloadData){
//            var manager = AioADShowManager(context as BaseActivity<*>, ADEnum.INT_AD, tag = "播放视频") {
//
//            }
//            manager.showScreenAD(AD_POINT.aobws_play_int)
            context.jumpActivity<VideoPreActivity>(Bundle().apply {
                putString("video_path", toJson(data))
            })
        }
    }

    override fun getBinding(inflater: LayoutInflater): VideoActivityPreviewBinding {
        return VideoActivityPreviewBinding.inflate(layoutInflater)
    }

    override fun setListener() {
    }
    var gsyVideoOptionBuilder: GSYVideoOptionBuilder? = GSYVideoOptionBuilder()
    var gsyVideoPlayer: CustomVideoView? = null

    var orientationUtils: OrientationUtils? = null

    var showAd = false

    /**
     * /storage/emulated/0/Android/data/com.boom.aiobrowser/files/Video/Download/tiktok_1731163898235/tiktok_1731163898235.video
     */
    override fun setShowView() {
       var data  = getBeanByGson(intent.getStringExtra("video_path"),VideoDownloadData::class.java)
        gsyVideoPlayer = acBinding.videoItemPlayer
        runCatching {
            var file = File(data?.downloadFilePath?:"")
            gsyVideoOptionBuilder!!
                .setIsTouchWiget(false) //.setThumbImageView(imageView)
                .setUrl(data?.downloadFilePath)
                .setVideoTitle(file.name)
//            .setCacheWithPlay(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setPlayTag("videoplay")
//            .setMapHeadData(header)
                .setShowFullAnimation(true)
                .setNeedLockFull(true)
                .setCacheWithPlay(false)
//                .setGSYStateUiListener(object : GSYStateUiListener {
//                    override fun onStateChanged(state: Int) {
//                        when (state) {
//                            CURRENT_STATE_PAUSE -> {
//                                if (showAd)return
//                                acBinding.flRoot.visibility = View.VISIBLE
//                                acBinding.ivClose.visibility = View.VISIBLE
//                                acBinding.ivClose.setOneClick {
//                                    acBinding.flRoot.visibility = View.GONE
//                                    acBinding.ivClose.visibility = View.GONE
//                                }
//                                if (AioADDataManager.adFilter1().not()) {
//                                    showAd = true
//                                    PointEvent.posePoint(PointEventKey.aobws_ad_chance, Bundle().apply {
//                                        putString(PointValueKey.ad_pos_id, AD_POINT.aobws_play_bnat)
//                                    })
//                                    val data = AioADDataManager.getCacheAD(ADEnum.BANNER_AD_NEWS_DETAILS)
//                                    data?.apply {
//                                        AioADShowManager(this@VideoPreActivity, ADEnum.BANNER_AD_NEWS_DETAILS,"播放视频"){
//                                        }.showNativeAD(acBinding.flRoot,AD_POINT.aobws_play_bnat)
//                                    }
//                                }
//                            }
//                            else -> {
//                                showAd = false
//                                acBinding.flRoot.visibility = View.GONE
//                                acBinding.ivClose.visibility = View.GONE
//                            }
//                        }
//                    }
//                })
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
            gsyVideoPlayer?.getBackButton()?.setOnClickListener(View.OnClickListener {
                back()
            })


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
            PointEvent.posePoint(PointEventKey.video_playback_page)
            gsyVideoPlayer!!.setSourceIcon(getUrlIcon(data?.url?:"")){
                var jumpData = getCurrentJumpData(tag = "视频页点击来源").apply {
                    jumpType = JumpConfig.JUMP_WEB
                    jumpUrl = getUrlSource(data?.url?:"")
                }
                updateCurrentJumpData(jumpData,tag = "视频页点击来源")
                APP.jumpLiveData.postValue(jumpData)
                JumpDataManager.closeAll()
            }
        }
    }


    override fun onBackPressed() {
///       不需要回归竖屏
        back()
    }

    private fun back() {
        if (orientationUtils?.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            gsyVideoPlayer?.getFullscreenButton()?.performClick();
            return;
        }else{
            PointEvent.posePoint(PointEventKey.video_playback_return)
            var manager = AioADShowManager(this@VideoPreActivity, ADEnum.INT_AD, tag = "插屏") {
                //释放所有
                gsyVideoPlayer?.setVideoAllCallBack(null)
                finish()
            }
            manager.showScreenAD(AD_POINT.aobws_return_int)
        }
    }

    override fun onDestroy() {
        orientationUtils?.releaseListener()
        GSYVideoManager.releaseAllVideos()
        super.onDestroy()
    }
}