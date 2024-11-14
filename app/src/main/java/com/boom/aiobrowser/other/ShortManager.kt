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
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.nf.NFJump.getFlags
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.ui.activity.MainActivity
import com.boom.aiobrowser.ui.pop.RatePop
import java.lang.ref.WeakReference


object ShortManager {
    val APP_WIDGET_UPDATE = "aio.widget_update"

    var TAG = APP_WIDGET_UPDATE

    fun addPinShortcut(weakReference: WeakReference<BaseActivity<*>>) {
        if (APP.instance.showPopLevel>0){
            AppLogs.dLog(TAG,"short添加失败 当前有更高等级弹窗 showPopLevel:${APP.instance.showPopLevel}")
            return
        }
        if (CacheManager.dayShowAddShort.not()){
            AppLogs.dLog(TAG,"short添加失败 当日只能展示一次")
            return
        }
        CacheManager.dayShowAddShort = false

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
                        intent.putExtra(ParamsConfig.NF_ENUM_NAME, ParamsConfig.SHORT)

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
//                        APP.instance.showPopLevel = 3
                        PointEvent.posePoint(PointEventKey.shoetcut)
                    }
                }
            }
        }.onFailure {
            AppLogs.eLog(TAG,it.stackTraceToString())
        }
    }



    fun widgetUpdate(context: Context,tag:String) {
        AppLogs.dLog(APP_WIDGET_UPDATE,tag)
        context.sendBroadcast(Intent(APP_WIDGET_UPDATE, null, context, WidgetProvider::class.java))
    }

    fun addWidgetToLaunch(context: Context,continueFilter:Boolean=false) { // 添加组件到桌面
        if (continueFilter.not()){
            if (CacheManager.isFirstClickDownloadButton){
                AppLogs.dLog(TAG,"Widget添加失败 新用户未使用下载功能")
                return
            }
            if (CacheManager.dayShowAddWidget.not()){
                AppLogs.dLog(TAG,"Widget添加失败 当日只能展示一次")
                return
            }
            if (APP.instance.showPopLevel>0){
                AppLogs.dLog(TAG,"Widget添加失败 当前有更高等级弹窗 showPopLevel:${APP.instance.showPopLevel}")
                return
            }
        }
        CacheManager.dayShowAddWidget = false
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
//                        APP.instance.showPopLevel = 2
                        PointEvent.posePoint(PointEventKey.widget_pop,Bundle().apply {
                            putString(PointValueKey.source_from,if(continueFilter)"profile_pop" else "other" )
                        })
                        ToastUtils.showLong(APP.instance.getString(R.string.app_add_widget_success))
                    }
                }
            }
        }.onFailure {
            AppLogs.eLog(TAG,it.stackTraceToString())
        }
    }

    fun addRate(weakReference: WeakReference<BaseActivity<*>>,allowShowAddTask:Boolean = false) {
        var activity: BaseActivity<*> = weakReference.get() ?: return
        RatePop(activity).createPop(allowShowAddTask)
    }

    fun allowRate(): Boolean {
        if (CacheManager.isRate5){
            AppLogs.dLog(TAG,"评分弹窗已经点击反馈")
            return false
        }
        var count = CacheManager.dayFeedBackCount
        if (count == 3){
            AppLogs.dLog(TAG,"未点击 feedBack 超过3次")
            return false
        }
        if (APP.instance.showPopLevel>0){
            AppLogs.dLog(TAG,"评分弹窗添加失败 当前有更高等级弹窗 showPopLevel:${APP.instance.showPopLevel}")
            return false
        }
        return true
    }

}