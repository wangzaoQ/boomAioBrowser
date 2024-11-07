package com.boom.aiobrowser.ui.pop

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import com.blankj.utilcode.util.SizeUtils
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.BrowserVideoFeedbackBinding
import com.boom.aiobrowser.databinding.VideoPopFirstDownloadBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import pop.basepopup.BasePopupWindow

class FirstDownloadTips(context: Context) : BasePopupWindow(context) {

    init {
        setContentView(R.layout.video_pop_first_download)
    }

    var defaultBinding: VideoPopFirstDownloadBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = VideoPopFirstDownloadBinding.bind(contentView)
    }
    fun createPop(view:View,type:Int){
        // 这里是位置显示方式,在屏幕的侧
//        var location = IntArray(2)
//        view.getLocationOnScreen(location);
//
//        val x = Math.abs(popupWindow.getContentView().getMeasuredWidth()-view.getWidth()) / 2;
//        val y = -(popupWindow.getContentView().getMeasuredHeight()+view.getHeight());

        if (type == 1){
            setPopupGravityMode(GravityMode.RELATIVE_TO_ANCHOR, GravityMode.RELATIVE_TO_ANCHOR)
            setPopupGravity(Gravity.TOP)
            setBackgroundColor(Color.TRANSPARENT)
            defaultBinding?.apply {
                tvTips.text = context.getString(R.string.app_download_tips1)
            }
        }else if (type == 2){
//            setPopupGravityMode(GravityMode.RELATIVE_TO_ANCHOR, GravityMode.RELATIVE_TO_ANCHOR)
            setPopupGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL)
            setBackgroundColor(Color.TRANSPARENT)
            defaultBinding?.apply {
                tvTips.text = context.getString(R.string.app_download_tips2)
            }
        }else if (type == 3){
            setPopupGravityMode(GravityMode.RELATIVE_TO_ANCHOR, GravityMode.RELATIVE_TO_ANCHOR)
            setPopupGravity(Gravity.TOP)
            setBackgroundColor(Color.TRANSPARENT)
            defaultBinding?.apply {
                tvTips.text = context.getString(R.string.app_download_tips3)
            }
        }

        setTouchable(false)
        setOutSideDismiss(true)
        showPopupWindow(view)
        val scaleXAnimator = ObjectAnimator.ofFloat(defaultBinding!!.root, "scaleX", 0.8f, 1.1f,1.0f)
        val scaleYAnimator = ObjectAnimator.ofFloat(defaultBinding!!.root, "scaleY", 0.8f, 1.1f,1.0f)
        val set = AnimatorSet()
        set.play(scaleXAnimator).with(scaleYAnimator)
        set.setDuration(2000)
        set.start()
        set.addListener(object : AnimatorListener {
            override fun onAnimationStart(p0: Animator) {

            }

            override fun onAnimationEnd(p0: Animator) {
            }

            override fun onAnimationCancel(p0: Animator) {
            }

            override fun onAnimationRepeat(p0: Animator) {
            }

        })
        (context as BaseActivity<*>).addLaunch(success = {
            delay(3000)
            withContext(Dispatchers.Main){
                dismiss()
            }
        }, failBack = {})
    }
}