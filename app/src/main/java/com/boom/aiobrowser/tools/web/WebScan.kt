package com.boom.aiobrowser.tools.web

import android.net.Uri
import android.text.TextUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.PManageData
import com.boom.aiobrowser.data.PVideoData
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.data.VideoDownloadData.Companion.TYPE_M3U8
import com.boom.aiobrowser.data.VideoUIData
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.clean.formatSize
import com.boom.aiobrowser.tools.extractDomain
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.tools.toJson
import com.boom.downloader.VideoInfoParserManager
import com.boom.downloader.model.Video
import com.boom.downloader.model.Video.Mime.MIME_TYPE_MP4
import com.boom.downloader.utils.HttpUtils
import com.boom.downloader.utils.VideoDownloadUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import java.util.regex.Pattern


object WebScan {

    var TAG = "WebScan"

    var isloading = false

    val videoTypeList = mutableListOf<String>(
        MIME_TYPE_MP4, "m3u8"
    )

    val mp4ContinueList = mutableListOf<String>(
        WebConfig.PORNHUB,WebConfig.XNXX,WebConfig.XVIDEOS,WebConfig.FMOVIES,WebConfig.TOPFLIX,WebConfig.YOUPORN,WebConfig.PORNHAT,WebConfig.Xhamster
    )
    @Volatile
    var loadResourceList = mutableListOf<String>()
    fun filterUri(uri: String, reference: WeakReference<BaseActivity<*>>) {
        var activity: BaseActivity<*>? = reference.get() ?: return
        var type = ""
        uri?.apply {
            var url = uri.toString()
//            if (url.contains(WebConfig.FILTER_TIKTOK, true)) {
//                var cookieManager = CookieManager.getInstance()
//                var cookieTikTok = cookieManager.getCookie(url)?:""
//                if (APP.isDebug){
//                    AppLogs.dLog(TAG, "判断 0 cookie :${cookieTikTok}  命中tiktok uri:${uri}")
//                }
//                if (cookieTikTok.isNullOrEmpty())return
//                type = WebConfig.TIKTOK
//                if (APP.isDebug){
//                    AppLogs.dLog(TAG, "判断 1 isloading :${isloading}  命中tiktok uri:${uri}")
//                }
//                var id = "tiktok_${getTkTokId(url)}"
//                var list = CacheManager.videoDownloadTempList
//                var allow = true
//                for (i in 0 until list.size) {
//                    var data = list.get(i)
//                    if (data.videoId == id) {
//                        allow = false
//                        break
//                    }
//                }
//                if (APP.isDebug){
//                    AppLogs.dLog(TAG, "判断 2 allow:${allow} list:${toJson(CacheManager.videoDownloadTempList)}")
//                }
//                if (allow) {
//                    var map = HashMap<String, Any>()
//                    map.put("Cookie", cookieTikTok)
//                    var data = VideoDownloadData().createDefault(
//                        videoId = id,
//                        fileName = id,
//                        url = url,
//                        imageUrl = url,
//                        paramsMap = map,
//                        size = BigDecimal(0).toLong(),
//                        videoType = ""
//                    )
//                    var list = CacheManager.videoDownloadTempList
//                    list.clear()
//                    list.add(0, data)
//                    CacheManager.videoDownloadTempList = list
//                    if (isloading.not()) {
//                        AppLogs.dLog(TAG, "判断 3 filterUri 发送数据变化 id:${data.videoId}")
//                        APP.videoScanLiveData.postValue(data)
//                        getVideoHeaderInfo(type, url, cookieTikTok)
//                    }
//                }
//            } else
            if (url.contains(WebConfig.FILTER_PORNHUB, true)) {
                activity?.addLaunch(success = {
                    if (url.contains("pornhub.com/view_video.php?viewkey", true)) {
                        var startTime = System.currentTimeMillis()
                        var doc = Jsoup.connect(url).get()

                        var element = doc.getElementById("videoElementPoster")
                        var img = element.attr("src")
                        var title = doc.title()
                        val scripts = doc.getElementsByTag("script")
                        val pattern = Pattern.compile("var\\s+flashvars_\\d+\\s*=\\s*(\\{.*?\\});")
                        var json = ""
//                        var content = scripts.get(46).html()
//                        val matcher = pattern.matcher(content)
                        for (i in 0 until scripts.size) {
                            var data = scripts.get(i)
                            val scriptContent = data.html()
                            val matcher = pattern.matcher(scriptContent)
                            if (matcher.find()) {
//                                AppLogs.dLog(TAG,"p 站视频数据:${matcher.group(1)}")
                                json = matcher.group(1)
                                break
                            }
                        }
                        var pData = getBeanByGson(json, PManageData::class.java)
                        var pVideo: PVideoData? = null
                        pData?.mediaDefinitions?.forEach {
                            if (it.defaultQuality) {
                                pVideo = it
                            }
                        }
                        var videoUrl = pVideo?.videoUrl
//                        val videoPattern = Pattern.compile("(\\d+K)")
//                        val matcher = videoPattern.matcher(videoUrl)
//                        if (matcher.find()){
//                            val group = matcher.group(1)
//                            bitRate = BigDecimal(group.substring(0,group.length-1)).toLong()
//                            bitRate = bitRate.getFileSize(pData?.video_duration?:0)
//                        }
//                        bitRate =

                        var allow = true
                        var videoId = "video_${url}"
                        var list = CacheManager.videoDownloadTempList
                        list.forEach {
                            it.formatsList.forEach {
                                if (it.videoId == videoId) {
                                    allow = false
                                }
                            }
                        }
                        if (allow.not()) return@addLaunch
                        var size = PManager.parseNetworkM3U8InfoByP(
                            videoUrl ?: "",
                            HashMap<String, String>(),
                            0,
                            pData?.video_duration ?: 0
                        )
                        AppLogs.dLog(
                            TAG, "p站  title :${doc.title()}-imgs:${img}" +
                                    "文件大小" + size.formatSize() + "-" +
                                    " 视频播放地址" + videoUrl + "-" +
                                    " 视频播放时长" + "${pData?.video_duration ?: 0}" + "-" +
                                    " 耗时:${System.currentTimeMillis() - startTime}"
                        )
                        var uiData = VideoUIData()

                        var map = HashMap<String, Any>()
                        var data = VideoDownloadData().createDefault(
                            videoId = videoId,
                            fileName = pData?.video_title ?: "",
                            url = videoUrl ?: "",
                            imageUrl = pData?.image_url ?: "",
                            paramsMap = map,
                            size = size,
                            videoType = TYPE_M3U8,
                            resolution = ""
                        )
                        uiData.formatsList.add(data)
                        uiData.videoResultId = videoId + "_parent"
                        uiData.source = "page"
                        uiData.thumbnail = pData?.image_url ?: ""
                        uiData.description = pData?.video_title ?: ""
                        list.add(0, uiData)
                        CacheManager.videoDownloadTempList = list
                        AppLogs.dLog(TAG, "filterUri 发送数据变化 id:${data.videoId}")
//                        APP.videoScanLiveData.postValue(uiData)
                    }
                }, failBack = {})
            } else {
                activity?.addLaunch(success = {
                    var doc = Jsoup.connect(url).get()
                    // 获取所有的 <video> 标签
                    val videoElements = doc.select("video")
                    var videoList = mutableListOf<String>()
                    for (videoElement in videoElements) {
                        // 直接获取 <video> 标签的 src 属性
                        val videoSrc = videoElement.attr("src")
                        // 如果 <video> 标签本身有 src
                        if (!videoSrc.isEmpty()) {
                            videoList.add(videoSrc)
                        }

                        // 有些视频可能放在 <source> 标签中
                        val sourceElements = videoElement.select("source")
                        for (sourceElement in sourceElements) {
                            val sourceSrc = sourceElement.attr("src")
                            videoList.add(sourceSrc)
                        }

                        // 这里假设视频地址存储在 data-src 属性中
                        val videoLinks = doc.select("[data-src]")

                        for (videoLink in videoLinks) {
                            val videoUrl = videoLink.attr("data-src")
                            videoList.add(videoUrl)
                        }
                    }
                    val iframe = doc.select("iframe").first()
                    if (iframe != null) {
                        val iframeSrc = iframe.attr("src")
                        videoList.add(iframeSrc)
                    }

                    var title = doc.title()
                    AppLogs.dLog(
                        "webReceive",
                        "网页 title :${title} 获取网页video:${toJson(videoList)}"
                    )
                }, failBack = {})
            }
        }
    }



