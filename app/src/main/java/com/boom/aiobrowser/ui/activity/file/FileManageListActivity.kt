package com.boom.aiobrowser.ui.activity.file

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.FileUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.FileManageData
import com.boom.aiobrowser.databinding.FileActivityImagesBinding
import com.boom.aiobrowser.databinding.FileActivityListManageBinding
import com.boom.aiobrowser.model.CleanViewModel
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.clean.CleanConfig
import com.boom.aiobrowser.tools.clean.CleanConfig.apkFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.audioFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.documentsFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.downloadFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.imageFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.largeFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.recentFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.videoFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.zipFiles
import com.boom.aiobrowser.tools.clean.clickFile
import com.boom.aiobrowser.tools.clean.removeDataByFileExt
import com.boom.aiobrowser.ui.adapter.FileListAdapter
import com.boom.aiobrowser.ui.pop.FileEditorPop
import com.boom.base.adapter4.QuickAdapterHelper
import com.boom.base.adapter4.util.addOnDebouncedChildClick
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import kotlinx.coroutines.Dispatchers

class FileManageListActivity : BaseActivity<FileActivityListManageBinding>() {

    private val viewModel by viewModels<CleanViewModel>()

    companion object {
        fun startActivity(context: BaseActivity<*>, type: Int) {
            context.jumpActivity<FileManageListActivity>(Bundle().apply {
                putInt("fromType", type)
            })
        }
    }

    override fun getBinding(inflater: LayoutInflater): FileActivityListManageBinding {
        return FileActivityListManageBinding.inflate(layoutInflater)
    }

    override fun setListener() {
        acBinding.ivBack.setOneClick { finish() }
        APP.deleteLiveData2.observe(this){
            runCatching {
                for (i in 0 until fileListAdapter.items.size){
                    var data = fileListAdapter.items.get(i)
                    if (data.filePath == it){
                        fileListAdapter.remove(data)
                        break
                    }
                }
            }.onFailure {
                AppLogs.eLog(acTAG,it.stackTraceToString())
            }
        }
    }

    val fileListAdapter by lazy {
        FileListAdapter()
    }

    val adapterHelper by lazy {
        QuickAdapterHelper.Builder(fileListAdapter)
            .build()
    }

    var fromType = FileManageData.FILE_TYPE_DOWNLOADS

    override fun setShowView() {
        fromType = intent.getIntExtra("fromType", FileManageData.FILE_TYPE_DOWNLOADS)
        acBinding.apply {
            rv.apply {
                layoutManager = LinearLayoutManager(
                    this@FileManageListActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                adapter = adapterHelper.adapter
                fileListAdapter.setOnDebouncedItemClick { adapter, view, position ->
                    var data = adapter.getItem(position)
                    if (data == null)return@setOnDebouncedItemClick
                    clickFile(this@FileManageListActivity,data!!)
                }
                fileListAdapter.addOnDebouncedChildClick(R.id.ivMore) { adapter, view, position ->
                    var data = adapter.getItem(position)
                    if (data == null)return@addOnDebouncedChildClick
                    FileEditorPop(this@FileManageListActivity).createPop(data) {
                        if (it == 1){
                            var builder =  AlertDialog.Builder(this@FileManageListActivity)
                            builder.setMessage(R.string.app_delete_msg)
                            builder.setCancelable(true);
                            builder.setNegativeButton(context.getString(R.string.app_yes)) { dialog, which ->
                                runCatching {
                                    FileUtils.delete(data.filePath)
                                    data.filePath.removeDataByFileExt()
                                }.onFailure {
                                    AppLogs.eLog("FileEditorPop",it.stackTraceToString())
                                }
                                APP.deleteLiveData2.postValue(data!!.filePath)
                            }
                            builder.setNeutralButton(context.getString(R.string.app_no)) { dialog, which ->
                            }
                            var dialog = builder.create()
                            dialog!!.show()
                        }
                    }
                }
            }
        }
        getDataList()
    }

    private fun updateList() {
        when (fromType) {
            FileManageData.FILE_TYPE_LARGE_FILE-> {
                acBinding.tvTitle.text = getString(R.string.app_large_file)
                fileListAdapter.submitList(largeFiles)
            }
            FileManageData.FILE_TYPE_APKS-> {
                acBinding.tvTitle.text = getString(R.string.app_apks)
                fileListAdapter.submitList(apkFiles)
            }
            FileManageData.FILE_TYPE_MUSIC-> {
                acBinding.tvTitle.text = getString(R.string.app_music)
                fileListAdapter.submitList(audioFiles)
            }
            FileManageData.FILE_TYPE_ZIP-> {
                acBinding.tvTitle.text = getString(R.string.app_zip)
                fileListAdapter.submitList(zipFiles)
            }
            FileManageData.FILE_TYPE_DOCUMENTS-> {
                acBinding.tvTitle.text = getString(R.string.app_documents)
                fileListAdapter.submitList(documentsFiles)
            }
            FileManageData.FILE_TYPE_OTHER->{
                acBinding.tvTitle.text = getString(R.string.app_recently)
                fileListAdapter.submitList(recentFiles)
            }
            FileManageData.FILE_TYPE_DOWNLOADS->{
                acBinding.tvTitle.text = getString(R.string.app_downloads)
                fileListAdapter.submitList(downloadFiles)
            }
            else -> {}
        }
    }

    fun getDataList(){
        if (APP.instance.cleanComplete.not()){
            APP.scanCompleteLiveData.observe(this){
                updateList()
            }
        }else{
            updateList()
        }
    }

    override fun onDestroy() {
        APP.scanCompleteLiveData.removeObservers(this)
        APP.deleteLiveData2.removeObservers(this)
        super.onDestroy()
    }
}