package com.boom.aiobrowser.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.LinearLayoutCompat
import com.boom.aiobrowser.tools.AppLogs

class BottomRootView(context: Context, attrs: AttributeSet) : LinearLayoutCompat(context,attrs) {
    private var scrollBack: (position: Int) -> Unit?={}
    var lastX = 0
    var lastY = 0
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val rawX = event.rawX.toInt()
        val rawY = event.rawY.toInt()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 记录触摸点坐标
                lastX = rawX
                lastY = rawY
                // 保证子View能够接收到Action_move事件
                parent.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_MOVE -> {
//                dealtX = Math.abs(x - dealtX)
//                dealtY = Math.abs(y - dealtY)
                var  offsetX = rawX - lastX;
                var  offsetY = rawY - lastY;
                // 在当前left,top,right,bottom的基础上加上偏移量
                AppLogs.dLog("BottomRootView", "dealtX:=$offsetX ")
                AppLogs.dLog("BottomRootView", "dealtY:=$offsetY ")
                // 这里是够拦截的判断依据是左右滑动，读者可根据自己的逻辑进行是否拦截
                if (offsetY >= offsetX) {
//                    if (offsetY>200)
//                    layout(getLeft(),
//                        getTop() + offsetY,
//                        getRight(),
//                        getBottom() + offsetY)
                    parent.requestDisallowInterceptTouchEvent(true)
                    scrollBack.invoke(offsetY)
                } else {
                    parent.requestDisallowInterceptTouchEvent(false)
                }

            }

            MotionEvent.ACTION_CANCEL -> {}
            MotionEvent.ACTION_UP -> {}
        }
        return super.dispatchTouchEvent(event)
    }
//
//
//    // 绝对坐标方式
//    override fun onTouchEvent(event: MotionEvent): Boolean {
//
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//
//            }
//
//            MotionEvent.ACTION_MOVE -> {
//                // 计算偏移量
//                val offsetX: Int = rawX - lastX
//                val offsetY: Int = rawY - lastY
//                // 在当前left,top,right,bottom的基础上加上偏移量
//                layout(
//                    left + offsetX,
//                    top + offsetY,
//                    right + offsetX,
//                    bottom + offsetY
//                )
//                // 重新设置初始坐标
//                lastX = rawX
//                lastY = rawY
//            }
//        }
//        return true
//    }


    fun addScrollBack(scrollBack: (position:Int)-> Unit){
        this.scrollBack = scrollBack
    }


}