package com.boom.aiobrowser.tools

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.provider.Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.base.BaseActivity


object BrowserManager {

    fun isDefaultBrowser(): Boolean {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://"))
        val resolveInfo: ResolveInfo? = APP.instance.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfo != null && APP.instance.getPackageName().equals(resolveInfo.activityInfo.packageName)
//        return isDefaultBrowserSet(APP.instance)
    }

    fun isDefaultBrowserSet(context: Context): Boolean {
        val actionIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://"))
        val resolvedActivities = context.packageManager.queryIntentActivities(actionIntent, 0)

        var isDefault = false
        for (info in resolvedActivities) {
            if (APP.instance.getPackageName().equals(info.activityInfo.packageName)) {
                isDefault = true
                break
            }
        }

        return isDefault
    }

    fun promptToSetDefaultBrowser(context: Context) {
        runCatching {
            val intent = Intent(ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
            context.startActivity(intent)
        }
    }


    fun setDefaultBrowser(context: Context, packageName: String?) {
        runCatching {
            if (Build.VERSION.SDK_INT < 29) {
                (context as BaseActivity<*>).startActivityForResult(
                    Intent("android.settings.MANAGE_DEFAULT_APPS_SETTINGS"),
                    100
                )
            } else {
                val roleManager = if (context != null) context.getSystemService(
                    RoleManager::class.java
                ) else null
                if (!roleManager!!.isRoleAvailable("android.app.role.BROWSER")) {
                    (context as BaseActivity<*>).startActivityForResult(
                        Intent("android.settings.MANAGE_DEFAULT_APPS_SETTINGS"),
                        70
                    )
                } else if (roleManager!!.isRoleHeld("android.app.role.BROWSER")) {
                    (context as BaseActivity<*>).startActivityForResult(
                        Intent("android.settings.MANAGE_DEFAULT_APPS_SETTINGS"),
                        70
                    )
                } else {
                    (context as BaseActivity<*>).startActivityForResult(
                        roleManager!!.createRequestRoleIntent("android.app.role.BROWSER"),
                        100
                    )
                }
            }
        }.onFailure {
            promptToSetDefaultBrowser(context)
        }


//        context.startActivity(createIntent(context))
//        try {
//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.setData(Uri.parse("http://"))
//
//            // 设置FLAG_ACTIVITY_NEW_TASK，确保在新的任务栈中启动Activity
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//
//            // 通过指定的packageName启动Activity
//            val componentName = ComponentName(packageName!!, "com.android.permissioncontroller.role.ui.DefaultAppActivity")
//            intent.setComponent(componentName)
//            context.startActivity(intent)
//
//            // 设置该应用为默认浏览器
//            context.packageManager.setComponentEnabledSetting(
//                componentName,
//                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                PackageManager.DONT_KILL_APP
//            )
//        } catch (e: ActivityNotFoundException) {
//            // 处理异常情况，例如应用未找到
//            promptToSetDefaultBrowser(context)
//        }
    }
}