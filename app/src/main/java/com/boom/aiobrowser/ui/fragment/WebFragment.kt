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
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pop.basepopup.BasePopupWindow.OnDismissListener
import java.lang.ref.WeakReference


class WebFragment : BaseWebFragment<BrowserFragmentWebBinding>() {

    var jumpData: JumpData? = null


    override fun getInsertParent(): ViewGroup {
        return fBinding.fl
    }

    override fun loadWebOnPageStared(url: String) {
        allowNeedCommon = false
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
            allowNeedCommon = true
            //通用
            for (i in 0 until list.size) {
                var pageData = list.get(i)
                if (pageData.cUrl == "*") {
                    AppLogs.dLog("webReceive", "page 模式执行通用脚本 js:${pageData.cDetail}")
                    var str ="(function () {\n" +
                            "  const url = window.location.href;\n" +
                            "  const of = window.fetch.bind(window);\n" +
                            "  const headFetch = (u) => of(u, { method: 'HEAD' });\n" +
                            "  const observed = new Set([]);\n" +
                            "  let mainImageInfo = undefined;\n" +
                            "  const sizeFromRange = (range) => range && parseInt(range.split('/')[1]);\n" +
                            "  const sizeFromResp = (resp) => {\n" +
                            "    return (\n" +
                            "      sizeFromRange(resp.headers.get('content-range')) ||\n" +
                            "      parseInt(resp.headers.get('content-length'))\n" +
                            "    );\n" +
                            "  };\n" +
                            "  const contentTypeMainPart = (type) => type && type.split(';', 2)[0];\n" +
                            "  const m3u8 = [\n" +
                            "    'application/vnd.apple.mpegurl',\n" +
                            "    'application/x-mpegurl',\n" +
                            "    'application/mpegurl',\n" +
                            "    'video/x-mpegurl',\n" +
                            "    'video/mpegurl',\n" +
                            "    'audio/x-mpegurl',\n" +
                            "    'audio/mpegurl',\n" +
                            "  ];\n" +
                            "  const m3u8Entries = {};\n" +
                            "  const m3u8Entry = async (url, text) => {\n" +
                            "    if (!text.includes('#EXT-X-STREAM-INF')) {\n" +
                            "      return;\n" +
                            "    }\n" +
                            "    const lines = text.split('\\n');\n" +
                            "    const resolutionPattern = /#EXT-X-STREAM-INF.*RESOLUTION=\\d+x(\\d+)/;\n" +
                            "    const result = [];\n" +
                            "    let resolution = null;\n" +
                            "    for (let i = 0; i < lines.length; i++) {\n" +
                            "      if (!lines[i]) {\n" +
                            "        continue;\n" +
                            "      }\n" +
                            "      if (lines[i][0] === '#') {\n" +
                            "        const match = resolutionPattern.exec(lines[i]);\n" +
                            "        if (match) {\n" +
                            "          resolution = parseInt(match[1]);\n" +
                            "        }\n" +
                            "      } else {\n" +
                            "        const ent = new URL(lines[i], url).toString();\n" +
                            "        m3u8Entries[ent] = url;\n" +
                            "        result.push({\n" +
                            "          resolution,\n" +
                            "          url: ent,\n" +
                            "        });\n" +
                            "        resolution = null;\n" +
                            "      }\n" +
                            "    }\n" +
                            "    return result.sort((a, b) => b.resolution - a.resolution);\n" +
                            "  };\n" +
                            "  const m3u8Size = async (url, text) => {\n" +
                            "    if (!text) {\n" +
                            "      return of(url).then((resp) => resp.text().then((t) => m3u8Size(url, t)));\n" +
                            "    }\n" +
                            "    if (!text.includes('#EXTINF')) {\n" +
                            "      return;\n" +
                            "    }\n" +
                            "    const samples = [];\n" +
                            "    let sampleDuration = 0;\n" +
                            "    let duration = 0;\n" +
                            "    const lines = text.split('\\n');\n" +
                            "    const durationPattern = /#EXTINF:\\s*([\\d\\.]+)/;\n" +
                            "    for (let i = 0; i < lines.length; i++) {\n" +
                            "      if (!lines[i]) {\n" +
                            "        continue;\n" +
                            "      }\n" +
                            "      if (lines[i][0] === '#') {\n" +
                            "        const match = durationPattern.exec(lines[i]);\n" +
                            "        if (match) {\n" +
                            "          duration += parseFloat(match[1]);\n" +
                            "          if (samples.length < 5) {\n" +
                            "            sampleDuration = duration;\n" +
                            "          }\n" +
                            "        }\n" +
                            "      } else {\n" +
                            "        if (samples.length < 5) {\n" +
                            "          samples.push(new URL(lines[i], url).toString());\n" +
                            "        }\n" +
                            "      }\n" +
                            "    }\n" +
                            "    if (!sampleDuration) {\n" +
                            "      return;\n" +
                            "    }\n" +
                            "    return Promise.all(samples.map((u) => headFetch(u).then((resp) => sizeFromResp(resp))))\n" +
                            "      .then((s) => (s.reduce((prev, curr) => prev + curr, 0) / sampleDuration) * duration)\n" +
                            "      .then(Math.floor);\n" +
                            "  };\n" +
                            "  const mp4 = ['video/mp4'];\n" +
                            "  const acceptMp4 = (size) => !!size;\n" +
                            "  const headers = {\n" +
                            "    'user-agent': window.navigator.userAgent,\n" +
                            "    accept: '*/*',\n" +
                            "    referer: url,\n" +
                            "  };\n" +
                            "\n" +
                            "  const result = (url, imageInfo = {}, videos) => {\n" +
                            "    if (videos.entries) {\n" +
                            "      Promise.all(\n" +
                            "        videos.entries.map(({ resolution, url: ent }) =>\n" +
                            "          m3u8Size(ent).then((size) => ({\n" +
                            "            url: ent,\n" +
                            "            format: videos.format,\n" +
                            "            cookie: true,\n" +
                            "            size,\n" +
                            "            resolution: `\${resolution}p`,\n" +
                            "          }))\n" +
                            "        )\n" +
                            "      ).then((formats) => {\n" +
                            "        const obj = {\n" +
                            "          source: 'PAGE',\n" +
                            "          id: url,\n" +
                            "          description: url,\n" +
                            "          ...imageInfo,\n" +
                            "          formats,\n" +
                            "          headers,\n" +
                            "        };\n" +
                            "        console.log(obj);\n" +
                            "        \$__.result('VIDEO', JSON.stringify(obj));\n" +
                            "      });\n" +
                            "    } else {\n" +
                            "      const obj = {\n" +
                            "        source: 'PAGE',\n" +
                            "        id: url,\n" +
                            "        description: url,\n" +
                            "        ...imageInfo,\n" +
                            "        formats: [\n" +
                            "          {\n" +
                            "            url,\n" +
                            "            format: 'mp4',\n" +
                            "            cookie: true,\n" +
                            "            size: undefined,\n" +
                            "            resolution: undefined,\n" +
                            "            ...videos,\n" +
                            "          },\n" +
                            "        ],\n" +
                            "        headers,\n" +
                            "      };\n" +
                            "      console.log(obj);\n" +
                            "      \$__.result('VIDEO', JSON.stringify(obj));\n" +
                            "    }\n" +
                            "  };\n" +
                            "\n" +
                            "  const handleResult = (videoUrl, imgInfo, videoInfo) => {\n" +
                            "    if (!observed.has(videoUrl)) {\n" +
                            "      observed.add(videoUrl);\n" +
                            "      const imageInfo = imgInfo ?? mainImageInfo;\n" +
                            "      if (!videoInfo) {\n" +
                            "        headFetch(videoUrl).then((resp) => {\n" +
                            "          const contentType = contentTypeMainPart(resp.headers.get('content-type'));\n" +
                            "          if (mp4.includes(contentType)) {\n" +
                            "            const size = sizeFromResp(resp);\n" +
                            "\n" +
                            "            \$__.logEvent(\"handleResult mp4\"+JSON.stringify(imgInfo));\n" +
                            "            result(resp.url, imageInfo, { format: 'mp4', size });\n" +
                            "          } else {\n" +
                            "           \$__.logEvent(\"handleResult m3u8\"+JSON.stringify(imgInfo));\n" +
                            "            result(videoUrl, imageInfo, { format: 'm3u8' });\n" +
                            "          }\n" +
                            "        });\n" +
                            "      } else {\n" +
                            "        const imageInfoOther = {\n" +
                            "          thumbnail:  metaThumbnail(),\n" +
                            "          description: document.title,\n" +
                            "        }\n" +
                            "        const imageInfo = imgInfo ?? imageInfoOther;\n" +
                            "        \$__.logEvent(\"handleResult other\"+JSON.stringify(imageInfo)+JSON.stringify(imageInfoOther));\n" +
                            "        result(videoUrl, imageInfo, videoInfo);\n" +
                            "      }\n" +
                            "    }\n" +
                            "  };\n" +
                            "\n" +
                            "  const externalUrl = (videoUrl) => {\n" +
                            "    return !!videoUrl && (videoUrl.startsWith('http://') || videoUrl.startsWith('https://'));\n" +
                            "  };\n" +
                            "\n" +
                            "  const blobUrl = (videoUrl) => {\n" +
                            "    return !!videoUrl && videoUrl.startsWith('blob:');\n" +
                            "  };\n" +
                            "\n" +
                            "  const findImg = (node) => {\n" +
                            "    if (node.poster) {\n" +
                            "      \$__.logEvent(\"findImg node.poster\");\n" +
                            "      return {\n" +
                            "        thumbnail: node.poster,\n" +
                            "        description:document.title\n" +
                            "      };\n" +
                            "    }\n" +
                            "    let depth = 3;\n" +
                            "    while (node && depth >= 0) {\n" +
                            "      const img = node.querySelector('img');\n" +
                            "      if (img && externalUrl(img.src)) {\n" +
                            "        \$__.logEvent(\"findImg img.src\");\n" +
                            "        return {\n" +
                            "          thumbnail: img.src,\n" +
                            "          description: img.alt,\n" +
                            "        };\n" +
                            "      }\n" +
                            "      node = node.parentNode;\n" +
                            "      depth--;\n" +
                            "    }\n" +
                            "    const ogImage = metaThumbnail()\n" +
                            "    if (ogImage){\n" +
                            "      \$__.logEvent(\"findImg ogImage\");\n" +
                            "      return {\n" +
                            "        thumbnail: ogImage,\n" +
                            "        description:document.title\n" +
                            "      };\n" +
                            "    }\n" +
                            "  };\n" +
                            "\n" +
                            "  const metaThumbnail = () => document.querySelector('meta[property=\"og:image\"]')?.content;\n" +
                            "\n" +
                            "  const updateMainImageInfo = ({ thumbnail, description }) => {\n" +
                            "    mainImageInfo = {\n" +
                            "      thumbnail: thumbnail || metaThumbnail(),\n" +
                            "      description: description || document.title,\n" +
                            "    };\n" +
                            "  };\n" +
                            "\n" +
                            "  const getFormat = (video_url) => {\n" +
                            "    const formatPattern = /\\.(mp4|m3u8)/;\n" +
                            "    const match = formatPattern.exec(video_url);\n" +
                            "    return (match && match[1]) || 'mp4';\n" +
                            "  };\n" +
                            "\n" +
                            "  const parseVideo = (node) => {\n" +
                            "    if (node.tagName !== 'VIDEO') {\n" +
                            "      return;\n" +
                            "    }\n" +
                            "    const src = node.src || node.querySelector('source')?.src;\n" +
                            "    if (externalUrl(src)) {\n" +
                            "      \$__.logEvent(\"parseVideo externalUrl\");\n" +
                            "      const imageInfo = findImg(node);\n" +
                            "      handleResult(src, imageInfo, { format: getFormat(src) });\n" +
                            "    } else if (blobUrl(src)) {\n" +
                            "      \$__.logEvent(\"parseVideo blobUrl\");\n" +
                            "      // const imageInfo = findImg(node);\n" +
                            "      // handleResult(src, imageInfo, { format: getFormat(src) });\n" +
                            "      updateMainImageInfo(imageInfo ?? {});\n" +
                            "    }\n" +
                            "  };\n" +
                            "\n" +
                            "  (function () {\n" +
                            "    window.fetch = function () {\n" +
                            "      var ret = of.apply(window, arguments);\n" +
                            "      return ret.then((resp) => {\n" +
                            "        if (!resp.ok) {\n" +
                            "          return resp;\n" +
                            "        }\n" +
                            "        try {\n" +
                            "          const contentType = contentTypeMainPart(resp.headers.get('content-type'));\n" +
                            "          if (m3u8.includes(contentType)) {\n" +
                            "\n" +
                            "            m3u8Size(resp.url).then(\n" +
                            "              (size) => size && handleResult(resp.url, undefined, { format: 'm3u8', size })\n" +
                            "            );\n" +
                            "          } else if (mp4.includes(contentType)) {\n" +
                            "            const size = sizeFromRange(resp.headers.get('content-range'));\n" +
                            "            handleResult(resp.url, undefined, { format: 'mp4', size });\n" +
                            "          }\n" +
                            "        } catch (e) {}\n" +
                            "        return resp;\n" +
                            "      });\n" +
                            "    };\n" +
                            "  })();\n" +
                            "\n" +
                            "  (function () {\n" +
                            "    var origOpen = XMLHttpRequest.prototype.open;\n" +
                            "    XMLHttpRequest.prototype.open = function () {\n" +
                            "      this.addEventListener('load', function () {\n" +
                            "        var contentType = contentTypeMainPart(this.getResponseHeader('content-type'));\n" +
                            "        try {\n" +
                            "          if (mp4.includes(contentType)) {\n" +
                            "            const size = sizeFromRange(this.getResponseHeader('content-range'));\n" +
                            "            if (acceptMp4(size)) {\n" +
                            "              handleResult(this.responseURL, undefined, { format: 'mp4', size });\n" +
                            "            }\n" +
                            "          } else if (m3u8.includes(contentType) || this.responseText?.startsWith('#EXTM3U')) {\n" +
                            "            if (m3u8Entries[this.responseURL]) {\n" +
                            "              return;\n" +
                            "            }\n" +
                            "            m3u8Entry(this.responseURL, this.responseText).then((result) => {\n" +
                            "              if (result?.length > 0) {\n" +
                            "                handleResult(this.responseURL, undefined, { format: 'm3u8', entries: result });\n" +
                            "              } else {\n" +
                            "                m3u8Size(this.responseURL, this.responseText).then(\n" +
                            "                  (size) =>\n" +
                            "                    size && handleResult(this.responseURL, undefined, { format: 'm3u8', size })\n" +
                            "                );\n" +
                            "              }\n" +
                            "            });\n" +
                            "          }\n" +
                            "        } catch (e) {}\n" +
                            "      });\n" +
                            "      origOpen.apply(this, arguments);\n" +
                            "    };\n" +
                            "  })();\n" +
                            "\n" +
                            "  const recognizeNode = (node) => {\n" +
                            "    if (node instanceof Element && node.tagName === 'VIDEO') {\n" +
                            "      parseVideo(node);\n" +
                            "    }\n" +
                            "    if (node?.childNodes?.length > 0) {\n" +
                            "      node?.querySelectorAll('video').forEach((videoNode) => parseVideo(videoNode));\n" +
                            "    }\n" +
                            "  };\n" +
                            "\n" +
                            "  new MutationObserver(function (mutations, observer) {\n" +
                            "    try {\n" +
                            "      mutations.forEach((mutation) => {\n" +
                            "        switch (mutation.type) {\n" +
                            "          case 'attributes':\n" +
                            "            recognizeNode(mutation.target);\n" +
                            "            break;\n" +
                            "          case 'childList':\n" +
                            "            mutation.addedNodes.forEach((n) => recognizeNode(n));\n" +
                            "            break;\n" +
                            "        }\n" +
                            "      });\n" +
                            "    } catch (e) {\n" +
                            "      console.log('catch error: ', e);\n" +
                            "    }\n" +
                            "  }).observe(document, {\n" +
                            "    subtree: true,\n" +
                            "    childList: true,\n" +
                            "    attributes: true,\n" +
                            "  });\n" +
                            "})();\n"
//                    mAgentWeb!!.getWebCreator().getWebView().loadUrl("javascript:${str}");
//                    mAgentWeb!!.getWebCreator().getWebView().evaluateJavascript(pageData.cDetail,{})
                    mAgentWeb!!.getWebCreator().getWebView().evaluateJavascript(str) {
                        AppLogs.dLog(
                            "webReceive",
                            "evaluateJavascript 接收:$it thread:${Thread.currentThread()}"
                        )
                    }
                    break
                }
            }
        }
        EasyFloat.dismiss(tag = "webPop", true)
        addDownload()
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
                var videoDownloadTempList = CacheManager.videoDownloadTempList
                if (videoDownloadTempList.isNotEmpty()){
                    var it = videoDownloadTempList.get(videoDownloadTempList.size-1)
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
                }
                jumpData?.apply {
                    autoDownload = false
                    JumpDataManager.updateCurrentJumpData(this, "自动下载后重置")
                }
            } else {
                popDown?.updateData(false)
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


    override fun loadWebFinished(url:String) {
        super.loadWebFinished(url)
//        if (allowShowTips()){
//            showTipsPop()
//        }
        fBinding.flTop.binding.tvToolbarSearch.text = "${jumpData?.jumpTitle} ${getSearchTitle()}"
        fBinding.refreshLayout.isRefreshing = false
//        var key = mAgentWeb?.webCreator?.webView?.url?:""
        addDownload()
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
            if (EasyFloat.isShow()){
                updateDownloadButtonStatus(true, 0)
                return
            }
//            EasyFloat.dismiss(tag = "webPop", true)
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
            updateDownloadButtonStatus(true, 0)
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
        return mAgentWeb?.webCreator?.webView?.url?:""
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