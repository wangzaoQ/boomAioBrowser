package com.boom.aiobrowser.tools

import android.provider.Settings
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.BuildConfig
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.data.LocationData
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.NewsTempData
import com.boom.aiobrowser.data.TopicBean
import com.boom.aiobrowser.data.UserData
import com.boom.aiobrowser.data.VideoUIData
import com.boom.aiobrowser.data.WebConfigData
import com.boom.aiobrowser.data.model.DownloadModel
import com.boom.aiobrowser.net.NetRequest
import com.boom.aiobrowser.nf.NFManager
import com.boom.aiobrowser.other.JumpConfig
import com.tencent.mmkv.MMKV
import java.util.LinkedList
import java.util.UUID
import java.util.Vector

object CacheManager {

    const val TAG = "CacheManager"

    val mmkv = MMKV.mmkvWithID("${BuildConfig.APPLICATION_ID}kv", MMKV.MULTI_PROCESS_MODE)
    const val KV_FIRST_START = "KV_FIRST_START"
    const val KV_FIRST_TO_DOWNLOAD = "KV_FIRST_TO_DOWNLOAD"
    const val KV_INSTALL_REFER = "KV_INSTALL_REFER"
    const val KV_ADJUST_FROM = "KV_ADJUST_FROM"
    const val KV_AF_FROM = "KV_AF_FROM"
    const val KV_FIRST_CLICK_DOWNLOAD_BUTTON = "KV_FIRST_CLICK_DOWNLOAD_BUTTON"
    const val KV_FIRST_DOWNLOAD_VIDEO_SUCCESS = "KV_FIRST_DOWNLOAD_VIDEO_SUCCESS"
    const val KV_IS_B_USER = "KV_IS_B_USER"
    const val KV_IS_A_USER = "KV_IS_A_USER"
    const val KV_IS_SUBSCRIBE_MEMBER = "KV_IS_SUBSCRIBE_MEMBER"
    const val KV_IS_SEND_B = "KV_IS_SEND_B"
    const val KV_DAY_DOWNLOAD_COUNT = "KV_DAY_DOWNLOAD_COUNT"
    const val KV_FIRST_TIME = "KV_FIRST_TIME"
    const val KV_NEWS_READ_COUNT = "KV_NEWS_READ_COUNT"
    const val KV_DAY_SHOW_ADD_SHORT = "KV_DAY_SHOW_ADD_SHORT2"
    const val KV_DAY_SHOW_BATTERY = "KV_DAY_SHOW_BATTERY"
    const val KV_DAY_SHOW_ADD_WIDGET = "KV_DAY_SHOW_ADD_SHORT"
    const val KV_FIRST_VIDEO = "KV_FIRST_VIDEO"
    const val KV_FIRST_DISCLAIMER = "KV_FIRST_DISCLAIMER"
    const val KV_RATE5 = "KV_RATE5"
    const val KV_ENGINE_GUIDE_FIRST = "KV_ENGINE_GUIDE_FIRST"
    const val KV_FIRST_SHOW_CLEAR = "KV_FIRST_SHOW_CLEAR"
    const val KV_FIRST_SHOW_BROWSER_DEFAULT = "KV_FIRST_SHOW_BROWSER_DEFAULT"
    const val KV_ENGINE_TYPE = "KV_ENGINE_TYPE"
    const val KV_TAB_DATA_NORMAL = "KV_TAB_DATA_NORMAL"
    const val KV_HOME_TAB = "KV_HOME_TAB"
    const val KV_TAB_DATA_PRIVATE = "KV_TAB_DATA_PRIVATE"
    const val KV_VIDEO_DOWNLOAD = "KV_VIDEO_DOWNLOAD_2"
    const val KV_VIDEO_DOWNLOAD_LIST = "KV_VIDEO_DOWNLOAD_LIST"
    const val KV_NEWS_LIST = "KV_NEWS_LIST"
    const val KV_VIDEO_DOWNLOAD_SINGLE = "KV_NEWS_LIST"
    const val KV_NEWS_VIDEO_LIST = "KV_NEWS_VIDEO_LIST"
    const val KV_TREND_NEWS_LIST = "KV_TREND_NEWS_LIST"
    const val KV_WEB_PAGE_LIST = "KV_WEB_PAGE_LIST"
    const val KV_WEB_FETCH_LIST = "KV_WEB_FETCH_LIST"
    const val KV_BROWSER_STATUS = "KV_BROWSER_STATUS"
    const val KV_RECENT_SEARCH_DATA = "KV_RECENT_SEARCH_DATA"
    const val KV_CITY_LIST = "KV_CITY_LIST"
    const val KV_LOCATION_DATA = "KV_LOCATION_DATA"
    const val KV_PHONE_ID = "KV_PHONE_ID"
    const val KV_NEWS_SAVE_TIME = "KV_NEWS_SAVE_TIME"
    const val KV_HISTORY_DATA = "KV_HISTORY_DATA"
    const val KV_HISTORY_DATA_JUMP = "KV_HISTORY_DATA_JUMP"
    const val KV_URL_LIST = "KV_URL_LIST"
    const val KV_TOPIC_LIST = "KV_TOPIC_LIST_NEW"
    const val KV_HOME_TOPIC_LIST = "KV_HOME_TOPIC_LIST"
    const val KV_ALL_TOPIC_LIST = "KV_ALL_TOPIC_LIST"
    const val KV_SAVE_DAY = "KV_SAVE_DAY"
    const val KV_GID = "KV_GID"
    const val KV_CAMPAIGN_ID = "KV_CAMPAIGN_ID"
    const val KV_CLICK_EVERY_DAY = "KV_CLICK_EVERY_DAY"
    const val KV_SHOW_EVERY_DAY = "KV_SHOW_EVERY_DAY"
    const val KV_LAST_LAUNCH_TIME = "KV_LAST_LAUNCH_TIME"
    const val KV_A_USER_TIME = "KV_A_USER_TIME"
    const val KV_CLEAN_TIME = "KV_CLEAN_TIME"
    const val KV_FIRST_SHOW_DOWNLOAD = "KV_FIRST_SHOW_DOWNLOAD"
    const val KV_FIRST_SHOW_DOWNLOAD_GUIDE = "KV_FIRST_SHOW_DOWNLOAD_GUIDE"
    const val KV_NEWS_NF_HISTORY = "KV_NEWS_NF_HISTORY"
    const val KV_NF_SHOW_LAST_TIME = "KV_NF_SHOW_LAST_TIME"
    const val KV_DAY_NF_SHOW_COUNT = "KV_DAY_NF_SHOW_COUNT"
    const val KV_DRAG_X = "KV_DRAG_X"
    const val KV_DRAG_Y = "KV_DRAG_Y"
    const val KV_FIRST_DOWNLOAD_TIPS = "KV_FIRST_DOWNLOAD_TIPS"
    const val KV_FIRST_DOWNLOAD_TIPS2 = "KV_FIRST_DOWNLOAD_TIPS2"
    const val KV_FIRST_DOWNLOAD_TIPS3 = "KV_FIRST_DOWNLOAD_TIPS3"
    const val KV_FIRST_DOWNLOAD_TIPS4 = "KV_FIRST_DOWNLOAD_TIPS4"
    const val KV_USER_DATA = "KV_USER_DATA"
    const val KV_VIDEO_UI_DATA = "KV_VIDEO_UI_DATA"
    const val KV_CITY_ADD_LIST = "KV_CITY_ADD_LIST"
    const val KV_PRELOAD_AD_COUNT = "KV_PRELOAD_AD_COUNT"
//    const val KV_FIRST_OPEN_APP = "KV_FIRST_OPEN_APP"
    const val KV_FIRST_ADD_SHORTCUT = "KV_FIRST_ADD_SHORTCUT"
    const val KV_AD_VALUE = "KV_AD_VALUE"
    const val KV_AD_001_VALUE = "KV_AD_001_VALUE"
    const val KV_AD_DAY_VALUE = "KV_AD_DAY_VALUE"
    const val KV_FIRST_VIDEO_GUIDE = "KV_FIRST_VIDEO_GUIDE"


