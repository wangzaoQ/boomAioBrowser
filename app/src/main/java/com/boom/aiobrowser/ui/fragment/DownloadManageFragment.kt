package com.boom.aiobrowser.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADShowManager
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.data.VideoUIData
import com.boom.aiobrowser.databinding.BrowserFragmentDownloadManageBinding
import com.boom.aiobrowser.model.NewsViewModel
import com.boom.aiobrowser.other.JumpConfig
import com.boom.aiobrowser.other.WebConfig
import com.boom.aiobrowser.point.AD_POINT
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.web.PManager.getVideoSegmentSize
import com.boom.aiobrowser.ui.activity.DownloadActivity
import com.boom.aiobrowser.ui.activity.HotVideosActivity
import com.boom.aiobrowser.ui.activity.VideoListActivity
import com.boom.aiobrowser.ui.adapter.NewsMainAdapter
import com.boom.aiobrowser.ui.pop.DownLoadPop
import com.boom.aiobrowser.ui.pop.DownloadVideoGuidePop
import com.boom.base.adapter4.QuickAdapterHelper
import com.boom.base.adapter4.loadState.LoadState
import com.boom.base.adapter4.loadState.trailing.TrailingLoadStateAdapter
import com.boom.base.adapter4.util.addOnDebouncedChildClick
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import com.boom.downloader.utils.VideoDownloadUtils
import kotlinx.coroutines.Dispatchers
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
                rootActivity.jumpActivity<HotVideosActivity>()
            }
        }
        viewModel.value.newsDownloadVideoLiveData.observe(this) {
            if (page == 1) {
                videoAdapter.submitList(it)
            }
            videoAdapter.addAll(it)
            videoAdapter.notifyDataSetChanged()
            adapterHelper.trailingLoadState = LoadState.NotLoading(false)
            fBinding.refreshLayout.finishRefresh()
            fBinding.refreshLayout.finishLoadMore()
        }
        fBinding.refreshLayout.setOnRefreshListener {
            page = 1
            loadData()
        }
        videoAdapter.apply {
            setOnDebouncedItemClick{adapter, view, position ->
                if (position>videoAdapter.items.size-1)return@setOnDebouncedItemClick
                var manager = AioADShowManager(rootActivity, ADEnum.INT_AD, tag = "新闻列表点击的广告"){
                    VideoListActivity.startVideoListActivity(
                        adapter.context as BaseActivity<*>,
                        position,
                        videoAdapter.mutableItems,
                        ""
                    )
                }
                manager.showScreenAD(AD_POINT.aobws_downclick_int)
            }
            addOnDebouncedChildClick(R.id.llDownload) { adapter, view, position ->
                rootActivity.addLaunch(success = {
                    var list = mutableListOf<VideoUIData>()
                    CacheManager.videoDownloadSingleTempList = list
                    var newsData = videoAdapter.mutableItems.get(position)

                    var uiData = VideoUIData()
                    uiData.thumbnail = newsData.iassum
                    uiData.videoResultId = "${VideoDownloadUtils.computeMD5(newsData.vbreas)}"
                    var videoDownloadData = VideoDownloadData().createDefault(
                        videoId = "${VideoDownloadUtils.computeMD5(newsData.vbreas)}",
                        fileName = rootActivity.getString(R.string.video_local_title),
                        url = newsData.vbreas?:"",
                        imageUrl = newsData.iassum?:"",
                        paramsMap = HashMap<String,Any>(),
                        size = getVideoSegmentSize(newsData.vbreas?:"",HashMap()),
                        videoType = "mp4",
                        resolution = ""
                    )
                    uiData.formatsList.add(videoDownloadData)
                    list.add(uiData)
                    CacheManager.videoDownloadSingleTempList = list
                    withContext(Dispatchers.Main){
                        DownLoadPop(rootActivity,2).createPop {  }
                    }
                }, failBack = {})
            }
        }
        fBinding.llDownload.getChildAt(0).setOneClick {
            var manager = AioADShowManager(rootActivity, ADEnum.INT_AD, tag = "下载管理点击教程") {
                DownloadVideoGuidePop(rootActivity).createPop(0) {  }
            }
            manager.showScreenAD(AD_POINT.aobws_downguide_int)
        }
        fBinding.llDownload.getChildAt(1).setOneClick {
            var manager = AioADShowManager(rootActivity, ADEnum.INT_AD, tag = "下载管理点击下载页") {
                rootActivity.jumpActivity<DownloadActivity>()
            }
            manager.showScreenAD(AD_POINT.aobws_downguide_int)
        }
    }

    private fun toWeb(url:String,title:String) {
        var data = JumpData().apply {
            jumpUrl = url
            jumpTitle = title
            jumpType = JumpConfig.JUMP_WEB
        }
        var jumpData = JumpDataManager.getCurrentJumpData(tag="download tab 点击", updateData = data)
        JumpDataManager.updateCurrentJumpData(jumpData,tag = "download tab 点击")
        APP.jumpLiveData.postValue(jumpData)
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
                layoutManager = GridLayoutManager(rootActivity, 2)
                adapter = adapterHelper.adapter
//                addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                        super.onScrolled(recyclerView, dx, dy)
//                        var topRowVerticalPosition = if (recyclerView == null || recyclerView.childCount === 0)
//                                0 else recyclerView.getChildAt(0).top
//                        AppLogs.dLog(fragmentTAG,"getChildAt:${topRowVerticalPosition}")
//
//
//                        val firstItemView = rv.layoutManager?.findViewByPosition(0)
//                        topRowVerticalPosition = firstItemView?.top ?: -1
//                        AppLogs.dLog(fragmentTAG,"firstItemView?.top:${topRowVerticalPosition}")
//                        topRowVerticalPosition =
//                            (recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstCompletelyVisibleItemPosition()
//                        AppLogs.dLog(fragmentTAG,"findFirstCompletelyVisibleItemPosition:${topRowVerticalPosition}")
//                        // 大于0表示正在向上滑动，小于等于0表示停止或向下滑动
//                        fBinding.refreshLayout.isEnabled = topRowVerticalPosition >= 0
//                    }
//                })
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