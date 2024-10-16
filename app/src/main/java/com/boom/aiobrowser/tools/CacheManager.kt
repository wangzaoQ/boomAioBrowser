package com.boom.aiobrowser.tools

import android.provider.Settings
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.BuildConfig
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.data.LocationData
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.net.NetRequest
import com.boom.aiobrowser.ui.JumpConfig
import com.tencent.mmkv.MMKV
import java.util.LinkedList
import java.util.UUID

object CacheManager {

    const val TAG = "CacheManager"

    val mmkv = MMKV.mmkvWithID("${BuildConfig.APPLICATION_ID}kv", MMKV.MULTI_PROCESS_MODE)
    const val KV_FIRST_START = "KV_FIRST_START"
    const val KV_INSTALL_REFER = "KV_INSTALL_REFER"
    const val KV_FIRST_VIDEO = "KV_FIRST_VIDEO"
    const val KV_FIRST_DISCLAIMER = "KV_FIRST_DISCLAIMER"
    const val KV_ENGINE_GUIDE_FIRST = "KV_ENGINE_GUIDE_FIRST"
    const val KV_FIRST_SHOW_CLEAR = "KV_FIRST_SHOW_CLEAR"
    const val KV_FIRST_SHOW_BROWSER_DEFAULT = "KV_FIRST_SHOW_BROWSER_DEFAULT"
    const val KV_ENGINE_TYPE = "KV_ENGINE_TYPE"
    const val KV_TAB_DATA_NORMAL = "KV_TAB_DATA_NORMAL"
    const val KV_TAB_DATA_PRIVATE = "KV_TAB_DATA_PRIVATE"
    const val KV_VIDEO_DOWNLOAD = "KV_VIDEO_DOWNLOAD"
    const val KV_NEWS_LIST = "KV_NEWS_LIST"
    const val KV_BROWSER_STATUS = "KV_BROWSER_STATUS"
    const val KV_RECENT_SEARCH_DATA = "KV_RECENT_SEARCH_DATA"
    const val KV_LAST_JUMP_DATA = "KV_LAST_JUMP_DATA"
    const val KV_LOCATION_DATA = "KV_LOCATION_DATA"
    const val KV_PHONE_ID = "KV_PHONE_ID"
    const val KV_NEWS_SAVE_TIME = "KV_NEWS_SAVE_TIME"
    const val KV_HISTORY_DATA = "KV_HISTORY_DATA"
    const val KV_HISTORY_DATA_JUMP = "KV_HISTORY_DATA_JUMP"
    const val KV_URL_LIST = "KV_URL_LIST"
    const val KV_SAVE_DAY = "KV_SAVE_DAY"
    const val KV_GID = "KV_GID"
    const val KV_CLICK_EVERY_DAY = "KV_CLICK_EVERY_DAY"
    const val KV_SHOW_EVERY_DAY = "KV_SHOW_EVERY_DAY"
    const val KV_LAST_LAUNCH_TIME = "KV_LAST_LAUNCH_TIME"
    const val KV_CLEAN_TIME = "KV_CLEAN_TIME"
    const val KV_FIRST_SHOW_DOWNLOAD = "KV_FIRST_SHOW_DOWNLOAD"
//    const val KV_FIRST_OPEN_APP = "KV_FIRST_OPEN_APP"


    var videoDownloadTempList :MutableList<VideoDownloadData>
        get() {
            var list = getListByGson(mmkv.decodeString(KV_VIDEO_DOWNLOAD),VideoDownloadData::class.java)?: mutableListOf()
            return list
        }
        set(value) {
            mmkv.encode(KV_VIDEO_DOWNLOAD, toJson(value))
        }
    // 是否首次打开start
    var installRefer: String
        get() {
            return mmkv.decodeString(KV_INSTALL_REFER)?:""
        }
        set(value) {
            mmkv.encode(KV_INSTALL_REFER, value)
        }
     var isFirstStart: Boolean
        get() {
            return mmkv.decodeBool(KV_FIRST_START, true)
        }
        set(value) {
            mmkv.encode(KV_FIRST_START, value)
        }
    var isFirstShowDownload: Boolean
        get() {
            return mmkv.decodeBool(KV_FIRST_SHOW_DOWNLOAD, true)
        }
        set(value) {
            mmkv.encode(KV_FIRST_SHOW_DOWNLOAD, value)
        }

    var isVideoFirst: Boolean
        get() {
            return mmkv.decodeBool(KV_FIRST_VIDEO, true)
        }
        set(value) {
            mmkv.encode(KV_FIRST_VIDEO, value)
        }
    var isDisclaimerFirst: Boolean
        get() {
            return mmkv.decodeBool(KV_FIRST_DISCLAIMER, true)
        }
        set(value) {
            mmkv.encode(KV_FIRST_DISCLAIMER, value)
        }


    var engineGuideFirst: Boolean
        get() {
            return mmkv.decodeBool(KV_ENGINE_GUIDE_FIRST, true)
        }
        set(value) {
            mmkv.encode(KV_ENGINE_GUIDE_FIRST, value)
        }

    // 是否首次展示清理数据tips
    var isFirstShowClear: Boolean
        get() {
            return mmkv.decodeBool(KV_FIRST_SHOW_CLEAR, true)
        }
        set(value) {
            mmkv.encode(KV_FIRST_SHOW_CLEAR, value)
        }