    suspend fun getResourceInfo(
        videoUrl: String,
        cookie: String,
        realUrl: String,
        hostList:MutableList<String>) {
        var map = getVideoHeaderInfo(videoUrl, cookie,realUrl)
        var contentLength = 0L
        var contentType = ""
        contentType = map.get(content_type) as? String ?: ""
        contentLength = map.get(content_length) as? Long ?: 0L
        if (videoTypeList.any { contentType.equals(it, true) }.not()){
//            AppLogs.dLog("webReceive","原生 videoType 无匹配1")
            return
        }
        var imageUrl = ""
        var videoType = ""
        var videoFileName = ""
        if (contentLength<=0){
            AppLogs.dLog("webReceive","原生 contentLength = 0 videoType:${contentType} url:${videoUrl}")
            return
        }
        var fileStart = ""
        if (fileStart.isNullOrEmpty()) {
            runCatching {
                if (fileStart.isNullOrEmpty()) {
                    var uri = Uri.parse(videoUrl)
                    var split = uri.host?.split(".")
                    if (split!!.size > 0) {
                        fileStart = "${split[0]}_${System.currentTimeMillis()}"
                    }
                }
            }
        }
        if (fileStart.isNullOrEmpty()) {
            fileStart = "${System.currentTimeMillis()}"
        }
        if (contentType == MIME_TYPE_MP4) {
            videoType = "mp4"
            imageUrl = videoUrl
            videoFileName = videoUrl
            AppLogs.dLog("webReceive","videoType mp4 匹配 url:${videoUrl}")
            hostList.apply {
                if (isContinueMp4Host(this)){
                    AppLogs.dLog("webReceive","原生 过滤mp4")
                    return
                }
            }
        } else if (contentType == "m3u8") {
            videoType = "m3u8"
            // 获取 Open Graph 元数据中的封面图 URL
            imageUrl = map.get(content_img) as? String?:""
            AppLogs.dLog("webReceive","videoType m3u8 匹配 url:${videoUrl}")
//            imageUrl = loadImg
        } else {
            AppLogs.dLog("shouldInterceptRequest","videoType ${contentType} 不匹配 url:${videoUrl}")
//            videoType = "video"
//            imageUrl = videoUrl
        }
        if (videoType.isNullOrEmpty()){
//            AppLogs.dLog("webReceive","原生 videoType 无匹配1")
            return
        }
        if (loadResourceList.contains(videoUrl)){
            AppLogs.dLog("webReceive","原生 过滤重复数据 url:${videoUrl}")
            return
        }
        loadResourceList.add(videoUrl)
//        AppLogs.dLog("webReceive", "videoType 通过 getResourceInfo:${videoUrl}")
        var paramsMap = HashMap<String, Any>()
        paramsMap.put("Cookie", cookie)
        var title = map.get(content_title) as? String?:""
        videoFileName = if (TextUtils.isEmpty(title)) "${fileStart}.${videoType}" else  "${title}.${videoType}"
        var videoDownloadData = VideoDownloadData().createDefault(
            videoId = "${VideoDownloadUtils.computeMD5(videoUrl)}",
            fileName = videoFileName,
            url = videoUrl,
            imageUrl = imageUrl,
            paramsMap = paramsMap,
            size = contentLength ?: 0L,
            videoType = videoType,
            resolution = ""
        )
        var list = CacheManager.videoDownloadTempList
//        var baseUrl = URL(URL(videoUrl), "1").toString()
        var baseUrl = URL(videoUrl).host
        var isAdd = false
        list.forEach {
            if (it.formatsList.isNotEmpty()&& videoType == "m3u8"){
//                var isOldData = false
//                for (j in 0 until it.formatsList.size){
//                    if (videoUrl == it.formatsList.get(j).url){
//                        isOldData = true
//                        break
//                    }
//                }
//                if (isOldData){
//                    AppLogs.dLog("webReceive","js 过滤重复数据2")
//                    return@forEach
//                }

                var tempDownloadData =  it.formatsList.get(0)
                var tempDataBaseUrl = URL(tempDownloadData.url).host
                if (tempDataBaseUrl == baseUrl){
                    var resolution = extractResolution(videoUrl)?:""
                    if (resolution.isNullOrEmpty()){
                        var index = videoUrl?.lastIndexOf("/")?:0
                        if (index>0){
                            resolution = videoUrl?.substring(index+1)?:""
                        }
                    }
                    //同一来源
                    isAdd = true
                    videoDownloadData.resolution = resolution?:""
//                    it.formatsList.add(videoDownloadData)
                    AppLogs.dLog("urlAdd", "原生 过滤后 加入到同一数url:${videoUrl}")
                    var formatsList = it.formatsList
                    runCatching {
                        var oldData = formatsList.get(it.formatsList.size-1)
                        var oldResolution = oldData.resolution?:""
                        var newResolution = resolution
                        if (oldResolution.contains("p",true) && newResolution.contains("p",true)){
                            runCatching {
                                oldResolution = oldResolution.substring(0,oldResolution.indexOf("p", startIndex = 0, ignoreCase=true))
                                newResolution = newResolution.substring(0,newResolution.indexOf("p", startIndex = 0, ignoreCase=true))
                                if (newResolution.toInt()>oldResolution.toInt()){
                                    formatsList.add(0,videoDownloadData)
                                }else{
                                    formatsList.add(videoDownloadData)
                                }
                            }.onFailure {
                                formatsList.add(videoDownloadData)
                            }
                        }else{
                            formatsList.add(videoDownloadData)
                        }
                    }
                }
            }
        }
        if (isAdd.not()){
            var uiData = VideoUIData()
            uiData.description = map.get(content_title) as? String?:""
            var resolution = extractResolution(videoUrl)
            if (resolution.isNullOrEmpty()){
                var urlIndex = videoUrl?.lastIndexOf("/")?:0
                if (urlIndex>0){
                    resolution = videoUrl?.substring(urlIndex+1)?:""
                }
            }
            videoDownloadData.resolution = resolution
            uiData.formatsList.add(videoDownloadData)
            uiData.videoResultId = "${VideoDownloadUtils.computeMD5(videoUrl)}"
            uiData.thumbnail = imageUrl
            list.add(0, uiData)
            CacheManager.videoDownloadTempList = list
            AppLogs.dLog("urlAdd", "原生 过滤后 非引导加入不同数据源:url:${videoUrl}")
            APP.videoScanLiveData.postValue(0)
        }else{
            CacheManager.videoDownloadTempList = list
            APP.videoScanLiveData.postValue(0)
        }

    }