    var videoDownloadTempList :MutableList<VideoUIData>
        get() {
            var list = getListByGson(mmkv.decodeString(KV_VIDEO_DOWNLOAD),VideoUIData::class.java)?: mutableListOf()
            return list
        }
        set(value) {
            mmkv.encode(KV_VIDEO_DOWNLOAD, toJson(value))
        }
    var videoPreTempList :MutableList<VideoUIData>
        get() {
            var list = getListByGson(mmkv.decodeString(KV_VIDEO_DOWNLOAD_LIST),VideoUIData::class.java)?: mutableListOf()
            return list
        }
        set(value) {
            mmkv.encode(KV_VIDEO_DOWNLOAD_LIST, toJson(value))
        }

    var videoDownloadSingleTempList :MutableList<VideoUIData>
        get() {
            var list = getListByGson(mmkv.decodeString(KV_VIDEO_DOWNLOAD_SINGLE),VideoUIData::class.java)?: mutableListOf()
            return list
        }
        set(value) {
            mmkv.encode(KV_VIDEO_DOWNLOAD_SINGLE, toJson(value))
        }
    // 是否首次打开start
    var installRefer: String
        get() {
            return mmkv.decodeString(KV_INSTALL_REFER)?:""
        }
        set(value) {
            mmkv.encode(KV_INSTALL_REFER, value)
        }
    var adJustFrom: String
        get() {
            return mmkv.decodeString(KV_ADJUST_FROM,"Organic")?:"Organic"
        }
        set(value) {
            mmkv.encode(KV_ADJUST_FROM, value)
        }
    var afFrom: String
        get() {
            return mmkv.decodeString(KV_AF_FROM,"Organic")?:"Organic"
        }
        set(value) {
            mmkv.encode(KV_AF_FROM, value)
        }
    var isFirstStart: Boolean
        get() {
            return mmkv.decodeBool(KV_FIRST_START, true)
        }
        set(value) {
            mmkv.encode(KV_FIRST_START, value)
        }
    var isFirstToDownload: Boolean
        get() {
            return mmkv.decodeBool(KV_FIRST_TO_DOWNLOAD, true)
        }
        set(value) {
            mmkv.encode(KV_FIRST_TO_DOWNLOAD, value)
        }

