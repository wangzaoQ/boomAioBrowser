package com.boom.aiobrowser.other

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import com.boom.aiobrowser.R
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.ui.ParamsConfig
import com.boom.aiobrowser.ui.activity.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class WidgetProvider : AppWidgetProvider() {
    // private val FJST = NowAppWidgetNewsContent::class.java.simpleName

    companion object {
        private val idlist: MutableSet<Int> = HashSet<Int>()
    }

//    fun print(funName: String, appWidgetIds: IntArray?) {
//        AppLogs.dLog(ShortManager.APP_WIDGET_UPDATE,"funName:${funName}")
//        appWidgetIds?.forEach {
//            AppLogs.dLog(ShortManager.APP_WIDGET_UPDATE,"id=$it")
//        }
//    }

    override fun onAppWidgetOptionsChanged(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetId: Int, newOptions: Bundle?) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        AppLogs.dLog(ShortManager.APP_WIDGET_UPDATE,"onAppWidgetOptionsChanged_appWidgetId:${appWidgetId}")
        idlist.add(appWidgetId)
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
//        print("onDeleted()", appWidgetIds)

        appWidgetIds?.forEach {
            idlist.remove(it)
        }
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        AppLogs.dLog(ShortManager.APP_WIDGET_UPDATE,"onEnabled")
//        fireLog2Server(PointInfo.widget_add,null)
    }

    override fun onDisabled(context: Context) {
        AppLogs.dLog(ShortManager.APP_WIDGET_UPDATE,"onDisabled")
//        fireLog2Server(PointInfo.widget_cancel,null)
        super.onDisabled(context)
    }


    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
//        print("onUpdate()", appWidgetIds)
        context?.apply {
            ShortManager.widgetUpdate(this)
        }

//        appWidgetIds?.forEach {
//            idlist.add(it)
//        }
//        context?.let {
//            MainScope().launch {
//                NowAppWidgetUtil.getNewsByForyou()
//                withContext(Dispatchers.Main) {
//                    idlist.forEach {
//                        val rView = upadateView(context, appWidgetManager, it)
//                        appWidgetManager?.updateAppWidget(it, rView)
//                    }
//                }
//            }
//        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        AppLogs.dLog(ShortManager.APP_WIDGET_UPDATE,"onReceive")
        when (intent?.action) {
            ShortManager.APP_WIDGET_UPDATE -> {
                context?.let {
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, ShortManager::class.java))
                    appWidgetIds.forEach { idlist.add(it) }
                    idlist.forEach {
                        val rView = upadateView(context, appWidgetManager, it)
                        appWidgetManager?.updateAppWidget(it, rView)
                    }
                }
            }
        }
        super.onReceive(context, intent)
    }

    @SuppressLint("RemoteViewLayout")
    private fun upadateView(context: Context, appWidgetManager: AppWidgetManager?, appWidgetId: Int): RemoteViews {
        val view = RemoteViews(context.packageName, R.layout.browser_widget_default)
        view.setOnClickPendingIntent(R.id.rlRoot, getDetailsIntent(context))
        return view
    }

    private fun getDetailsIntent(context: Context): PendingIntent {
        // logsi(FJST, "startPager() index=" + index)
        val intent = Intent().apply {
            setClass(context, MainActivity::class.java)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            putExtra(ParamsConfig.NF_ENUM_NAME, ParamsConfig.WIDGET)
        }
        return PendingIntent.getActivity(context, requestCode(), intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun requestCode(): Int {
        return Random.nextInt(100, 1999999900)
    }


}