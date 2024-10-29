package com.boom.aiobrowser.other

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
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


    val APP_WIDGET_UPDATE = "aio.widget_update"

    fun widgetUpdate(context: Context) {
        AppLogs.dLog(APP_WIDGET_UPDATE,"widgetUpdate")
        context.sendBroadcast(Intent(APP_WIDGET_UPDATE, null, context, WidgetProvider::class.java))
    }

    fun addWidgetToLaunch(context: Context) { // 添加组件到桌面
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val appWidgetManager: AppWidgetManager? = context.getSystemService(AppWidgetManager::class.java) as AppWidgetManager
                appWidgetManager?.let {
                    if (appWidgetManager.isRequestPinAppWidgetSupported) {
                        val myProvider = ComponentName(context, WidgetProvider::class.java)
                        val addWidgetCallIntent = Intent(context, WidgetProvider::class.java)
                        val successCallback: PendingIntent = PendingIntent.getBroadcast(
                            context,
                            0,
                            addWidgetCallIntent, PendingIntent.FLAG_IMMUTABLE
//                    PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
//                    PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_IMMUTABLE
//                    PendingIntent.FLAG_UPDATE_CURRENT
                        )
                        appWidgetManager.requestPinAppWidget(myProvider, Bundle().apply { putString("add_widget", "add_widget") }, successCallback)
                    }
                }
            }
        }.onFailure {
            AppLogs.eLog(TAG,it.stackTraceToString())
        }
    }

}