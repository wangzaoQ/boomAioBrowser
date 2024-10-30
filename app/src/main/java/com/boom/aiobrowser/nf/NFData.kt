package com.boom.aiobrowser.nf

import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.net.NetController
import com.boom.aiobrowser.net.NetParams
import com.boom.aiobrowser.net.NetRequest
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager

object NFData {

    suspend fun getNFForYou(refreshSession: Boolean):MutableList<NewsData>{
        if (refreshSession){
            AppLogs.dLog(NFManager.TAG,"ForYou推送 强制刷新session 原始值为 :${CacheManager.getSession(NetParams.FOR_YOU_PUSH)}")
            CacheManager.saveSession(NetParams.FOR_YOU_PUSH, "")
            AppLogs.dLog(NFManager.TAG,"ForYou推送 强制刷新session 刷新后为 :${CacheManager.getSession(NetParams.FOR_YOU_PUSH)}")
        }
        var list = mutableListOf<NewsData>()
        runCatching {
            list = NetRequest.request(HashMap<String, Any>().apply {
                put("sessionKey", NetParams.FOR_YOU)
            }) { NetController.getNewsList(NetParams.getParamsMap(NetParams.FOR_YOU_PUSH)) }.data?: mutableListOf()
        }.onFailure {
            AppLogs.eLog(NFManager.TAG, "ForYou推送" + it.stackTraceToString())
        }
        return list
    }

    suspend fun getWidgetData(refreshSession: Boolean): MutableList<NewsData> {
        if (refreshSession){
            AppLogs.dLog(NFManager.TAG,"Widget 强制刷新session 原始值为 :${CacheManager.getSession(NetParams.WIDGET)}")
            CacheManager.saveSession(NetParams.WIDGET, "")
            AppLogs.dLog(NFManager.TAG,"Widget 强制刷新session 刷新后为 :${CacheManager.getSession(NetParams.WIDGET)}")
        }
        var list = mutableListOf<NewsData>()
        runCatching {
            list = NetRequest.request(HashMap<String, Any>().apply {
                put("sessionKey", NetParams.WIDGET)
            }) { NetController.getNewsList(NetParams.getParamsMap(NetParams.WIDGET)) }.data?: mutableListOf()
        }.onFailure {
            AppLogs.eLog(NFManager.TAG, "Widget" + it.stackTraceToString())
        }
        return list
    }
}