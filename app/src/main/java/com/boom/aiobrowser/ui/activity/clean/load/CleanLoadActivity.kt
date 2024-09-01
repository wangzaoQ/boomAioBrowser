package com.boom.aiobrowser.ui.activity.clean.load

import android.animation.ValueAnimator
import android.content.Intent
import android.view.LayoutInflater
import android.view.animation.LinearInterpolator
import com.blankj.utilcode.util.FileUtils
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.CleanActivityCompleteLoadBinding
import com.boom.aiobrowser.databinding.CleanActivityLoadBinding
import com.boom.aiobrowser.tools.clean.CleanConfig.cacheFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.documentCacheFiles
import com.boom.aiobrowser.tools.clean.CleanManager
import com.boom.aiobrowser.tools.clean.formatSize
import com.boom.aiobrowser.tools.rotateAnim
import com.boom.aiobrowser.ui.isAndroid11
import com.boom.aiobrowser.ui.isAndroid12
import kotlinx.coroutines.Dispatchers

class CleanLoadActivity : BaseActivity<CleanActivityLoadBinding>() {


    companion object {
        /**
         * fromType 0 clean  1 process
         */
        fun startCleanLoadActivity(activity: BaseActivity<*>,stopNum:Long){
            activity.startActivity(Intent(activity,CleanLoadActivity::class.java).apply {
                putExtra("num",stopNum)
            })
            activity.finish()
        }
    }

    override fun getBinding(inflater: LayoutInflater): CleanActivityLoadBinding {
        return CleanActivityLoadBinding.inflate(layoutInflater)
    }

    override fun setListener() {
    }
    var num = 0L
    override fun setShowView() {
        num = intent.getLongExtra("num",0L)
        acBinding.ivClean.animation = 2000L.rotateAnim()
        startSizeAnim(3000L,num)
        addLaunch(success = {
            CleanManager.deleteFile()
        }, failBack = {},Dispatchers.IO)
    }

    private var sizeAnim: ValueAnimator? = null

    private fun startSizeAnim(mDuration: Long,size:Long) {
        cancelSizeAnim()
        sizeAnim = ValueAnimator.ofInt(100, 0)
        sizeAnim?.run {
            duration = mDuration
            interpolator = LinearInterpolator()
            addUpdateListener {
                val value = it.animatedValue as Int
                val showSize = size * value / 100
                acBinding.tvMemory.text = showSize.formatSize()
                if (0 == value){
                    complete()
                }
            }
        }
        sizeAnim?.start()
    }

    private fun complete() {
        CompleteLoadActivity.startCompleteLoadActivity(this,num,0)
    }


    private fun cancelSizeAnim() {
        sizeAnim?.cancel()
        sizeAnim = null
    }
}