package com.boom.aiobrowser.tools

import com.boom.aiobrowser.APP
import com.boom.aiobrowser.BuildConfig
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.data.RecentSearchData
import com.boom.aiobrowser.data.TabData
import com.boom.aiobrowser.ui.JumpConfig
import com.tencent.mmkv.MMKV

object CacheManager {
    val mmkv = MMKV.mmkvWithID("${BuildConfig.APPLICATION_ID}kv", MMKV.MULTI_PROCESS_MODE)
    const val KV_FIRST_START = "KV_FIRST_START"
    const val KV_ENGINE_TYPE = "KV_ENGINE_TYPE"
    const val KV_TAB_DATA_NORMAL = "KV_TAB_DATA_NORMAL"
    const val KV_TAB_DATA_PRIVATE = "KV_TAB_DATA_PRIVATE"
    const val KV_BROWSER_STATUS = "KV_BROWSER_STATUS"
    const val KV_RECENT_SEARCH_DATA = "KV_RECENT_SEARCH_DATA"

    // 是否首次打开start
    var isFirstStart: Boolean
        get() {
            return mmkv.decodeBool(KV_FIRST_START, true)
        }
        set(value) {
            mmkv.encode(KV_FIRST_START, value)
        }


    fun getBrowserTabList():MutableList<JumpData>{
        if (browserStatus == 0){
            return tabDataListNormal
        }else{
            return tabDataListPrivate
        }
    }

    fun saveBrowserTabList(dataList:MutableList<JumpData>){
        if (browserStatus == 0){
            tabDataListNormal = dataList
        }else{
            tabDataListPrivate = dataList
        }
    }

    fun addBrowserTab(data:JumpData,restSelected:Boolean = false){
        var list = getBrowserTabList()
        if (restSelected){
            list.forEach {
                it.isCurrent = false
            }
        }
        if (browserStatus == 0){
            list.add(data)
            tabDataListNormal = list
        }else{
            tabDataListPrivate = list
        }
    }

    // 0 normal 1 private
    var browserStatus: Int
        get() {
            return mmkv.decodeInt(KV_BROWSER_STATUS, 0)
        }
        set(value) {
            mmkv.encode(KV_BROWSER_STATUS, value)
        }

    // 搜索引擎类型 0 google 1 Bing 2 Yahoo 3 Perplexity
    var engineType: Int
        get() {
            return mmkv.decodeInt(KV_ENGINE_TYPE, 0)
        }
        set(value) {
            mmkv.encode(KV_ENGINE_TYPE, value)
        }

    fun updateCurrentJumpData(currentData: JumpData){
        var list = getBrowserTabList()
        var index = -1
        for (i in 0 until list.size){
            if (list.get(i).isCurrent){
                index = i
                break
            }
        }
        var data = list.get(index)
        data.updateData(currentData)
        saveBrowserTabList(list)
    }

    fun getCurrentJumpData(isReset:Boolean = false,updateTime:Boolean = false,updateData: JumpData?=null):JumpData{
        var list = getBrowserTabList()
        var index = -1
        for (i in 0 until list.size){
            if (list.get(i).isCurrent){
                index = i
                break
            }
        }
        var data = list.get(index)
        if (isReset){
            data.jumpTitle = APP.instance.getString(R.string.app_home)
            data.jumpUrl = ""
        }
        if (updateData!=null){
            data.jumpTitle = APP.instance.getString(R.string.app_home)
            data.jumpUrl = ""
            data.jumpType = updateData.jumpType
        }
        if (updateTime){
            data.updateTime = System.currentTimeMillis()
        }
        return data
    }

    // 网页tab数据 normal
    var tabDataListNormal:MutableList<JumpData>
        get() {
            var list = getListByGson(mmkv.decodeString(KV_TAB_DATA_NORMAL),JumpData::class.java)?: mutableListOf()
            if (list.isNullOrEmpty()){
                list = mutableListOf<JumpData>().apply {
                    add(JumpData().apply {
                        jumpTitle = APP.instance.getString(R.string.app_home)
                        jumpType = JumpConfig.JUMP_HOME
                        isCurrent = true
                    })
                }
                mmkv.encode(KV_TAB_DATA_NORMAL, toJson(list))
            }
            return list
        }
        set(value) {
            mmkv.encode(KV_TAB_DATA_NORMAL, toJson(value))
        }

    var tabDataListPrivate:MutableList<JumpData>
        get() {
            return getListByGson(mmkv.decodeString(KV_TAB_DATA_PRIVATE),JumpData::class.java) ?: mutableListOf()
        }
        set(value) {
            mmkv.encode(KV_TAB_DATA_PRIVATE, toJson(value))
        }

    fun saveRecentSearchData(data: JumpData){
        var list = recentSearchDataList
        var index = -1
        for (i in 0 until list.size){
            if (list.get(i).jumpTitle == data.jumpTitle){
                index = i
                break
            }
        }
        if (index>=0){
            list.removeAt(index)
        }
        list.add(0,data)
        recentSearchDataList = list
    }

    var recentSearchDataList:MutableList<JumpData>
        get() {
            return getListByGson(mmkv.decodeString(KV_RECENT_SEARCH_DATA),JumpData::class.java) ?: mutableListOf()
        }
        set(value) {
            mmkv.encode(KV_RECENT_SEARCH_DATA, toJson(value))
        }
}