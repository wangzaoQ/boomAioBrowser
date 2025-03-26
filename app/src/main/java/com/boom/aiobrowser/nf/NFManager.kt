package com.boom.aiobrowser.nf

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.APP.Companion.isDebug
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.AioNFData
import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.nf.NFShow.getForegroundNF
import com.boom.aiobrowser.other.ShortManager
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValue
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.appDataReset
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.other.ParamsConfig
import com.boom.aiobrowser.other.isAndroid12
import com.boom.aiobrowser.other.isAndroid14
import com.boom.aiobrowser.tools.allowShowNF
import com.boom.aiobrowser.tools.getListByGson
import com.boom.aiobrowser.tools.jobCancel
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.Locale
import java.util.Objects
import kotlin.random.Random

object NFManager {

    var TAG = "NFManager"

    const val FROM_TIMER: String = "timer"
    const val FROM_WORK: String = "work"
    const val FROM_UNLOCK: String = "unlock"
    const val FROM_FCM: String = "fcm"

    var nfRootBean: AioNFData? = null

    val videoNFMap = LinkedHashMap<String,Int>()
    val videoTimeMap = HashMap<String,Long>()

    @Volatile
    var nfForeground:Notification?=null
    var defaultNewsList:MutableList<NewsData>?=null


    val manager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(APP.instance)
    }

    /**
     * fcm 用 defaultChannel
     * 正常通知 用 enum.channelId
     */
    fun newChannel(enum: NFEnum,defaultChannel:String="") {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var endChannel = if (defaultChannel.isNullOrEmpty()) enum.channelId else defaultChannel
            val channel: NotificationChannel? = manager.getNotificationChannel(endChannel)
            if (Objects.isNull(channel)) {
                val channelNew: NotificationChannel = NotificationChannel(endChannel, endChannel, enum.channelPriority)
                if (endChannel == NFEnum.NF_NEW_USER.channelId || endChannel == NFEnum.NF_NEWS.channelId || endChannel == defaultChannel){
                    channelNew!!.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 600) //设置震动频率
                    channelNew!!.enableVibration(true) //设置是否绕过免打扰模式
                }else{
                    channelNew.setSound(null,null)
                }
                channelNew.setShowBadge(true)
                channelNew.lockscreenVisibility = Notification.VISIBILITY_PUBLIC;
                manager.createNotificationChannel(channelNew)
            }
        }
    }

    fun nfAllow():Boolean{
        var refuseContent = ""
        if (XXPermissions.isGranted(APP.instance, Permission.POST_NOTIFICATIONS).not()){
            refuseContent = "通知无权限"
        }
        if (refuseContent.isNullOrEmpty()){
            AppLogs.dLog(NFManager.TAG,"NF allow ")
            return true
        }else{
            AppLogs.dLog(NFManager.TAG,"NF refuse: $refuseContent")
            return false
        }
    }

    fun requestNotifyPermission(weakReference: WeakReference<BaseActivity<*>>,onSuccess: () -> Unit = {}, onFail: () -> Unit = {}) {
        var activity = weakReference.get()
        if (activity == null){
            onFail.invoke()
            return
        }
        val hasPermission = XXPermissions.isGranted(
            activity!!,
            Permission.POST_NOTIFICATIONS
        )
        if (hasPermission){
            onSuccess.invoke()
            return
        }
        val xxPermissions = XXPermissions.with(activity!!)
        xxPermissions.permission(Permission.POST_NOTIFICATIONS)
        runCatching {
            xxPermissions.request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                    AppLogs.dLog(TAG,"onGranted:${allGranted}")
                    PointEvent.posePoint(PointEventKey.noti_req_allow)
                    onSuccess.invoke()
                }

                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                    super.onDenied(permissions, doNotAskAgain)
                    PointEvent.posePoint(PointEventKey.noti_req_refuse)
                    onFail.invoke()
                }
            })
        }.onFailure {
            onFail.invoke()
        }
    }

    fun clickPoint(nfData: String, nfTo: Int,enumName: String,isLaunch:Boolean) {

        var from = ""
        if (isLaunch){
            from = "launch"
        }
        when (enumName) {
            NFEnum.NF_DOWNLOAD_VIDEO.menuName -> {
                from = "download_push"
                var data = getBeanByGson(nfData,VideoDownloadData::class.java)
                // 0 进度中点击 1 失败点击 2成功点击  3 成功点击观看视频
                when (nfTo) {
                     0-> {
                         PointEvent.posePoint(PointEventKey.all_noti_c, Bundle().apply {
                             putString(PointValueKey.video_url, data?.url?:"")
                             putString(PointValueKey.push_type, PointEventKey.download_push_conduct)
                         })
                     }
                    1->{
                        PointEvent.posePoint(PointEventKey.all_noti_c, Bundle().apply {
                            putString(PointValueKey.video_url, data?.url?:"")
                            putString(PointValueKey.push_type, PointEventKey.download_push_fail)
                        })
                    }
                    2,3->{
                        PointEvent.posePoint(PointEventKey.all_noti_c, Bundle().apply {
                            putString(PointValueKey.video_url, data?.url?:"")
                            putString(PointValueKey.push_type, PointEventKey.download_push_success)
                        })
                        runCatching {
                            manager.cancel(data?.nfId?:0)
                        }
                    }
                    else -> {}
                }
            }
            NFEnum.NF_SEARCH_VIDEO.menuName -> {
                from = "search_push"
                // 0 暂无 1 点击search  2-4 右侧按钮
                var type = ""
                if (nfTo == 0){
                    type = "home"
                }else if (nfTo == 1){
                    type = "search"
                }else if (nfTo == 2){
                    type = "x"
                }else if (nfTo == 3){
                    type = "ins"
                }else if (nfTo == 4){
                    type = "download"
                }
                PointEvent.posePoint(PointEventKey.fixed_explore, Bundle().apply {
                    putString(PointValueKey.type,type)
                })
            }
            NFEnum.NF_NEWS.menuName,NFEnum.NF_LOCAL.menuName,NFEnum.NF_HOT.menuName,NFEnum.NF_EDITOR.menuName,NFEnum.NF_UNLOCK.menuName,NFEnum.NF_NEW_USER.menuName,NFEnum.NF_DEFAULT.menuName,NFEnum.NF_TREND.menuName -> {
                var data :NewsData?=null
                var dataList = getListByGson(nfData,NewsData::class.java)
                if (dataList.isNullOrEmpty().not()){
                    data = dataList!!.get(0)
                }else{
                    data = getBeanByGson(nfData,NewsData::class.java)
                }

                from = "news_push"
                PointEvent.posePoint(PointEventKey.all_noti_c, Bundle().apply {
                    putString(PointValueKey.push_type, enumName)
                    putString(PointValueKey.news_id, data?.itackl?:"")
                    if (enumName == NFEnum.NF_DEFAULT.menuName && data!=null){
                        putString(PointValueKey.source_from, data?.nfSource?:"")
                    }
                })
                var id = NFEnum.NF_NEWS.position
                when (enumName) {
                    NFEnum.NF_LOCAL.menuName -> {
                        id = NFEnum.NF_LOCAL.position
                    }
                    NFEnum.NF_HOT.menuName -> {
                        id = NFEnum.NF_HOT.position
                    }
                    NFEnum.NF_EDITOR.menuName -> {
                        id = NFEnum.NF_EDITOR.position
                    }
                    NFEnum.NF_UNLOCK.menuName -> {
                        id = NFEnum.NF_UNLOCK.position
                    }
                    NFEnum.NF_NEW_USER.menuName -> {
                        id = NFEnum.NF_NEW_USER.position
                    }
                    NFEnum.NF_DEFAULT.menuName -> {
                        id = NFEnum.NF_DEFAULT.position
                    }
                    NFEnum.NF_TREND.menuName->{
                        id = NFEnum.NF_TREND.position
                    }
                    else -> {}
                }
                runCatching {
                    manager.cancel(id)
                }
            }
            NFEnum.NF_NEWS_FCM.menuName->{
                var data = getBeanByGson(nfData,NewsData::class.java)

                from = "push"
                PointEvent.posePoint(PointEventKey.all_noti_c, Bundle().apply {
                    putString(PointValueKey.push_type, enumName)
                    putString(PointValueKey.news_id, data?.itackl?:"")
                })
                getBeanByGson(nfData,NewsData::class.java)?.apply {
                    runCatching {
                        manager.cancel(nId)
                    }
                }
            }
            NFEnum.NF_POINTS_DAY0.menuName,NFEnum.NF_POINTS_DAY1.menuName,NFEnum.NF_POINTS_DAY3.menuName->{
                from = enumName
                var fromType = when (enumName) {
                    NFEnum.NF_POINTS_DAY0.menuName -> {
                        "d0"
                    }
                    NFEnum.NF_POINTS_DAY1.menuName -> {
                        "d1"
                    }
                    NFEnum.NF_POINTS_DAY3.menuName -> {
                        "d2"
                    }
                    else -> {
                        "other"
                    }
                }
                PointEvent.posePoint(PointEventKey.points_push_c, Bundle().apply {
                    putString(PointValueKey.push_type, fromType)
                })
                var id = NFEnum.NF_POINTS_DAY0.position
                when (enumName) {
                    NFEnum.NF_POINTS_DAY1.menuName -> {
                        id = NFEnum.NF_POINTS_DAY1.position
                    }
                    NFEnum.NF_POINTS_DAY3.menuName -> {
                        id = NFEnum.NF_POINTS_DAY3.position
                    }
                    else -> {}
                }
                runCatching {
                    manager.cancel(id)
                }
            }
            ParamsConfig.WIDGET->{
                from = "widget_short"
                if (nfTo == 1){
                    PointEvent.posePoint(PointEventKey.widget_click)
                    ShortManager.widgetUpdate(APP.instance,"widgetClick")
                }else{
                    PointEvent.posePoint(PointEventKey.widget_search)
                }
            }
            ParamsConfig.SHORT->{
                from = "shoetcut"
            }
            else -> {}
        }
        if (from.isNotEmpty()){
            PointEvent.posePoint(PointEventKey.launch_page, Bundle().apply {
                putInt(PointValueKey.open_type,if (CacheManager.isFirstStart) 0 else 1)
                putString(PointValueKey.type,from)
            })
        }
    }

    @SuppressLint("MissingPermission")
    fun startForeground(tag: String) {
        AppLogs.dLog(NFManager.TAG,"前台服务 触发 ${tag}-${Build.VERSION.SDK_INT}")
        if (nfAllow().not())return
        if (isAndroid12() ){
            AppLogs.dLog(NFManager.TAG,"大于12 不允许后台展示前台服务${Build.VERSION.SDK_INT}")
            return
        }
        if (isAndroid14()){
//            if (XXPermissions.isGranted(APP.instance, Permissions.FOREGROUND_SERVICE_DATA_SYNC, Permission.ACCESS_COARSE_LOCATION).not())return
        }
//        if (RomUtils.isOneplus() && CacheManager.showForeground <=1){
        if (CacheManager.showForeground <=1){
            AppLogs.dLog(NFManager.TAG,"前台服务 第一次不启动")
            return
        }
//        AppLogs.dLog(NFManager.TAG,"前台服务 允许展示 ${tag}-${Build.VERSION.SDK_INT}")
        runCatching {
            if (APP.instance.showForeground.not()){
                Handler(Looper.getMainLooper()).postDelayed({
                    var nf = getForegroundNF()
                    if (nf!=null){
                        NFManager.manager.notify(NFEnum.NF_SEARCH_VIDEO.position,NFManager.nfForeground!!)
                        ContextCompat.startForegroundService(APP.instance,
                            Intent(APP.instance, NFService::class.java)
                        )
                    }
                },1000)
            }else{
                AppLogs.dLog(NFManager.TAG,"前台服务 正在展示中不需要重复启动 ${tag}-${Build.VERSION.SDK_INT}")
            }
        }.onFailure {
            AppLogs.eLog(NFManager.TAG,it.stackTraceToString())
        }
    }

    /**
     *     {
     *         "channel_id": "ch_a",
     *         "NEWS_ID": "8993843577094145",
     *         "tag": "tg_a",
     *         "body": "33.A driver was brutally attacked by a large group of people after having his vehicle damaged in downtown Los Angeles.",
     *         "image": "https://clevertap.com/wp-content/uploads/2021/05/Push-Notification-Header.png?w\u003d1024",
     *         "title": "33-ck_a-ch_a-lbl_a-tg_a",
     *         "KEY_NOW_NAV_TYPE": "3"
     *     }
     */
    fun showFCM(map: Map<String, String>) {
        var chanel = map.get("channel_id")?:""
        var tag = map.get("tag")?:""
        var body = map.get("body")?:""
        var image = map.get("image")?:""
        var newsId = map.get("NEWS_ID")?:""
        var title = map.get("title")?:""
        var url = map.get("news_url")?:""
        if (tag.isNullOrEmpty()){
            tag = "fcm"
        }
        if (chanel.isNullOrEmpty()){
            chanel = NFEnum.NF_NEWS_FCM.channelId
        }
        var data = NewsData().apply {
            tconsi = title
            iassum = image
            itackl = newsId
            uweek = url
            sissue = body
            this.channel = chanel
            this.tag = tag
            this.nId = Random.nextInt(300000 - 200000 + 1) + 200000
        }
        NFShow.showNewsNF(data,NFEnum.NF_NEWS_FCM,FROM_FCM)
    }

    fun needRefreshData(key:String): Boolean {
        var oldTime = CacheManager.getLastRefreshTime(key)
        var limit = 3 * 60 * 60 * 1000
        var refresh = (System.currentTimeMillis() - oldTime) > limit
        AppLogs.dLog(
            NFManager.TAG,
            "name:${key} 判断是否需要强制刷新数据/refresh:${refresh} key:${key} oldTime:${oldTime}"
        )
        return refresh
    }

    var showCount = 0

    var timerJob: Job?=null

    fun notifyByTimerTask() {
        timerJob.jobCancel()
        NFShow.showForegroundNF()
        timerJob = CoroutineScope(Dispatchers.IO).launch{
            appDataReset()
            if (allowShowNF()){
                NFWorkManager.startNF()
            }
            delay(10*1000)
            while (true) {
                if (allowShowNF()){
                    showNFByCount()
                    showCount++
                }
                delay((if (APP.isDebug)1 else 10)*60*1000L)
                appDataReset()
            }
        }
    }

    private suspend fun showNFByCount() {
        var count = showCount%4
        when (count) {
            0 -> {
                NFShow.showNewsNFFilter(NFEnum.NF_NEW_USER)
            }
            1 -> {
                NFShow.showNewsNFFilter(NFEnum.NF_NEWS)
            }
            2 -> {
                NFShow.showNewsNFFilter(NFEnum.NF_EDITOR)
            }
            3->{
                NFShow.showNewsNFFilter(NFEnum.NF_LOCAL)
            }
            4->{
                NFShow.showNewsNFFilter(NFEnum.NF_TREND)
                PointEvent.posePoint(PointEventKey.session_st)
            }
//            5->{
//                NFShow.showNewsNFFilter(NFEnum.NF_HOT)
//            }
            else -> {}
        }
    }

}