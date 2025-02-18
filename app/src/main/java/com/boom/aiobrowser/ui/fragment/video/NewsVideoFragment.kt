package com.boom.aiobrowser.ui.fragment.video

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.SizeUtils.dp2px
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.data.VideoUIData
import com.boom.aiobrowser.databinding.BrowserDragLayoutBinding
import com.boom.aiobrowser.databinding.FragmentNewsVideoBinding
import com.boom.aiobrowser.model.NewsViewModel
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.BigDecimalUtils
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.download.DownloadCacheManager
import com.boom.aiobrowser.tools.getListByGson
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.tools.web.PManager.getVideoSegmentSize
import com.boom.aiobrowser.ui.adapter.VideoListAdapter
import com.boom.aiobrowser.ui.pop.DisclaimerPop
import com.boom.aiobrowser.ui.pop.DownLoadPop
import com.boom.aiobrowser.ui.pop.FirstDownloadTips
import com.boom.downloader.utils.VideoDownloadUtils
import com.boom.drag.EasyFloat
import com.boom.drag.enums.SidePattern
import com.boom.drag.utils.DisplayUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import pop.basepopup.BasePopupWindow.OnDismissListener
import java.math.BigDecimal

class NewsVideoFragment :  BaseFragment<FragmentNewsVideoBinding>(){

    private val viewModel by lazy {
        viewModels<NewsViewModel>()
    }


    override fun startLoadData() {
        if (CacheManager.isFirstVideoGuide){
            CacheManager.isFirstVideoGuide = false
            fBinding.lottieAnim.visibility = View.VISIBLE
            fBinding.lottieAnim.apply {
                setAnimation("video_guide.json")
                playAnimation()
            }
            rootActivity.addLaunch(success = {
                delay(3000)
                withContext(Dispatchers.Main){
                    fBinding.lottieAnim.cancelAnimation()
                    fBinding.lottieAnim.visibility = View.GONE
                }
            }, failBack = {})
        }
    }

    override fun setListener() {
    }

    var popDown: DownLoadPop? = null

    var dragBiding: BrowserDragLayoutBinding? = null

