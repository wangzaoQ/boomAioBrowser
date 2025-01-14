package com.boom.aiobrowser.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.SizeUtils.dp2px
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.base.BaseWebFragment
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.databinding.BrowserDragLayoutBinding
import com.boom.aiobrowser.databinding.BrowserFragmentWebBinding
import com.boom.aiobrowser.nf.NFManager
import com.boom.aiobrowser.nf.NFShow
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.BigDecimalUtils
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.tools.web.WebScan
import com.boom.aiobrowser.other.JumpConfig
import com.boom.aiobrowser.other.ParamsConfig
import com.boom.aiobrowser.tools.download.DownloadCacheManager
import com.boom.aiobrowser.tools.extractDomain
import com.boom.aiobrowser.ui.activity.MainActivity
import com.boom.aiobrowser.ui.pop.ClearPop
import com.boom.aiobrowser.ui.pop.DisclaimerPop
import com.boom.aiobrowser.ui.pop.DownLoadPop
import com.boom.aiobrowser.ui.pop.FirstDownloadTips
import com.boom.aiobrowser.ui.pop.TabPop
import com.boom.aiobrowser.ui.pop.TipsPop
import com.boom.aiobrowser.ui.pop.VideoPop2
import com.boom.downloader.VideoDownloadManager
import com.boom.drag.EasyFloat
import com.boom.drag.enums.SidePattern
import com.boom.drag.utils.DisplayUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import pop.basepopup.BasePopupWindow.OnDismissListener
import java.lang.ref.WeakReference


class WebFragment : BaseWebFragment<BrowserFragmentWebBinding>() {

    var jumpData: JumpData? = null


    override fun getInsertParent(): ViewGroup {
        return fBinding.fl
    }

