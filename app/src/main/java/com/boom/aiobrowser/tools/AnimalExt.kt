package com.boom.aiobrowser.tools

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation


fun Long.rotateAnim(): RotateAnimation = RotateAnimation(
    0f, 360f,
    Animation.RELATIVE_TO_SELF, 0.5f,
    Animation.RELATIVE_TO_SELF, 0.5f
).apply {
    duration = this@rotateAnim
    repeatCount = Animation.INFINITE
    interpolator = LinearInterpolator()
    fillAfter = true
}

fun View.translationY(start:Float,end: Float) {
//    if(start<end){
//        return
//    }
    var animator2 = ObjectAnimator.ofFloat(this, "translationY",start,end)
    animator2.setDuration(5000L)
    animator2.start()
}

