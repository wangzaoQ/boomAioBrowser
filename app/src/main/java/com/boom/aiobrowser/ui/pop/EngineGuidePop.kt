package com.boom.aiobrowser.ui.pop

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SizeUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.BrowserPopDefaultBinding
import com.boom.aiobrowser.databinding.BrowserPopEngineBinding
import com.boom.aiobrowser.ui.adapter.SearchEngineAdapter
import com.boom.base.adapter4.QuickAdapterHelper
import pop.basepopup.BasePopupWindow

class EngineGuidePop(context: Context) : BasePopupWindow(context) {
    init {
        setContentView(R.layout.browser_pop_engine)
    }

    var defaultBinding: BrowserPopEngineBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = BrowserPopEngineBinding.bind(contentView)
    }

    fun createPop(view:View){
        // 这里是位置显示方式,在屏幕的侧
//        var location = IntArray(2)
//        view.getLocationOnScreen(location);
//
//        val x = Math.abs(popupWindow.getContentView().getMeasuredWidth()-view.getWidth()) / 2;
//        val y = -(popupWindow.getContentView().getMeasuredHeight()+view.getHeight());

        setPopupGravityMode(GravityMode.RELATIVE_TO_ANCHOR, GravityMode.RELATIVE_TO_ANCHOR)
        setPopupGravity(Gravity.TOP)
        setBackgroundColor(Color.TRANSPARENT)
        setOffsetX(SizeUtils.dp2px(-19f))
        setOffsetY(SizeUtils.dp2px(-2f))
        showPopupWindow(view)
        val scaleXAnimator = ObjectAnimator.ofFloat(defaultBinding!!.root, "scaleX", 1.0f, 1.1f,1.0f)
        val scaleYAnimator = ObjectAnimator.ofFloat(defaultBinding!!.root, "scaleY", 1.0f, 1.1f,1.0f)
        val set = AnimatorSet()
        set.play(scaleXAnimator).with(scaleYAnimator)
        set.setDuration(3000)
        set.start()
        set.addListener(object :AnimatorListener{
            override fun onAnimationStart(p0: Animator) {

            }

            override fun onAnimationEnd(p0: Animator) {
                dismiss()
            }

            override fun onAnimationCancel(p0: Animator) {
            }

            override fun onAnimationRepeat(p0: Animator) {
            }

        })
    }

}