package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.view.View
import android.view.animation.Animation
import androidx.recyclerview.widget.LinearLayoutManager
import com.boom.aiobrowser.BuildConfig
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.databinding.BrowserPopDefaultBinding
import com.boom.aiobrowser.databinding.VideoPopDownloadBinding
import com.boom.aiobrowser.tools.BrowserManager
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.ui.adapter.VideoDownloadAdapter
import pop.basepopup.BasePopupWindow
import pop.util.animation.AnimationHelper
import pop.util.animation.TranslationConfig

class DownLoadPop (context: Context) : BasePopupWindow(context){

    init {
        setContentView(R.layout.video_pop_download)
    }
    private val downloadAdapter by lazy {
        VideoDownloadAdapter()
    }

    var defaultBinding: VideoPopDownloadBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = VideoPopDownloadBinding.bind(contentView)
    }


    fun updateData() {
        downloadAdapter.submitList(CacheManager.videoDownloadTempList)
    }

    fun createPop(){
        defaultBinding?.apply {
            rv.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
                adapter = downloadAdapter
            }
        }
        downloadAdapter.submitList(CacheManager.videoDownloadTempList)
        setOutSideDismiss(true)
        showPopupWindow()
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