package com.boom.aiobrowser.ui.activity.clean.load

import android.view.LayoutInflater
import androidx.activity.viewModels
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.CleanActivityLoadBinding
import com.boom.aiobrowser.databinding.FileActivityProcessBinding
import com.boom.aiobrowser.model.ProcessDataModel
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.jobCancel
import com.boom.aiobrowser.ui.activity.clean.ProcessActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class ProcessLoadActivity: BaseActivity<FileActivityProcessBinding>() {
    private val viewModel by viewModels<ProcessDataModel>()

    override fun getBinding(inflater: LayoutInflater): FileActivityProcessBinding {
        return FileActivityProcessBinding.inflate(layoutInflater)
    }

    override fun setListener() {
        acBinding.ivBack.setOneClick {
            finish()
        }
    }

    var job:Job?=null
    override fun setShowView() {
        acBinding.processLoad.apply {
            setAnimation("process_load.json")
            playAnimation()
        }
        viewModel.getProcessData()
        job = addLaunch(success = {
            delay(3000L)
            jumpActivity<ProcessActivity>()
            finish()
        }, failBack = {},Dispatchers.Main)
    }

    override fun onDestroy() {
        job?.jobCancel()
        super.onDestroy()
    }
}