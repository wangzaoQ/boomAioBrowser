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
import com.boom.aiobrowser.ui.activity.VideoListActivity
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

    }

    override fun setListener() {
    }

    val dataList by lazy {
        mutableListOf<NewsData>()
    }

    var list:MutableList<NewsData>?=null

    var page = 1
    var enumName =""
    var fromType =""
    var index = 0

    var firstLoad = true


    val videoListAdapter by lazy {VideoListAdapter(rootActivity,dataList,fromType) }

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
                }
            })
            adapter = videoListAdapter
            offscreenPageLimit = 1
            videoListAdapter.notifyDataSetChanged()
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

//        runCatching {
//            var bean = list?.get(index)
//            var videoUrl = bean?.vbreas?:""
//            if (videoUrl.isNullOrEmpty())return
//
//            firstPlayVideoUrl = CacheManager.firstPlayVideoUrl
//            if (firstPlayVideoUrl.isNullOrEmpty()){
//                CacheManager.firstPlayVideoUrl = videoUrl
//                firstPlayVideoUrl = videoUrl
//                EasyFloat.dismiss(tag = "download")
//            }else{
//                if (firstPlayVideoUrl != videoUrl){
//                    (rootActivity as VideoListActivity).addDownload()
//                    showGuideAnimal()
//                }else{
//                    EasyFloat.dismiss(tag = "download")
//                }
//            }
//        }
        fBinding.videoVp.setCurrentItem(index,false)
    }

    fun showGuideAnimal(){
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

    var firstPlayVideoUrl = ""

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