    override fun loadWebOnPageStared(url: String) {
        addLast(url)
        var list = CacheManager.pageList
        var index = -1
        var hostList = extractDomain(url)

        for (i in 0 until list.size) {
            var pageData = list.get(i)
            var splits = pageData.cUrl.split(".")
            var host = ""
            if (splits.size > 0) {
                host = splits.get(0)
            }
            if (hostList.contains(host)) {
                if (APP.isDebug) {
                    AppLogs.dLog(
                        "webReceive",
                        "page 模式执行特定脚本:host:${host} js:${pageData.cDetail}"
                    )
                }
//                mAgentWeb!!.getWebCreator().getWebView().loadUrl("javascript:${pageData.cDetail}");
                mAgentWeb!!.getWebCreator().getWebView().evaluateJavascript(pageData.cDetail) {
                    AppLogs.dLog(
                        "webReceive",
                        "evaluateJavascript 接收:$it thread:${Thread.currentThread()}"
                    )
                }
                index = i
                break
            }
        }
        if (index == -1) {
            //通用
            for (i in 0 until list.size) {
                var pageData = list.get(i)
                if (pageData.cUrl == "*") {
                    AppLogs.dLog("webReceive", "page 模式执行通用脚本 js:${pageData.cDetail}")
//                    var str = "!function(){const e=window.location.href,t=window.fetch.bind(window),n=e=>t(e,{method:\"HEAD\"}),r=new Set([]);let s;const o=e=>e&&parseInt(e.split(\"/\")[1]),i=e=>o(e.headers.get(\"content-range\"))||parseInt(e.headers.get(\"content-length\")),a=e=>e&&e.split(\";\",2)[0],c=[\"application/vnd.apple.mpegurl\",\"application/x-mpegurl\",\"application/mpegurl\",\"video/x-mpegurl\",\"video/mpegurl\",\"audio/x-mpegurl\",\"audio/mpegurl\"],l={},u=async(e,r)=>{if(!r)return t(e).then((t=>t.text().then((t=>u(e,t)))));if(!r.includes(\"#EXTINF\"))return;const s=[];let o=0,a=0;const c=r.split(\"\\n\"),l=/#EXTINF:\\s*([\\d\\.]+)/;for(let t=0;t<c.length;t++)if(c[t])if(\"#\"===c[t][0]){const e=l.exec(c[t]);e&&(a+=parseFloat(e[1]),s.length<5&&(o=a))}else s.length<5&&s.push(new URL(c[t],e).toString());return o?Promise.all(s.map((e=>n(e).then((e=>i(e)))))).then((e=>e.reduce(((e,t)=>e+t),0)/o*a)).then(Math.floor):void 0},p=[\"video/mp4\"],d={\"user-agent\":window.navigator.userAgent,accept:\"*/*\",referer:e},h=(e,t={},n)=>{if(n.entries)Promise.all(n.entries.map((({resolution:e,url:t})=>u(t).then((r=>({url:t,format:n.format,cookie:!0,size:r,resolution:`\${e}p`})))))).then((n=>{const r={source:\"PAGE\",id:e,description:e,...t,formats:n,headers:d};\$__.result(\"VIDEO\",JSON.stringify(r))}));else{const r={source:\"PAGE\",id:e,description:e,...t,formats:[{url:e,format:\"mp4\",cookie:!0,size:void 0,resolution:void 0,...n}],headers:d};\$__.result(\"VIDEO\",JSON.stringify(r))}},f=(e,t,o)=>{if(!r.has(e)){r.add(e);const c=t??s;o?h(e,c,o):n(e).then((t=>{const n=a(t.headers.get(\"content-type\"));if(p.includes(n)){const e=i(t);h(t.url,c,{format:\"mp4\",size:e})}else h(e,c,{format:\"m3u8\"})}))}},m=e=>!!e&&(e.startsWith(\"http://\")||e.startsWith(\"https://\")),g=e=>{const t=/\\.(mp4|m3u8)/.exec(e);return t&&t[1]||\"mp4\"},v=e=>{if(\"VIDEO\"!==e.tagName)return;const t=e.src||e.querySelector(\"source\")?.src;if(m(t)){const n=(e=>{if(e.poster)return{thumbnail:e.poster};let t=3;for(;e&&t>=0;){const n=e.querySelector(\"img\");if(n&&m(n.src))return{thumbnail:n.src,description:n.alt};e=e.parentNode,t--}})(e);f(t,n,{format:g(t)})}else n=t,n&&n.startsWith(\"blob:\")&&(({thumbnail:e,description:t})=>{s={thumbnail:e||document.querySelector('meta[property=\"og:image\"]')?.content,description:t||document.title}})(imageInfo??{});var n};var y;window.fetch=function(){var e=t.apply(window,arguments);return e.then((e=>{if(!e.ok)return e;try{const t=a(e.headers.get(\"content-type\"));if(c.includes(t))u(e.url).then((t=>t&&f(e.url,void 0,{format:\"m3u8\",size:t})));else if(p.includes(t)){const t=o(e.headers.get(\"content-range\"));f(e.url,void 0,{format:\"mp4\",size:t})}}catch(e){}return e}))},y=XMLHttpRequest.prototype.open,XMLHttpRequest.prototype.open=function(){this.addEventListener(\"load\",(function(){var e=a(this.getResponseHeader(\"content-type\"));try{if(p.includes(e)){const e=o(this.getResponseHeader(\"content-range\"));(e=>!!e)(e)&&f(this.responseURL,void 0,{format:\"mp4\",size:e})}else if(c.includes(e)||this.responseText?.startsWith(\"#EXTM3U\")){if(l[this.responseURL])return;(async(e,t)=>{if(!t.includes(\"#EXT-X-STREAM-INF\"))return;const n=t.split(\"\\n\"),r=/#EXT-X-STREAM-INF.*RESOLUTION=\\d+x(\\d+)/,s=[];let o=null;for(let t=0;t<n.length;t++)if(n[t])if(\"#\"===n[t][0]){const e=r.exec(n[t]);e&&(o=parseInt(e[1]))}else{const r=new URL(n[t],e).toString();l[r]=e,s.push({resolution:o,url:r}),o=null}return s.sort(((e,t)=>t.resolution-e.resolution))})(this.responseURL,this.responseText).then((e=>{e?.length>0?f(this.responseURL,void 0,{format:\"m3u8\",entries:e}):u(this.responseURL,this.responseText).then((e=>e&&f(this.responseURL,void 0,{format:\"m3u8\",size:e})))}))}}catch(e){}})),y.apply(this,arguments)};const E=e=>{e instanceof Element&&\"VIDEO\"===e.tagName&&v(e),e?.childNodes?.length>0&&e?.querySelectorAll(\"video\").forEach((e=>v(e)))};new MutationObserver((function(e,t){try{e.forEach((e=>{switch(e.type){case\"attributes\":E(e.target);break;case\"childList\":e.addedNodes.forEach((e=>E(e)))}}))}catch(e){}})).observe(document,{subtree:!0,childList:!0,attributes:!0})}();"
//                    mAgentWeb!!.getWebCreator().getWebView().loadUrl("javascript:${str}");
//                    mAgentWeb!!.getWebCreator().getWebView().evaluateJavascript(pageData.cDetail,{})
                    mAgentWeb!!.getWebCreator().getWebView().evaluateJavascript(pageData.cDetail) {
                        AppLogs.dLog(
                            "webReceive",
                            "evaluateJavascript 接收:$it thread:${Thread.currentThread()}"
                        )
                    }
                    break
                }
            }
        }
        updateDownloadButtonStatus(false)
    }

