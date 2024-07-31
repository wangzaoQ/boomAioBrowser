package com.boom.aiobrowser.tools

import com.boom.aiobrowser.BuildConfig
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.data.RecentSearchData
import com.boom.aiobrowser.data.TabData
import com.tencent.mmkv.MMKV

object CacheManager {
    val mmkv = MMKV.mmkvWithID("${BuildConfig.APPLICATION_ID}kv", MMKV.MULTI_PROCESS_MODE)
    const val KV_FIRST_START = "KV_FIRST_START"
    const val KV_ENGINE_TYPE = "KV_ENGINE_TYPE"
    const val KV_TAB_DATA = "KV_TAB_DATA"
    const val KV_RECENT_SEARCH_DATA = "KV_RECENT_SEARCH_DATA"

    // 是否首次打开start
    var isFirstStart: Boolean
        get() {
            return mmkv.decodeBool(KV_FIRST_START, true)
        }
        set(value) {
            mmkv.encode(KV_FIRST_START, value)
        }

    // 搜索引擎类型 0 google 1 Bing 2 Yahoo 3 Perplexity
    var engineType: Int
        get() {
            return mmkv.decodeInt(KV_ENGINE_TYPE, 0)
        }
        set(value) {
            mmkv.encode(KV_ENGINE_TYPE, value)
        }

    // 网页tab数据
    var tabDataList:MutableList<TabData>
        get() {
            return getListByGson(mmkv.decodeString(KV_TAB_DATA),TabData::class.java) ?: mutableListOf()
        }
        set(value) {
            mmkv.encode(KV_TAB_DATA, toJson(value))
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