    private var call: Call? = null

    var content_length = "contentLength"
    var content_type = "contentType"
    var content_img = "imageUrl"
    var content_title = "webTitle"

    var webTitle = ""

    suspend fun getVideoHeaderInfo(videoUrl: String, cookie: String,realUrl:String): Map<String, Any> {
        var infoMap = HashMap<String, Any>()
        var connection: HttpURLConnection? = null
        var headers = HashMap<String, String>().apply {
            put("Cookie", cookie)
            put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
        }
        // Redirect is enabled, send redirect request to get final location.
        var finalUrl = videoUrl
        try {
            connection = HttpUtils.getConnection(
                finalUrl,
                headers,
                VideoDownloadUtils.getDownloadConfig().shouldIgnoreCertErrors(),true
            )
        } catch (e: Exception) {
            AppLogs.dLog("webReceive","原生 无法创建链接 url:${finalUrl}")
            HttpUtils.closeConnection(connection)
            return infoMap
        }
        if (connection == null) {
            AppLogs.dLog("webReceive","原生 链接为 null url:${finalUrl}")
            return infoMap
        }
        finalUrl = connection.url.toString()
        if (TextUtils.isEmpty(finalUrl)) {
            HttpUtils.closeConnection(connection)
            return infoMap
        }
        val contentType = connection.contentType
//        finalUrl = "https://cdn77-vid.xnxx-cdn.com/g_RrfJQs9IIf11KsD3AkWQ==,1736930045/videos/hls/bf/5d/ca/bf5dcaaf9b3e82dff80c3ebb0ea7f9da/hls-250p-0b616.m3u8"
        if (finalUrl.contains(Video.TypeInfo.M3U8) || VideoDownloadUtils.isM3U8Mimetype(contentType)) {
            AppLogs.dLog("webReceive","原生 视频格式为 m3u8 url:${finalUrl}")
            AppLogs.dLog("m3u8","m3u8 finalUrl:${finalUrl} cookie:${cookie}")
            //这是M3U8视频类型
            infoMap.put(content_type, "m3u8")
            AppLogs.dLog("m3u8","calculateM3U8Size Start")
            AppLogs.dLog("m3u8","realUrl:${realUrl}")
            var hostList = extractDomain(realUrl)
            var m3u8Size = PManager.calculateM3U8Size(finalUrl,headers,hostList)
            AppLogs.dLog("m3u8","calculateM3U8Size end")

            if (isXhaMaster(hostList) && m3u8Size<10*1024*1024){
                AppLogs.dLog("webReceive","过滤调广告 url:${finalUrl}")
                return infoMap
            }
            var startTime = System.currentTimeMillis()
            runBlocking {
                while (webTitle.isNullOrEmpty()&&(System.currentTimeMillis()-startTime)<3000L){
                    delay(500)
                }
            }
            infoMap.put(content_length,m3u8Size)
            infoMap.put(content_img, imageUrl)
            infoMap.put(content_title, webTitle)
        } else {
            //这是非M3U8类型, 需要获取视频的totalLength ===> contentLength
            val contentLength: Long = VideoInfoParserManager.getInstance()
                .getContentLength(finalUrl, headers, connection, false)
            if (contentLength == VideoDownloadUtils.DEFAULT_CONTENT_LENGTH || contentLength<1024 *1024) {
                HttpUtils.closeConnection(connection)
                return infoMap
            }
            infoMap.put(content_type, contentType)
            infoMap.put(content_length, contentLength)
            infoMap.put(content_title, webTitle)
        }
        HttpUtils.closeConnection(connection)
//        AppLogs.dLog("webReceive","infoMap:${toJson(infoMap)} url:${videoUrl}")
        return infoMap
    }


