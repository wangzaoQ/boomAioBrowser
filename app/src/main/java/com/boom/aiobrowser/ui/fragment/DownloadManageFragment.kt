package com.boom.aiobrowser.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
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
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
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
                var data = JumpData().apply {
                    jumpUrl = WebConfig.URL_Vimeo
                    jumpTitle = APP.instance.getString(R.string.video_vimeo)
                    jumpType = JumpConfig.JUMP_WEB
                }
                var jumpData = JumpDataManager.getCurrentJumpData(tag="download tab 点击", updateData = data)
                JumpDataManager.updateCurrentJumpData(jumpData,tag = "download tab 点击")
                APP.jumpLiveData.postValue(jumpData)
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
            fBinding.refreshLayout.isRefreshing = false
        }
        fBinding.refreshLayout.setOnRefreshListener {
            fBinding.refreshLayout.isRefreshing = true
            page = 1
            loadData()
        }
        videoAdapter.apply {
            setOnDebouncedItemClick{adapter, view, position ->
                VideoListActivity.startVideoListActivity(
                    adapter.context as BaseActivity<*>,
                    position,
                    videoAdapter.mutableItems,
                    ""
                )
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
                    CacheManager.videoDownloadSingleTempList = list
                    withContext(Dispatchers.Main){
                        DownLoadPop(rootActivity,2).createPop {  }
                    }
                }, failBack = {})
            }
        }
        fBinding.llDownload.getChildAt(0).setOneClick {
            DownloadVideoGuidePop(rootActivity).createPop(0) {  }
        }
        fBinding.llDownload.getChildAt(1).setOneClick {
            rootActivity.jumpActivity<DownloadActivity>()
        }
    }

    fun loadData() {
        if (page > 1) {
            adapterHelper.trailingLoadState = LoadState.Loading
        } else {
            adapterHelper.trailingLoadState = LoadState.None
            fBinding.refreshLayout.isRefreshing = true
        }
        viewModel.value.getDownloadVideo(videoAdapter.mutableItems, page, false)
    }

    override fun setShowView() {
        fBinding.apply {
            rv.apply {
                layoutManager = GridLayoutManager(rootActivity, 2)
                adapter = adapterHelper.adapter
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        val topRowVerticalPosition =
                            if (recyclerView == null || recyclerView.childCount === 0)
                                0 else recyclerView.getChildAt(0).top
                        // 大于0表示正在向上滑动，小于等于0表示停止或向下滑动
                        fBinding.refreshLayout.isEnabled = topRowVerticalPosition >= 0
                    }
                })
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