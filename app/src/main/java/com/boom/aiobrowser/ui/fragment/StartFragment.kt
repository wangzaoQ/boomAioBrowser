package com.boom.aiobrowser.ui.fragment

import android.animation.ValueAnimator
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.databinding.BrowserFragmentStartBinding
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.ui.activity.MainActivity

class StartFragment :BaseFragment<BrowserFragmentStartBinding>() {
    override fun startLoadData() {

    }

    override fun setListener() {
        fBinding.btnBrowser.setOneClick {
            toMain("点击Start")
        }
    }

    private fun toMain(tag: String) {
        if (isAdded.not())return
        fBinding.llLoadingRoot.visibility = View.VISIBLE
        fBinding.rlStart.visibility = View.GONE
        startPb(time = 1000L, update = {
            fBinding.progress.progress = it
        }, complete = {
            if (rootActivity is MainActivity){
                (rootActivity as MainActivity).hideStart()
                CacheManager.isFirstStart = false
            }
        })
    }

    override fun setShowView() {
        fBinding.btnBrowser.isEnabled = fBinding.btnCheck.isChecked
        fBinding.btnCheck.setOnCheckedChangeListener { compoundButton, b ->
            fBinding.btnBrowser.isEnabled = fBinding.btnCheck.isChecked
        }
    }

    fun updateUI(intent: Intent){
        if (CacheManager.isFirstStart){
            fBinding.rlStart.visibility = View.VISIBLE
            fBinding.llLoadingRoot.visibility = View.GONE
        }else{
            toMain("非首次")
        }
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BrowserFragmentStartBinding {
        return BrowserFragmentStartBinding.inflate(inflater)
    }


    var pbAnimal: ValueAnimator? = null

    fun cancelPb() {
        pbAnimal?.cancel()
        pbAnimal = null
        firstComplete = true
    }

    var firstComplete = true

    fun startPb(cur: Int = 0, max: Int = 100, time: Long, update: (value: Int) -> Unit = {}, complete: () -> Unit = {}) {
        cancelPb()
        pbAnimal = ValueAnimator.ofInt(cur, max)
        pbAnimal?.run {
            duration = time
            interpolator = LinearInterpolator()
            addUpdateListener {
                val value = it.animatedValue as Int
                update.invoke(value)
                if (value >= max && firstComplete){
                    firstComplete = false
                    AppLogs.dLog(fragmentTAG,"complete")
                    complete.invoke()
                }
            }
        }
        pbAnimal?.start()

    }
}