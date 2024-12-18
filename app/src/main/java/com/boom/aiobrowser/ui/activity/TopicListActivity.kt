package com.boom.aiobrowser.ui.activity

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.TopicBean
import com.boom.aiobrowser.databinding.NewsActivityTopicListBinding
import com.boom.aiobrowser.model.NewsViewModel
import com.boom.aiobrowser.other.ParamsConfig
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.adapter.NewsMainAdapter
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import com.ironsource.ke

class TopicListActivity :BaseActivity<NewsActivityTopicListBinding>() {

    private val viewModel by lazy {
        viewModels<NewsViewModel>()
    }


    val newsAdapter by lazy {
        NewsMainAdapter(null)
    }

    override fun getBinding(inflater: LayoutInflater): NewsActivityTopicListBinding {
        return NewsActivityTopicListBinding.inflate(layoutInflater)
    }

    var isSearch = false

    override fun setListener() {
        acBinding.apply {
            ivBack.setOneClick {
                finish()
            }
            etSearch.setOnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    isSearch = true
                    newsSmart.autoRefresh()
                    hideKeyBoard(v)
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
            newsSmart.apply {
                setOnRefreshListener {
                    page = 1
                    loadData()
                }
                setOnLoadMoreListener {
                    page++
                    loadData()
                }
            }
        }
        viewModel.value.newsTopicListLiveData.observe(this){
            isSearch = false
            var key = 0
            var list:MutableList<NewsData>?= null
            it.forEach { i, newsData ->
                key = i
                list = newsData
            }
            list?.apply {
                if (page == 1){
                    if (key>0){
                        add(0,NewsData().apply {
                            dataType = NewsData.TYPE_TOPIC_HEADER
                        })
                    }
                    newsAdapter.submitList(this)
                    acBinding.rv.scrollToPosition(0)
                }else{
                    newsAdapter.addAll(this)
                }
            }
            finishLoadData()
        }
        viewModel.value.failLiveData.observe(this){
            isSearch = false
            finishLoadData()
        }
    }

    fun finishLoadData(){
        acBinding.newsSmart.finishLoadMore()
        acBinding.newsSmart.finishRefresh()
    }

    var page = 1

    var topicBean:TopicBean?=null

    override fun setShowView() {
        topicBean= getBeanByGson(intent.getStringExtra("topic")?:"",TopicBean::class.java)

        acBinding.apply {
            etSearch.setText(topicBean?.topic?:"")
            rv.apply {
                layoutManager = LinearLayoutManager(this@TopicListActivity,LinearLayoutManager.VERTICAL,false)
                adapter = newsAdapter
                newsAdapter.setOnDebouncedItemClick{adapter, view, position ->
                    if (position>newsAdapter.items.size-1)return@setOnDebouncedItemClick
                    var data = newsAdapter.items.get(position)
                    if (data.dataType == NewsData.TYPE_NEWS) {
                        jumpActivity<WebDetailsActivity>(Bundle().apply {
                            putString(ParamsConfig.JSON_PARAMS, toJson(data))
                        })
                    }
                }
            }
            newsSmart.autoRefresh()
        }
    }

    fun loadData(){
        viewModel.value.getNewsByTopic(acBinding.etSearch.text.toString().trim(),page,isSearch)
    }
}