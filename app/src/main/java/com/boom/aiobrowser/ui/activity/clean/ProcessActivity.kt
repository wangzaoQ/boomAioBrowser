package com.boom.aiobrowser.ui.activity.clean

import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.CleanActivityProcessBinding
import com.boom.aiobrowser.model.ProcessDataModel
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.BigDecimalUtils
import com.boom.aiobrowser.tools.clean.CleanConfig
import com.boom.aiobrowser.tools.clean.CleanToolsManager
import com.boom.aiobrowser.tools.clean.CleanToolsManager.getUsedMemoryPercent
import com.boom.aiobrowser.tools.clean.formatSize
import com.boom.aiobrowser.tools.clean.toAppDetails
import com.boom.aiobrowser.ui.activity.clean.load.CompleteLoadActivity
import com.boom.aiobrowser.ui.adapter.ProcessAdapter
import com.boom.base.adapter4.util.addOnDebouncedChildClick
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.Dispatchers

class ProcessActivity : BaseActivity<CleanActivityProcessBinding>() {

    private val viewModel by viewModels<ProcessDataModel>()


    override fun getBinding(inflater: LayoutInflater): CleanActivityProcessBinding {
        return CleanActivityProcessBinding.inflate(layoutInflater)
    }
    var absVerticalOffset = 0

    var firstNum = 0
    var endNum = 0
    var firstInit = true

    override fun setListener() {
        acBinding.ivBack.setOneClick {
            finish()
        }
        viewModel.processListLiveData.observe(this){
            acBinding.refreshLayout.isRefreshing = false
            processAdapter.submitList(it)
            if (firstInit){
                firstInit = false
                firstNum = it.size
                endNum = it.size
            }else{
                endNum = it.size
            }
        }
        acBinding.mainAppBar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                absVerticalOffset = Math.abs(verticalOffset) //AppBarLayout竖直方向偏移距离px
                if (absVerticalOffset == 0)return
                val totalScrollRange = appBarLayout!!.totalScrollRange //AppBarLayout总的距离px
                var offset = BigDecimalUtils.mul(BigDecimalUtils.div(255.toDouble(), totalScrollRange.toDouble(),1),absVerticalOffset.toDouble()).toInt()
//                var offset = absVerticalOffset / 2
//                offset = 255 - offset
                AppLogs.dLog("onOffsetChanged", "offset=$offset")
                if (offset > 255) {
                    offset = 255
                } else if (offset <= 0) {
                    offset = 0
                }
                if (offset <10) {
                    acBinding.mainCl.alpha = 1f
                    acBinding.tvTitle.alpha = 0f
                } else {
                    val div = BigDecimalUtils.div(offset.toDouble(), 255.0, 2)
                    AppLogs.dLog("onOffsetChanged", "div=$div")
                    acBinding.tvTitle.alpha = div.toFloat()
                    acBinding.mainCl.alpha = 1-div.toFloat()
                }
            }
        })
        acBinding.refreshLayout.setOnRefreshListener{
            viewModel.getProcessData()
        }
        acBinding.cleanButton.setOneClick {
            addLaunch(success = {
                CleanToolsManager.cleanBackgroundProcess()
            }, failBack = {
                AppLogs.eLog(acTAG,it)
            },Dispatchers.IO)
            CompleteLoadActivity.startCompleteLoadActivity(this@ProcessActivity,(firstNum-endNum).toLong(),1)
        }
    }

    val processAdapter by lazy {
        ProcessAdapter()
    }

    override fun setShowView() {
        acBinding.apply {
            rv.apply {
                layoutManager =
                    LinearLayoutManager(this@ProcessActivity, LinearLayoutManager.VERTICAL, false)
                // 设置预加载，请调用以下方法
                adapter = processAdapter
                processAdapter.setOnDebouncedItemClick { adapter, view, position ->
                    var data = processAdapter.getItem(position)
                    if (data == null) {
                        return@setOnDebouncedItemClick
                    }
                }
                processAdapter.addOnDebouncedChildClick(R.id.tvStop) { adapter, view, position ->
                    var data = processAdapter.getItem(position)
                    if (data == null ) {
                        return@addOnDebouncedChildClick
                    }
                    toAppDetails(this@ProcessActivity,data.pkg)
                }
            }
            tvSize.text = "${getUsedMemoryPercent()}%"
            tvTitle.text = "${getUsedMemoryPercent()}%"
            progress.progress = getUsedMemoryPercent()
            var info = CleanToolsManager.getMemoryInfo()
            tvMemory.text = "${info.availMem.formatSize()}/${info.totalMem.formatSize()}"
        }
    }

    var isFirst = true

    override fun onResume() {
        super.onResume()
        if (isFirst.not()||CleanConfig.runningAppInfo.isNullOrEmpty()){
            viewModel.getProcessData()
        }else{
            viewModel.processListLiveData.postValue(CleanConfig.runningAppInfo)
            isFirst = false
        }
    }
}