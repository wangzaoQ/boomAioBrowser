package com.boom.aiobrowser.tools.clean

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.FileUtils
import java.io.File

object UriManager {
    var DEFAULT_ROOTPATH = Environment.getExternalStorageDirectory().getAbsolutePath()
    var PATH_ANRROID_DATA = DEFAULT_ROOTPATH + "/Android/data"
    var PATH_ANRROID_OBB = DEFAULT_ROOTPATH + "/Android/obb"

    //(/storage/emulated/0/Android/data/)存储后的uri
    val URI_STORAGE_SAVED_ANRROID_DATA = "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata"
    val URI_STORAGE_SAVED_ANRROID_DATA_MEDIA = "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia"

    //(/storage/emulated/0/Android/obb/)存储后的uri
    val URI_STORAGE_SAVED_ANRROID_OBB =
        "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fobb"

    //获取(/storage/emulated/0/Android/data/)目录权限跳转的uri
    val URI_STORAGE_JUMP_ANRROID_DATA =
        "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3AAndroid%2Fdata"

    //获取(/storage/emulated/0/Android/obb/)目录权限跳转的uri
    val URI_STORAGE_JUMP_ANRROID_OBB =
        "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3AAndroid%2Fobb"

    //uri请求权限构建前缀
    val URI_PERMISSION_REQUEST_PREFIX = "com.android.externalstorage.documents"

    //uri请求权限构建完整前缀
    val URI_PERMISSION_REQUEST_COMPLETE_PREFIX = "content://com.android.externalstorage.documents"

    //uri请求权限构建后缀主要特殊符号
    val URI_PERMISSION_REQUEST_SUFFIX_SPECIAL_SYMBOL = "primary:"

    //uri路径分割符
    val URI_SEPARATOR = "%2F"


    /**
     * 获取Android/Data下的软件包名
     *
     * @param context
     * @return
     */
    fun getAndroidDataPackageNames(context: Context?): List<String>? {
        if (context == null)return null
        // 得到PackageManager对象
        val pm = context.packageManager
        val packageNameList: MutableList<String> = ArrayList()
        val intent = Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfos = pm.queryIntentActivities(intent, 0)
        var packageName: String
        for (resolveInfo in resolveInfos) {
            packageName = resolveInfo.activityInfo.packageName
            if (FileUtils.isFileExists(PATH_ANRROID_DATA + File.separator + packageName)) {
                packageNameList.add(packageName)
            }
        }
        return packageNameList
    }

    /**
     * path转uri
     *
     * @param path /storage/emulated/0/Android/data/moli/m3d/m5.txt
     * @param tree false
     * @return content://com.android.externalstorage.documents/document/primary%3AAndroid%2Fdata%2Fmoli%2Fm3d%2Fm5.txt
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun path2Uri(path: String, tree: Boolean): Uri? {
        /**
         * DocumentsContract.buildTreeDocumentUri():
         * content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata
         * DocumentsContract.buildDocumentUri():
         * content://com.android.externalstorage.documents/document/primary%3AAndroid%2Fdata
         */
        val uriSuf = URI_PERMISSION_REQUEST_SUFFIX_SPECIAL_SYMBOL + path.replaceFirst(
            (DEFAULT_ROOTPATH + File.separator).toRegex(),
            ""
        )
        val uri: Uri
        uri = if (tree) {
            DocumentsContract.buildTreeDocumentUri(URI_PERMISSION_REQUEST_PREFIX, uriSuf)
        } else {
            DocumentsContract.buildDocumentUri(URI_PERMISSION_REQUEST_PREFIX, uriSuf)
        }
        return uri
    }
}