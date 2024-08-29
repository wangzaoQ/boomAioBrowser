package com.boom.aiobrowser.tools


import android.content.Context
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.base.BaseActivity
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

    fun requestCachePermission() {
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

        }
    }

}