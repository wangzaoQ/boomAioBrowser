package com.boom.aiobrowser.other

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.nf.NFJump.getFlags
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.ui.ParamsConfig
import com.boom.aiobrowser.ui.activity.MainActivity
import java.lang.ref.WeakReference


object ShortManager {

    var TAG = "ShortManager"

    fun addPinShortcut(weakReference: WeakReference<MainActivity>) {
        runCatching {
            weakReference.get()?.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val shortcutManager: ShortcutManager =
                        APP.instance.getSystemService(ShortcutManager::class.java)
                    var allow = shortcutManager.isRequestPinShortcutSupported && (shortcutManager.pinnedShortcuts.size == 0)
                    if (APP.isDebug){
                        AppLogs.dLog(TAG,"是否允许添加 isRequestPinShortcutSupported：${shortcutManager.isRequestPinShortcutSupported} size:${shortcutManager.pinnedShortcuts.size}")
                    }
                    if (allow) {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intent.setAction("LOCATION_SHORTCUT");

                        //具体点的是当前通知哪一个区域 状态各有不同
//                intent.putExtra(ParamsConfig.NF_TO, nfTo)
                        //点击的是哪一种通知
                        intent.putExtra(ParamsConfig.NF_ENUM_NAME,"short")

                        // Enable the existing shortcut with the ID "my-shortcut".
                        val pinShortcutInfo =
                            ShortcutInfo.Builder(APP.instance, "short-aio")
                                .setIcon(
                                    Icon.createWithResource(
                                        APP.instance,
                                        R.mipmap.ic_start_logo
                                    )
                                )
                                .setShortLabel(APP.instance.getString(R.string.app_name))
                                .setLongLabel(APP.instance.getString(R.string.app_name))
                                .setIntent(intent)
                                .build()

                        val pinnedShortcutCallbackIntent =
                            shortcutManager.createShortcutResultIntent(pinShortcutInfo)

                        val successCallback = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            PendingIntent.getActivity(
                                APP.instance,
                                101,
                                pinnedShortcutCallbackIntent,
                                getFlags()
                            )
                        } else {
                            PendingIntent.getActivity(
                                APP.instance,
                                101,
                                pinnedShortcutCallbackIntent,
                                getFlags()
                            )
                        }
                        val ret = shortcutManager.requestPinShortcut(
                            pinShortcutInfo,
                            successCallback.intentSender
                        )
                    }
                }
            }
        }.onFailure {
            AppLogs.eLog(TAG,it.stackTraceToString())
        }
    }

}