package com.boom.aiobrowser.ui.activity

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import com.blankj.utilcode.util.SizeUtils.dp2px
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.databinding.BrowserDragLayoutBinding
import com.boom.aiobrowser.databinding.NewsActivityVideoListBinding
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.BigDecimalUtils
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.FragmentManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.download.DownloadCacheManager
import com.boom.aiobrowser.tools.getListByGson
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.tools.video.VideoPreloadManager
import com.boom.aiobrowser.ui.fragment.video.NewsVideoFragment
import com.boom.aiobrowser.ui.pop.DisclaimerPop
import com.boom.aiobrowser.ui.pop.DownLoadPop
import com.boom.aiobrowser.ui.pop.FirstDownloadTips
import com.boom.drag.EasyFloat
import com.boom.drag.enums.SidePattern
import com.boom.drag.utils.DisplayUtils
import com.boom.video.GSYVideoManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import pop.basepopup.BasePopupWindow.OnDismissListener

class VideoListActivity : BaseActivity<NewsActivityVideoListBinding>() {
    override fun getBinding(inflater: LayoutInflater): NewsActivityVideoListBinding {
        return NewsActivityVideoListBinding.inflate(layoutInflater)
    }

    override fun setListener() {
    }

    var enumName = ""
    var fromType = ""
    var list:MutableList<NewsData>?=null
    var index = 0

    override fun setShowView() {
        var manager = FragmentManager()
        var jsonString = intent.getStringExtra("data")?:""
        index = intent.getIntExtra("index",0)
        list = getListByGson(jsonString,NewsData::class.java)
        enumName = intent.getStringExtra("enumName")?:""
        fromType = intent.getStringExtra(PointValueKey.from_type)?:""
        dragBiding = BrowserDragLayoutBinding.inflate(layoutInflater, null, false)
        manager.addFragment(supportFragmentManager, NewsVideoFragment.newInstance(index,jsonString,enumName,fromType?:""),
            R.id.flRoot)
        saveStayTime = true
    }
    var dragBiding: BrowserDragLayoutBinding? = null
    var tips1: FirstDownloadTips? = null

    open fun updateDownloadButtonStatus(type: Int = 0) {
        addLaunch(success = {
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
                        AppLogs.dLog(acTAG, "展示有数据下载状态 type:${type}")
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
                                tips1 = FirstDownloadTips(this@VideoListActivity)
                                tips1?.createPop(root, 1)
                            }
                        }
                        AppLogs.dLog(acTAG, "展示有数据下载状态完成 type:${type}")
                    } else {
                        AppLogs.dLog(acTAG, "展示无数据下载状态")
                        ivDownload.visibility = View.VISIBLE
                        ivDownload2.visibility = View.GONE
                        tvDownload.visibility = View.GONE
                        ivDownload2.cancelAnimation()
                        AppLogs.dLog(acTAG, "展示无数据下载状态完成")
                    }
                }
            }
        }, failBack = {})
    }

    fun addDownload() {
        CacheManager.videoPreTempList = mutableListOf()
        //            EasyFloat.dismiss(tag = "webPop", true)
        var startX = 0
        var startY = 0
        var dragX = CacheManager.dragX
        var dragY = CacheManager.dragY
        startX = if (dragX == 0) {
            DisplayUtils.getScreenWidth(this) - dp2px(85f) - dp2px(28f)
        } else {
            dragX
        }
        startY = if (dragY == 0) {
            DisplayUtils.getScreenHeight(this) - (BigDecimalUtils.div(
                DisplayUtils.getScreenWidth(
                    this
                ).toDouble(), 3.0
            ).toInt() * 2)
        } else {
            dragY
        }
        dragBiding?.apply {
            ivDownload.visibility = View.GONE
            ivDownload2.visibility = View.VISIBLE
            EasyFloat.with(this@VideoListActivity)
                .setSidePattern(SidePattern.RESULT_HORIZONTAL)
                .setImmersionStatusBar(true)
                .setGravity(Gravity.START or Gravity.BOTTOM, offsetX = startX, offsetY = startY)
                .setLocation(startX, startY)
                .setTag("download")
                // 传入View，传入布局文件皆可，如：MyCustomView(this)、R.layout.float_custom
                .setLayout(root) {
                    ivDownload2.setOneClick {
                        ivDownload2.cancelAnimation()
                        addLaunch(success = {
                            delay(500)
                            withContext(Dispatchers.Main) {
                                if (CacheManager.isDisclaimerFirst) {
                                    CacheManager.isDisclaimerFirst = false
                                    DisclaimerPop(this@VideoListActivity).createPop {
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
    var popDown: DownLoadPop? = null

    private fun showDownloadPop() {
        popDown = DownLoadPop(this,1)
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



    companion object{
        /**
         * enumName 有值则是从通知进入
         */
        fun startVideoListActivity(activity: BaseActivity<*>,index:Int,list: MutableList<NewsData>?,enumName:String,fromType:String){
            activity.jumpActivity<VideoListActivity>(Bundle().apply {
                putString("data", toJson(list))
                putInt("index", index)
                putString("enumName", enumName)
                putString(PointValueKey.from_type, fromType)
            })
        }
    }

    override fun onDestroy() {
        if (saveStayTime){
            PointEvent.posePoint(PointEventKey.video_stay_time,Bundle().apply {
                putLong(PointValueKey.stay_time,stayTime)
                putString(PointValueKey.from_type,fromType)
            })
        }
        AppLogs.dLog("VideoListFragment","activity onDestroy1")
//        GSYVideoManager.releaseAllVideos()
        AppLogs.dLog("VideoListFragment","activity onDestroy2")
        if (enumName.isNullOrEmpty().not()){
            APP.homeJumpLiveData.postValue(0)
        }
        EasyFloat.dismiss(tag = "download")
        AppLogs.dLog("VideoListFragment","activity onDestroy3")
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        AppLogs.dLog(VideoPreloadManager.TAG,"VideoListActivity onPause")
        GSYVideoManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        AppLogs.dLog(VideoPreloadManager.TAG,"VideoListActivity onResume")
        GSYVideoManager.onResume()
    }
}