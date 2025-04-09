package com.boom.aiobrowser.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADShowManager
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.data.VideoUIData
import com.boom.aiobrowser.databinding.BrowserFragmentDownloadManageBinding
import com.boom.aiobrowser.model.NewsViewModel
import com.boom.aiobrowser.other.JumpConfig
import com.boom.aiobrowser.other.WebConfig
import com.boom.aiobrowser.point.AD_POINT
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.video.VideoPreloadManager
import com.boom.aiobrowser.tools.web.PManager.getVideoSegmentSize
import com.boom.aiobrowser.ui.activity.DownloadActivity
import com.boom.aiobrowser.ui.activity.HotVideosActivity
import com.boom.aiobrowser.ui.activity.SearchActivity
import com.boom.aiobrowser.ui.activity.VideoListActivity
import com.boom.aiobrowser.ui.adapter.NewsMainAdapter
import com.boom.aiobrowser.ui.pop.DisclaimerPop
import com.boom.aiobrowser.ui.pop.DownLoadPop
import com.boom.aiobrowser.ui.pop.DownloadVideoGuidePop
import com.boom.base.adapter4.QuickAdapterHelper
import com.boom.base.adapter4.loadState.LoadState
import com.boom.base.adapter4.loadState.trailing.TrailingLoadStateAdapter
import com.boom.base.adapter4.util.addOnDebouncedChildClick
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import com.boom.downloader.utils.VideoDownloadUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


class DownloadManageFragment : BaseFragment<BrowserFragmentDownloadManageBinding>() {

    val videoAdapter by lazy {
        NewsMainAdapter(this)
    }

    val adapterHelper by lazy {
        QuickAdapterHelper.Builder(videoAdapter)
            .setTrailingLoadStateAdapter(object :
                TrailingLoadStateAdapter.OnTrailingListener {
                override fun onLoad() {
                    AppLogs.dLog(fragmentTAG, "加载更多")
                    page++
                    loadData()
                }

                override fun onFailRetry() {

                }

                override fun isAllowLoading(): Boolean {
                    return fBinding.refreshLayout.isRefreshing.not()
                }

            }).build()
    }

    var page = 1

    private val viewModel by lazy {
        viewModels<NewsViewModel>()
    }

    override fun startLoadData() {

    }

    override fun setListener() {
        fBinding.apply {
            rlSearch.setOneClick {
                rootActivity.jumpActivity<SearchActivity>(Bundle().apply {
                    putString(PointValueKey.from_type,"download")
                })
            }
            llVimeo.setOneClick {
                toWeb(WebConfig.URL_Vimeo,rootActivity.getString(R.string.video_vimeo))
            }
            llTiktok.setOneClick {
                toWeb(WebConfig.URL_TikTok,rootActivity.getString(R.string.video_tiktok))
            }
            llInstagram.setOneClick {
                toWeb(WebConfig.URL_Instagram,rootActivity.getString(R.string.app_instagram))
            }
            llDailyMotion.setOneClick {
                toWeb(WebConfig.URL_Dailymotion,rootActivity.getString(R.string.app_dailymotion))
            }
            llHotVideos.setOneClick {
                PointEvent.posePoint(PointEventKey.hot_videos)
                rootActivity.jumpActivity<HotVideosActivity>()
            }
        }
        viewModel.value.newsDownloadVideoLiveData.observe(this) {
            rootActivity.addLaunch(success = {
                it.forEach {
                    if (it.vbreas.isNullOrEmpty().not()){
                        VideoPreloadManager.serialList(1, mutableListOf<NewsData>().apply {
                            add(it)
                        })
                    }
                }
            }, failBack = {})
            if (page == 1) {
                videoAdapter.submitList(it)
            }else{
                videoAdapter.addAll(it)
            }
            videoAdapter.notifyDataSetChanged()
            adapterHelper.trailingLoadState = LoadState.NotLoading(false)
            fBinding.refreshLayout.finishRefresh()
            fBinding.refreshLayout.finishLoadMore()
        }
        viewModel.value.failLiveData.observe(this){
            fBinding.refreshLayout.finishRefresh()
            fBinding.refreshLayout.finishLoadMore()
            ToastUtils.showShort(rootActivity.getString(R.string.net_error))
        }
        fBinding.refreshLayout.setOnRefreshListener {
            page = 1
            loadData()
        }
        videoAdapter.apply {
            setOnDebouncedItemClick{adapter, view, position ->
                if (position == 0 || position>videoAdapter.items.size-1)return@setOnDebouncedItemClick
                var videoList = mutableListOf<NewsData>()
                for (i in 1 until videoAdapter.mutableItems.size){
                    videoList.add(videoAdapter.mutableItems.get(i))
                }
                VideoListActivity.startVideoListActivity(
                    adapter.context as BaseActivity<*>,
                    position-1,
                    videoList,
                    "",
                    "daily_video"
                )
            }
            addOnDebouncedChildClick(R.id.llDownload) { adapter, view, position ->
                if (CacheManager.isDisclaimerFirst) {
                    CacheManager.isDisclaimerFirst = false
                    DisclaimerPop(rootActivity).createPop {
                        clickDownload(position)
                    }
                } else {
                    clickDownload(position)
                }
            }
        }
        fBinding.llDownload.getChildAt(0).setOneClick {
            DownloadVideoGuidePop(rootActivity).createPop("download_tab") {  }
        }
        fBinding.llDownload.getChildAt(1).setOneClick {
            PointEvent.posePoint(PointEventKey.download_manager)
            rootActivity.jumpActivity<DownloadActivity>()
        }
    }

