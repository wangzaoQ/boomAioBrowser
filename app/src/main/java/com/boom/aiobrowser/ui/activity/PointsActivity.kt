package com.boom.aiobrowser.ui.activity

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.PointsData
import com.boom.aiobrowser.databinding.BrowserActivityPointsBinding
import com.boom.aiobrowser.model.NewsViewModel
import com.boom.aiobrowser.other.ParamsConfig
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.PointsManager
import com.boom.aiobrowser.tools.appDataReset
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.pop.SignRejoinPop
import com.boom.aiobrowser.ui.pop.TempNoADPop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class PointsActivity: BaseActivity<BrowserActivityPointsBinding>() {
    override fun getBinding(inflater: LayoutInflater): BrowserActivityPointsBinding {
        return BrowserActivityPointsBinding.inflate(layoutInflater)
    }


    private val viewModel by lazy {
        viewModels<NewsViewModel>()
    }

    override fun setListener() {
        acBinding.apply {
            ivBack.setOneClick {
                finish()
            }
            tvSign.setOneClick {
                appDataReset()
                showPop()
                PointsManager.signPoints{
                    addLaunch(success = {
                        hidePop()
                        if (it == null){
                            ToastUtils.showShort(APP.instance.getString(R.string.sign_in_error))
                            return@addLaunch
                        }
                        tvPoints.text = "${it.allPoints}"
                        updateSignUI(it)
                    }, failBack = {},Dispatchers.Main)
                }
            }
            tvVip1Exchange.setOneClick {
                exchange(0)
            }
            tvVip2Exchange.setOneClick {
                exchange(1)
            }
            tvVip3Exchange.setOneClick {
                exchange(2)
            }
            tvReadNews.setOneClick {
                PointsManager.receiveReadNewsPoints{
                    if (it == 0){
                        var startTime = System.currentTimeMillis()
                        showPop()
                        addLaunch(success = {
                            viewModel.value.getPointsNews{
                                AppLogs.dLog(PointsManager.TAG,"跳转随机未阅读的新闻 data.title:${it.tconsi}")
                                addLaunch(success = {
                                    var middleTime = System.currentTimeMillis() - startTime
                                    if (middleTime<1000){
                                        delay(1000-middleTime)
                                    }
                                    withContext(Dispatchers.Main){
                                        hidePop()
                                        jumpActivity<WebDetailsActivity>(Bundle().apply {
                                            putString(ParamsConfig.JSON_PARAMS, toJson(it))
                                        })
                                    }
                                }, failBack = {})
                            }
                        }, failBack = {})

                    }else{
                        ToastUtils.showShort(getString(R.string.app_success_receive))
                        updateAllPoints()
                        updateDailyPoints(tvReadNews,getString(R.string.app_receive),CacheManager.pointsData.newsPoints())
                    }
                }
            }
            tvDownloadVideo.setOneClick {
                PointsManager.receiveDownloadVideoPoints{
                    if (it == 0){
                        viewModel.value.getPointsDownloadVideo {
                            if (it.isNullOrEmpty())return@getPointsDownloadVideo
                            addLaunch(success = {
                                VideoListActivity.startVideoListActivity(
                                    this@PointsActivity,
                                    0,
                                    it,
                                    "",
                                    "points_download"
                                )
                            }, failBack = {},Dispatchers.Main)
                        }
                    }else{
                        ToastUtils.showShort(getString(R.string.app_success_receive))
                        updateAllPoints()
                        updateDailyPoints(tvDownloadVideo,getString(R.string.app_receive),CacheManager.pointsData.downloadVideoPoints())
                    }
                }
            }
            tvShowVideo.setOneClick {
                PointsManager.receiveShowVideoPoints{
                    if (it == 0){
                        viewModel.value.getPointsShowVideo {
                            if (it.isNullOrEmpty())return@getPointsShowVideo
                            addLaunch(success = {
                                VideoListActivity.startVideoListActivity(
                                    this@PointsActivity,
                                    0,
                                    it,
                                    "",
                                    "points_show"
                                )
                            }, failBack = {},Dispatchers.Main)
                        }
                    }else{
                        ToastUtils.showShort(getString(R.string.app_success_receive))
                        updateAllPoints()
                        updateDailyPoints(tvShowVideo,getString(R.string.app_receive),CacheManager.pointsData.showVideoPoints())
                    }
                }
            }
            tvDailyLogin.setOneClick {
                if (CacheManager.pointsData.isDailyLogin){
                    ToastUtils.showShort(getString(R.string.app_receive_points_already))
                }else{
                    PointsManager.login{
                        if (it == 0){
                            ToastUtils.showShort(getString(R.string.app_success_receive))
                            updateAllPoints()
                            updateDailyQuests(tvDailyLogin)
                        }
                    }
                }
            }
        }
    }

    private fun updateAllPoints() {
        acBinding.tvPoints.text = "${CacheManager.pointsData.allPoints}"
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun exchange(type: Int) {
        showPop()
        PointsManager.addTempVip(type){
            addLaunch(success = {
                if (it == -1){
                    hidePop()
                    ToastUtils.showShort(getString(R.string.app_exchange_error))
                    return@addLaunch
                }
                if (it == -2){
                    hidePop()
                    ToastUtils.showShort(getString(R.string.app_exchange_refuse))
                    return@addLaunch
                }
                hidePop()
                updateAllPoints()
                if (type == 1 || type == 2){
                    TempNoADPop(this@PointsActivity){}.createPop()
                }
            }, failBack = {},Dispatchers.Main)
        }
    }

    private fun updateSignUI(pointsData: PointsData) {
        acBinding.apply {
            tvSign7Time.text = "${this@PointsActivity.getString(R.string.app_day)} ${7}"
            var signInCount = pointsData.checkInCount
            if (pointsData.todaySignIn){
                signInCount+=1
                tvSign.isEnabled = false
                tvSign.text = this@PointsActivity.getString(R.string.app_completed)
            }else{
                tvSign.isEnabled = true
                tvSign.text = this@PointsActivity.getString(R.string.sign_in_now)
            }

            tvSignDay.text = "${signInCount}"
            ((llSignRoot2.get(0)as LinearLayoutCompat).getChildAt(2) as AppCompatTextView).text = "${APP.instance.getString(R.string.app_day)} 5"
            ((llSignRoot2.get(1)as LinearLayoutCompat).getChildAt(2) as AppCompatTextView).text = "${APP.instance.getString(R.string.app_day)} 6"
            for (i in 0 until llSignRoot1.childCount){
                var childRoot = (llSignRoot1.getChildAt(i) as LinearLayoutCompat)
                if (pointsData.checkInCount>i){
                    childRoot.setBackgroundResource(R.drawable.shape_check_in_uncheck)
                }else{
                    childRoot.setBackgroundResource(R.drawable.shape_check_in_uncheck)
                }
                var textView = childRoot.get(0) as AppCompatTextView
                var imageView = childRoot.get(1) as AppCompatImageView
                var textView2 = childRoot.get(2) as AppCompatTextView
                textView2.text = "${this@PointsActivity.getString(R.string.app_day)} ${i+1}"
                if (pointsData.checkInCount == i){
                    //当天
                    if (pointsData.todaySignIn){
                        imageView.setImageResource(R.mipmap.ic_check0)
                        textView.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.color_black_C2C2C2))
                        textView2.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.color_black_C2C2C2))
                        childRoot.setBackgroundResource(R.drawable.shape_check_in_uncheck)
                    }else{
                        imageView.setImageResource(R.mipmap.ic_check2)
                        textView.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.red_FF1803))
                        textView2.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.red_FF1803))
                        childRoot.setBackgroundResource(R.drawable.shape_check_in_check)
                        childRoot.setOneClick {
                            tvSign.performClick()
                        }
                    }
                }else if (pointsData.checkInCount>i){
                    //已签到过
                    if (i == 2){
                        imageView.setImageResource(R.mipmap.ic_check4)
                    }else{
                        imageView.setImageResource(R.mipmap.ic_check0)
                    }
                    textView.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.color_black_C2C2C2))
                    textView2.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.color_black_C2C2C2))
                    childRoot.setOneClick {
                        ToastUtils.showShort(getString(R.string.app_sign_complete))
                    }
                }else{
                    childRoot.setOneClick {
                        ToastUtils.showShort(getString(R.string.app_sign_error))
                    }
                    //未签到
                    if (i == 2){
                        imageView.setImageResource(R.mipmap.ic_check3)
                    }else{
                        imageView.setImageResource(R.mipmap.ic_check2)
                    }
                    textView.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.color_black_4C4C4C))
                    textView2.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.color_black_4C4C4C))
                }
            }
            if (pointsData.checkInCount>3){
                if (pointsData.checkInCount == 6){
                    llSign7.setBackgroundResource(R.drawable.shape_check_in_check)
                    tvSign7.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.red_FF1803))
                    tvSign7Time.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.red_FF1803))
                }else{
                    llSign7.setBackgroundResource(R.drawable.shape_check_in_uncheck)
                    tvSign7.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.color_black_4C4C4C))
                    tvSign7Time.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.color_black_4C4C4C))
                }
                var checkInCount = pointsData.checkInCount-4
                for (i in 0 until llSignRoot2.childCount){
                    var childRoot = llSignRoot2.getChildAt(i) as LinearLayoutCompat
                    if (checkInCount == i){
                        if (pointsData.todaySignIn){
                            childRoot.setBackgroundResource(R.drawable.shape_check_in_uncheck)
                        }else{
                            childRoot.setBackgroundResource(R.drawable.shape_check_in_check)
                        }
                    }else if (checkInCount>i){
                        childRoot.setBackgroundResource(R.drawable.shape_check_in_uncheck)
                    }else{
                        childRoot.setBackgroundResource(R.drawable.shape_check_in_uncheck)
                    }
                    var textView = childRoot.get(0) as AppCompatTextView
                    var imageView = childRoot.get(1) as AppCompatImageView
                    var textView2 = childRoot.get(2) as AppCompatTextView
                    textView2.text = "${this@PointsActivity.getString(R.string.app_day)} ${i+5}"
                    if (checkInCount == i){
                        if (pointsData.todaySignIn){
                            imageView.setImageResource(R.mipmap.ic_check0)
                            childRoot.setOneClick {
                                ToastUtils.showShort(getString(R.string.app_sign_error))
                            }
                            textView.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.color_black_C2C2C2))
                            textView2.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.color_black_C2C2C2))
                        }else{
                            imageView.setImageResource(R.mipmap.ic_check2)
                            childRoot.setOneClick {
                                tvSign.performClick()
                            }
                            textView.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.red_FF1803))
                            textView2.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.red_FF1803))
                        }
                    }else if (checkInCount>i){
                        imageView.setImageResource(R.mipmap.ic_check0)
                        textView.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.color_black_C2C2C2))
                        textView2.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.color_black_C2C2C2))
                        childRoot.setOneClick {
                            ToastUtils.showShort(getString(R.string.app_sign_error))
                        }
                    }else{
                        imageView.setImageResource(R.mipmap.ic_check2)
                        textView.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.color_black_4C4C4C))
                        textView2.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.color_black_4C4C4C))
                        childRoot.setOneClick {
                            ToastUtils.showShort(getString(R.string.app_sign_error))
                        }
                    }
                }
                if (pointsData.checkInCount == 6){
                    if (pointsData.todaySignIn){
                        llSign7.setBackgroundResource(R.drawable.shape_check_in_uncheck)
                        ivSign7.setImageResource(R.mipmap.ic_check4)
                        tvSign7.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.color_black_C2C2C2))
                        tvSign7Time.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.color_black_C2C2C2))
                    }else{
                        ivSign7.setImageResource(R.mipmap.ic_check3)
                        llSign7.setBackgroundResource(R.drawable.shape_check_in_check)
                        tvSign7.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.color_black_4C4C4C))
                        tvSign7Time.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.color_black_4C4C4C))
                    }
                }else{
                    ivSign7.setImageResource(R.mipmap.ic_check3)
                    llSign7.setBackgroundResource(R.drawable.shape_check_in_uncheck)
                    tvSign7.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.color_black_4C4C4C))
                    tvSign7Time.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.color_black_4C4C4C))
                }
            }
        }
    }

    override fun setShowView() {
        var content = "${getString(R.string.app_points_ad_free_for)} ${getString(R.string.app_points_2_hours)}"
        var content2 = "${getString(R.string.app_points_ad_free_for)} ${getString(R.string.app_points_30_m)}"
        acBinding.tvExchange1Content.setText(updateTextColor(content,getString(R.string.app_points_ad_free_for).length,content.length))
        acBinding.tvExchange2Content.setText(updateTextColor(content2,getString(R.string.app_points_ad_free_for).length,content2.length))
    }

    var first = true

    private fun loadData() {
        appDataReset()
        var data = CacheManager.pointsData
        acBinding.apply {
            tvPoints.text = "${data.allPoints}"
            tvSignDay.text = "${data.checkInCount}"

            updateDailyPoints(tvDailyLoginPoints,getString(R.string.app_daily_login),PointsManager.DAILY_LOGIN_POINTS)
            updateDailyPoints(tvReadNewsPoints,getString(R.string.app_points_read_news),PointsManager.READ_NEWS_POINTS)
            updateDailyPoints(tvDownloadVideoPoints,getString(R.string.app_points_completed_download),PointsManager.DOWNLOAD_VIDEO_POINTS)
            updateDailyPoints(tvShowVideoPoints,getString(R.string.app_points_watch_video),PointsManager.SHOW_VIDEO_POINTS)

            updateDailyQuests(acBinding.tvDailyLogin)
            updateDailyPoints(tvReadNews,getString(R.string.app_receive),data.newsPoints())
            updateDailyPoints(tvDownloadVideo,getString(R.string.app_receive),data.downloadVideoPoints())
            updateDailyPoints(tvShowVideo,getString(R.string.app_receive),data.showVideoPoints())
            updateSignUI(data)
        }
        CacheManager.pointsData = data
        PointsManager.inspectSignIn{
            addLaunch(success = {
                withContext(Dispatchers.Main){
                    if (it == -1){
                        ToastUtils.showShort(R.string.app_net_error)
                    }else if (it == 1){
                        updateSignUI(CacheManager.pointsData)
                        SignRejoinPop(this@PointsActivity){
                            acBinding.tvSign.performClick()
                        }.createPop()
                    }
                }
            }, failBack = {})
        }
    }

    private fun updateDailyPoints(
        textView: AppCompatTextView,
        content: String,
        points: Int
    ) {
        textView.text = "${content}  +${points}"
        textView.setText(updateTextColor(textView.text.toString(),content.length+2,textView.text.length))
    }

    private fun updateDailyQuests(textView: AppCompatTextView) {
        var pointsData = CacheManager.pointsData
        if (pointsData.isDailyLogin.not()){
            textView.text = getString(R.string.app_receive)
            textView.setBackgroundResource(R.drawable.shape_points_receive)
            updateDailyPoints(textView,getString(R.string.app_receive),pointsData.dailyLoginPoints())
        }else{
            textView.text = getString(R.string.app_done)
            textView.setBackgroundResource(R.drawable.shape_points_done)
        }
    }

    fun updateTextColor(content:String,startIndex:Int,endIndex:Int):SpannableStringBuilder{
        var s = SpannableStringBuilder(content)
        s.setSpan(
            ForegroundColorSpan(getColor(R.color.color_red_FF4000)),
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return s
    }
}