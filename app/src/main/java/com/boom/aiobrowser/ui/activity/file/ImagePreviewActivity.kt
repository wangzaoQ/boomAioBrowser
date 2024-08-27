package com.boom.aiobrowser.ui.activity.file

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.blankj.utilcode.util.FileUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.FileManageData
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.databinding.FileActivityImagesBinding
import com.boom.aiobrowser.databinding.FileActivityPreviewBinding
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.clean.CleanConfig.imageFiles
import com.boom.aiobrowser.tools.shareUseIntent
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
                if(imageFiles.isNullOrEmpty())return@runCatching
                val bean = imageFiles?.get(index)
                shareUseIntent(bean?.filePath ?: "")
            }
        }
        acBinding.ivDelete.setOneClick {
            runCatching {
                if(imageFiles.isNullOrEmpty())return@runCatching
                val bean = imageFiles?.get(index)
                if (bean == null)return@runCatching
                imageFiles.remove(bean)
                FileUtils.delete(File(bean?.filePath))
                APP.deleteLiveData.postValue(HashMap<Int, Int>().apply {
                    put(FileManageData.FILE_TYPE_IMAGES,index)
                })
                finish()
            }
        }
    }

    companion object {
        fun startActivity(context: BaseActivity<*>, index: Int) {
            context.jumpActivity<ImagePreviewActivity>(Bundle().apply {
                putInt("index", index)
            })
        }
    }

    val previewAdapter by lazy { PreviewAdapter() }
    var index = 0
    override fun setShowView() {
        index = intent.getIntExtra("index",0)
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
        previewAdapter.submitList(mutableListOf<FilesData>().apply {
            add(imageFiles.get(index))
        })
    }
}