package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.view.View
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.databinding.VideoPopRenameBinding
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import pop.basepopup.BasePopupWindow
import java.io.File

class RenamePop (context: Context) : BasePopupWindow(context) {

    init {
        setContentView(R.layout.video_pop_rename)
    }

    var defaultBinding: VideoPopRenameBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = VideoPopRenameBinding.bind(contentView)
    }

    fun createPop(callBack: (type:String) -> Unit):RenamePop{
        defaultBinding?.apply {
            btnOk.setOnClickListener {
                if (data == null)return@setOnClickListener
                PointEvent.posePoint(PointEventKey.download_page_more_ra)
                var newName = etFile.text.toString().trim()
                var oldFile = File(data!!.downloadFilePath)
                var ext = FileUtils.getFileExtension(oldFile)
                var isSuccess= FileUtils.rename(oldFile, "$newName.$ext")
                if (isSuccess){
                    var newFile = File(oldFile.getParent() + File.separator + "$newName.$ext")
                    callBack.invoke(newFile.absolutePath)
                    dismiss()
                }
            }
        }
        setOutSideDismiss(true)
        showPopupWindow()
        return this
    }

    override fun dismiss() {
        KeyboardUtils.hideSoftInput(defaultBinding!!.etFile)
        super.dismiss()
    }

    var data : VideoDownloadData?=null

    fun setFileData(data: VideoDownloadData) {
        this.data = data
        defaultBinding?.apply {
            var index = data.downloadFileName.indexOf(".")
            if (index >=0){
                etFile.setText(data.downloadFileName.substring(0,index))
            }
        }
    }


}