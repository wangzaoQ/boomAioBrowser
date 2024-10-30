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
import com.blankj.utilcode.util.SizeUtils.dp2px
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.net.NetParams
import com.boom.aiobrowser.nf.NFData
import com.boom.aiobrowser.nf.NFManager
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.GlideManager
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
            ShortManager.widgetUpdate(this,"widgetUpdate")
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
                    CoroutineScope(Dispatchers.IO).launch{
                        var data = getNewsByWidget() // 填充数据
                        withContext(Dispatchers.Main) {
                            // logsi(FJST, "onReceive()222 intent=" + intent)
                            val appWidgetManager = AppWidgetManager.getInstance(context)
                            val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, ShortManager::class.java))
                            appWidgetIds.forEach { idlist.add(it) }
                            idlist.forEach {
                                val rView = upadateView(context, appWidgetManager, it,data)
                                appWidgetManager?.updateAppWidget(it, rView)
                            }
                        }
                    }
                }
            }
        }
        super.onReceive(context, intent)
    }


    @SuppressLint("RemoteViewLayout")
    private fun upadateView(
        context: Context,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        data: NewsData?
    ): RemoteViews {
        val view = RemoteViews(context.packageName, R.layout.browser_widget_default)
        view.setTextViewText(R.id.tvTitle,data?.tconsi?:"")
        view.setTextViewText(R.id.tvContent,data?.sissue?:"")
        view.setOnClickPendingIntent(R.id.rlRoot, getDetailsIntent(context,0))
        view.setOnClickPendingIntent(R.id.llSearch, getDetailsIntent(context,1))
        view.setImageViewResource(R.id.ivNews, R.mipmap.bg_news_default)
        var width = dp2px(68f)
        var height = dp2px(51f)
        GlideManager.loadNFBitmap(
            APP.instance,data?.iassum?:"",width,height, bitmapCall = {
                view.setImageViewBitmap(R.id.ivNews, it)
                appWidgetManager?.updateAppWidget(appWidgetId, view)
            })
        return view
    }

    private fun getDetailsIntent(context: Context, nfTo: Int): PendingIntent {
        // logsi(FJST, "startPager() index=" + index)
        val intent = Intent().apply {
            setClass(context, MainActivity::class.java)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(ParamsConfig.NF_ENUM_NAME, ParamsConfig.WIDGET)
            putExtra(ParamsConfig.NF_TO, nfTo)
        }
        return PendingIntent.getActivity(context, requestCode(), intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun requestCode(): Int {
        return Random.nextInt(100, 1999999900)
    }


    suspend fun getNewsByWidget(): NewsData? {
        if (NFManager.needRefreshData(NetParams.WIDGET)) {
            CacheManager.saveNFNewsList(NetParams.WIDGET, mutableListOf())
        }
        var newsList = CacheManager.getNFNewsList(NetParams.WIDGET)
        var data:NewsData?=null
        var count = 0
        var refreshSession = false
        while (count < 10 && newsList.isNullOrEmpty()) {
            AppLogs.dLog(NFManager.TAG, "name:${NetParams.WIDGET} 获取数据来源次数count:${count + 1}")
            newsList = NFData.getWidgetData(refreshSession)
            count++
            if (count == 7) {
                refreshSession = true
            }
        }
        if (newsList.isNotEmpty()){
            data = newsList.removeFirstOrNull()
        }
        return data
    }

}