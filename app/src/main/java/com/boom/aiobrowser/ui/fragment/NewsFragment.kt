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
import com.boom.aiobrowser.ui.adapter.NewsMainAdapter

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
        }
        fBinding.newsSmart.setOnRefreshListener{
            page = 1
        }
        fBinding.newsSmart.setOnLoadMoreListener{
            page++
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