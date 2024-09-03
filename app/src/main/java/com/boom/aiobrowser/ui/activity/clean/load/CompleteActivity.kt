package com.boom.aiobrowser.ui.activity.clean.load

import android.content.Intent
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.CleanActivityCompleteBinding
import com.boom.aiobrowser.model.NewsViewModel
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.StoragePermissionManager
import com.boom.aiobrowser.tools.clean.formatSize
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.activity.clean.CleanScanActivity
import com.boom.aiobrowser.ui.activity.clean.ProcessActivity
import com.boom.aiobrowser.ui.adapter.NewsMainAdapter
import com.boom.base.adapter4.QuickAdapterHelper
import com.boom.base.adapter4.loadState.LoadState
import com.boom.base.adapter4.loadState.trailing.TrailingLoadStateAdapter
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import java.lang.ref.WeakReference

class CompleteActivity: BaseActivity<CleanActivityCompleteBinding>() {


    companion object {
        /**
         * fromType 0 clean  1 process
         */
        fun startCompleteActivity(activity: BaseActivity<*>,stopNum:Long,fromType:Int){
            activity.startActivity(Intent(activity,CompleteActivity::class.java).apply {
                putExtra("num",stopNum)
                putExtra("fromType",fromType)
            })
            activity.finish()
        }
    }

    private val viewModel by lazy {
        viewModels<NewsViewModel>()
    }

    override fun getBinding(inflater: LayoutInflater): CleanActivityCompleteBinding {
        return CleanActivityCompleteBinding.inflate(layoutInflater)
    }

    override fun setListener() {
        acBinding.ivBack.setOneClick {
            finish()
        }
        viewModel.value.newsLiveData.observe(this){
            if (page == 1){
                newsAdapter.submitList(it)
                acBinding.rv.scrollToPosition(0)
            }else{
                newsAdapter.addAll(it)
            }
            adapterHelper.trailingLoadState = LoadState.NotLoading(false)
            acBinding.refreshLayout.isRefreshing = false
        }
        viewModel.value.failLiveData.observe(this){
            adapterHelper.trailingLoadState = LoadState.NotLoading(false)
            acBinding.refreshLayout.isRefreshing = false
        }
    }

    var firstLoad = true

    var page = 1

    val newsAdapter by lazy {
        NewsMainAdapter()
    }

    val adapterHelper  by lazy {
        QuickAdapterHelper.Builder(newsAdapter)
            .setTrailingLoadStateAdapter(object :
                TrailingLoadStateAdapter.OnTrailingListener {
                override fun onLoad() {
                    AppLogs.dLog(acTAG,"加载更多")
                    page++
                    loadData()
                }

                override fun onFailRetry() {

                }

                override fun isAllowLoading(): Boolean {
                    return acBinding.refreshLayout.isRefreshing.not()
                }

            }).build()
    }


    fun loadData(){
        viewModel.value.getNewsData()
    }

    var fromType = 0
    var num = 0L

    override fun setShowView() {
        fromType = intent.getIntExtra("fromType",0)
        num = intent.getLongExtra("num",0L)
        acBinding.apply {
            if (fromType == 1){
                //process
                tvContent.text = getString(R.string.app_process_title,"${num}")

                ivContent.setImageResource(R.mipmap.ic_scan_clean)
                tvJumpTitle.text = getString(R.string.app_clean)
                tvJumpContent.text = getString(R.string.app_clean_content)
                rlToJump.setOneClick {
                    var permissionManager = StoragePermissionManager(WeakReference(this@CompleteActivity), 1,onGranted = {
                        jumpActivity<CleanScanActivity>()
                    }, onDenied = {
                    })
                    permissionManager.requestStoragePermission()
                }
            }else{
                if (num>0){
                    tvContent.text = getString(R.string.app_clean_title,num.formatSize())
                }else{
                    acBinding.tvContent.text = getString(R.string.app_clean_no_junk)
                }
                ivContent.setImageResource(R.mipmap.ic_scan_process)
                tvJumpTitle.text = getString(R.string.app_process)
                tvJumpContent.text = getString(R.string.app_process_content)
                rlToJump.setOneClick {
                    jumpActivity<ProcessLoadActivity>()
                }
            }

            adapterHelper.trailingLoadState = LoadState.NotLoading(false)
            rv.apply {
                layoutManager = LinearLayoutManager(this@CompleteActivity, LinearLayoutManager.VERTICAL,false)
                // 设置预加载，请调用以下方法
//                 helper.trailingLoadStateAdapter?.preloadSize = 1
                adapter = adapterHelper.adapter
                newsAdapter.setOnDebouncedItemClick{adapter, view, position ->
                    var data = newsAdapter.items.get(position)
                    var jumpData = JumpDataManager.getCurrentJumpData(tag="点击新闻item")
                    jumpData.apply {
                        jumpUrl= data.uweek?:""
                        jumpType = JumpConfig.JUMP_WEB
                        jumpTitle = data.tconsi?:""
                        isJumpClick = true
                    }
                    APP.jumpLiveData.postValue(jumpData)
                    finish()
                }
            }
            refreshLayout.setOnRefreshListener {
                page = 1
                adapterHelper.trailingLoadState = LoadState.None
                loadData()
            }
        }
        acBinding.refreshLayout.isRefreshing = true
        loadData()
    }
}