package com.boom.aiobrowser.ui.adapter.custom

import android.content.Context
import androidx.recyclerview.widget.LinearSmoothScroller

class MySmooth2(context: Context) : LinearSmoothScroller(context) {
    companion object {
        const val SNAP_TO_CENTER = 2
    }

    override fun getHorizontalSnapPreference(): Int {
        return SNAP_TO_CENTER
    }

    override fun getVerticalSnapPreference(): Int {
        return SNAP_TO_CENTER
    }

    override fun calculateDtToFit(
        viewStart: Int,
        viewEnd: Int,
        boxStart: Int,
        boxEnd: Int,
        snapPreference: Int
    ): Int {
        when (snapPreference) {
            SNAP_TO_CENTER -> {
//                return (boxStart + boxEnd) / 2 - (viewStart + viewEnd) / 2
                return boxStart-viewStart
            }
            else -> throw IllegalArgumentException(
                "snap preference should be one of the"
                        + " constants defined in SmoothScroller, starting with SNAP_"
            )
        }
        return 0
    }


}