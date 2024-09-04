package com.boom.aiobrowser.ui.activity.clean.load

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.CleanActivityCompleteLoadBinding
import com.boom.aiobrowser.tools.clean.formatSize
import com.boom.aiobrowser.tools.jobCancel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class CompleteLoadActivity : BaseActivity<CleanActivityCompleteLoadBinding>() {


    companion object {
        /**
         * fromType 0 clean  1 process
         */
        fun startCompleteLoadActivity(activity: BaseActivity<*>,stopNum:Long,fromType:Int){
            activity.startActivity(Intent(activity,CompleteLoadActivity::class.java).apply {
                putExtra("num",stopNum)
                putExtra("fromType",fromType)
            })
            activity.finish()
        }
    }

    override fun getBinding(inflater: LayoutInflater): CleanActivityCompleteLoadBinding {
        return CleanActivityCompleteLoadBinding.inflate(layoutInflater)
    }

    override fun setListener() {
        acBinding.ivBack.setOneClick {
            finish()
        }
    }

    var job:Job?=null

    override fun setShowView() {
        var num = intent.getLongExtra("num",0L)
        var fromType = intent.getIntExtra("fromType",0)
        if (fromType == 1){
            acBinding.tvContent.text = getString(R.string.app_process_title,"${num}")
        }else{
            if (num == 0L){
                acBinding.tvContent.text = getString(R.string.app_clean_no_junk)
            }else{
                acBinding.tvContent.text = getString(R.string.app_clean_title,num.formatSize())
            }
        }
        job = addLaunch(success = {
            delay(2000)
            CompleteActivity.startCompleteActivity(this@CompleteLoadActivity,num,fromType)
        }, failBack = {},Dispatchers.Main)
        acBinding.ivClear.run {
            visibility = View.VISIBLE
            setAnimation("loading.json")
            playAnimation()
        }
    }

    override fun onDestroy() {
        job.jobCancel()
        super.onDestroy()
    }


    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {

    }
}