    var isFirstVideoGuide: Boolean
        get() {
            return mmkv.decodeBool(KV_FIRST_VIDEO_GUIDE, true)
        }
        set(value) {
            mmkv.encode(KV_FIRST_VIDEO_GUIDE, value)
        }

    var isFirstClickDownloadButton: Boolean
        get() {
            return mmkv.decodeBool(KV_FIRST_CLICK_DOWNLOAD_BUTTON, true)
        }
        set(value) {
            mmkv.encode(KV_FIRST_CLICK_DOWNLOAD_BUTTON, value)
        }

    var dayFirstDownloadVideoSuccess: Boolean
        get() {
            return mmkv.decodeBool(KV_FIRST_DOWNLOAD_VIDEO_SUCCESS, false)
        }
        set(value) {
            mmkv.encode(KV_FIRST_DOWNLOAD_VIDEO_SUCCESS, value)
        }

    var isBUser: Boolean
        get() {
            return mmkv.decodeBool(KV_IS_B_USER, false)
        }
        set(value) {
            mmkv.encode(KV_IS_B_USER, value)
        }

    var isAUser: Boolean
        get() {
            return mmkv.decodeBool(KV_IS_A_USER, false)
        }
        set(value) {
            mmkv.encode(KV_IS_A_USER, value)
        }

    var isSubscribeMember: Boolean
        get() {
            return mmkv.decodeBool(KV_IS_SUBSCRIBE_MEMBER, false)
        }
        set(value) {
            mmkv.encode(KV_IS_SUBSCRIBE_MEMBER, value)
        }

    var isSendB: Boolean
        get() {
            return mmkv.decodeBool(KV_IS_SEND_B, false)
        }
        set(value) {
            mmkv.encode(KV_IS_SEND_B, value)
        }

    var dayDownloadCount:Int
        get() {
            return mmkv.decodeInt(KV_DAY_DOWNLOAD_COUNT, 0)
        }
        set(value) {
            mmkv.encode(KV_DAY_DOWNLOAD_COUNT, value)
        }

    var firstTime:Long
        get() {
            return mmkv.decodeLong(KV_FIRST_TIME, 0)
        }
        set(value) {
            mmkv.encode(KV_FIRST_TIME, value)
        }

    var newsReadCount:Int
        get() {
            return mmkv.decodeInt(KV_NEWS_READ_COUNT, 0)
        }
        set(value) {
            mmkv.encode(KV_NEWS_READ_COUNT, value)
        }

    var dayShowAddWidget: Boolean
        get() {
            return mmkv.decodeBool(KV_DAY_SHOW_ADD_WIDGET, true)
        }
        set(value) {
            mmkv.encode(KV_DAY_SHOW_ADD_WIDGET, value)
        }

