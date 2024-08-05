package com.boom.aiobrowser.ui.activity

import android.content.Intent
import android.view.LayoutInflater
import com.boom.aiobrowser.BuildConfig
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.BrowserActivityAboutBinding

class AboutActivity: BaseActivity<BrowserActivityAboutBinding>()  {
    override fun getBinding(inflater: LayoutInflater): BrowserActivityAboutBinding {
        return BrowserActivityAboutBinding.inflate(layoutInflater)
    }

    override fun setListener() {
        acBinding.ivBack.setOneClick {
            finish()
        }
        acBinding.apply {
            tvPrivate.setOneClick {
//                startActivity(Intent(this@AboutActivity,WebActivity::class.java).putExtra("url",))
            }
        }
    }

    override fun setShowView() {
        acBinding.apply {
            tvVersion.text = "V ${BuildConfig.VERSION_NAME}"
        }
    }
}