    private fun addLast(url: String) {
        jumpData?.apply {
            jumpUrl = url
            jumpType = JumpConfig.JUMP_WEB
            JumpDataManager.updateCurrentJumpData(this, "MainFragment onResume 更新 jumpData")
        }
    }

    override fun getFromSource(): String {
        return "page"
    }

    override fun startLoadData() {
    }

    override fun setListener() {
        APP.engineLiveData.observe(this) {
            fBinding.flTop.updateEngine(it)
        }
        fBinding.flTop.updateTopView(2, searchRefresh = {
            refresh()
        })
        fBinding.tvTabCount.setOneClick {
            showTabPop()
        }
        fBinding.ivClear.setOneClick {
            clearData()
        }
        fBinding.ivHome.setOneClick {
//            JumpDataManager.toMain()
//            PointEvent.posePoint(PointEventKey.webpage_home, Bundle().apply {
//                putString(PointValueKey.model_type,if (CacheManager.browserStatus == 1) "private" else "normal")
//            })
            if (rootActivity is MainActivity) {
                rootActivity.onKeyDown(KeyEvent.KEYCODE_BACK, null)
            }
        }

        APP.videoScanLiveData.observe(this) {
            if (jumpData?.autoDownload == true) {
                if (it.formatsList.size == 1) {
                    it?.apply {
                        (context as BaseActivity<*>).addLaunch(success = {
                            if (it.formatsList.isNotEmpty()) {
                                var data = it.formatsList.get(0)
                                var model = DownloadCacheManager.queryDownloadModel(data)
                                if (model == null) {
                                    data.downloadType = VideoDownloadData.DOWNLOAD_PREPARE
                                    DownloadCacheManager.addDownLoadPrepare(data)
                                    withContext(Dispatchers.Main) {
                                        var headerMap = HashMap<String, String>()
                                        data.paramsMap?.forEach {
                                            headerMap.put(it.key, it.value.toString())
                                        }
                                        VideoDownloadManager.getInstance()
                                            .startDownload(data.createDownloadData(data), headerMap)
                                        NFManager.requestNotifyPermission(
                                            WeakReference((context as BaseActivity<*>)),
                                            onSuccess = {
                                                NFShow.showDownloadNF(data, true)
                                            },
                                            onFail = {})
                                    }
                                }
                            }
                        }, failBack = {})
                    }
                } else {
                    var status = popDown?.isShowing ?: false
                    if (!status && it.formatsList.size > 1) {
                        showDownloadPop()
                    }
                }
                jumpData?.apply {
                    autoDownload = false
                    JumpDataManager.updateCurrentJumpData(this, "自动下载后重置")
                }
            } else {
                popDown?.updateDataByScan(it)
            }
            updateDownloadButtonStatus(true, 0)
        }
        APP.videoNFLiveData.observe(this) {
            popDown?.updateDataByNF(it)
        }
        APP.videoLiveData.observe(this) {
            var map = it
            it.keys.forEach {
                popDown?.updateStatus(rootActivity, it, map.get(it)) {
//                    itemRemoveData(it)
                }
                if (it == VideoDownloadData.DOWNLOAD_SUCCESS) {
                    updateDownloadButtonStatus(true, 1)
                }
            }
        }

        APP.videoUpdateLiveData.observe(this) {
            var updateId = it
            rootActivity.addLaunch(success = {
                var list = CacheManager.videoDownloadTempList
                if (list.isNotEmpty()) {
                    list.forEach {
                        it.formatsList.forEach {
                            if (it.videoId == updateId) {
                                it.downloadType = VideoDownloadData.DOWNLOAD_NOT
                                CacheManager.videoDownloadTempList = list
                            }
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    popDown?.updateItem()
                }
            }, failBack = {})
        }
        APP.downloadButtonLiveData.observe(this) {
            addDownload()
        }
    }


    open fun updateDownloadButtonStatus(status: Boolean, type: Int = 0) {
        rootActivity.addLaunch(success = {
            var size = 0
            var tempList = CacheManager.videoDownloadTempList
            var modelList = DownloadCacheManager.queryDownloadModelDone()

            for (i in 0 until tempList.size) {
                var allow = false
                tempList.get(i).formatsList.forEach {
                    for (j in 0 until (modelList?.size ?: 0)) {
                        var dbData = modelList?.get(j)
                        if (it.videoId == dbData?.videoId ?: "") {
                            it.downloadType = dbData?.downloadType ?: 0
                            break
                        }
                    }
                    if (it.downloadType != VideoDownloadData.DOWNLOAD_SUCCESS) {
                        allow = true
                    }
                }
                if (allow) {
                    size++
                }
            }
            withContext(Dispatchers.Main) {
                if (allowShowTips().not()) {
                    dragBiding?.apply {
                        if (tempList.size > 0) {
                            AppLogs.dLog(fragmentTAG, "展示有数据下载状态 type:${type}")
                            ivDownload.visibility = View.GONE
                            ivDownload2.visibility = View.VISIBLE
                            if (size > 0) {
                                tvDownload.visibility = View.VISIBLE
                                tvDownload.text = "$size"
                            } else {
                                tvDownload.visibility = View.GONE
                            }
                            if (type != 1) {
                                ivDownload2.apply {
                                    setAnimation("download.json")
                                    playAnimation()
                                }
                                if (CacheManager.isFirstDownloadTips) {
                                    CacheManager.isFirstDownloadTips = false
                                    tips1 = FirstDownloadTips(rootActivity)
                                    tips1?.createPop(root, 1)
                                }
                            }
                            AppLogs.dLog(fragmentTAG, "展示有数据下载状态完成 type:${type}")
                        } else {
                            AppLogs.dLog(fragmentTAG, "展示无数据下载状态")
                            ivDownload.visibility = View.VISIBLE
                            ivDownload2.visibility = View.GONE
                            tvDownload.visibility = View.GONE
                            ivDownload2.cancelAnimation()
                            AppLogs.dLog(fragmentTAG, "展示无数据下载状态完成")
                        }
                    }
                }
            }
        }, failBack = {})
    }


    private fun showDownloadPop() {
        popDown = DownLoadPop(rootActivity)
        popDown?.createPop() {
            updateDownloadButtonStatus(true, 1)
        }
        popDown?.setOnDismissListener(object : OnDismissListener() {
            override fun onDismiss() {
                updateDownloadButtonStatus(true, 1)
            }
        })
        CacheManager.isFirstClickDownloadButton = false
    }

    var popDown: DownLoadPop? = null


    fun showTabPop() {
        var tabPop = TabPop(rootActivity)
        tabPop.createPop()
        tabPop.setOnDismissListener(object : OnDismissListener() {
            override fun onDismiss() {
                updateTabCount()
            }
        })
        PointEvent.posePoint(PointEventKey.webpage_tag)
    }

    fun updateTabCount() {
        fBinding.tvTabCount.text = "${
            JumpDataManager.getBrowserTabList(
                CacheManager.browserStatus,
                tag = "WebDetailsActivity 更新tab 数量"
            ).size
        }"
    }


    fun clearData() {
        ClearPop(rootActivity).createPop {
            CacheManager.clearAll()
            JumpDataManager.toMain()
        }
        PointEvent.posePoint(PointEventKey.webpage_delete)
    }

    private fun refresh() {
        if (mAgentWeb != null) {
            mAgentWeb!!.urlLoader.reload() // 刷新
        }
    }

    override fun loadWebFinished() {
        super.loadWebFinished()
//        if (allowShowTips()){
//            showTipsPop()
//        }
        fBinding.flTop.binding.tvToolbarSearch.text = "${jumpData?.jumpTitle} ${getSearchTitle()}"
        fBinding.refreshLayout.isRefreshing = false
//        var key = mAgentWeb?.webCreator?.webView?.url?:""
//        addDownload()
    }

    fun getSearchTitle(): String {
        var search = when (CacheManager.engineType) {
            else -> {
                "Google"
            }
        }
        var unit = rootActivity.getString(R.string.app_search)
        return " - $search $unit"
    }

    open fun updateData(data: JumpData?) {
        jumpData = data
        initWeb()
        fBinding.flTop.updateEngine(CacheManager.engineType)
        fBinding.flTop.binding.tvToolbarSearch.text = jumpData?.jumpUrl
        fBinding.refreshLayout.isEnabled = false
        fBinding.flTop.setData(jumpData)
        fBinding.root.postDelayed({
            jumpData?.apply {
                jumpType = JumpConfig.JUMP_WEB
                JumpDataManager.updateCurrentJumpData(this, "MainFragment onResume 更新 jumpData")
                if (CacheManager.browserStatus == 0) {
                    CacheManager.saveRecentSearchData(this)
                }
            }
        }, 0)
        back = {
//            jumpData?.apply {
//                nextJumpType = JumpConfig.JUMP_WEB
//                nextJumpUrl = mAgentWeb?.webCreator?.webView?.url
//                JumpDataManager.updateCurrentJumpData(this,tag="webFragment goBack")
//                if (rootActivity is WebDetailsActivity){
//                    (rootActivity as WebDetailsActivity).apply {
//                        updateBottom(true,true, jumpData = jumpData,"webView goBack")
//                    }
//                }
//            }
        }
    }


    var dragBiding: BrowserDragLayoutBinding? = null

    /**
     * 进入
     */

    override fun setShowView() {
        dragBiding = BrowserDragLayoutBinding.inflate(layoutInflater, null, false)
        updateData(
            getBeanByGson(
                arguments?.getString(ParamsConfig.JSON_PARAMS) ?: "",
                JumpData::class.java
            )
        )
        updateTabCount()
        CacheManager.videoDownloadTempList = mutableListOf()
//        fBinding.ivDownload.visibility = View.VISIBLE
        PointEvent.posePoint(PointEventKey.webpage_page, Bundle().apply {
            putString(
                PointValueKey.model_type,
                if (CacheManager.browserStatus == 1) "private" else "normal"
            )
        })
        addDownload()
    }

    private fun addDownload() {
        if (allowShowTips().not() && isAdded) {
            EasyFloat.dismiss(tag = "webPop", true)
            var startX = 0
            var startY = 0
            var dragX = CacheManager.dragX
            var dragY = CacheManager.dragY
            startX = if (dragX == 0) {
                DisplayUtils.getScreenWidth(rootActivity) - dp2px(85f) - dp2px(28f)
            } else {
                dragX
            }
            startY = if (dragY == 0) {
                DisplayUtils.getScreenHeight(rootActivity) - (BigDecimalUtils.div(
                    DisplayUtils.getScreenWidth(
                        rootActivity
                    ).toDouble(), 3.0
                ).toInt() * 2)
            } else {
                dragY
            }
            AppLogs.dLog(fragmentTAG, "startX:${startX} startY:${startY}")
            dragBiding?.apply {
                EasyFloat.with(rootActivity)
                    .setSidePattern(SidePattern.RESULT_HORIZONTAL)
                    .setImmersionStatusBar(true)
                    .setGravity(Gravity.START or Gravity.BOTTOM, offsetX = startX, offsetY = startY)
                    .setLocation(startX, startY)
                    .setTag("webPop")
                    // 传入View，传入布局文件皆可，如：MyCustomView(this)、R.layout.float_custom
                    .setLayout(root) {
                        ivDownload.setOneClick {
                            if (allowShowTips()) {
                                showTipsPop()
                                return@setOneClick
                            }
                            rootActivity.addLaunch(success = {
                                delay(500)
                                withContext(Dispatchers.Main) {
                                    VideoPop2(rootActivity).createPop(getRealParseUrl()) { }
                                }
                            }, failBack = {})
                            PointEvent.posePoint(PointEventKey.webpage_download, Bundle().apply {
                                putString(PointValueKey.type, "no_have")
                                putString(PointValueKey.url, jumpData?.jumpUrl)
                                putString(
                                    PointValueKey.model_type,
                                    if (CacheManager.browserStatus == 1) "private" else "normal"
                                )
                            })
                        }
                        ivDownload2.setOneClick {
                            tips1?.dismiss()
                            ivDownload2.cancelAnimation()
                            rootActivity.addLaunch(success = {
                                delay(500)
                                withContext(Dispatchers.Main) {
                                    if (CacheManager.isDisclaimerFirst) {
                                        CacheManager.isDisclaimerFirst = false
                                        DisclaimerPop(rootActivity).createPop {
                                            showDownloadPop()
                                        }
                                    } else {
                                        showDownloadPop()
                                    }
                                }
                            }, failBack = {})
                            PointEvent.posePoint(PointEventKey.webpage_download, Bundle().apply {
                                putString(PointValueKey.type, "have")
                                putString(PointValueKey.url, jumpData?.jumpUrl)
                                putString(
                                    PointValueKey.model_type,
                                    if (CacheManager.browserStatus == 1) "private" else "normal"
                                )
                            })
                        }

                    }
//            .setTag(TAG_1)
                    .registerCallback {
                        // 在此处设置view也可以，建议在setLayout进行view操作
                        createResult { isCreated, msg, _ ->
//                    toast("isCreated: $isCreated")
//                    logger.e("DSL:  $isCreated   $msg")
                        }
                        show {

                        }
                        hide {
                        }
                        dismiss {
                        }

                        touchEvent { view, event ->
                            if (event.action == MotionEvent.ACTION_DOWN) {

                            }
                        }

                        drag { view, motionEvent ->
//                    view.findViewById<TextView>(R.id.textView).apply {
//                        text = "我被拖拽..."
//                        setBackgroundResource(R.drawable.corners_red)
//                    }
//                    DragUtils.registerDragClose(motionEvent, object : OnTouchRangeListener {
//                        override fun touchInRange(inRange: Boolean, view: BaseSwitchView) {
//                            setVibrator(inRange)
//                        }
//
//                        override fun touchUpInRange() {
//                            EasyFloat.dismiss(tag, true)
//                        }
//                    })
                        }

                        dragEnd {
                            root?.apply {
                                val location = IntArray(2)
                                this.getLocationOnScreen(location)
                                val x = location[0] // view距离 屏幕左边的距离（即x轴方向）
                                val y = location[1] // view距离 屏幕顶边的距离（即y轴方向）
                                CacheManager.dragX = x
                                CacheManager.dragY = y
                                AppLogs.dLog("dragParams", "拖拽后x:${x} 拖拽后y:${y}")
                            }
//                    it.findViewById<TextView>(R.id.textView).apply {
//                        text = "拖拽结束"
//                        val location = IntArray(2)
//                        getLocationOnScreen(location)
//                        setBackgroundResource(if (location[0] > 10) R.drawable.corners_left else R.drawable.corners_right)
//                    }
                        }
                    }
                    .show()
            }
        } else {
            EasyFloat.dismiss(tag = "webPop", true)
        }
    }

    var tips1: FirstDownloadTips? = null

    override fun onResume() {
        super.onResume()
        updateDownloadButtonStatus(false, 1)
    }

    override fun getUrl(): String {
        return jumpData?.jumpUrl ?: ""
    }

    override fun getRealParseUrl(): String {
        return jumpData?.jumpUrl ?: ""
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BrowserFragmentWebBinding {
        return BrowserFragmentWebBinding.inflate(inflater)
    }

    override fun onDestroy() {
        EasyFloat.dismiss(tag = "webPop", true)
        APP.engineLiveData.removeObservers(this)
        APP.downloadButtonLiveData.removeObservers(this)
        APP.videoNFLiveData.removeObservers(this)
        APP.videoLiveData.removeObservers(this)
        APP.videoScanLiveData.removeObservers(this)
        APP.videoUpdateLiveData.removeObservers(this)
        super.onDestroy()
    }
}