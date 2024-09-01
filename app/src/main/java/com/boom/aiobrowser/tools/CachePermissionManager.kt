package com.boom.aiobrowser.tools


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.text.TextUtils
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.UriUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.tools.clean.UriManager
import com.boom.aiobrowser.tools.clean.UriManager.URI_SEPARATOR
import com.boom.aiobrowser.ui.isAndroid11
import com.boom.aiobrowser.ui.isAndroid12
import com.boom.aiobrowser.ui.pop.StoragePop
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import java.lang.ref.WeakReference

class CachePermissionManager(
    reference: WeakReference<BaseActivity<*>>,
    var onGranted: () -> Unit = {},
    var onDenied: () -> Unit = {}
) {
    val TAG = "PermissionManager:"
    private var activity: BaseActivity<*>? = null

    init {
        activity = reference.get()
    }

    fun requestCachePermission(forceScan:Boolean) {
        if (activity!!.isCacheGranted()){
            onGranted.invoke()
            return
        }
        if (isAndroid12()){
            runCatching {
                XXPermissions.with(activity!!).permission(Permission.PACKAGE_USAGE_STATS)
                    .request(object : OnPermissionCallback {
                        override fun onGranted(permissions: List<String>, all: Boolean) {
                            onGranted.invoke()
                            AppLogs.dLog(TAG, "PACKAGE_USAGE_STATS 通过")
                        }

                        override fun onDenied(permissions: List<String>, never: Boolean) {
                            onDenied.invoke()
                            AppLogs.dLog(TAG, "PACKAGE_USAGE_STATS 未通过  never--" + never)
                        }
                    })
            }.onFailure {
                onDenied.invoke()
            }
        }else if (isAndroid11()){
            if (forceScan.not()){
                val uriPermissions = activity!!.contentResolver.persistedUriPermissions
                if (uriPermissions.size >0){
                    onGranted.invoke()
                    return
                }
            }
            goApplyUriPermissionPage(UriManager.URI_STORAGE_SAVED_ANRROID_DATA,activity)
        }else{
            var permissionManager = StoragePermissionManager(WeakReference(activity), onGranted = {
                onGranted.invoke()
            }, onDenied = {
                onDenied.invoke()
            })
            permissionManager.requestStoragePermission()
        }
    }


    /**
     * 跳转请求权限SAF页面
     * 注意Android 13对 Android/data目录进行了更加严格的限制已经无法获取其权限了
     * 但是可以获取其子目录权限，我们可以对其子目录进行权限申请，从而达到操作Android/data目录的目的
     * 其子目录可以通过Android/data+本机安装软件包名来获得
     * 如获取谷歌浏览器包名:com.android.chrome
     * 拼接:Android/data/com.android.chrome
     * 我们只要申请这个目录的uri权限即可操作这个目录
     * 最终跳转的uir字符串为:content://com.android.externalstorage.documents/document/primary%3AAndroid%2Fdata%2Fcom.android.chrome
     * 存储的uir字符串为:content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata%2Fcom.android.chrome
     *
     *
     * 如果你需要在Activity的onActivityResult(int requestCode, int resultCode, Intent data)中保存则使用带有Activity的方法
     * fragment反之
     *
     * @param uri      完整的请求Uri
     * @param activity
     * @param fragment
     */

    val PERMISSION_REQUEST_CODE: Int = 5411122

    fun goApplyUriPermissionPage(uri: String?, context: Context?){
        if (uri == null || context == null)return
        if (!isAndroid11()) {
            return
        }

        //获取所有已授权并存储的Uri列表，遍历并判断需要申请的uri是否在其中,在则说明已经授权了
        val isGet: Boolean = TextUtils.isEmpty(context.existsGrantedUriPermission(uri, context)).not()
        //这里会对activity重新赋值
        if (isGet) {
            onGranted.invoke()
            return
        }
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.flags = (Intent.FLAG_GRANT_READ_URI_PERMISSION
                or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
        intent.putExtra("android.provider.extra.SHOW_ADVANCED", true)
            .putExtra("android.content.extra.SHOW_ADVANCED", true)
            .putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)


//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
//        intent.setFlags(
//            Intent.FLAG_GRANT_READ_URI_PERMISSION
//                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//                    or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
//                    or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
//        )
        val documentFile = DocumentFile.fromTreeUri(activity!!, Uri.parse(uri))
        checkNotNull(documentFile)
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, documentFile.uri)

        if (context != null && context is Fragment) {
            var fragment = context as Fragment
            fragment.startActivityForResult(
                intent,
                PERMISSION_REQUEST_CODE
            )
        } else if (context != null && context is Activity){
            var activity = context as Activity
            activity.startActivityForResult(
                intent,
                PERMISSION_REQUEST_CODE
            )
        }
        return
    }

}