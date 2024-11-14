package com.boom.aiobrowser.ui.activity

import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.HistoryData
import com.boom.aiobrowser.databinding.BrowserActivityHistoryBinding
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.TimeManager
import com.boom.aiobrowser.ui.adapter.HistoryAdapter
import com.boom.base.adapter4.util.setOnDebouncedItemClick

class HistoryActivity: BaseActivity<BrowserActivityHistoryBinding>() {
    override fun getBinding(inflater: LayoutInflater): BrowserActivityHistoryBinding {
        return BrowserActivityHistoryBinding.inflate(layoutInflater)
    }

    override fun setListener() {
        acBinding.ivBack.setOneClick {
            finish()
        }
        acBinding.ivDelete.setOneClick {
            CacheManager.recentSearchDataList = mutableListOf()
            historyAdapter.submitList(mutableListOf())
        }
    }

    val historyList by lazy {
        mutableListOf<HistoryData>()
    }

    val historyAdapter by lazy {
        HistoryAdapter()
    }

    override fun setShowView() {
        acBinding.apply {
            rv.layoutManager = LinearLayoutManager(this@HistoryActivity,LinearLayoutManager.VERTICAL,false)
            rv.adapter = historyAdapter
        }
        var list = CacheManager.recentSearchDataList
        var lastTime = ""
        for (i in 0 until list.size){
            var data = list.get(i)
            var day = TimeManager.getHistoryDay(data.updateTime)
            if (day!=lastTime){
                historyList.add(HistoryData().apply {
                    title = day
                    type = HistoryData.HISTORY_TITLE
                })
                lastTime = day
            }
            historyList.add(HistoryData().apply {
                jumpData = data
            })
        }
        historyAdapter.setOnDebouncedItemClick{adapter, view, position ->
            var data = historyAdapter.items.get(position)
            if (data.type == HistoryData.HISTORY_TITLE)return@setOnDebouncedItemClick
            var jumpData = JumpDataManager.getCurrentJumpData(tag="历史点击", updateData = data.jumpData)
            APP.jumpLiveData.postValue(jumpData)
            finish()
        }
        historyAdapter.submitList(historyList)
    }
}