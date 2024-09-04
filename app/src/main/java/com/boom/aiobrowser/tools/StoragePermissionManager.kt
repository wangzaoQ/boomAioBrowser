package com.boom.aiobrowser.tools


import android.content.Context
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.ui.isAndroid11
import com.boom.aiobrowser.ui.pop.StoragePop
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import java.lang.ref.WeakReference

class StoragePermissionManager(
    reference: WeakReference<BaseActivity<*>>,
    var jumpType :Int= 0,
    var onGranted: () -> Unit = {},
    var onDenied: () -> Unit = {}
) {
    val TAG = "PermissionManager:"
    private var activity: BaseActivity<*>? = null

    init {
        activity = reference.get()
    }

    fun requestStoragePermission() {
        APP.instance.isGoOther = true
        activity?.getPermission(onRequestTips = {
            StoragePop(activity!!).createPop{
                if (it == 0){
                    activity?.getManageAllFilesPermission()
                }
            }
        })
    }

    fun toOtherSetting(){
        if (activity == null)return
        APP.instance.isGoOther = true
        if (isAndroid11()){
            XXPermissions.startPermissionActivity(activity!!, Permission.MANAGE_EXTERNAL_STORAGE)
        }else{
            XXPermissions.startPermissionActivity(activity!!, Permission.Group.STORAGE)
        }
    }


    fun Context.getPermission(onRequestTips: () -> Unit = {}) {
        if (isAndroid11()) {
            if (isManageAllFilesGranted()) {
                onGranted.invoke()
                AppLogs.dLog(TAG, "MANAGE_EXTERNAL_STORAGE 已经申请")
                return
            }
            AppLogs.dLog(TAG, "MANAGE_EXTERNAL_STORAGE 未申请")
            onRequestTips.invoke()
        } else {
            getStoragePermission()
        }
    }

    fun Context.getPermissionState(): Boolean {
        if (isAndroid11()) {
            return isManageAllFilesGranted()
        } else {
            return isStoragePermissionGranted()
        }
    }

    private fun Context.getStoragePermission() {
        if (isStoragePermissionGranted()) {
            onGranted.invoke()
            return
        }
        runCatching {
            XXPermissions.with(this).permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: List<String>, all: Boolean) {
                        onGranted.invoke()
                        AppLogs.dLog(TAG, "STORAGE 通过")
                    }

                    override fun onDenied(permissions: List<String>, never: Boolean) {
                        onDenied.invoke()
                        AppLogs.dLog(TAG, "STORAGE 未通过  never--" + never)
                        if (jumpType == 1){
                            toOtherSetting()
                        }
                    }
                })
        }.onFailure {
            onDenied.invoke()
            if (jumpType == 1){
                toOtherSetting()
            }
        }
    }

    fun Context.getManageAllFilesPermission() {
        if (isManageAllFilesGranted()) {
            onGranted.invoke()
        } else {
            runCatching {
                XXPermissions.with(this).permission(Permission.MANAGE_EXTERNAL_STORAGE)
                    .request(object : OnPermissionCallback {
                        override fun onGranted(permissions: List<String>, all: Boolean) {
                            APP.instance.isGoOther = false
                            AppLogs.dLog(TAG, "MANAGE_EXTERNAL_STORAGE 通过")
                            onGranted.invoke()
                        }

                        override fun onDenied(permissions: List<String>, never: Boolean) {
//                            onDenied.invoke()
//                            if (jumpType == 1){
//                                toOtherSetting()
//                            }
                        }
                    })
            }.onFailure {
                onDenied.invoke()
                if (jumpType == 1){
                    toOtherSetting()
                }
            }
        }
    }
}