package com.boom.aiobrowser.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.databinding.NewsFragmentBinding
import com.boom.aiobrowser.model.NewsViewModel
import com.boom.aiobrowser.net.NetParams
import com.boom.aiobrowser.other.NewsConfig
import com.boom.aiobrowser.other.ParamsConfig
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.activity.WebDetailsActivity
import com.boom.aiobrowser.ui.adapter.NewsMainAdapter
import com.boom.base.adapter4.util.setOnDebouncedItemClick

class NewsFragment: BaseFragment<NewsFragmentBinding>() {

    var topic:String = ""


    private val viewModel by lazy {
        viewModels<NewsViewModel>()
    }

    val newsAdapter by lazy {
        NewsMainAdapter(this)
    }

    var page = 1

    override fun startLoadData() {
        viewModel.value.getNewsData("${NewsConfig.TOPIC_TAG}${topic}")
    }

    override fun setListener() {
        viewModel.value.newsLiveData.observe(this){
            if (page == 1){
                newsAdapter.submitList(it)
            }else{
                newsAdapter.addAll(it)
            }
            fBinding.newsSmart.finishRefresh()
            fBinding.newsSmart.finishLoadMore()
        }
        fBinding.newsSmart.setOnRefreshListener{
            viewModel.value.getNewsData("${NewsConfig.TOPIC_TAG}${topic}",true)
            page = 1
        }
        fBinding.newsSmart.setOnLoadMoreListener{
            viewModel.value.getNewsData("${NewsConfig.TOPIC_TAG}${topic}",true)
            page++
        }
        newsAdapter.setOnDebouncedItemClick{adapter, view, position ->
            var data = newsAdapter.items.get(position)
            rootActivity.jumpActivity<WebDetailsActivity>(Bundle().apply {
                putString(ParamsConfig.JSON_PARAMS, toJson(data))
            })
        }
    }

    override fun setShowView() {
        topic =  arguments?.getString("selectedTopic")?:""
        fBinding.newsRv.apply {
            layoutManager = LinearLayoutManager(rootActivity,LinearLayoutManager.VERTICAL,false)
            adapter = newsAdapter
        }
    }

    companion object {
        fun newInstance(topic: String): BaseFragment<*>{
            val args = Bundle()
            args.putString("selectedTopic",topic)
            val fragment = NewsFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): NewsFragmentBinding {
        return NewsFragmentBinding.inflate(layoutInflater)
    }
}