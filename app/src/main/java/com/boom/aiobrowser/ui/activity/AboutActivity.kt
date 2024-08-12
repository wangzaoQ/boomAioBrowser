package com.boom.aiobrowser.ui.activity

import android.content.Intent
import android.view.LayoutInflater
import androidx.recyclerview.widget.ConcatAdapter.Config
import com.boom.aiobrowser.BuildConfig
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.BrowserActivityAboutBinding
import com.boom.aiobrowser.ui.UrlConfig

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
                startActivity(Intent(this@AboutActivity,WebActivity::class.java).putExtra("url",
                    UrlConfig.PRIVATE_URL).putExtra("title",getString(R.string.app_private_policy)))
            }
            tvTermsService.setOneClick {
                startActivity(Intent(this@AboutActivity,WebActivity::class.java).putExtra("url",
                    UrlConfig.SERVICE_URL).putExtra("title",getString(R.string.app_terms_of_service)))
            }
        }
    }

    override fun setShowView() {
        acBinding.apply {
            tvVersion.text = "V ${BuildConfig.VERSION_NAME}"
        }
    }
}