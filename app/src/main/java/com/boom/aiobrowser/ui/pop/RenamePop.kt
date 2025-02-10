package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.view.View
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.databinding.VideoPopRenameBinding
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.tools.download.DownloadCacheManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
                (context as BaseActivity<*>).addLaunch(success = {
                    var model = DownloadCacheManager.queryDownloadModel(data!!)
                    model?.fileName = newName
                    DownloadCacheManager.updateModel(model!!)
                    withContext(Dispatchers.Main){
                        callBack.invoke(newName)
                        dismiss()
                    }
                }, failBack = {})
//                var oldFile = File(data!!.downloadFilePath)
//                var ext = FileUtils.getFileExtension(oldFile)
//                var isSuccess= FileUtils.rename(oldFile, "$newName.$ext")
//                if (isSuccess){
//                    var newFile = File(oldFile.getParent() + File.separator + "$newName.$ext")
//                    callBack.invoke(newFile.absolutePath)
//                    dismiss()
//                }
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
            etFile.setText(data.fileName?:"")
        }
    }


}