package com.boom.aiobrowser.base

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.view.KeyEvent
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.viewbinding.ViewBinding
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.toJson
import com.boom.web.AbsAgentWebSettings
import com.boom.web.AgentWeb
import com.boom.web.AgentWebConfig
import com.boom.web.DefaultWebClient
import com.boom.web.PermissionInterceptor
import com.boom.web.WebChromeClient
import com.boom.web.WebViewClient
import com.google.gson.Gson

abstract class BaseWebFragment<V :ViewBinding> :BaseFragment<V>(){
    var mAgentWeb :AgentWeb?=null

    var back: () -> Unit = {}

    fun initWeb(){
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
                .interceptUnkownUrl() //拦截找不到相关页面的Url AgentWeb 3.0.0 加入。
                .createAgentWeb() //创建AgentWeb。
                .ready() //设置 WebSettings。
                .go(getUrl()) //WebView载入该url地址的页面并显示。
        }

        AgentWebConfig.debug()

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

    abstract fun getInsertParent(): ViewGroup


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
            rootActivity.onBackPressed()
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
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
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
                loadWebFinished()
                timer.remove(url)
            }
        }
    }

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
        var url = "https://www.youtube.com"

//		return "http://ggzy.sqzwfw.gov.cn/WebBuilderDS/WebbuilderMIS/attach/downloadZtbAttach.jspx?attachGuid=af982055-3d76-4b00-b5ab-36dee1f90b11&appUrlFlag=sqztb&siteGuid=7eb5f7f1-9041-43ad-8e13-8fcb82ea831a";
        return url
    }


    open fun loadWebFinished(){

    }


}