package com.boom.aiobrowser.ui.pop

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.view.animation.Animation
import com.blankj.utilcode.util.FileUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.FileManageData
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.databinding.BrowserPopClearBinding
import com.boom.aiobrowser.databinding.BrowserPopStorageBinding
import com.boom.aiobrowser.databinding.VideoPopManageBinding
import com.boom.aiobrowser.tools.clean.CleanConfig.imageFiles
import pop.basepopup.BasePopupWindow
import pop.util.animation.AnimationHelper
import pop.util.animation.TranslationConfig
import java.io.File
import java.util.HashMap

class VideoMorePop(context: Context) : BasePopupWindow(context) {

    init {
        setContentView(R.layout.video_pop_manage)
    }

    var defaultBinding: VideoPopManageBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = VideoPopManageBinding.bind(contentView)
    }

    fun createPop(renameBack: (type:String) -> Unit,deleteBack: (type:String) -> Unit):VideoMorePop{
        defaultBinding?.apply {
            llRename.setOnClickListener {
                data?.apply {
                    RenamePop(context).createPop(renameBack)
                        .setFileData(this)
                }
            }
            llDelete.setOnClickListener {
                if (data == null)return@setOnClickListener
                var builder =  AlertDialog.Builder(context)
                builder.setMessage(R.string.app_delete_msg)
                builder.setCancelable(true);
                builder.setNegativeButton(context.getString(R.string.app_yes)) { dialog, which ->
                    runCatching {
                        var isSuccess = FileUtils.delete(File(data!!.downloadFilePath))
                        if (isSuccess){
                            deleteBack.invoke(data!!.downloadFilePath)
                            dismiss()
                        }
                    }
                }
                builder.setNeutralButton(context.getString(R.string.app_no)) { dialog, which ->
                    dialog.dismiss()
                }
                var dialog = builder.create()
                dialog!!.show()
            }
            llInfo.setOnClickListener {

            }
        }
        setOutSideDismiss(true)
        showPopupWindow()
        return this
    }

    var data :VideoDownloadData?=null

    fun setFileData(data: VideoDownloadData) {
        this.data = data
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