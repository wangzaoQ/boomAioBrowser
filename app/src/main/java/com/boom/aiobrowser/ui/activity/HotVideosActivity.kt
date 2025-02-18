package com.boom.aiobrowser.ui.activity

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADShowManager
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.data.VideoUIData
import com.boom.aiobrowser.databinding.BrowserActivityHotVideosBinding
import com.boom.aiobrowser.model.NewsViewModel
import com.boom.aiobrowser.point.AD_POINT
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.web.PManager.getVideoSegmentSize
import com.boom.aiobrowser.ui.adapter.NewsMainAdapter
import com.boom.aiobrowser.ui.pop.DownLoadPop
import com.boom.base.adapter4.util.addOnDebouncedChildClick
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import com.boom.downloader.utils.VideoDownloadUtils
import com.boom.drag.utils.DisplayUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HotVideosActivity:BaseActivity<BrowserActivityHotVideosBinding>() {

    val videoAdapter by lazy {
        NewsMainAdapter(null)
    }

    var page = 1

    private val viewModel by lazy {
        viewModels<NewsViewModel>()
    }

    override fun onBackPressed() {
        acBinding.ivBack.performClick()
    }

    override fun getBinding(inflater: LayoutInflater): BrowserActivityHotVideosBinding {
        return BrowserActivityHotVideosBinding.inflate(layoutInflater)
    }

    override fun setListener() {
        acBinding.ivBack.setOneClick {
            var manager = AioADShowManager(this@HotVideosActivity, ADEnum.INT_AD, tag = "hotVideos"){
                finish()
            }
            manager.showScreenAD(AD_POINT.aobws_return_int)
        }
        acBinding.newsSmart.setOnLoadMoreListener {
            page++
            loadData()
        }
        acBinding.newsSmart.setOnRefreshListener {
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
                addLaunch(success = {
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
                        DownLoadPop(this@HotVideosActivity,2).createPop {  }
                    }
                }, failBack = {})
            }
        }
        viewModel.value.newsHotVideoLiveData.observe(this){
            if (page == 1){
                videoAdapter.submitList(it)
            }else{
                videoAdapter.addAll(it)
            }
            acBinding.newsSmart.finishRefresh()
            acBinding.newsSmart.finishLoadMore()
        }
    }

    fun loadData(){
        viewModel.value.getHotVideos()
    }

    override fun setShowView() {
        acBinding.apply {
            rv.apply {
                layoutManager = GridLayoutManager(this@HotVideosActivity, 2)
                adapter = videoAdapter
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        val topRowVerticalPosition =
                            if (recyclerView == null || recyclerView.childCount === 0)
                                0 else recyclerView.getChildAt(0).top
                        // 大于0表示正在向上滑动，小于等于0表示停止或向下滑动
//                        fBinding.refreshLayout.isEnabled = topRowVerticalPosition >= 0
                    }
                })
                addItemDecoration(object : RecyclerView.ItemDecoration() {

                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        super.getItemOffsets(outRect, view, parent, state)
                        val itemPosition = parent.getChildAdapterPosition(view)
                        if (itemPosition == 0 || itemPosition == 1) {
                            outRect.top = DisplayUtils.dp2px(
                                context,
                                16f
                            )
                        }
                    }
                })
            }
        }
        loadData()
    }
}