    fun isTikTok(url: String): Boolean {
        return url.contains(WebConfig.TIKTOK, true)
    }

    fun isPornhub(url: String): Boolean {
        return url.contains(WebConfig.PORNHUB, true)
    }

    fun isYoutube(url: String): Boolean {
        return url.contains(WebConfig.YOUTUBE, true)
    }

    fun isVimeo(urlList: MutableList<String>): Boolean {
        var index = -1
        for (i in 0 until urlList.size) {
            var url = urlList.get(i)
            if (url.equals(WebConfig.VIMEO, true)) {
                index = i
                break
            }
        }
        return (index == -1).not()
    }

    fun isContinueMp4Host(urlList: MutableList<String>): Boolean {
        AppLogs.dLog("webReceive","hostList:${toJson(urlList)}")
        var index = -1
        for (i in 0 until urlList.size) {
            var url = urlList.get(i)
            if (mp4ContinueList.any { url.contains(it, true)}){
                index = 1
            }
        }
        return (index == -1).not()
    }
    fun isXhaMaster(urlList: MutableList<String>): Boolean {
        var index = -1
        for (i in 0 until urlList.size) {
            var url = urlList.get(i)
            if (url.startsWith(WebConfig.Xhamster, true)||url.startsWith(WebConfig.faphouse, true)) {
                index = i
                break
            }
        }
        return (index == -1).not()
    }

