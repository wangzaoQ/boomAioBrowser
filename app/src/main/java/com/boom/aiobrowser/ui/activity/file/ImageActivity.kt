package com.boom.aiobrowser.ui.activity.file

import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.FileManageData
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.databinding.FileActivityImagesBinding
import com.boom.aiobrowser.model.CleanViewModel
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.clean.CleanConfig.imageFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.videoFiles
import com.boom.aiobrowser.tools.clean.clickFile
import com.boom.aiobrowser.ui.adapter.PhotoAdapter
import com.boom.base.adapter4.QuickAdapterHelper
import com.boom.base.adapter4.util.setOnDebouncedItemClick

class ImageActivity : BaseActivity<FileActivityImagesBinding>() {
    override fun getBinding(inflater: LayoutInflater): FileActivityImagesBinding {
        return FileActivityImagesBinding.inflate(layoutInflater)
    }
    private val viewModel by viewModels<CleanViewModel>()

    override fun setListener() {
        acBinding.ivBack.setOneClick { finish() }
        APP.deleteLiveData.observe(this){
            photoAdapter.notifyDataSetChanged()
        }
    }

    val photoAdapter by lazy {
        PhotoAdapter()
    }

    val adapterHelper by lazy {
        QuickAdapterHelper.Builder(photoAdapter)
            .build()
    }
    var type = FileManageData.FILE_TYPE_IMAGES
    override fun setShowView() {
        type = intent.getIntExtra("fromType", FileManageData.FILE_TYPE_IMAGES)
        acBinding.apply {
            tvTitle.text = getString(if (type == FileManageData.FILE_TYPE_IMAGES) R.string.app_images else R.string.app_videos)
            rv.apply {
                layoutManager =
                    GridLayoutManager(this@ImageActivity, 4)
                // 设置预加载，请调用以下方法
//                 helper.trailingLoadStateAdapter?.preloadSize = 1
                adapter = adapterHelper.adapter
                photoAdapter.setOnDebouncedItemClick { adapter, view, position ->
                    var data = photoAdapter.items.get(position)
                    if(type == FileManageData.FILE_TYPE_IMAGES){
                        ImagePreviewActivity.startActivity(this@ImageActivity,position)
                    }else{
                        clickFile(this@ImageActivity,data!!)
                    }
                }
            }
        }
        getDataList()
    }

    fun getDataList(){
        if (type == FileManageData.FILE_TYPE_IMAGES){
            if (APP.instance.cleanComplete.not()){
                APP.scanCompleteLiveData.observe(this){
                    photoAdapter.submitList(imageFiles)
                }
            }else{
                photoAdapter.submitList(imageFiles)
            }
        }else if (type == FileManageData.FILE_TYPE_VIDEOS){
            if (APP.instance.cleanComplete.not()){
                APP.scanCompleteLiveData.observe(this){
                    photoAdapter.submitList(videoFiles)
                }
            }else{
                photoAdapter.submitList(videoFiles)
            }
        }
    }

    override fun onDestroy() {
        APP.deleteLiveData.removeObservers(this)
        APP.scanCompleteLiveData.removeObservers(this)
        super.onDestroy()
    }
}