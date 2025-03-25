package com.boom.aiobrowser.ui.fragment.guide

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADShowManager
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.databinding.BrowserFragmentVideoGuide3Binding
import com.boom.aiobrowser.databinding.BrowserFragmentVideoGuideBinding
import com.boom.aiobrowser.other.JumpConfig
import com.boom.aiobrowser.point.AD_POINT
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.tools.JumpDataManager

class GuideFragment3 :BaseFragment<BrowserFragmentVideoGuide3Binding>() {
    override fun startLoadData() {

    }

    override fun setListener() {
        APP.videoGuideLiveData.observe(this){
            if (it == 2){
                fBinding.apply {
                    rlGuide1.visibility = View.VISIBLE
                    val scaleXAnimator = ObjectAnimator.ofFloat(ivGuideAnimal, "scaleX", 1.0f, 1.6f)
                    val scaleYAnimator = ObjectAnimator.ofFloat(ivGuideAnimal, "scaleY", 1.0f, 1.6f)
                    val alphaAnimator = ObjectAnimator.ofFloat(ivGuideAnimal, "alpha", 1.0f, 0.0f)
                    val set = AnimatorSet()
                    set.play(scaleXAnimator).with(scaleYAnimator).with(alphaAnimator)
                    set.setDuration(2000)
                    set.start()
                    set.addListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(p0: Animator) {

                        }

                        override fun onAnimationEnd(p0: Animator) {
                            rlGuide1.visibility = View.GONE
                        }

                        override fun onAnimationCancel(p0: Animator) {
                        }

                        override fun onAnimationRepeat(p0: Animator) {
                        }
                    })
                }
            }
        }
        fBinding.tvCommit.setOnClickListener {
            var manager = AioADShowManager(rootActivity, ADEnum.INT_AD, tag = "教程点击Try Now 增加广告") {
                PointEvent.posePoint(PointEventKey.download_tutorial_try)
                var data = JumpDataManager.getCurrentJumpData(tag = "DownloadVideoGuidePop guide")
                data.jumpType = JumpConfig.JUMP_WEB
                data.jumpUrl = rootActivity.getString(R.string.video_local_title)
                data.jumpTitle = rootActivity.getString(R.string.video_local_title)
//                data.jumpUrl = "https://www.pexels.com/videos"
                JumpDataManager.updateCurrentJumpData(data,tag = "DownloadVideoGuidePop guide")
                APP.jumpLiveData.postValue(data)
                JumpDataManager.closeAll()
                PointEvent.posePoint(PointEventKey.tutorial_webpage)
                APP.videoGuideLiveData.postValue(10)
            }
            manager.showScreenAD(AD_POINT.aobws_downguide_int)
        }
        fBinding.ivLeft.setOnClickListener {
            APP.videoGuideLiveData.postValue(11)
        }
    }

    override fun setShowView() {
        fBinding.apply {

        }
    }


    override fun onDestroy() {
        APP.videoGuideLiveData.removeObservers(this)
        super.onDestroy()
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BrowserFragmentVideoGuide3Binding {
        return BrowserFragmentVideoGuide3Binding.inflate(layoutInflater)
    }

}