    fun isDailymotion(urlList: MutableList<String>): Boolean{
        var index = -1
        for (i in 0 until urlList.size) {
            var url = urlList.get(i)
            if (url.startsWith(WebConfig.Dailymotion, true)) {
                index = i
                break
            }
        }
        return (index == -1).not()
    }

    // 从 URL 查询字符串中提取某个特定的 key 对应的值
    fun getQueryParam(url: String, key: String): String? {
        // 使用 URI 类解析 URL
        val uri = URL(url).toURI()
        // 分割查询字符串并将每个参数分解为 key-value 对
        val queryParams = uri.query.split("&").map { it.split("=") }
            .associate { it[0] to URLDecoder.decode(it[1], "UTF-8") }

        // 返回指定 key 对应的值
        return queryParams[key]
    }


    // 从 URL 中提取分辨率信息
    fun extractResolution(url: String): String? {
        // 定义一个正则表达式，用于匹配像 360p, 720p, 1080p 等分辨率格式
        val resolutionRegex = "(\\d{2,4}p)"  // 匹配类似 360p, 720p, 1080p 的格式

        // 使用正则表达式查找分辨率
        val pattern = Pattern.compile(resolutionRegex)
        val matcher = pattern.matcher(url)

        return if (matcher.find()) {
            matcher.group()  // 提取匹配到的分辨率
        } else {
            ""  // 如果没有找到分辨率，返回 null
        }
    }

