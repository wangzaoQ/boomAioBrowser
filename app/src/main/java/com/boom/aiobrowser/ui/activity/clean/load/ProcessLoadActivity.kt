package com.boom.aiobrowser.ui.activity.clean.load

import android.view.LayoutInflater
import androidx.activity.viewModels
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.CleanActivityLoadBinding
import com.boom.aiobrowser.databinding.FileActivityProcessBinding
import com.boom.aiobrowser.model.ProcessDataModel
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.ui.activity.clean.ProcessActivity

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

    override fun setShowView() {
        acBinding.processLoad.apply {
            setAnimation("process_load.json")
            playAnimation()
        }
        viewModel.getProcessData()
        acBinding.root.postDelayed({
            jumpActivity<ProcessActivity>()
            finish()
        },3000L)
    }
}