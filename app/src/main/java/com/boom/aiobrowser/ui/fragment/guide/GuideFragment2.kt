package com.boom.aiobrowser.ui.fragment.guide

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.databinding.BrowserFragmentVideoGuide2Binding
import com.boom.aiobrowser.databinding.BrowserFragmentVideoGuideBinding

class GuideFragment2 :BaseFragment<BrowserFragmentVideoGuide2Binding>() {
    override fun startLoadData() {

    }

    override fun onResume() {
        super.onResume()
        fBinding.apply {

        }
    }

    override fun setListener() {
        APP.videoGuideLiveData.observe(this){
            if (it == 1){
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
    ): BrowserFragmentVideoGuide2Binding {
        return BrowserFragmentVideoGuide2Binding.inflate(layoutInflater)
    }

}