package com.boom.aiobrowser.ui.activity

import android.content.Intent
import android.view.LayoutInflater
import com.boom.aiobrowser.BuildConfig
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADShowManager
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.BrowserActivityAboutBinding
import com.boom.aiobrowser.other.UrlConfig
import com.boom.aiobrowser.point.AD_POINT

class AboutActivity: BaseActivity<BrowserActivityAboutBinding>()  {
    override fun getBinding(inflater: LayoutInflater): BrowserActivityAboutBinding {
        return BrowserActivityAboutBinding.inflate(layoutInflater)
    }

    override fun setListener() {
        acBinding.ivBack.setOneClick {
            var manager = AioADShowManager(this, ADEnum.INT_AD, tag = "appAbout"){
                finish()
            }
            manager.showScreenAD(AD_POINT.aobws_return_int)
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

    override fun onBackPressed() {
        acBinding.ivBack.performClick()
    }

    override fun setShowView() {
        acBinding.apply {
            tvVersion.text = "V ${BuildConfig.VERSION_NAME}"
        }
    }
}