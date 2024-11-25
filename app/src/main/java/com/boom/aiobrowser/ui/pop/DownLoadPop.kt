package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADShowManager
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.data.VideoUIData
import com.boom.aiobrowser.databinding.VideoPopDownloadBinding
import com.boom.aiobrowser.nf.NFManager
import com.boom.aiobrowser.nf.NFShow
import com.boom.aiobrowser.other.ShortManager
import com.boom.aiobrowser.point.AD_POINT
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.clean.formatSize
import com.boom.aiobrowser.tools.download.DownloadCacheManager
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.tools.video.VideoManager
import com.boom.aiobrowser.ui.activity.DownloadActivity
import com.boom.aiobrowser.ui.activity.VideoPreActivity
import com.boom.aiobrowser.ui.adapter.DownloadAdapter
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import com.boom.downloader.VideoDownloadManager
import com.boom.downloader.model.VideoTaskItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pop.basepopup.BasePopupWindow
import pop.util.animation.AnimationHelper
import pop.util.animation.TranslationConfig
import java.lang.ref.WeakReference

class DownLoadPop(context: Context) : BasePopupWindow(context) {

    init {
        setContentView(R.layout.video_pop_download)
    }

    private val downloadAdapter by lazy {
        DownloadAdapter()
    }

    var defaultBinding: VideoPopDownloadBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = VideoPopDownloadBinding.bind(contentView)
    }

    fun updateItem() {
        var list = CacheManager.videoDownloadTempList
        var adapterList = downloadAdapter.items
        var endList = mutableListOf<VideoUIData>()
        if (adapterList.isNullOrEmpty()) {
            endList.addAll(list)
        } else {
            for (i in 0 until list.size) {
                var data = list.get(i)
                endList.add(data)
            }
        }
        downloadAdapter.submitList(endList)
    }

    fun updateDataByScan(data: VideoUIData) {
        (context as BaseActivity<*>).addLaunch(success = {
            //1.如果是第一次进入 先将数据与库里状态对齐
            var modelList = DownloadCacheManager.queryDownloadModelOther()
            if (modelList.isNullOrEmpty().not()) {
                for (k in 0 until modelList!!.size) {
                    var bean = modelList.get(k)
                    data.formatsList.forEach {
                        if (bean.videoId == it.videoId) {
                            it.covertByDbData(bean)
                        }
                    }
                }
            }
            withContext(Dispatchers.Main) {
                downloadAdapter.add(data)
            }
        }, failBack = {})
    }

    fun updateDataByNF(data: VideoDownloadData) {
        (context as BaseActivity<*>).addLaunch(success = {
            var index = -1
            //如果现有的item 里的数据 能和 data 里的数据匹配上就更新，否则就新增
            for (i in 0 until downloadAdapter.items.size) {
                var uiData = downloadAdapter.items.get(i)
                uiData.formatsList.forEach {
                    var uiVideoDownloadData = it
                    if (data.videoId == uiVideoDownloadData.videoId){
                        index = i
                        uiVideoDownloadData.size = it.size
                        uiVideoDownloadData.downloadType = it.downloadType
                    }

                }
            }
            withContext(Dispatchers.Main) {
                if (index >= 0) {
                    downloadAdapter.notifyItemChanged(index)
                }
            }
        }, failBack = {})
    }

    fun updateData() {
        (context as BaseActivity<*>).addLaunch(success = {
            var modelList = mutableListOf<VideoDownloadData>()
            DownloadCacheManager.queryAllModel().forEach {
                modelList.add(VideoDownloadData().createVideoDownloadData(it))
            }
            var list = CacheManager.videoDownloadTempList
            var endList = mutableListOf<VideoUIData>()
            if (modelList.isNullOrEmpty()) {
                //如果库里无数据则用临时缓存
                endList.addAll(list)
            } else {
                //如果库里有数据更新临时缓存
                for (i in 0 until list.size) {
                    var data = list.get(i)
                    data.formatsList.forEach {
                        for (k in 0 until modelList.size) {
                            var bean = modelList.get(k)
                            if (bean.videoId == it.videoId) {
                                it.covertByDbData(bean)
                                break
                            }
                        }
                    }
                    endList.add(data)
                }
            }
            var firstIndex = -1
            for (i in 0 until endList.size){
                //如果有多个数据默认只选中第一个
                //如果第一条有分辨率下载成功了，就过滤分辨率的item
                var data = endList.get(i)
                var index = -1
                var selectedResolution = "0"
                if (firstIndex == -1){
                    for (j in 0 until data.formatsList.size){
                        var formatData = data.formatsList.get(j)
                        var resolution = formatData.resolution?:""
                        formatData.videoChecked = false
                        if (allowCheckStatus(formatData) && index == -1){
                            index = j
                            firstIndex = i
//                        runCatching {
//                            resolution = resolution.substring(0,resolution.length-1)
//                            if (resolution.toInt()>selectedResolution.toInt()){
//                                selectedResolution = resolution
//                                index = i
//                            }
//                        }
                        }
                    }
                    if (index >= 0){
                        data.formatsList.get(index).videoChecked = true
                    }
                }else{
                    data.formatsList.forEach {
                        it.videoChecked = false
                    }
                }
            }
            withContext(Dispatchers.Main) {
                downloadAdapter.submitList(endList)
                updateBottomSize()
            }
        }, failBack = {})
    }

    fun updateBottomSize() {
        //未下载完成的个数
        var defaultSize = 0
        var sizeGone = defaultSize <=1
        var allSize = 0L
        var downSize = 0
        for (i in 0 until downloadAdapter.items.size){
           downloadAdapter.items.get(i).formatsList.forEach {
               if (it.downloadType != VideoDownloadData.DOWNLOAD_SUCCESS){
                   defaultSize++
               }
               if (it.videoChecked){
                   allSize += it?.size ?: 0
                   downSize++
               }
           }
        }
        defaultBinding?.apply {
            if (allSize == 0L){
                if (defaultSize == 0){
                    btnDownloadAll.text =
                        "${context.getString(R.string.app_open)}"

                }else{
                    btnDownloadAll.text =
                        "${context.getString(R.string.app_download)}"
                }
            }else{
                btnDownloadAll.text =
                    "${context.getString(R.string.app_download)}(${if (sizeGone) "" else context.getString(R.string.app_all)} ${allSize.formatSize()})"
            }
        }
    }

    fun updateStatus(
        activity: BaseActivity<*>,
        type: Int,
        data: VideoTaskItem?,
        callBack: (data: VideoDownloadData) -> Unit
    ) {
//        AppLogs.dLog(VideoManager.TAG, "updateStatus type:${type} url:${data?.url}")
//        if (data == null) return
//        var position = -1
//        for (i in 0 until downloadAdapter.items.size) {
//            var item = downloadAdapter.getItem(i)
//            if (item?.videoId ?: "" == data.downloadVideoId) {
//                position = i
//                break
//            }
//        }
//        AppLogs.dLog(VideoManager.TAG, "updateStatus position:${position} url:${data?.url}")
//        if (position == -1) return
//        var item = downloadAdapter.getItem(position) ?: return
//
//        var downloadModel = DownloadCacheManager.queryDownloadModelByUrl(data.url)
//        if (downloadModel == null) {
//            // 已删除
//            item.downloadType = VideoDownloadData.DOWNLOAD_NOT
//            item.downloadSize = 0
//        } else {
//            if (item.downloadType == VideoDownloadData.DOWNLOAD_SUCCESS) return
//            item.downloadType = type
//            item.downloadSize = data.downloadSize
//            if (item.downloadType != VideoDownloadData.DOWNLOAD_PAUSE) {
//                item.size = data.totalSize
//            }
//        }
//        if (type == VideoDownloadData.DOWNLOAD_SUCCESS) {
//            item.downloadFilePath = data.filePath
//            item.downloadFileName = data.fileName
//            downloadAdapter.notifyItemChanged(position, "updateLoading")
//            callBack.invoke(item)
//        } else {
//            downloadAdapter.notifyItemChanged(position, "updateLoading")
//        }
    }

    var isSelectedAll = false
    var tips2 :FirstDownloadTips?=null

    fun createPop(callBack: () -> Unit) {
        defaultBinding?.apply {
            root.postDelayed({
                if (CacheManager.isFirstDownloadTips2) {
                    CacheManager.isFirstDownloadTips2 = false
                    tips2 = FirstDownloadTips(context)
                    tips2?.createPop(btnDownloadAll,2)
                }
            },500)
            rv.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = downloadAdapter
                downloadAdapter.setPopContext(this@DownLoadPop)
            }
            tvDownload.setOnClickListener {
                context.startActivity(Intent(context, DownloadActivity::class.java).apply {
                    putExtra("fromPage", "webpage_download_pop")
                })
                PointEvent.posePoint(PointEventKey.webpage_download_pop_record)
            }
            tvClear.setOnClickListener {
                if (isSelectedAll) {
                    downloadAdapter.items.forEach {
                        it.formatsList.forEach {
                            it.videoChecked = false
                        }
                    }
                    var parentIndex = -1
                    var childIndex = -1
                    for (i in 0 until downloadAdapter.items.size){
                        var data = downloadAdapter.items.get(i)
                        for (j in 0 until data.formatsList.size){
                            var childData = data.formatsList.get(j)
                            if (allowCheckStatus(childData)){
                                childData.videoChecked = true
                                parentIndex = i
                                childIndex = j
                                break
                            }
                        }
                        if (parentIndex!=-1 && childIndex!=-1){
                            break
                        }
                    }
                    downloadAdapter.notifyDataSetChanged()
                    isSelectedAll = false
                    tvClear.text = context.getString(R.string.app_all)
                } else {
                    downloadAdapter.items.forEach {
                        it.formatsList.forEach {
                            it.videoChecked = true
                        }
                    }
                    isSelectedAll = true
                    tvClear.text = context.getString(R.string.app_clear_all)
                }
                updateBottomSize()
                downloadAdapter.notifyDataSetChanged()
            }
            btnDownloadAll.setOnClickListener {
                PointEvent.posePoint(PointEventKey.download_click)
                CacheManager.dayDownloadCount += 1
                if (btnDownloadAll.text.toString() == context.getString(R.string.app_open)){
                    context.startActivity(Intent(context, DownloadActivity::class.java).apply {
                        putExtra("fromPage", "webpage_download_pop")
                    })
                }else{
                    if (CacheManager.isDisclaimerFirst) {
                        CacheManager.isDisclaimerFirst = false
                        DisclaimerPop(context).createPop {
                            download(callBack)
                        }
                    } else {
                        showDownloadAD {
                            download(callBack)
                        }
                    }
                }
                tips2?.dismiss()
            }
        }

        downloadAdapter.setOnDebouncedItemClick { adapter, view, position ->
            var data = downloadAdapter.getItem(position)
            data?.apply {
                if (data.formatsList.size == 1){
                    var downloadData = data.formatsList.get(0)
                    if (downloadData.downloadType == VideoDownloadData.DOWNLOAD_SUCCESS) {
                        context.jumpActivity<VideoPreActivity>(Bundle().apply {
                            putString("video_path", toJson(downloadData))
                        })
                    } else{
                        if (allowCheckStatus(downloadData) && !downloadData.videoChecked){
                            downloadAdapter.items.forEach {
                                it.formatsList.forEach {
                                    it.videoChecked = false
                                }
                            }
                            downloadData.videoChecked = downloadData.videoChecked.not()
                            updateBottomSize()
                            downloadAdapter.notifyDataSetChanged()
                        }
//                        if (getCurrentCheckSize() <= 1 ){
//
//                        }else{
//
//                        }
                    }
                }
            }
        }
        updateData()
        setOutSideDismiss(true)
        setBackground(R.color.color_70_black)
        showPopupWindow()
        PointEvent.posePoint(PointEventKey.webpage_download_pop)
    }

    override fun dismiss() {
        tips2?.dismiss()
        super.dismiss()
    }

    private fun allowCheckStatus(data: VideoDownloadData): Boolean {
        return data.downloadType != VideoDownloadData.DOWNLOAD_SUCCESS
    }

    private fun getCurrentCheckSize(): Int {
        var checkSize = 0
        for (i in 0 until downloadAdapter.items.size){
            downloadAdapter.items.get(i).formatsList.forEach {
                if (it.videoChecked){
                    checkSize++
                }
            }
        }
        return checkSize
    }

    private fun download(callBack: () -> Unit) {
        var list = CacheManager.videoDownloadTempList
        var realDownload = false
        for (i in 0 until downloadAdapter.items.size){
            var data = downloadAdapter.items.get(i)
            var videoId = ""
            data.formatsList.forEach {
                if (it.videoChecked) {
                    realDownload = true
                    videoId = it.videoId?:""
                    clickDownload(it)
                    for (k in 0 until list.size){
                        var cacheData = list.get(k)
                        cacheData.formatsList.forEach {
                            if (it.videoId == videoId){
                                it.downloadType = VideoDownloadData.DOWNLOAD_PREPARE
                            }
                        }
                    }
                }
            }
        }
        CacheManager.videoDownloadTempList = list
        callBack.invoke()
        if (ShortManager.allowRate()) {
            var count = CacheManager.dayDownloadCount
            if (count == 3 || count == 5 || count == 10 || count == 15) {
                ShortManager.addRate(WeakReference(context as BaseActivity<*>),realDownload)
            }else{
                if (realDownload){
                    TaskAddPop(context).createPop()
                }
            }
        }else{
            if (realDownload){
                TaskAddPop(context).createPop()
            }
        }
        dismiss()
    }

    private fun clickDownload(data: VideoDownloadData) {
        data?.apply {
            if (data.downloadType!=VideoDownloadData.DOWNLOAD_NOT)return
            PointEvent.posePoint(PointEventKey.webpage_download_pop_dl)
            (context as BaseActivity<*>).addLaunch(success = {
                var model = DownloadCacheManager.queryDownloadModel(data)
                if (model == null) {
                    data.downloadType = VideoDownloadData.DOWNLOAD_PREPARE
                    DownloadCacheManager.addDownLoadPrepare(data)
                    withContext(Dispatchers.Main) {
//                                downloadAdapter.notifyItemChanged(position, "updateStatus")
                        var headerMap = HashMap<String, String>()
                        paramsMap?.forEach {
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
                } else {
                    withContext(Dispatchers.Main) {
                        ToastUtils.showLong(APP.instance.getString(R.string.app_already_download))
                    }
                }
            }, failBack = {})

        }
    }

    fun showDownloadAD(result: () -> Unit) {
        var manager = AioADShowManager(context as BaseActivity<*>, ADEnum.INT_AD, tag = "插屏") {
            result.invoke()
        }
        manager.showScreenAD(AD_POINT.aobws_download_int)
    }

    override fun onCreateShowAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withTranslation(TranslationConfig.FROM_BOTTOM)
            .toShow()
    }

    override fun onCreateDismissAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withTranslation(TranslationConfig.TO_BOTTOM)
            .toDismiss()
    }

}