    var isFirstShowBrowserDefault: Boolean
        get() {
            return mmkv.decodeBool(KV_FIRST_SHOW_BROWSER_DEFAULT, true)
        }
        set(value) {
            mmkv.encode(KV_FIRST_SHOW_BROWSER_DEFAULT, value)
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

    var GID: String
        get() {
            return mmkv.decodeString(KV_GID)?:""
        }
        set(value) {
            mmkv.encode(KV_GID, value)
        }

    // 不一样就清除部分数据
    var saveDay: String
        get() {
            return mmkv.decodeString(KV_SAVE_DAY)?:""
        }
        set(value) {
            mmkv.encode(KV_SAVE_DAY, value)
        }

    var clickEveryDay: Int
        get() {
            return mmkv.decodeInt(KV_CLICK_EVERY_DAY)
        }
        set(value) {
            mmkv.encode(KV_CLICK_EVERY_DAY, value)
        }
    var showEveryDay: Int
        get() {
            return mmkv.decodeInt(KV_SHOW_EVERY_DAY)
        }
        set(value) {
            mmkv.encode(KV_SHOW_EVERY_DAY, value)
        }

    var adLastTime: Long
        get() {
            return mmkv.decodeLong(KV_LAST_LAUNCH_TIME)
        }
        set(value) {
            mmkv.encode(KV_LAST_LAUNCH_TIME, value)
        }

//
//    fun getLastJumpData(id:Long):JumpData?{
//        if (id == 0L)return null
//        return getBeanByGson(mmkv.decodeString("${id}_${KV_LAST_JUMP_DATA}",""),JumpData::class.java)
//    }
//
//    fun saveLastJumpData(id:Long,data:JumpData?){
//        mmkv.encode("${id}_${KV_LAST_JUMP_DATA}", toJson(data))
//    }

    var locationData:LocationData?
        get() {
            return getBeanByGson(mmkv.decodeString(KV_LOCATION_DATA,""),LocationData::class.java)
        }
        set(value) {
            mmkv.encode(KV_LOCATION_DATA, toJson(value))
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

    var newsSaveTime :Long
        get() {
            return mmkv.decodeLong(KV_NEWS_SAVE_TIME, 0)
        }
        set(value) {
            mmkv.encode(KV_NEWS_SAVE_TIME, value)
        }
    var newsList:MutableList<NewsData>
        get() {

            return getListByGson(mmkv.decodeString(KV_NEWS_LIST),NewsData::class.java) ?: mutableListOf()
        }
        set(value) {
            mmkv.encode(KV_NEWS_LIST, toJson(value))
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

    var historyDataList :MutableList<JumpData>
        get() {
            return getListByGson(mmkv.decodeString(KV_HISTORY_DATA),JumpData::class.java) ?: mutableListOf()
        }
        set(value) {
            mmkv.encode(KV_HISTORY_DATA, toJson(value))
        }

    var historyJumpList :MutableList<JumpData>
        get() {
            return getListByGson(mmkv.decodeString(KV_HISTORY_DATA_JUMP),JumpData::class.java) ?: mutableListOf()
        }
        set(value) {
            mmkv.encode(KV_HISTORY_DATA_JUMP, toJson(value))
        }

    fun addHistoryJump(data: JumpData){
        var list = historyJumpList
        var index = -1
        for ( i in 0 until list.size){
            var item = list.get(i)
            if (item.jumpTitle == data.jumpTitle){
                index = i
                break
            }
        }
        if (index>=0){
            list.removeAt(index)
        }
        list.add(0,data)
        historyJumpList = list
    }

//
//    var firstOpenApp:Boolean
//        get(){
//            return mmkv.decodeBool(KV_FIRST_OPEN_APP, true)
//        }
//        set(value) {
//            mmkv.encode(KV_FIRST_OPEN_APP,value)
//        }

    fun getID():String{
        var phoneId = mmkv.decodeString(KV_PHONE_ID,"")?:""
        if (phoneId.isNullOrEmpty().not()){
            AppLogs.dLog(TAG,"ANDROID_ID:${phoneId}")
            return phoneId
        }
        val id = Settings.Secure.getString(
            APP.instance.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        AppLogs.dLog(TAG,"ANDROID_ID:${id}")
        phoneId = if ("9774d56d682e549c" == id ||"0000000000000000" == id) "" else id
        if (phoneId.isNullOrEmpty().not()) {
            mmkv.encode(KV_PHONE_ID, phoneId)
            return phoneId
        }
        phoneId = UUID.randomUUID().toString().replace("-", "")
        AppLogs.dLog(KV_PHONE_ID,"UUID:${phoneId}")
        if (phoneId.isNullOrEmpty().not()){
            mmkv.encode(KV_PHONE_ID, phoneId)
        }
        return phoneId
    }

    fun saveSession(key:String,value:String){
        mmkv.encode("${key}_${NetRequest.keyTag}",value)
    }

    fun getSession(key:String):String{
        return mmkv.decodeString("${key}_${NetRequest.keyTag}","")?:""
    }

    var linkedUrlList :LinkedList<String>
        get() {
            return getLinkedListByGson(mmkv.decodeString(KV_URL_LIST),String::class.java) ?: LinkedList<String>()
        }
        set(value) {
            mmkv.encode(KV_URL_LIST, toJson(value))
        }

    fun clearAll() {
        historyDataList = mutableListOf()
        recentSearchDataList = mutableListOf()
        tabDataListNormal = mutableListOf()
        tabDataListPrivate = mutableListOf()
        browserStatus = 0
    }

    fun isAllowShowCleanTips(): Boolean {
        var lastCleanTime = mmkv.decodeLong(KV_CLEAN_TIME)
        return (System.currentTimeMillis()-lastCleanTime)>8*60*60*1000
    }

    fun saveCleanTips(){
        mmkv.encode(KV_CLEAN_TIME,System.currentTimeMillis())
    }
}