package com.boom.aiobrowser.ui.adapter.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.base.BaseViewHolder
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.NewsDetailsItemTopVideoBinding
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.base.adapter4.BaseQuickAdapter
import com.boom.video.GSYVideoManager
import com.boom.video.builder.GSYVideoOptionBuilder
import com.boom.video.listener.GSYSampleCallBack


internal class TopVideoItem(parent: ViewGroup) : BaseViewHolder<NewsDetailsItemTopVideoBinding>(
    NewsDetailsItemTopVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) {

    fun bind(item: NewsData, fragment: BaseFragment<*>?, position:Int, adapter: BaseQuickAdapter<*, *>) {
        viewBinding?.apply {
            var tag = clRoot.getTag(R.id.clRoot) as? NewsData
            if (tag == null || tag != item) {
                runCatching {
                    var gsyVideoOptionBuilder = GSYVideoOptionBuilder()
                    gsyVideoOptionBuilder!!
                        .setIsTouchWiget(false) //.setThumbImageView(imageView)
                        .setUrl(item?.vbreas ?: "")
                        .setVideoTitle("")
                        .setRotateViewAuto(false)
                        .setLockLand(false)
                        .setPlayTag("videoplay")
                        .setShowFullAnimation(true)
                        .setNeedLockFull(true)
                        .setCacheWithPlay(true)
                        .setVideoAllCallBack(object : GSYSampleCallBack() {
                            override fun onPrepared(
                                url: String,
                                vararg objects: Any
                            ) {
                                super.onPrepared(url, *objects)
                            }

                            override fun onQuitFullscreen(
                                url: String,
                                vararg objects: Any
                            ) {
                                super.onQuitFullscreen(url, *objects)
                                //全屏不静音
//                    GSYVideoManager.instance().isNeedMute = true
                            }

                            override fun onEnterFullscreen(
                                url: String,
                                vararg objects: Any
                            ) {
                                super.onEnterFullscreen(url, *objects)
                                GSYVideoManager.instance().isNeedMute = false
                            }

                            override fun onAutoComplete(
                                url: String?,
                                vararg objects: Any?
                            ) {
                                super.onAutoComplete(url, *objects)
                                player?.startPlayLogic()
                            }
                        })
                        .build(player)
                    player?.apply {
                        //设置返回按键功能
//                                    backButton?.setOnClickListener(View.OnClickListener {
////                                        back()
//                                    })
//                                    //设置旋转
//                                    var  orientationUtils = OrientationUtils(context as BaseActivity<*>, player)
//                                    //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
//                                    fullscreenButton?.setOnClickListener(View.OnClickListener { // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
//                                        // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
//                                        orientationUtils?.resolveByClick();
//                                    })
                        backButton.visibility = View.GONE
                        fullscreenButton.visibility = View.GONE
                        titleTextView.visibility = View.GONE
                        loadCoverImage(
                            if (item.iassum.isNullOrEmpty()) item.vbreas
                                ?: "" else item.iassum ?: ""
                        )
                        startPlayLogic()
                    }
                    PointEvent.posePoint(PointEventKey.video_playback_page)
                }
                clRoot.setTag(R.id.clRoot, item)
            }
        }
    }
}