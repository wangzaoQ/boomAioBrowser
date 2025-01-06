package com.boom.aiobrowser.base

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.viewbinding.ViewBinding
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.data.VideoUIData
import com.boom.aiobrowser.data.WebDetailsData
import com.boom.aiobrowser.firebase.FirebaseConfig
import com.boom.aiobrowser.net.WebNet
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.UIManager
import com.boom.aiobrowser.tools.extractDomain
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.tools.getMapByGson
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.tools.web.WebScan
import com.boom.aiobrowser.ui.pop.TipsPop
import com.boom.downloader.utils.VideoDownloadUtils
import com.boom.web.AbsAgentWebSettings
import com.boom.web.AgentWeb
import com.boom.web.AgentWebConfig
import com.boom.web.DefaultWebClient
import com.boom.web.PermissionInterceptor
import com.boom.web.WebChromeClient
import com.boom.web.WebViewClient
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import okhttp3.Response
import java.lang.ref.WeakReference


abstract class BaseWebFragment<V :ViewBinding> :BaseFragment<V>(){
    var mAgentWeb :AgentWeb?=null

    var back: () -> Unit = {}

    fun initWeb(){
        runCatching {
            if (mAgentWeb != null){
                mAgentWeb!!.go(getUrl())
            }else{
                mAgentWeb = AgentWeb.with(this) //
                    .setAgentWebParent(
                        getInsertParent(),
                        -1,
                        LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    ) //传入AgentWeb的父控件。
                    .useDefaultIndicator(-1, 3) //设置进度条颜色与高度，-1为默认值，高度为2，单位为dp。
                    .setAgentWebWebSettings(getSettings()) //设置 IAgentWebSettings。
                    .setWebViewClient(mWebViewClient) //WebViewClient ， 与 WebView 使用一致 ，但是请勿获取WebView调用setWebViewClient(xx)方法了,会覆盖AgentWeb DefaultWebClient,同时相应的中间件也会失效。
                    .setWebChromeClient(webChromeClient) //WebChromeClient
                    .setPermissionInterceptor(mPermissionInterceptor) //权限拦截 2.0.0 加入。
                    .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK) //严格模式 Android 4.2.2 以下会放弃注入对象 ，使用AgentWebView没影响。
//            .setAgentWebUIController(UIController(activity)) //自定义UI  AgentWeb3.0.0 加入。
//            .setMainFrameErrorView(
//                com.just.agentweb.R.layout.agentweb_error_page,
//                -1
//            ) //参数1是错误显示的布局，参数2点击刷新控件ID -1表示点击整个布局都刷新， AgentWeb 3.0.0 加入。
//            .useMiddlewareWebChrome(getMiddlewareWebChrome()) //设置WebChromeClient中间件，支持多个WebChromeClient，AgentWeb 3.0.0 加入。
//            .additionalHttpHeader(getUrl(), "cookie", "41bc7ddf04a26b91803f6b11817a5a1c")
//            .useMiddlewareWebClient(getMiddlewareWebClient()) //设置WebViewClient中间件，支持多个WebViewClient， AgentWeb 3.0.0 加入。
                    .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK) //打开其他页面时，弹窗质询用户前往其他应用 AgentWeb 3.0.0 加入。
                    .addJavascriptInterface("${"$"}__",MyJavaScriptInterface(WeakReference(this@BaseWebFragment),getFromSource()))
                    .interceptUnkownUrl() //拦截找不到相关页面的Url AgentWeb 3.0.0 加入。
                    .createAgentWeb() //创建AgentWeb。
                    .ready() //设置 WebSettings。
                    .go(getUrl()) //WebView载入该url地址的页面并显示。
            }

            if (APP.isDebug){
                AgentWebConfig.debug()
            }


