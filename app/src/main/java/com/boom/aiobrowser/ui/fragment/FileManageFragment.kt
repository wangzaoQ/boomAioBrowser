package com.boom.aiobrowser.ui.fragment

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.FileManageData
import com.boom.aiobrowser.databinding.FileFragmentFileManagerBinding
import com.boom.aiobrowser.model.CleanViewModel
import com.boom.aiobrowser.tools.BigDecimalUtils
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.clean.CleanToolsManager.getTotalStorage
import com.boom.aiobrowser.tools.clean.CleanToolsManager.getUsedStorage
import com.boom.aiobrowser.tools.clean.formatSize
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.activity.clean.CleanScanActivity
import com.boom.aiobrowser.ui.activity.file.ImageActivity
import com.boom.aiobrowser.ui.adapter.FileManageAdapter
import com.boom.aiobrowser.ui.adapter.NewsMainAdapter
import com.boom.base.adapter4.QuickAdapterHelper
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FileManageFragment : BaseFragment<FileFragmentFileManagerBinding>() {

    val fileManageAdapter by lazy {
        FileManageAdapter()
    }
    private val cleanViewModel by viewModels<CleanViewModel>()

    val dataList by lazy {
        mutableListOf<FileManageData>()
    }

    override fun startLoadData() {
        cleanViewModel.startScan(Environment.getExternalStorageDirectory())
    }

    override fun setListener() {
        fBinding.llClean.setOneClick {
            rootActivity.jumpActivity<CleanScanActivity>()
        }
        fileManageAdapter.setOnDebouncedItemClick{adapter, view, position ->
            var data = fileManageAdapter.items.get(position)
        }
    }

    override fun setShowView() {
        fBinding.apply {
            rv.apply {
                layoutManager = GridLayoutManager(rootActivity, 4)
                rv.adapter = QuickAdapterHelper.Builder(fileManageAdapter).build().adapter
            }
        }
        rootActivity.addLaunch(success = {
            var useStorage = getUsedStorage(rootActivity)
            var totalStorage = getTotalStorage(rootActivity)
            var content = "${useStorage.formatSize()}/${totalStorage.formatSize()}"
            dataList.add(FileManageData.createManageData(FileManageData.FILE_TYPE_DOWNLOADS))
            dataList.add(FileManageData.createManageData(FileManageData.FILE_TYPE_LARGE_FILE))
            dataList.add(FileManageData.createManageData(FileManageData.FILE_TYPE_IMAGES))
            dataList.add(FileManageData.createManageData(FileManageData.FILE_TYPE_VIDEOS))
            dataList.add(FileManageData.createManageData(FileManageData.FILE_TYPE_APKS))
            dataList.add(FileManageData.createManageData(FileManageData.FILE_TYPE_MUSIC))
            dataList.add(FileManageData.createManageData(FileManageData.FILE_TYPE_ZIP))
            dataList.add(FileManageData.createManageData(FileManageData.FILE_TYPE_DOCUMENTS))
            withContext(Dispatchers.Main){
                fBinding.apply {
                    var num = BigDecimalUtils.mul(BigDecimalUtils.div("$useStorage","$totalStorage"),100.0).toInt()
                    progress.progress = num
                    tvStorageInfo.text = content
                    tvStorage.text = useStorage.formatSize()
                }
                fileManageAdapter.setOnDebouncedItemClick { adapter, view, position ->
                    var data = fileManageAdapter.items.get(position)
                    if (data.type == FileManageData.FILE_TYPE_IMAGES || data.type == FileManageData.FILE_TYPE_VIDEOS){
                        rootActivity.jumpActivity<ImageActivity>(Bundle().apply {
                            putInt("fromType",data.type)
                        })
                    }
                }
                fileManageAdapter.submitList(dataList)
            }
        }, failBack = {},Dispatchers.IO)
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FileFragmentFileManagerBinding {
        return FileFragmentFileManagerBinding.inflate(layoutInflater)
    }
}