package com.boom.aiobrowser.tools

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