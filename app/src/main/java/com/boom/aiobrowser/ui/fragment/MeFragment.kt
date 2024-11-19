package com.boom.aiobrowser.ui.fragment

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.databinding.NewsFragmentMeBinding
import com.boom.aiobrowser.other.ShortManager
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.tools.BrowserManager
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.ui.activity.AboutActivity
import com.boom.aiobrowser.ui.activity.DownloadActivity
import com.boom.aiobrowser.ui.activity.HistoryActivity
import com.boom.aiobrowser.ui.pop.ClearPop
import com.boom.aiobrowser.ui.pop.DefaultPop
import com.boom.aiobrowser.ui.pop.TabPop
import pop.basepopup.BasePopupWindow.OnDismissListener

class MeFragment : BaseFragment<NewsFragmentMeBinding>() {
    override fun startLoadData() {

    }

    override fun setListener() {
        fBinding?.apply {
            llNewTab.setOnClickListener {
                showTabPop()
                PointEvent.posePoint(PointEventKey.profile_newtab)
            }
            llClearData.setOnClickListener {
                clearData()
                PointEvent.posePoint(PointEventKey.profile_cleardate)
            }
            llHistory.setOnClickListener {
                if (context is BaseActivity<*>) {
                    (context as BaseActivity<*>).startActivity(
                        Intent(
                            context,
                            HistoryActivity::class.java
                        )
                    )
                }
                PointEvent.posePoint(PointEventKey.profile_history)
            }
            llWidget.setOnClickListener {
                PointEvent.posePoint(PointEventKey.profile_add_widget)
                ShortManager.addWidgetToLaunch(rootActivity, true)
            }
            llAbout.setOnClickListener {
                if (context is BaseActivity<*>) {
                    (context as BaseActivity<*>).startActivity(
                        Intent(
                            context,
                            AboutActivity::class.java
                        )
                    )
                }
                PointEvent.posePoint(PointEventKey.profile_about)
            }
            llDownload.setOnClickListener {
                if (context is BaseActivity<*>) {
                    (context as BaseActivity<*>).startActivity(
                        Intent(
                            context,
                            DownloadActivity::class.java
                        ).apply {
                            putExtra("fromPage", "home_more_pop")
                        })
                }
                PointEvent.posePoint(PointEventKey.profile_download)
            }
            updateUI()
        }
    }

    fun updateUI() {
        fBinding?.apply {
            var isDefault = BrowserManager.isDefaultBrowser()
            if (APP.instance.clickSetBrowser) {
                APP.instance.clickSetBrowser = false
                PointEvent.posePoint(if (isDefault) PointEventKey.default_pop_set_s else PointEventKey.default_pop_set_f)
            }
            if (isDefault) {
                llBrowser.visibility = View.GONE
                viewLine.visibility = View.GONE
            } else {
                llBrowser.visibility = View.VISIBLE
                viewLine.visibility = View.VISIBLE
                switchBrowser.setChecked(isDefault)
                switchBrowser.isClickable = false
                llBrowser.setOnClickListener {
                    PointEvent.posePoint(PointEventKey.profile_setdefault)
                    if (isDefault.not()) {
                        var pop = DefaultPop(rootActivity)
                        pop.createPop()
                    }
                }
            }
        }
    }

    fun clearData() {
        ClearPop(rootActivity).createPop {
            CacheManager.clearAll()
            JumpDataManager.toMain()
        }
    }

    fun showTabPop() {
        var tabPop = TabPop(rootActivity)
        tabPop.createPop()
        tabPop.setOnDismissListener(object : OnDismissListener() {
            override fun onDismiss() {
            }
        })
    }

    override fun setShowView() {
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NewsFragmentMeBinding {
        return NewsFragmentMeBinding.inflate(layoutInflater)
    }
}