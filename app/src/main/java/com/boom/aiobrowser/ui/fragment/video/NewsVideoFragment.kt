package com.boom.aiobrowser.ui.fragment.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.FragmentNewsVideoBinding
import com.boom.aiobrowser.model.NewsViewModel
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.getListByGson
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.adapter.VideoListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

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



    val dataList by lazy {
        mutableListOf<NewsData>()
    }

    var list:MutableList<NewsData>?=null

    var page = 1

    var firstLoad = true


    val videoListAdapter by lazy {VideoListAdapter(rootActivity,dataList) }

    override fun setShowView() {
        list = getListByGson(arguments?.getString("data",""),NewsData::class.java)
        var index = arguments?.getInt("index")?:0
        dataList.addAll(list ?: ArrayList())

        fBinding.videoVp.apply {
            setOrientation(ViewPager2.ORIENTATION_VERTICAL)
            adapter = videoListAdapter
            videoListAdapter.notifyDataSetChanged()
            offscreenPageLimit = 1
            setCurrentItem(index,false)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (dataList.size>0){
                        if (position >= dataList.size-3 && firstLoad){
                            firstLoad = false
                            page++
                            viewModel.value.getNewsVideoList()
                        }
                    }
                }
            })
//            post(Runnable { playPosition(0) })
        }
        fBinding.smart.apply {
            setOnRefreshListener {
                page=1
                viewModel.value.getNewsVideoList()
            }
            setOnLoadMoreListener {
                page++
                viewModel.value.getNewsVideoList()
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
    }

    companion object{
        fun newInstance(index:Int,jsonString:String): NewsVideoFragment {
            val args = Bundle()
            args.putInt("index",index)
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