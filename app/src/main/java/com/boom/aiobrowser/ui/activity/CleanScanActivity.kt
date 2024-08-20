package com.boom.aiobrowser.ui.activity

import android.os.Environment
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.ScanData
import com.boom.aiobrowser.databinding.BrowserActivityCleanScanBinding
import com.boom.aiobrowser.model.CleanViewModel
import com.boom.aiobrowser.tools.clean.CleanConfig
import com.boom.aiobrowser.tools.clean.CleanConfig.downloadApks
import com.boom.aiobrowser.tools.clean.CleanConfig.junkFiles
import com.boom.aiobrowser.ui.adapter.ScanAdapter
import com.boom.base.adapter4.BaseQuickAdapter
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

class CleanScanActivity: BaseActivity<BrowserActivityCleanScanBinding>()  {

    private val viewModel by viewModels<CleanViewModel>()

    override fun getBinding(inflater: LayoutInflater): BrowserActivityCleanScanBinding {
        return BrowserActivityCleanScanBinding.inflate(layoutInflater)
    }

    override fun setListener() {
        viewModel.apply {
            currentPathLiveData.observe(this@CleanScanActivity){
                acBinding.tvPath.text = "Storage : $it"
            }
            currentSizeLiveData.observe(this@CleanScanActivity){
                acBinding.tvSize.text = it
            }
        }
    }

    val scanAdapter by lazy {
        ScanAdapter()
    }

    override fun setShowView() {
        acBinding.apply {
            rv.apply {
                layoutManager = LinearLayoutManager(this@CleanScanActivity, LinearLayoutManager.VERTICAL,false)
                // 设置预加载，请调用以下方法
                adapter = scanAdapter
                scanAdapter.setOnDebouncedItemClick{adapter, view, position ->

                }
            }
        }
        scanAdapter.submitList(mutableListOf<ScanData>().apply {
            add(ScanData().createJunkData(this@CleanScanActivity,false).apply {
                isLoading = true
            })
        })
        addLaunch(success = {
            delay(500)
            scanAdapter.add(ScanData().createApksData(this@CleanScanActivity,false).apply {
                isLoading = true
            })
            delay(500)
            scanAdapter.add(ScanData().createResidualData(this@CleanScanActivity,false).apply {
                isLoading = true
            })
            delay(500)
            scanAdapter.add(ScanData().createADData(this@CleanScanActivity,false).apply {
                isLoading = true
            })
        }, failBack = {},Dispatchers.Main)
        scanAdapter.setItemAnimation(BaseQuickAdapter.AnimationType.SlideInRight)
        viewModel.startScan(Environment.getExternalStorageDirectory(), onScanPath = {

        }, onComplete = {
            var list = mutableListOf<ScanData>()
            list.add(ScanData().createJunkData(this@CleanScanActivity).apply {
                checkedAll(true)
            })
            list.add(ScanData().createApksData(this@CleanScanActivity).apply {
                checkedAll(true)
            })
            list.add(ScanData().createResidualData(this@CleanScanActivity).apply {
                checkedAll(true)
            })
            list.add(ScanData().createADData(this@CleanScanActivity).apply {
                checkedAll(true)
            })
            scanAdapter.submitList(list)
        })
    }
}