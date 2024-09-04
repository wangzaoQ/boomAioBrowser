package com.boom.aiobrowser.ui.activity.file

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.blankj.utilcode.util.FileUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.FileManageData
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.databinding.FileActivityImagesBinding
import com.boom.aiobrowser.databinding.FileActivityPreviewBinding
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.clean.CleanConfig.imageFiles
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.tools.shareUseIntent
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.adapter.PreviewAdapter
import java.io.File
import java.util.HashMap

class ImagePreviewActivity: BaseActivity<FileActivityPreviewBinding>() {
    override fun getBinding(inflater: LayoutInflater): FileActivityPreviewBinding {
        return FileActivityPreviewBinding.inflate(layoutInflater)
    }

    override fun setListener() {
        acBinding.ivBack.setOneClick {
            finish()
        }
        acBinding.ivShare.setOneClick {
            runCatching {
                if (filesData == null){
                    if(imageFiles.isNullOrEmpty())return@runCatching
                    val bean = imageFiles?.get(index)
                    shareUseIntent(bean?.filePath ?: "")
                }else{
                    shareUseIntent(filesData!!.filePath )
                }
            }
        }
        acBinding.ivDelete.setOneClick {
            var builder =  AlertDialog.Builder(this@ImagePreviewActivity)
            builder.setMessage(R.string.app_delete_msg)
            builder.setCancelable(true);
            builder.setNegativeButton(getString(R.string.app_yes)) { dialog, which ->
                runCatching {
                    if (filesData == null){
                        if(imageFiles.isNullOrEmpty())return@runCatching
                        val bean = imageFiles?.get(index)
                        if (bean == null)return@runCatching
                        imageFiles.remove(bean)
                        FileUtils.delete(File(bean?.filePath))
                        APP.deleteLiveData.postValue(HashMap<Int, Int>().apply {
                            put(FileManageData.FILE_TYPE_IMAGES,index)
                        })
                    }else{
                        var index = -1
                        for (i in 0 until imageFiles.size){
                            if (imageFiles.get(i).filePath == filesData!!.filePath){
                                index = i
                                break
                            }
                        }
                        imageFiles.removeAt(index)
                        FileUtils.delete(File(filesData!!.filePath))
                        APP.deleteLiveData2.postValue(filesData!!.filePath)
                    }
                    finish()
                }
            }
            builder.setNeutralButton(getString(R.string.app_no)) { dialog, which ->
                dialog.dismiss()
            }
            var dialog = builder.create()
            dialog!!.show()

        }
    }

    companion object {
        /**
         * 列表跳转
         */
        fun startActivity(context: BaseActivity<*>, index: Int) {
            context.jumpActivity<ImagePreviewActivity>(Bundle().apply {
                putInt("index", index)
            })
        }

        /**
         * 单独的image跳转
         */
        fun startActivity(context: BaseActivity<*>, filesData: FilesData) {
            context.jumpActivity<ImagePreviewActivity>(Bundle().apply {
                putString("filesData", toJson(filesData))
            })
        }
    }

    val previewAdapter by lazy { PreviewAdapter() }
    var index = 0
    var filesData :FilesData ?= null
    override fun setShowView() {
        index = intent.getIntExtra("index",0)
        filesData = getBeanByGson(intent.getStringExtra("filesData"),FilesData::class.java)
        acBinding.vp.apply {
            adapter = previewAdapter
            setCurrentItem(index ?: 0, false)
            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                }

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
//                    index = position
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                }
            })
        }
        if (filesData == null){
            previewAdapter.submitList(mutableListOf<FilesData>().apply {
                add(imageFiles.get(index))
            })
        }else{
            previewAdapter.submitList(mutableListOf<FilesData>().apply {
                add(filesData!!)
            })
        }
    }
}