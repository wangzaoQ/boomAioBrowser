package com.boom.aiobrowser.ui.pop

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.animation.Animation
import androidx.appcompat.app.AlertDialog
import com.blankj.utilcode.util.FileUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.databinding.BrowserPopClearBinding
import com.boom.aiobrowser.databinding.BrowserPopEngineBinding
import com.boom.aiobrowser.databinding.FilePopEditorBinding
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.clean.removeDataByFileExt
import com.boom.aiobrowser.tools.shareUseIntent
import com.boom.aiobrowser.ui.activity.AboutActivity
import com.boom.aiobrowser.ui.activity.HistoryActivity
import com.boom.aiobrowser.ui.activity.MainActivity
import pop.basepopup.BasePopupWindow
import pop.util.animation.AnimationHelper
import pop.util.animation.TranslationConfig

class FileEditorPop(context: Context) : BasePopupWindow(context){
    init {
        setContentView(R.layout.file_pop_editor)
    }

    var defaultBinding: FilePopEditorBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = FilePopEditorBinding.bind(contentView)
    }

    fun createPop(filesData: FilesData,callBack: (type:Int) -> Unit){
        defaultBinding?.apply {
           llShare.setOnClickListener {
               (context as BaseActivity<*>).shareUseIntent(filesData?.filePath?:"")
               dismiss()
           }
            llDelete.setOnClickListener {
                callBack.invoke(1)
                dismiss()
            }
        }
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