    private fun clickDownload(position: Int) {
        rootActivity.showPop()
        var currentTime = System.currentTimeMillis()
        rootActivity.addLaunch(success = {
            var list = mutableListOf<VideoUIData>()
            CacheManager.videoDownloadSingleTempList = list
            var newsData = videoAdapter.mutableItems.get(position)
            PointEvent.posePoint(PointEventKey.download_tab_dl, Bundle().apply {
                putString(PointValueKey.from_type, "daily_video")
                putString(PointValueKey.news_id, newsData.itackl)
            })
            var uiData = VideoUIData()
            uiData.thumbnail = newsData.iassum
            uiData.videoResultId = "${VideoDownloadUtils.computeMD5(newsData.vbreas)}"
            var videoDownloadData = VideoDownloadData().createDefault(
                videoId = "${VideoDownloadUtils.computeMD5(newsData.vbreas)}",
                fileName = newsData.tconsi ?: "",
                url = newsData.vbreas ?: "",
                imageUrl = newsData.iassum ?: "",
                paramsMap = HashMap<String, Any>(),
                size = getVideoSegmentSize(newsData.vbreas ?: "", HashMap()),
                videoType = "mp4",
                resolution = ""
            )
            uiData.formatsList.add(videoDownloadData)
            list.add(uiData)
            CacheManager.videoDownloadSingleTempList = list
            var middleTime = System.currentTimeMillis() - currentTime
            if (middleTime < 1000) {
                delay(1000 - middleTime)
            }
            withContext(Dispatchers.Main) {
                rootActivity.hidePop()
                DownLoadPop(rootActivity, 2).createPop("download_tab") { }
            }
        }, failBack = {})
    }

    private fun toWeb(url:String,title:String) {
        var manager = AioADShowManager(rootActivity, ADEnum.INT_AD, tag = "下载页点击enum") {
            var data = JumpData().apply {
                jumpUrl = url
                jumpTitle = title
                jumpType = JumpConfig.JUMP_WEB
            }
            var jumpData = JumpDataManager.getCurrentJumpData(tag="download tab 点击", updateData = data)
            JumpDataManager.updateCurrentJumpData(jumpData,tag = "download tab 点击")
            APP.jumpLiveData.postValue(jumpData)
        }
        manager.showScreenAD(AD_POINT.aobws_downclick_int)
    }

    fun loadData() {
        if (page > 1) {
            adapterHelper.trailingLoadState = LoadState.Loading
        } else {
            adapterHelper.trailingLoadState = LoadState.None
        }
        viewModel.value.getDownloadVideo(videoAdapter.mutableItems, page, false)
    }

    override fun setShowView() {
        fBinding.apply {
            rv.apply {
                var manager = GridLayoutManager(rootActivity, 2)
                manager.spanSizeLookup = object : SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if(position == 0){
                            2
                        }else{
                            1
                        }
                    }

                }
                layoutManager = manager
                adapter = adapterHelper.adapter
            }
        }
        loadData()
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BrowserFragmentDownloadManageBinding {
        return BrowserFragmentDownloadManageBinding.inflate(layoutInflater)
    }
}