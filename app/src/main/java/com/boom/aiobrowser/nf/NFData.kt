package com.boom.aiobrowser.nf

import com.boom.aiobrowser.APP
import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.NewsTempData
import com.boom.aiobrowser.net.NetController
import com.boom.aiobrowser.net.NetParams
import com.boom.aiobrowser.net.NetRequest
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import java.util.Vector

object NFData {

    suspend fun getNFData(refreshSession: Boolean, key: String): MutableList<NewsData> {
        if (key == NFEnum.NF_TREND.menuName){
            var newsList = CacheManager.trendNews
            if (newsList.isNullOrEmpty().not()){
                if (newsList.size>3){
                    newsList = newsList.subList(0,3)
                }
            }else{
                APP.instance.appModel.getTrendsNews()
                newsList = CacheManager.trendNews
            }
            return newsList
        }else{
            if (refreshSession) {
                AppLogs.dLog(
                    NFManager.TAG,
                    "${key}推送 强制刷新session 原始值为 :${CacheManager.getSession(key)}"
                )
                CacheManager.saveSession(key, "")
                AppLogs.dLog(
                    NFManager.TAG,
                    "${key}推送 强制刷新session 刷新后为 :${CacheManager.getSession(key)}"
                )
            }
            var list = mutableListOf<NewsData>()
            runCatching {
                when (key) {
                    NFEnum.NF_HOT.menuName -> {
                        // 热榜
                        list = NetRequest.request(HashMap<String, Any>().apply {
                            put("sessionKey", key)
                        }) { NetController.getHotNewsList(NetParams.getParamsMap(key)) }.data
                            ?: mutableListOf()

                    }

                    NFEnum.NF_EDITOR.menuName,NFEnum.NF_UNLOCK.menuName -> {
                        // 人工
                        list = NetRequest.request(HashMap<String, Any>().apply {
                            put("sessionKey", key)
                        }) { NetController.getEditorNewsList(NetParams.getParamsMap(key)) }.data
                            ?: mutableListOf()
                    }

                    else -> {
                        list = NetRequest.request(HashMap<String, Any>().apply {
                            put("sessionKey", key)
                        }) { NetController.getNewsList(NetParams.getParamsMap(key)) }.data
                            ?: mutableListOf()
                    }
                }
            }.onFailure {
                AppLogs.eLog(NFManager.TAG, "${key}推送" + it.stackTraceToString())
            }
            return list
        }
    }


    /**
     *  1.广播解锁通知 2.notify自己刷新 3shou的逻辑 4.work,5 fcm
     *  action 区分通知类型
     */
    fun filterList1(enum: NFEnum, sourceType: String): Boolean {
        AppLogs.dLog(NFManager.TAG, "enum:${enum.menuName} 来源:${sourceType} filter1 start")

        var limitContent = ""
        //如果上一次触发和这次触发为同一时间段 则不触发（为了防止FCM/本地/work 同一时间段触发都进行）
        var allow = true

        runCatching {
            var lastTime = CacheManager.getNFShowLastTime(enum.menuName)
            var count = CacheManager.getDayNFShowCount(enum.menuName)
            var limitCount = getLimitCountByAction(enum.menuName)
            var limitFirstTime = getLimitFirstTime(enum.menuName)
            var limitInTime = getLimitInTime(enum.menuName)
            AppLogs.dLog(NFManager.TAG,"enum:${enum.menuName} 来源:${sourceType} 最大展示次数limitCount:${limitCount} 第一次安装间隔时间 limitFirstTime:${limitFirstTime} 每次间隔时间limitInTime:${limitInTime}")
            if (count>limitCount){
                limitContent = "max_show_count:${count}_nfConfig.times:${limitCount}"
                allow =  false
            }else if ((System.currentTimeMillis() - APP.instance.installTime) < limitFirstTime * 60000){
                limitContent = "first_time_limit:install_${System.currentTimeMillis() - APP.instance.installTime}_first_time:${limitFirstTime * 60000}"
                allow =  false
            }else if (lastTime != 0L && (System.currentTimeMillis() - lastTime) < (limitInTime * 60_000L)) {
                limitContent = "Time_Limit :${(limitInTime* 60_000L) - (System.currentTimeMillis() - lastTime)}"
                allow =  false
            }
//            if (allow){
//                //判断各个渠道间的时间间隔
//                allow = System.currentTimeMillis() - NowNewsKVUtils.nfLastTime > (nfConfig?.notify_gap?:0)*60*1000L
//                if (allow == false){
//                    limitContent = "Time_Limit各个渠道间通知间隔 :${ (nfConfig?.notify_gap?:0)*60*1000L - (System.currentTimeMillis() - NowNewsKVUtils.nfLastTime)}"
//                }
//            }
        }.onFailure {
            AppLogs.eLog(NFManager.TAG,"filterList1 error:${it.stackTraceToString()}")
        }
        if (allow.not()){
            AppLogs.dLog(NFManager.TAG,"不允许执行通知 limitContent:${limitContent}")
        }else{
            AppLogs.dLog(NFManager.TAG,"允许执行通知")
        }
        AppLogs.dLog(NFManager.TAG, "enum:${enum.menuName} 来源:${sourceType} filter1 end")
        return allow
    }


