package com.boom.aiobrowser.tools.clean

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.clean.FileFilter.isApk
import com.boom.aiobrowser.tools.clean.FileFilter.isImage
import com.boom.aiobrowser.tools.openFile
import com.boom.aiobrowser.ui.activity.file.ImagePreviewActivity

fun toAppDetails(activity: Activity, pkgName: String) {
    runCatching {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$pkgName")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        activity.startActivity(intent)
        APP.instance.isGoOther = true
    }.onFailure {
        ToastUtils.showShort(it.message)
    }
}

fun clickFile(activity: BaseActivity<*>,filesData: FilesData){
    var extension = FileUtils.getFileExtension(filesData.filePath)
    if (extension.isImage()){
        ImagePreviewActivity.startActivity(activity,filesData)
    }else {
        activity.openFile(filesData.filePath)
    }
}