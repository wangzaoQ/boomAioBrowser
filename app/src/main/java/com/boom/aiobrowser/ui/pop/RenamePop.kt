package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.view.View
import com.blankj.utilcode.util.FileUtils
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.databinding.VideoPopRenameBinding
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
                var newName = etFile.text.toString().trim()
                var oldFile = File(data!!.downloadFilePath)
                var isSuccess = FileUtils.rename(oldFile,newName)
                if (isSuccess){
                    var newFile = File(oldFile.getParent() + File.separator + newName)
                    callBack.invoke(newFile.absolutePath)
                    dismiss()
                }
            }
        }
        setOutSideDismiss(true)
        showPopupWindow()

        return this
    }

    var data : VideoDownloadData?=null

    fun setFileData(data: VideoDownloadData) {
        this.data = data
        defaultBinding?.apply {
            etFile.setText(data.downloadFileName?:"")
        }
    }


}