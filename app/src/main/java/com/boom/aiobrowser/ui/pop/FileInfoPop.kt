package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.view.View
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.TimeUtils
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.databinding.BrowserPopFileInfoBinding
import com.boom.aiobrowser.tools.GlideManager
import com.boom.aiobrowser.tools.TimeManager
import com.boom.aiobrowser.tools.clean.formatSize
import pop.basepopup.BasePopupWindow
import java.io.File

class FileInfoPop(context: Context): BasePopupWindow(context) {
    init {
        setContentView(R.layout.browser_pop_file_info)
    }

    var defaultBinding: BrowserPopFileInfoBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = BrowserPopFileInfoBinding.bind(contentView)
    }

    fun createPop(videoData: VideoDownloadData) {
        setOutSideDismiss(true)
        showPopupWindow()
        defaultBinding?.apply {
            GlideManager.loadImg(
                null,
                ivVideo,
                videoData.downloadFilePath,
                0,
                R.mipmap.ic_default_download,
                0
            )
            tvFileName.text = videoData.fileName
            tvFileSize.text = videoData.size?.formatSize()
            tvFileTime.text =
                TimeManager.getVideoTime(File(videoData.downloadFilePath).lastModified())
        }
    }
}