    private fun addDownload() {
        CacheManager.videoPreTempList = mutableListOf()
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
        dragBiding?.apply {
            ivDownload.visibility = View.GONE
            ivDownload2.visibility = View.VISIBLE
            EasyFloat.with(rootActivity)
                .setSidePattern(SidePattern.RESULT_HORIZONTAL)
                .setImmersionStatusBar(true)
                .setGravity(Gravity.START or Gravity.BOTTOM, offsetX = startX, offsetY = startY)
                .setLocation(startX, startY)
                .setTag("download")
                // 传入View，传入布局文件皆可，如：MyCustomView(this)、R.layout.float_custom
                .setLayout(root) {
                    ivDownload2.setOneClick {
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
                            list?.apply {
                                if(index<size){
                                    putString(PointValueKey.url, get(index).itackl)
                                }
                            }
                            putString(PointValueKey.from_type,fromType)
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
        updateDownloadButtonStatus(0)
    }

    private fun showDownloadPop() {
        popDown = DownLoadPop(rootActivity,1)
        popDown?.createPop("video") {
            updateDownloadButtonStatus( 1)
        }
        popDown?.setOnDismissListener(object : OnDismissListener() {
            override fun onDismiss() {
                updateDownloadButtonStatus( 1)
            }
        })
        CacheManager.isFirstClickDownloadButton = false
    }
    open fun updateDownloadButtonStatus(type: Int = 0) {
        rootActivity.addLaunch(success = {
            var size = 0
            var tempList = CacheManager.videoPreTempList
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
        }, failBack = {})
    }
    var tips1: FirstDownloadTips? = null



    val dataList by lazy {
        mutableListOf<NewsData>()
    }

    var list:MutableList<NewsData>?=null

    var page = 1
    var enumName =""
    var fromType =""
    var index = 0

    var firstLoad = true


    val videoListAdapter by lazy {VideoListAdapter(rootActivity,dataList) }

    override fun setShowView() {
        list = getListByGson(arguments?.getString("data",""),NewsData::class.java)
        index = arguments?.getInt("index")?:0
        enumName = arguments?.getString("enumName","")?:""
        fromType = arguments?.getString(PointValueKey.from_type)?:""
        dataList.addAll(list ?: ArrayList())
        list?.let {
            PointEvent.posePoint(PointEventKey.download_videos_page,Bundle().apply {
                putString(PointValueKey.from_type,fromType)
                if(index<it.size){
                    putString(PointValueKey.news_id,it.get(index).itackl)
                }
            })
        }

        fBinding.videoVp.apply {
            setOrientation(ViewPager2.ORIENTATION_VERTICAL)
            adapter = videoListAdapter
            videoListAdapter.notifyDataSetChanged()
            offscreenPageLimit = 1

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (dataList.size>0){
                        if (position >= dataList.size-3 && firstLoad){
                            firstLoad = false
                            page++
                            viewModel.value.getNewsVideoList(enumName,list!!.get(index),dataList)
                        }
                    }
                    rootActivity.addLaunch(success = {
                        var list = CacheManager.videoPreTempList

                        var newsData = dataList.get(position)
                        var index = -1
                        for (i in 0 until list.size){
                            var data = list.get(i)
                            if (data.videoResultId == VideoDownloadUtils.computeMD5(newsData.vbreas)){
                                index = i
                                break
                            }
                        }
                        if(index>=0)return@addLaunch
                        var uiData = VideoUIData()
                        uiData.thumbnail = newsData.iassum
                        uiData.videoResultId = "${VideoDownloadUtils.computeMD5(newsData.vbreas)}"
                        var videoDownloadData = VideoDownloadData().createDefault(
                            videoId = "${VideoDownloadUtils.computeMD5(newsData.vbreas)}",
                            fileName = getString(R.string.video_local_title),
                            url = newsData.vbreas?:"",
                            imageUrl = newsData.iassum?:"",
                            paramsMap = HashMap<String,Any>(),
                            size = getVideoSegmentSize(newsData.vbreas?:"",HashMap()),
                            videoType = "mp4",
                            resolution = ""
                        )
                        uiData.formatsList.add(videoDownloadData)
                        list.add(uiData)
                        CacheManager.videoPreTempList = list
                        withContext(Dispatchers.Main){
                            updateDownloadButtonStatus(0)
                        }
                    }, failBack = {})
                }
            })
//            post(Runnable { playPosition(0) })
        }
        fBinding.smart.apply {
            setOnRefreshListener {
                page=1
                viewModel.value.getNewsVideoList(enumName,list!!.get(index),dataList)
            }
            setOnLoadMoreListener {
                page++
                viewModel.value.getNewsVideoList(enumName,list!!.get(index),dataList)
            }
        }

        viewModel.value.newsVideoLiveData.observe(this) {
            runCatching {
                if (page == 1){
                    dataList.clear()
                }
                var startSize = dataList.size
                dataList.addAll(it)
                if (page == 1){
                    videoListAdapter.notifyDataSetChanged()
                }else{
                    videoListAdapter.notifyItemRangeChanged(startSize,dataList.size)
                }
                fBinding.smart.finishRefresh()
                fBinding.smart.finishLoadMore()
                firstLoad = true
            }
        }
        dragBiding = BrowserDragLayoutBinding.inflate(layoutInflater, null, false)
        addDownload()
        fBinding.videoVp.setCurrentItem(index,false)
    }

    companion object{
        fun newInstance(index:Int,jsonString:String,enumName:String,fromType:String): NewsVideoFragment {
            val args = Bundle()
            args.putInt("index",index)
            args.putString("enumName",enumName)
            args.putString(PointValueKey.from_type,fromType)
            if (jsonString.isNullOrEmpty().not()){
                args.putString("data", jsonString)
            }
            val fragment = NewsVideoFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNewsVideoBinding {
        return FragmentNewsVideoBinding.inflate(layoutInflater)
    }
}