    fun getVideoCoverImageFromNearbyImages(doc: Document?): String? {
        if (doc == null)return null
        // 查找包含视频的元素
        val videoElements = doc.select("video")

        // 遍历每个视频元素，查找其周围的图片
        for (videoElement in videoElements) {
            // 检查 video 元素是否有 poster 属性
            val posterImage = videoElement.attr("poster")
            if (posterImage.isNotEmpty()) {
                return posterImage
            }

            // 向上查找视频元素周围的图片
            var parentElement = videoElement.parent()
            var depth = 3  // 设置查找的深度
            while (parentElement != null && depth > 0) {
                // 查找父元素下的所有 img 标签
                val img = parentElement.select("img").first()
                if (img != null && img.hasAttr("src") && img.attr("src").startsWith("http")) {
                    return img.attr("src")  // 返回第一个找到的图片 src
                }
                parentElement = parentElement.parent()  // 向上遍历父元素
                depth--
            }
        }

        return null
    }

    fun reset() {
        isloading = false
        call?.cancel()
    }

    var imageUrl = ""


   suspend fun getImg(realUrl: String) {
       imageUrl = ""
       webTitle = ""
        AppLogs.dLog("m3u8","获取 title 和 img start")
        var doc: Document?=null
        runCatching {
            doc = Jsoup.connect(realUrl).get()
        }

        val ogImage = doc?.select("meta[property=og:image]")?.attr("content")?:""
        if (imageUrl.isNullOrEmpty()){
            AppLogs.dLog("webReceive","loadWebFinished 图片1:${ogImage}")
            imageUrl = ogImage
        }

        if (imageUrl.isNullOrEmpty()){
            val videoOtherImg = getVideoCoverImageFromNearbyImages(doc)?:""
            AppLogs.dLog("webReceive","loadWebFinished 图片3:${videoOtherImg}")
            imageUrl = videoOtherImg
        }
       val videoElement = doc!!.select("video").first()
       webTitle = videoElement?.attr("alt")?:"" // 如果视频有 alt 属性，可以获取
       if (webTitle.isNullOrEmpty()){
           webTitle = doc?.title()?:""
       }
       AppLogs.dLog("webReceive","loadWebFinished title:${webTitle}")
   }
}