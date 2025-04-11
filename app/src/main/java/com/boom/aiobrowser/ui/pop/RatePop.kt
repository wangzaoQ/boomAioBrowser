package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.BuildConfig
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.BrowserPopRateBinding
import com.boom.aiobrowser.other.ShortManager
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.ui.adapter.RateAdapter
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import com.google.android.play.core.review.testing.FakeReviewManager
import pop.basepopup.BasePopupWindow

class RatePop(context: Context) : BasePopupWindow(context) {
    init {
        setContentView(R.layout.browser_pop_rate)
    }

    val rateAdapter by lazy {
        RateAdapter()
    }

    var defaultBinding: BrowserPopRateBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = BrowserPopRateBinding.bind(contentView)
    }

    var isClickFeedBack = false

    var allowShowAddTask = false

    var submit = false

    var tempList:MutableList<String>?=null

    fun createPop(allowShowAddTask:Boolean = false,downloadVideoIdList:MutableList<String> = mutableListOf()){
        tempList = downloadVideoIdList
        this.allowShowAddTask = allowShowAddTask
        CacheManager.dayFirstDownloadVideoSuccess = false
        APP.instance.showPopLevel = 4
        defaultBinding?.apply {
            rvRate.apply {
                layoutManager = GridLayoutManager(context,5)
                adapter = rateAdapter
            }
            rateAdapter.updateIndex(-1)
            var dataList = mutableListOf<Int>()
            for (i in 0 until 5){
                dataList.add(i)
            }
            rateAdapter.submitList(dataList)
            rateAdapter.setOnDebouncedItemClick{adapter, view, position ->
                rateAdapter.updateIndex(position)
                if (position == 4){
                    ivRate.setImageResource(R.mipmap.ic_rate5)
                    commit.text = context.getString(R.string.app_submit)
//                    tvRateTitle.text = context.getString(R.string.app_rate_title)
//                    tvRateContent.text = context.getString(R.string.app_rate_content)
                }else{
                    ivRate.setImageResource(R.mipmap.ic_rate_other)
                    commit.text = context.getString(R.string.app_feedback)
//                    tvRateTitle.text = context.getString(R.string.app_rate_title)
//                    tvRateContent.text = context.getString(R.string.app_rate_content)
                }
                commit.setBackgroundResource(R.drawable.shape_commit)
            }
            commit.setOnClickListener {
                if (rateAdapter.index == -1)return@setOnClickListener
                submit = true
                CacheManager.isRate5 = true
                PointEvent.posePoint(PointEventKey.rate_us_submit, Bundle().apply {
                    putInt(PointValueKey.type,rateAdapter.index+1)
                })
                if (rateAdapter.index == 4){
                    showGoogleStar()
                }else{
                    isClickFeedBack = true
                    ToastUtils.showLong(context.getString(R.string.app_rate_feedback))
                    dismiss()
                }
            }
        }
        setBackground(R.color.color_70_black)
        showPopupWindow()
        PointEvent.posePoint(PointEventKey.rate_us_pop)
    }


    fun showGoogleStar() {
        val manager = if (APP.isDebug){
            FakeReviewManager(context as BaseActivity<*>)
        }else{
            ReviewManagerFactory.create(context as BaseActivity<*>)
        }
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            AppLogs.dLog(ShortManager.TAG,"showGoogleStar:${task.isSuccessful}")
            if (task.isSuccessful) {
                val reviewInfo = task.result
                if (context == null){
                    dismiss()
                }else{
                    val flow = manager.launchReviewFlow(context as BaseActivity<*>, reviewInfo)
                    flow.addOnCompleteListener { _ ->
                        dismiss()
                    }
                }
            } else {// 如果应用内拉评分弹框失败，就去商店
                runCatching {
                    @ReviewErrorCode
                    val reviewErrorCode = (task.exception as ReviewException).errorCode
                    AppLogs.dLog(ShortManager.TAG,"reviewErrorCode:${reviewErrorCode}")
                }
                toAppShop()
            }
        }
        PointEvent.posePoint(PointEventKey.rate_send_feedback)
    }

    fun toAppShop(){
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
            setPackage("com.android.vending")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        };
        runCatching {
            context.startActivity(intent)
        }.onFailure {
            it.printStackTrace()
        }
        dismiss()
    }


    override fun onDismiss() {
        APP.instance.showPopLevel = 0
        if (allowShowAddTask){
            TaskAddPop(context).createPop(tempList?: mutableListOf())
        }
        if (submit.not()){
            PointEvent.posePoint(PointEventKey.rate_us_pop_close)
        }
        super.onDismiss()
    }
}