            // AgentWeb 没有把WebView的功能全面覆盖 ，所以某些设置 AgentWeb 没有提供 ， 请从WebView方面入手设置。
            mAgentWeb!!.getWebCreator().getWebView().setOverScrollMode(WebView.OVER_SCROLL_NEVER)
            if (CacheManager.browserStatus == 1){
                runCatching {
                    // 禁止记录cookie
                    CookieManager.getInstance().setAcceptCookie(true)
                    CookieManager.getInstance().removeSessionCookie()
                    mAgentWeb!!.getWebCreator().getWebView().settings.setSaveFormData(false)
                }
            }
        }
    }

    abstract fun getInsertParent(): ViewGroup

    class MyJavaScriptInterface(var fragmentWebReference: WeakReference<BaseWebFragment<*>>,var fromSource:String) {

        @Volatile
        var allow = true


        @JavascriptInterface
        fun result(kind: String, detail: String){
            AppLogs.dLog("webReceive","结果类型:${kind} thread:${Thread.currentThread()} detail:${detail}")
            fragmentWebReference.get()?.rootActivity?.addLaunch(success = {
                allow = true
                if (kind == "ERROR"){
                    PointEvent.posePoint(PointEventKey.webpage_download_show, Bundle().apply {
                        putString(PointValueKey.type,"no_have")
                    })
                }else{
                    PointEvent.posePoint(PointEventKey.webpage_download_show, Bundle().apply {
                        putString(PointValueKey.type,"have")
                    })
                }
                var url = ""
                withContext(Dispatchers.Main){
                    if (fragmentWebReference.get()?.allowShowTips() == true){
                        allow = false
                        return@withContext
                    }
                    url = fragmentWebReference.get()?.mAgentWeb?.webCreator?.webView?.url?:""
                }
                if (allow.not())return@addLaunch
                getBeanByGson(detail,WebDetailsData::class.java)?.apply {
                    var uiData = VideoUIData()
                    var description = description?:""
                    formats?.forEach {
                        var map = HashMap<String,Any>()
                        if (it.cookie == true){
                            withContext(Dispatchers.Main){
                                var cookie = CookieManager.getInstance().getCookie(url)?:""
                                map.put("Cookie", cookie)
                            }

                        }
                        var sourceList = if (fromSource == "page") CacheManager.pageList else  CacheManager.fetchList
                        var fileStart = ""
//                    if (description.startsWith("http").not()){
//                        fileStart = description
//                    }
                        if (fileStart.isNullOrEmpty()){
                            runCatching {
                                for (i in 0 until sourceList.size){
                                    var pageData = sourceList.get(i)
                                    var uri = Uri.parse(url)
                                    if (uri.host?.contains(pageData.cUrl)?:false){
                                        var split = pageData.cUrl.split(".")
                                        if (split.size>0){
                                            fileStart = "${split[0]}_${System.currentTimeMillis()}"
                                        }
                                        break
                                    }
                                }

                            }
                            runCatching {
                                if (fileStart.isNullOrEmpty()){
                                    var uri = Uri.parse(url)
                                    var split = uri.host?.split(".")
                                    if (split!!.size>0){
                                        fileStart = "${split[0]}_${System.currentTimeMillis()}"
                                    }
                                }
                            }
                        }
                        if (fileStart.isNullOrEmpty()){
                            fileStart = "${System.currentTimeMillis()}"
                        }
                        var videoDownloadData = VideoDownloadData().createDefault(
                            videoId = "${VideoDownloadUtils.computeMD5(id)}_${it.resolution}",
                            fileName = "${fileStart}.${it.format}",
                            url = it.url?:"",
                            imageUrl = this.thumbnail?:"",
                            paramsMap = map,
                            size = it.size?.toLong()?:0,
                            videoType = it.format?:"",
                            resolution = if (it.resolution.isNullOrEmpty()) "--" else it.resolution?:""
                        )
                        if (uiData.formatsList.isNullOrEmpty()){
                            uiData.formatsList.add(videoDownloadData)
                        }else{
                            runCatching {
                                var oldData = uiData.formatsList.get(uiData.formatsList.size-1)
                                var oldResolution = oldData.resolution?:""
                                var newResolution = it.resolution?:""
                                if (oldResolution.contains("p",true) && newResolution.contains("p",true)){
                                    runCatching {
                                        oldResolution = oldResolution.substring(0,oldResolution.indexOf("p", startIndex = 0, ignoreCase=true))
                                        newResolution = newResolution.substring(0,newResolution.indexOf("p", startIndex = 0, ignoreCase=true))
                                        if (newResolution.toInt()>oldResolution.toInt()){
                                            uiData.formatsList.add(0,videoDownloadData)
                                        }else{
                                            uiData.formatsList.add(videoDownloadData)
                                        }
                                    }.onFailure {
                                        uiData.formatsList.add(videoDownloadData)
                                    }
                                }else{
                                    uiData.formatsList.add(0,videoDownloadData)
                                }
                            }
                        }
                        uiData.videoResultId = "${id}"
                        uiData.source = fromSource
                        uiData.thumbnail = this.thumbnail?:""
                        uiData.description = "${fileStart}.${it.format}"
                    }
                    var index = -1
                    var list =  CacheManager.videoDownloadTempList
                    for (i in 0 until list.size){
                        var data = list.get(i)
                        AppLogs.dLog("webReceive","data.videoResultId:${data.videoResultId} uiData.videoResultId:${uiData.videoResultId}")
                        if (data.videoResultId == uiData.videoResultId){
                            index = i
                            break
                        }
                    }
                    if (index == -1){
                        list.add(0,uiData)
                        CacheManager.videoDownloadTempList = list
                        APP.videoScanLiveData.postValue(uiData)
                    }
                }
            }, failBack = {})

        }

        @JavascriptInterface
        fun load(url: String, headers: String): String?{
            AppLogs.dLog("webReceive","load:${url} headers:${headers} thread:${Thread.currentThread()}")
            var map = getMapByGson(headers)
            val request: Request.Builder = Request.Builder()
                .url(url)
//                .build()
            map?.forEach { s, any ->
                request.addHeader(s,any.toString())
            }
            var call = WebNet.netClient.newCall(request.build())
            var result:Response?=null
            runCatching {
                result = call.execute()
            }.onFailure {
                AppLogs.eLog("webRecive",it.stackTraceToString())
            }
            if (result ==null){
                return toJson(HashMap<String,Any>().apply {
                    put("code",502)
                })
            }else{
                var bodyString = result!!.body?.string()?:""
                AppLogs.dLog("webRecive","body:${bodyString}")
                result!!.headers.toMultimap().get("set-cookie")?.forEach {
                    CookieManager.getInstance().setCookie(url,it)
                    AppLogs.dLog("webReceive","cookie:${it}")
                }
                return toJson(HashMap<String,Any>().apply {
                    put("code",result!!.code)
                    put("body",bodyString)
                })
            }
        }
    }

    var tipsPop:TipsPop?=null

    fun showTipsPop(){
        if (tipsPop == null){
            tipsPop = TipsPop(rootActivity)
        }
        if (tipsPop!!.isShowing.not()){
            tipsPop!!.createPop {  }
        }
    }

    fun allowShowTips(): Boolean {
        var showTask = false
        if (UIManager.isBuyUser().not()){
            var hostList = extractDomain(mAgentWeb?.webCreator?.webView?.url?:"")
            AppLogs.dLog(fragmentTAG,"当前加载Url:${mAgentWeb?.webCreator?.webView?.url?:""} host:${hostList}")
            if (WebScan.isVimeo(hostList)){}
            else if (FirebaseConfig.switchOpenFilter1){
                AppLogs.dLog(fragmentTAG,"命中filter1")
                showTask = true
            }else{
                var index = -1
                for (i in 0 until FirebaseConfig.switchOpenFilterList.size){
                    var filterWeb = FirebaseConfig.switchOpenFilterList.get(i).trim()
                    for (j in 0 until hostList.size){
                        if (hostList.get(j).equals(filterWeb,true)){
                            index = i
                            break
                        }
                    }
                }
                if (index>=0){
                    AppLogs.dLog(fragmentTAG,"命中filter2 indexUrl:${FirebaseConfig.switchOpenFilterList.get(index) } host:${hostList}")
                    showTask = true
                }
            }
        }
        return showTask
    }

    fun goBack(keyCode:Int , event: KeyEvent?):Boolean {
        back.invoke()
        return mAgentWeb!!.handleKeyEvent(keyCode, event)
    }

    fun goBack(){
        back.invoke()
        var webView = mAgentWeb?.webCreator?.webView
        if (webView!= null && webView.canGoBack()) {
            webView.goBack()
        }else{
            rootActivity.finish()
        }
    }

    /**
     * @return IAgentWebSettings
     */
    fun getSettings(): AbsAgentWebSettings {
        return object : AbsAgentWebSettings() {
            private var mAgentWeb: AgentWeb? = null
            override fun bindAgentWebSupport(agentWeb: AgentWeb?) {
                this.mAgentWeb = agentWeb
            }
        }
    }

    protected var webChromeClient : WebChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view:WebView,  newProgress:Int) {
            super.onProgressChanged(view, newProgress)
//            AppLogs.dLog(fragmentTAG, "onProgressChanged:$newProgress  view:$view")
        }

        override fun getDefaultVideoPoster(): Bitmap? {
            return Bitmap.createBitmap(10, 10, Bitmap.Config.RGB_565)
        }
    }


    /**
     * 注意，重写WebViewClient的方法,super.xxx()请务必正确调用， 如果没有调用super.xxx(),则无法执行DefaultWebClient的方法
     * 可能会影响到AgentWeb自带提供的功能,尽可能调用super.xxx()来完成洋葱模型
     */
    protected var mWebViewClient: WebViewClient = object : WebViewClient() {
        private val timer = HashMap<String, Long?>()
        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            AppLogs.dLog(
                fragmentTAG,
                "mWebViewClient shouldOverrideUrlLoading:${request?.url}"
            )
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
//            AppLogs.dLog(
//                fragmentTAG,
//                "mWebViewClient shouldInterceptRequest:${request?.url}"
//            )
//            request?.url?.apply {
//                if (WebScan.isTikTok(this.toString())){
//                    var  builder = Request.Builder()
//                    //do you want to do for builder
//
//                    var  req = builder.url(request?.url.toString()).get().build()
//                    var  response = WebNet.netClient.newCall(req).execute()
//                    //do you want to do for response
//                    var  webResourceResponse = WebResourceResponse("text/html", "utf-8",  ByteArrayInputStream(response.body?.bytes()))
//                    return webResourceResponse
//                }
//            }
//            WebScan.filterUri(request?.url?.toString()?:"",WeakReference(rootActivity))
            return super.shouldInterceptRequest(view, request)
        }

        //
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            AppLogs.dLog(
                fragmentTAG,
                "view:" + Gson().toJson(view.hitTestResult)
            )
            AppLogs.dLog(
                fragmentTAG,
                "mWebViewClient shouldOverrideUrlLoading:$url"
            )
            //优酷想唤起自己应用播放该视频 ， 下面拦截地址返回 true  则会在应用内 H5 播放 ，禁止优酷唤起播放该视频， 如果返回 false ， DefaultWebClient  会根据intent 协议处理 该地址 ， 首先匹配该应用存不存在 ，如果存在 ， 唤起该应用播放 ， 如果不存在 ， 则跳到应用市场下载该应用 .
            return if (url.startsWith("intent://") && url.contains("com.youku.phone")) {
                true
            } else super.shouldOverrideUrlLoading(view, url)
            /*else if (isAlipay(view, mUrl))   //1.2.5开始不用调用该方法了 ，只要引入支付宝sdk即可 ， DefaultWebClient 默认会处理相应url调起支付宝
			    return true;*/
        }

        override fun onPageStarted(view: WebView?, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            CacheManager.videoDownloadTempList = mutableListOf()
            AppLogs.dLog(
                fragmentTAG,
                "mUrl:" + url + " onPageStarted  target:" + getUrl()
            )
            loadWebOnPageStared(url)
            timer[url] = System.currentTimeMillis()
//            if (url == getUrl()) {
//                pageNavigator(View.GONE)
//            } else {
//                pageNavigator(View.VISIBLE)
//            }
        }


        override fun onPageFinished(view: WebView?, url: String) {
            super.onPageFinished(view, url)
            if (timer[url] != null) {
                val overTime = System.currentTimeMillis()
                val startTime = timer[url]
                AppLogs.dLog(
                    fragmentTAG,
                    "  page mUrl:" + url + "  used time:" + (overTime - startTime!!)
                )
                timer.remove(url)
                loadWebFinished()
                var cookieManager = CookieManager.getInstance()
                var cookie = cookieManager.getCookie(url)?:""
                AppLogs.dLog(fragmentTAG,"onPageFinished page mUrl:${url}  cookie:${cookie}")
//                if (WebScan.isTikTok(url)){
//                CacheManager.pageList.get(0).cDetail
//                mAgentWeb!!.getWebCreator().getWebView().loadUrl("javascript:${CacheManager.pageList.get(0).cDetail}");
//                }else if (WebScan.isPornhub(url)){
//                    WebScan.filterUri(url, WeakReference(rootActivity))
//                }
//            evaluateHTML(view!!)
                WebScan.reset()
            }
        }
    }


    abstract fun getFromSource():String

    abstract fun loadWebOnPageStared(url: String)

    protected var mPermissionInterceptor: PermissionInterceptor = object : PermissionInterceptor {
        /**
         * PermissionInterceptor 能达到 url1 允许授权， url2 拒绝授权的效果。
         * @param url
         * @param permissions
         * @param action
         * @return true 该Url对应页面请求权限进行拦截 ，false 表示不拦截。
         */
        override fun intercept(url: String, permissions: Array<String?>?, action: String): Boolean {
            AppLogs.dLog(
                fragmentTAG,
                "mUrl:" + url + "  permission:" + toJson(permissions) + " action:" + action
            )
            return false
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /**
         * 2.0.0开始 废弃该api ，没有api代替 ,使用 ActionActivity 绕过该方法 ,降低使用门槛,4.0.0 删除该API。
         */
//        mAgentWeb.uploadFileResult(requestCode, resultCode, data);
    }


    open fun getUrl(): String {
//        var url = "https://www.tiktok.com/@poppiesstudios/video/7396952171536796971?is_from_webapp=1&sender_device=pc"
//        var url = "https://nnyy.in/dianying/20308129.html"
//        var url = "https://fmovie-s.to/movie"
        var url = "https://cn.pornhub.com/"

//		return "http://ggzy.sqzwfw.gov.cn/WebBuilderDS/WebbuilderMIS/attach/downloadZtbAttach.jspx?attachGuid=af982055-3d76-4b00-b5ab-36dee1f90b11&appUrlFlag=sqztb&siteGuid=7eb5f7f1-9041-43ad-8e13-8fcb82ea831a";
        return url
    }

    abstract fun getRealParseUrl():String


    open fun loadWebFinished(){

    }


    override fun onPause() {
        super.onPause()
//        runCatching {
//            mAgentWeb!!.getWebCreator().getWebView()?.onPause()
//            mAgentWeb!!.getWebCreator().getWebView()?.pauseTimers()
//        }
    }

    override fun onResume() {
        super.onResume()
//        runCatching {
//            mAgentWeb!!.getWebCreator().getWebView()?.onResume()
//            mAgentWeb!!.getWebCreator().getWebView()?.resumeTimers()
//        }
    }


    private fun evaluateHTML(webView: WebView) {
        webView.evaluateJavascript(
            "javascript:document.getElementsByTagName('html')[0].innerHTML;"
        ) { html -> // 这里的html就是网页的HTML内容
            AppLogs.dLog(fragmentTAG,"HTML:${html!!}")
        }
    }


}