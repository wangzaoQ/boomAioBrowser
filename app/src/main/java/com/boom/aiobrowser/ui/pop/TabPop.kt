package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SizeUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.data.TabData
import com.boom.aiobrowser.databinding.BrowserPivateEmptyViewBinding
import com.boom.aiobrowser.databinding.BrowserPopSearchBinding
import com.boom.aiobrowser.databinding.BrowserPopTabBinding
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.adapter.NewsMainAdapter
import com.boom.aiobrowser.ui.adapter.SearchEngineAdapter
import com.boom.aiobrowser.ui.adapter.TabAdapter
import com.boom.base.adapter4.QuickAdapterHelper
import com.boom.base.adapter4.loadState.trailing.TrailingLoadStateAdapter
import pop.basepopup.BasePopupWindow
import pop.util.animation.AnimationHelper
import pop.util.animation.TranslationConfig

class TabPop(context: Context) : BasePopupWindow(context) {
    init {
        setContentView(R.layout.browser_pop_tab)
    }

    var popBinding: BrowserPopTabBinding? = null


    val tabAdapter by lazy {
        TabAdapter()
    }

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        popBinding = BrowserPopTabBinding.bind(contentView)
    }

    fun createPop(){
//        setPopupGravityMode(GravityMode.RELATIVE_TO_ANCHOR, GravityMode.RELATIVE_TO_ANCHOR)
//        setPopupGravity(Gravity.TOP)
//        setBackgroundColor(Color.TRANSPARENT)
        popBinding?.apply {
            rv.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                var helper = QuickAdapterHelper.Builder(tabAdapter).build()

                // 设置预加载，请调用以下方法
                // helper.trailingLoadStateAdapter?.preloadSize = 1
                adapter = helper.adapter
                // 打开空布局功能
                tabAdapter.isStateViewEnable = true
                tabAdapter.isUseStateViewSize = true
            }
            var browserStatus = CacheManager.browserStatus == 0
            tvNormal.isEnabled = browserStatus.not()
            tvPrivate.isEnabled = browserStatus
            tvNormal.setOnClickListener {
                updateStatus()
                loadNormalData()
            }
            tvPrivate.setOnClickListener {
                updateStatus()
                loadPrivateData()
            }
        }
        popBinding!!.ivAdd.setOnClickListener {
            var data = JumpData().apply {
                jumpType = JumpConfig.JUMP_HOME
                jumpTitle = context.getString(R.string.app_home)
                isCurrent = true
            }
            CacheManager.addBrowserTab(data,true)
            dismiss()
            APP.jumpLiveData.postValue(data)
        }
        showPopupWindow()
        if (CacheManager.browserStatus == 0){
            loadNormalData()
        }else{
            loadPrivateData()
        }
    }

    private fun loadPrivateData() {
        var list = CacheManager.getBrowserTabList()
        if (list.isNullOrEmpty()){
            popBinding!!.emptyView.llEmpty.visibility = View.VISIBLE
        }
        tabAdapter.submitList(list)
    }

    private fun loadNormalData() {
        popBinding!!.emptyView.llEmpty.visibility = View.GONE
        tabAdapter.submitList(CacheManager.getBrowserTabList())
    }

    private fun updateStatus() {
        popBinding?.apply {
            tvNormal.isEnabled = tvNormal.isEnabled.not()
            tvPrivate.isEnabled = tvPrivate.isEnabled.not()
            CacheManager.browserStatus = if (tvNormal.isEnabled) 1 else 0
        }
    }

    override fun onCreateShowAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withTranslation(TranslationConfig.FROM_BOTTOM)
            .toShow()
    }


    override fun onCreateDismissAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withTranslation(TranslationConfig.TO_BOTTOM)
            .toDismiss()
    }

}