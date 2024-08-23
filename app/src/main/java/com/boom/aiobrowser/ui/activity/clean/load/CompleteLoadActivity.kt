package com.boom.aiobrowser.ui.activity.clean.load

import android.content.Intent
import android.view.LayoutInflater
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.CleanActivityCompleteLoadBinding
import com.boom.aiobrowser.tools.clean.formatSize

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

    override fun setShowView() {
        var num = intent.getLongExtra("num",0L)
        var fromType = intent.getIntExtra("fromType",0)
        if (fromType == 1){
            acBinding.tvContent.text = getString(R.string.app_process_title,num)
        }else{
            acBinding.tvContent.text = getString(R.string.app_clean_title,num.formatSize())
        }
        acBinding.root.postDelayed({
            CompleteActivity.startCompleteActivity(this@CompleteLoadActivity,num,fromType)
        },2000)
    }
}