    suspend fun filterList2(
        enum: NFEnum,
        sourceType: String,
        newsList: MutableList<NewsData>
    ) :MutableList<NewsData>{
        AppLogs.dLog(NFManager.TAG, "enum:${enum.menuName} 来源:${sourceType} filter2 start")
        runCatching {
            var tempList = mutableListOf<NewsData>()
            newsList.forEach {
                if (it.iassum.isNullOrEmpty()) {
                    tempList.add(it)
                }
            }
            AppLogs.dLog(NFManager.TAG, "需要过滤的数据 size=${newsList.size}")
            newsList.removeAll(tempList)
            AppLogs.dLog(NFManager.TAG, "经过图片过滤后 size=${newsList.size}")
        }.onFailure {
            AppLogs.eLog(NFManager.TAG, it.stackTraceToString())
        }
        AppLogs.dLog(
            NFManager.TAG,
            "enum::${enum.menuName} 来源:${sourceType} 经过图片过滤后:${newsList.size}"
        )
        AppLogs.dLog(NFManager.TAG, "enum:${enum.menuName} 来源:${sourceType} filter2 end")
        return newsList
    }

    suspend fun filterList3(
        enum: NFEnum,
        sourceType: String,
        newsList: MutableList<NewsData>
    ):MutableList<NewsData> {
        AppLogs.dLog(NFManager.TAG, "enum:${enum.menuName} 来源:${sourceType} filter3 start")
        synchronized("filterList2") {
            AppLogs.dLog(NFManager.TAG, "缓存过滤开始")
            var list = CacheManager.getNewsListHistory() ?: Vector<NewsTempData>()
            var removeHistoryList = mutableListOf<NewsTempData>()
            var removeNewsList = mutableListOf<NewsData>()
            runCatching {
                newsList.forEach {
                    for (i in 0 until list.size) {
                        var data = list.get(i)
                        if (it.itackl == data.newsId) {
                            if (data.isExpire()) {
                                //过期的移除
                                if (removeHistoryList.contains(data).not()) {
                                    removeHistoryList.add(data)
                                }
                            } else {
                                //重复的移除
                                removeNewsList.add(it)
                                break
                            }
                        }
                    }
                }
                AppLogs.dLog(NFManager.TAG, "现有缓存 size=${list.size}")
                AppLogs.dLog(NFManager.TAG, "过期缓存 size=${removeHistoryList.size}")
                list.removeAll(removeHistoryList)
                AppLogs.dLog(NFManager.TAG, "移除过期缓存后 size=${list.size}")
                CacheManager.saveNewsListHistory(list)
                AppLogs.dLog(NFManager.TAG, "需要过滤的数据 size=${newsList.size}")
                newsList.removeAll(removeNewsList)
                AppLogs.dLog(NFManager.TAG, "经过缓存过滤后 size=${newsList.size}")
            }.onFailure {
                AppLogs.eLog(NFManager.TAG, "filterList3 error:${it.stackTraceToString()}")
            }
        }
        AppLogs.dLog(NFManager.TAG, "enum:${enum.menuName} 来源:${sourceType} filter3 end")
        return newsList
    }


    private fun getLimitCountByAction(action: String): Int {
        return when (action) {
            NFEnum.NF_EDITOR.menuName-> {
                NFManager.nfRootBean?.aio_editor?.times?:0
            }
            NFEnum.NF_LOCAL.menuName-> {
                NFManager.nfRootBean?.aio_local?.times?:0
            }
            NFEnum.NF_HOT.menuName-> {
                NFManager.nfRootBean?.aio_hot?.times?:0
            }
            NFEnum.NF_NEW_USER.menuName-> {
                NFManager.nfRootBean?.aio_newuser?.times?:0
            }
            NFEnum.NF_UNLOCK.menuName->{
                NFManager.nfRootBean?.aio_unlock?.times?:0
            }
            else -> {
                NFManager.nfRootBean?.aio_for_you?.times?:0
            }
        }
    }

    private fun getLimitFirstTime(action: String): Int {
        return when (action) {
            NFEnum.NF_EDITOR.menuName-> {
                NFManager.nfRootBean?.aio_editor?.first_time?:0
            }
            NFEnum.NF_LOCAL.menuName-> {
                NFManager.nfRootBean?.aio_local?.first_time?:0
            }
            NFEnum.NF_HOT.menuName-> {
                NFManager.nfRootBean?.aio_hot?.first_time?:0
            }
            NFEnum.NF_NEW_USER.menuName-> {
                NFManager.nfRootBean?.aio_newuser?.first_time?:0
            }
            NFEnum.NF_UNLOCK.menuName->{
                NFManager.nfRootBean?.aio_unlock?.first_time?:0
            }
            else -> {
                NFManager.nfRootBean?.aio_for_you?.first_time?:0
            }
        }
    }
    private fun getLimitInTime(action: String): Int {
        return when (action) {
            NFEnum.NF_EDITOR.menuName-> {
                NFManager.nfRootBean?.aio_editor?.in_time?:0
            }
            NFEnum.NF_LOCAL.menuName-> {
                NFManager.nfRootBean?.aio_local?.in_time?:0
            }
            NFEnum.NF_HOT.menuName-> {
                NFManager.nfRootBean?.aio_hot?.in_time?:0
            }
            NFEnum.NF_NEW_USER.menuName-> {
                NFManager.nfRootBean?.aio_newuser?.in_time?:0
            }
            NFEnum.NF_UNLOCK.menuName->{
                NFManager.nfRootBean?.aio_unlock?.in_time?:0
            }
            else -> {
                NFManager.nfRootBean?.aio_for_you?.in_time?:0
            }
        }    }

}