    var dayShowAddShort: Boolean
        get() {
            return mmkv.decodeBool(KV_DAY_SHOW_ADD_SHORT, true)
        }
        set(value) {
            mmkv.encode(KV_DAY_SHOW_ADD_SHORT, value)
        }

    var firstAddShortcut: Boolean
        get() {
            return mmkv.decodeBool(KV_FIRST_ADD_SHORTCUT, true)
        }
        set(value) {
            mmkv.encode(KV_FIRST_ADD_SHORTCUT, value)
        }

    var dayShowBattery: Boolean
        get() {
            return mmkv.decodeBool(KV_DAY_SHOW_BATTERY, true)
        }
        set(value) {
            mmkv.encode(KV_DAY_SHOW_BATTERY, value)
        }

    var isFirstShowDownload: Boolean
        get() {
            return mmkv.decodeBool(KV_FIRST_SHOW_DOWNLOAD, true)
        }
        set(value) {
            mmkv.encode(KV_FIRST_SHOW_DOWNLOAD, value)
        }
    var isFirstShowDownloadGuide: Boolean
        get() {
            return mmkv.decodeBool(KV_FIRST_SHOW_DOWNLOAD_GUIDE, true)
        }
        set(value) {
            mmkv.encode(KV_FIRST_SHOW_DOWNLOAD_GUIDE, value)
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
    var isRate5: Boolean
        get() {
            return mmkv.decodeBool(KV_RATE5, false)
        }
        set(value) {
            mmkv.encode(KV_RATE5, value)
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

    var isFirstDownloadTips:Boolean
        get() {
            return mmkv.decodeBool(KV_FIRST_DOWNLOAD_TIPS, true)
        }
        set(value) {
            mmkv.encode(KV_FIRST_DOWNLOAD_TIPS, value)
        }

    var isFirstDownloadTips2:Boolean
        get() {
            return mmkv.decodeBool(KV_FIRST_DOWNLOAD_TIPS2, true)
        }
        set(value) {
            mmkv.encode(KV_FIRST_DOWNLOAD_TIPS2, value)
        }

    var isFirstDownloadTips3:Boolean
        get() {
            return mmkv.decodeBool(KV_FIRST_DOWNLOAD_TIPS3, true)
        }
        set(value) {
            mmkv.encode(KV_FIRST_DOWNLOAD_TIPS3, value)
        }

    var isFirstDownloadTips4:Boolean
        get() {
            return mmkv.decodeBool(KV_FIRST_DOWNLOAD_TIPS4, true)
        }
        set(value) {
            mmkv.encode(KV_FIRST_DOWNLOAD_TIPS4, value)
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
    var campaignId: String
        get() {
            return mmkv.decodeString(KV_CAMPAIGN_ID)?:""
        }
        set(value) {
            mmkv.encode(KV_CAMPAIGN_ID, value)
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

    var AUserTime: Long
        get() {
            return mmkv.decodeLong(KV_A_USER_TIME)
        }
        set(value) {
            mmkv.encode(KV_A_USER_TIME, value)
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

    var homeTabList:MutableList<JumpData>
        get() {
            var list = getListByGson(mmkv.decodeString(KV_HOME_TAB),JumpData::class.java)
            if (list.isNullOrEmpty()){
                list = WebSourceManager.getDefaultTabJump()
                mmkv.encode(KV_HOME_TAB, toJson(list))
            }
            return list
        }
        set(value) {
            mmkv.encode(KV_HOME_TAB, toJson(value))
        }

    fun addHomeTab(data:JumpData){
        var list = homeTabList
        var index = -1
        for (i in 0 until list.size){
            if (list.get(i).jumpUrl == data.jumpUrl){
                index = i
                break
            }
        }
        if (index == -1){
            list.add(0,data)
        }
        homeTabList = list
    }

    fun removeHomeTab(data:JumpData){
        var list = homeTabList
        var index = -1
        for (i in 0 until list.size){
            if (list.get(i).jumpUrl == data.jumpUrl){
                index = i
                break
            }
        }
        if (index>=0){
            list.removeAt(index)
        }
        homeTabList = list
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


    fun getNewsSaveTime(topic:String): Long {
        return mmkv.decodeLong("${KV_NEWS_SAVE_TIME}_${topic}", 0)
    }
    fun saveNewsSaveTime(topic:String) {
        mmkv.encode("${KV_NEWS_SAVE_TIME}_${topic}", System.currentTimeMillis())
    }

    fun getNewsSaveList(topic:String): MutableList<NewsData> {
        return getListByGson(mmkv.decodeString("${KV_NEWS_LIST}_${topic}"),NewsData::class.java) ?: mutableListOf()
    }
    fun saveNewsSaveList(topic:String,list: MutableList<NewsData>) {
        mmkv.encode("${KV_NEWS_LIST}_${topic}", toJson(list))
    }

    var videoList:MutableList<NewsData>
        get() {

            return getListByGson(mmkv.decodeString(KV_NEWS_VIDEO_LIST),NewsData::class.java) ?: mutableListOf()
        }
        set(value) {
            mmkv.encode(KV_NEWS_VIDEO_LIST, toJson(value))
        }
    var trendNews:MutableList<NewsData>
        get() {

            return getListByGson(mmkv.decodeString(KV_TREND_NEWS_LIST),NewsData::class.java) ?: mutableListOf()
        }
        set(value) {
            mmkv.encode(KV_TREND_NEWS_LIST, toJson(value))
        }
    var trendNewsTime:Long
        get() {
            return mmkv.decodeLong("trendNewsTime", 0)
        }
        set(value) {
            mmkv.encode("trendNewsTime", value)
        }

    var pageList:MutableList<WebConfigData>
        get() {
            return getListByGson(mmkv.decodeString(KV_WEB_PAGE_LIST),WebConfigData::class.java) ?: mutableListOf()
        }
        set(value) {
            mmkv.encode(KV_WEB_PAGE_LIST, toJson(value))
        }

    var fetchList:MutableList<WebConfigData>
        get() {
            return getListByGson(mmkv.decodeString(KV_WEB_FETCH_LIST),WebConfigData::class.java) ?: mutableListOf()
        }
        set(value) {
            mmkv.encode(KV_WEB_FETCH_LIST, toJson(value))
        }


    fun saveRecentSearchData(data: JumpData){
        if (data.jumpTitle.isNullOrEmpty())return
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

    fun getLastRefreshTime(key:String):Long{
        AppLogs.dLog(NFManager.TAG,"getLastRefreshTime key:${key}")
        return mmkv.decodeLong("${key}_refreshTime",0L)
    }
    fun saveLastRefreshTime(key:String){
        AppLogs.dLog(NFManager.TAG,"saveLastRefreshTime key:${key}")
        mmkv.encode("${key}_refreshTime",System.currentTimeMillis())
    }

    fun getNFNewsList(key:String):MutableList<NewsData>{
        return getListByGson(mmkv.decodeString("${key}_nfNewsList"),NewsData::class.java) ?: mutableListOf()
    }

    fun saveNFNewsList(key: String,list:MutableList<NewsData>){
        mmkv.encode("${key}_nfNewsList", toJson(list))
    }


    var linkedUrlList :LinkedList<String>
        get() {
            return getLinkedListByGson(mmkv.decodeString(KV_URL_LIST),String::class.java) ?: LinkedList<String>()
        }
        set(value) {
            mmkv.encode(KV_URL_LIST, toJson(value))
        }

    var defaultTopicList :MutableList<TopicBean>
        get() {
            return getListByGson(mmkv.decodeString(KV_TOPIC_LIST,""),TopicBean::class.java)?: mutableListOf()
        }
        set(value) {
            mmkv.encode(KV_TOPIC_LIST, toJson(value))
        }

    var homeTopicList :MutableList<TopicBean>
        get() {
            return getListByGson(mmkv.decodeString(KV_HOME_TOPIC_LIST,""),TopicBean::class.java)?: mutableListOf()
        }
        set(value) {
            mmkv.encode(KV_HOME_TOPIC_LIST, toJson(value))
        }

    var allTopicList :MutableList<TopicBean>
        get() {
            return getListByGson(mmkv.decodeString(KV_ALL_TOPIC_LIST,""),TopicBean::class.java)?: mutableListOf()
        }
        set(value) {
            mmkv.encode(KV_ALL_TOPIC_LIST, toJson(value))
        }


    fun clearAll() {
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

    fun updateTempList(model: DownloadModel) {
        var list = videoDownloadTempList
        var needBreak = false
        for (i in 0 until list.size){
            list.get(i).formatsList.forEach {
                if (it.videoId == model.videoId){
                    it.downloadType = model.downloadType?:0
                    needBreak = true
                }
            }
            if (needBreak)break
        }
        videoDownloadTempList = list
    }

    var newsListHistoryData: String
        get() {
            return mmkv.decodeString(KV_NEWS_NF_HISTORY)?:""
        }
        set(value) {
            mmkv.encode(KV_NEWS_NF_HISTORY, value)
        }


    fun saveNewsListHistory(list: Vector<NewsTempData>){
        newsListHistoryData = gson.toJson(list)
    }

    fun saveNewsDataHistory(data:NewsData,tag:String){
        AppLogs.dLog(NFManager.TAG,"更新通知展示历史 tag:${tag}")
        getNewsListHistory()?.apply {
            AppLogs.dLog(NFManager.TAG,"原有历史 size: ${size}")
            var index = -1
            for (i in 0 until size){
                if (get(i).newsId == data.itackl){
                    index = i
                    break
                }
            }
            if (index == -1){
                add(NewsTempData().apply {
                    newsId = data.itackl?:""
                })
            }
            saveNewsListHistory(this)
        }
        if (APP.isDebug){
            AppLogs.dLog(NFManager.TAG,"更新后通知历史 size: ${getNewsListHistory()?.size}")
        }
    }
    fun getNewsListHistory(): Vector<NewsTempData>? {
        return getVectorByGson(newsListHistoryData,NewsTempData::class.java)
    }

    fun getNFShowLastTime(action: String): Long {
        return  mmkv.decodeLong("${KV_NF_SHOW_LAST_TIME}_$action",0)
    }

    fun saveNFShowLastTime(action: String,time:Long){
        mmkv.encode("${KV_NF_SHOW_LAST_TIME}_$action", time)
    }

    fun saveDayNFShowCount(action: String,count:Int=-1){
        mmkv.encode("${KV_DAY_NF_SHOW_COUNT}_$action", count)
    }

    fun getDayNFShowCount(action: String):Int{
        return mmkv.decodeInt("${KV_DAY_NF_SHOW_COUNT}_$action", 0)
    }

    var dragX: Int
        get() {
            return mmkv.decodeInt(KV_DRAG_X,0)
        }
        set(value) {
            mmkv.encode(KV_DRAG_X,value )
        }
    var dragY: Int
        get() {
            return mmkv.decodeInt(KV_DRAG_Y,0)
        }
        set(value) {
            mmkv.encode(KV_DRAG_Y,value )
        }

    fun saveUser(user:UserData?){
        mmkv.encode(KV_USER_DATA, toJson(user))
    }

    fun getUser():UserData?{
        return getBeanByGson(mmkv.decodeString(KV_USER_DATA,""),UserData::class.java)
    }

    fun removeAlreadyAddCity(data: LocationData){
        var list = alreadyAddCityList
        var index = -1
        for (i in 0 until list.size){
            if (list.get(i).locationCity == data.locationCity){
                index = i
                break
            }
        }
        if (index>=0){
            list.removeAt(index)
        }
        alreadyAddCityList = list
    }

    var alreadyAddCityList:MutableList<LocationData>
        get(){
            return getListByGson(mmkv.decodeString(KV_CITY_ADD_LIST,""),LocationData::class.java)?: mutableListOf()
        }
        set(value) {
            mmkv.encode(KV_CITY_ADD_LIST, toJson(value))
        }

    fun addAlreadyAddCity(data: LocationData?){
        if (data == null)return
        var list = alreadyAddCityList
        var index = -1
        for (i in 0 until list.size){
            if (list.get(i).locationCity == data.locationCity){
                index = i
                break
            }
        }
        if (index==-1){
            list.add(data)
        }
        alreadyAddCityList = list
    }

    var dayPreloadCount: Int
        get(){
            return mmkv.decodeInt(KV_PRELOAD_AD_COUNT, 0)
        }
        set(value) {
            mmkv.encode(KV_PRELOAD_AD_COUNT,value)
        }

//    var adValue :Double
//        get() {
//            return mmkv.decodeDouble(KV_AD_VALUE,0.0)
//        }
//        set(value) {
//            mmkv.encode(KV_AD_VALUE, value)
//        }


    var ad001Value :Double
        get() {
            return mmkv.decodeDouble(KV_AD_001_VALUE,0.0)
        }
        set(value) {
            mmkv.encode(KV_AD_001_VALUE, value)
        }

    var adDayValue :Double
        get() {
            return mmkv.decodeDouble(KV_AD_DAY_VALUE,0.0)
        }
        set(value) {
            mmkv.encode(KV_AD_DAY_VALUE, value)
        }

}