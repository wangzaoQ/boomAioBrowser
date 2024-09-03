package com.boom.aiobrowser.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.FileManageData
import com.boom.aiobrowser.data.FileManageData.Companion.FILE_TYPE_OTHER
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.databinding.FileFragmentFileManagerBinding
import com.boom.aiobrowser.model.CleanViewModel
import com.boom.aiobrowser.tools.BigDecimalUtils
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.StoragePermissionManager
import com.boom.aiobrowser.tools.clean.CleanToolsManager.getTotalStorage
import com.boom.aiobrowser.tools.clean.CleanToolsManager.getUsedStorage
import com.boom.aiobrowser.tools.clean.clickFile
import com.boom.aiobrowser.tools.clean.formatSize
import com.boom.aiobrowser.tools.isStorageGranted
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.activity.clean.CleanScanActivity
import com.boom.aiobrowser.ui.activity.file.FileManageListActivity
import com.boom.aiobrowser.ui.activity.file.ImageActivity
import com.boom.aiobrowser.ui.adapter.FileManageAdapter
import com.boom.aiobrowser.ui.adapter.FileRecentAdapter
import com.boom.aiobrowser.ui.adapter.NewsMainAdapter
import com.boom.base.adapter4.QuickAdapterHelper
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class FileManageFragment : BaseFragment<FileFragmentFileManagerBinding>() {

    val fileManageAdapter by lazy {
        FileManageAdapter()
    }

    val fileRecentAdapter by lazy {
        FileRecentAdapter()
    }

    private val cleanViewModel by viewModels<CleanViewModel>()

    val dataList by lazy {
        mutableListOf<FileManageData>()
    }


    override fun startLoadData() {
        if (rootActivity.isStorageGranted()){
            fBinding.refresh.isRefreshing = true
            cleanViewModel.startScan(Environment.getExternalStorageDirectory())
            fBinding.rlSetting.visibility = View.GONE
            fBinding.rvRecent.visibility = View.VISIBLE
        }else{
            fBinding.rlSetting.visibility = View.VISIBLE
            fBinding.rvRecent.visibility = View.GONE
        }
    }


    override fun setListener() {
        fBinding.refresh.setOnRefreshListener {
            cleanViewModel.startScan(Environment.getExternalStorageDirectory())
        }
        fBinding.llClean.setOneClick {
            var permissionManager = StoragePermissionManager(WeakReference(rootActivity), jumpType = 1, onGranted = {
                rootActivity.jumpActivity<CleanScanActivity>()
            }, onDenied = {
            })
            permissionManager.requestStoragePermission()
        }
        fBinding.llCleanTipsRoot.setOneClick {
            var permissionManager = StoragePermissionManager(WeakReference(rootActivity), jumpType = 1,onGranted = {
                rootActivity.jumpActivity<CleanScanActivity>()
            }, onDenied = {
            })
            permissionManager.requestStoragePermission()
        }
        fBinding.rlSetting.setOneClick {
            var permissionManager = StoragePermissionManager(WeakReference(rootActivity),jumpType = 1, onGranted = {
                startLoadData()
            }, onDenied = {
            })
            permissionManager.requestStoragePermission()
        }
        cleanViewModel.recentListLiveData.observe(this){
            fBinding.rvRecent.visibility = View.VISIBLE
            var list = mutableListOf<FilesData>()
            if (it.size>=4){
                list = it.subList(0,4)
                list.add(FilesData().apply {
                    fileName = getString(R.string.app_more_recent,"${it.size}")
                    imgId = R.mipmap.ic_more_recent
                })
                fileRecentAdapter.submitList(list)
            }
        }
        fileRecentAdapter.setOnDebouncedItemClick { adapter, view, position ->
            var data = fileRecentAdapter.getItem(position)
            if (data == null)return@setOnDebouncedItemClick
            if (position == 4){
                FileManageListActivity.startActivity(rootActivity,FILE_TYPE_OTHER)
            }else{
                clickFile(rootActivity,data)
            }
        }
        APP.scanCompleteLiveData.observe(this){
//            fBinding.tvMemory.text = cleanViewModel.allFilesLength.formatSize()
            fileManageAdapter.showSize(true)
            fBinding.refresh.isRefreshing = false
        }
    }

    override fun setShowView() {
        fBinding.apply {
            rv.apply {
                layoutManager = GridLayoutManager(rootActivity, 4)
                adapter = QuickAdapterHelper.Builder(fileManageAdapter).build().adapter
            }
            rvRecent.apply {
                layoutManager = GridLayoutManager(rootActivity, 5)
                adapter = QuickAdapterHelper.Builder(fileRecentAdapter).build().adapter
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
                    var permissionManager = StoragePermissionManager(WeakReference(rootActivity), onGranted = {
                        var data = fileManageAdapter.items.get(position)
                        if (data.type == FileManageData.FILE_TYPE_IMAGES || data.type == FileManageData.FILE_TYPE_VIDEOS){
                            rootActivity.jumpActivity<ImageActivity>(Bundle().apply {
                                putInt("fromType",data.type)
                            })
                        }else{
                            FileManageListActivity.startActivity(rootActivity,data.type)
                        }
                    }, onDenied = {
                    })
                    permissionManager.requestStoragePermission()
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