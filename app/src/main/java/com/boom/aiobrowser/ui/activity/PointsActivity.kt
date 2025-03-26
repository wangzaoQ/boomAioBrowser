package com.boom.aiobrowser.ui.activity

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADShowManager
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.PointsData
import com.boom.aiobrowser.databinding.BrowserActivityPointsBinding
import com.boom.aiobrowser.model.NewsViewModel
import com.boom.aiobrowser.other.ParamsConfig
import com.boom.aiobrowser.point.AD_POINT
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
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
                var manager = AioADShowManager(this@PointsActivity, ADEnum.INT_AD, tag = "积分页面") {
                    finish()
                }
                manager.showScreenAD(AD_POINT.aobws_return_int)
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
                        PointEvent.posePoint(PointEventKey.daily_sign_in,Bundle().apply {
                            putInt(PointValueKey.sign_day,it.checkInCount)
                        })
                    }, failBack = {},Dispatchers.Main)
                }
            }
            tvVip1Exchange.setOneClick {
                exchange(0)
            }
            llVip2Exchange.setOneClick {
                exchange(1)
            }
            llVip3Exchange.setOneClick {
                exchange(2)
            }
            llReadNews.setOneClick {
                PointEvent.posePoint(PointEventKey.points_news)
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
                        loadData()
                    }
                }
            }
            llDownload.setOneClick {
                PointEvent.posePoint(PointEventKey.points_dl)
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
                                    "daily_video"
                                )
                            }, failBack = {},Dispatchers.Main)
                        }
                    }else{
                        ToastUtils.showShort(getString(R.string.app_success_receive))
                        updateAllPoints()
                        loadData()
                    }
                }
            }
            llWatchVideo.setOneClick {
                PointEvent.posePoint(PointEventKey.points_watch)
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
                                    "daily_video"
                                )
                            }, failBack = {},Dispatchers.Main)
                        }
                    }else{
                        ToastUtils.showShort(getString(R.string.app_success_receive))
                        updateAllPoints()
                        loadData()
                    }
                }
            }
            llDailyLogin.setOneClick {
                PointEvent.posePoint(PointEventKey.daily_login)
                if (CacheManager.pointsData.isDailyLogin){
                    ToastUtils.showShort(getString(R.string.app_receive_points_already))
                }else{
                    PointsManager.login{
                        if (it == 0){
                            ToastUtils.showShort(getString(R.string.app_success_receive))
                            updateAllPoints()
                            loadData()
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
                    PointEvent.posePoint(PointEventKey.points_ad_pop,Bundle().apply {
                        putString(PointValueKey.type,if (type == 1) "2h" else "30min")
                    })
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
                tvSign.text = this@PointsActivity.getString(R.string.app_receive)
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
                        llSign7.setOneClick {
                            ToastUtils.showShort(getString(R.string.app_sign_error))
                        }
                    }else{
                        ivSign7.setImageResource(R.mipmap.ic_check3)
                        llSign7.setBackgroundResource(R.drawable.shape_check_in_check)
                        tvSign7.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.red_FF1803))
                        tvSign7Time.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.red_FF1803))
                        llSign7.setOneClick {
                            tvSign.performClick()
                        }
                    }
                }else{
                    ivSign7.setImageResource(R.mipmap.ic_check3)
                    llSign7.setBackgroundResource(R.drawable.shape_check_in_uncheck)
                    tvSign7.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.color_black_4C4C4C))
                    tvSign7Time.setTextColor(ContextCompat.getColor(this@PointsActivity,R.color.color_black_4C4C4C))
                    llSign7.setOneClick {
                        ToastUtils.showShort(getString(R.string.app_sign_error))
                    }
                }
            }
        }
    }

    override fun setShowView() {
        var content = "${getString(R.string.app_points_ad_free_for)} ${getString(R.string.app_points_2_hours)}"
        var content2 = "${getString(R.string.app_points_ad_free_for)} ${getString(R.string.app_points_30_m)}"
        acBinding.tvExchange1Content.setText(updateTextColor(content,getString(R.string.app_points_ad_free_for).length,content.length))
        acBinding.tvExchange2Content.setText(updateTextColor(content2,getString(R.string.app_points_ad_free_for).length,content2.length))
        var from_type = intent.getStringExtra(PointValueKey.from_type)
        if (from_type.isNullOrEmpty()){
            from_type = "other"
        }
        PointEvent.posePoint(PointEventKey.points_act,Bundle().apply {
            putString(PointValueKey.from_type,from_type)
        })
    }

    var first = true

    private fun loadData() {
        appDataReset()
        var data = CacheManager.pointsData
        acBinding.apply {
            tvPoints.text = "${data.allPoints}"
            tvSignDay.text = "${data.checkInCount}"
            var dailyContent = "${getString(R.string.app_daily_login)} +${PointsManager.DAILY_LOGIN_POINTS}"
            tvDailyLoginPoints.setText(updateTextColor(dailyContent,getString(R.string.app_daily_login).length+1,dailyContent.length))
            var newsCount = data.newReadCount()
            if (newsCount == 0){
                var newsStart = String.format(getString(R.string.app_points_read_news),1)
                var newContent = "${newsStart} +${1*PointsManager.READ_NEWS_POINTS}"
                tvReadNewsPoints.setText(updateTextColor(newContent,newsStart.length+1,newContent.length))
            }else{
                var newsStart = String.format(getString(R.string.app_points_read_news),newsCount)
                var newContent = "${newsStart} +${newsCount*PointsManager.READ_NEWS_POINTS}"
                tvReadNewsPoints.setText(updateTextColor(newContent,newsStart.length+1,newContent.length))
            }
            var downloadCount = data.downVideoCount()
            if (downloadCount == 0){
                var downloadStart = String.format(getString(R.string.app_points_completed_download),1)
                var downloadContent = "${downloadStart} +${1*PointsManager.DOWNLOAD_VIDEO_POINTS}"
                tvDownloadVideoPoints.setText(updateTextColor(downloadContent,downloadStart.length+1,downloadContent.length))
            }else{
                var downloadStart = String.format(getString(R.string.app_points_completed_download),downloadCount)
                var downloadContent = "${downloadStart} +${downloadCount*PointsManager.DOWNLOAD_VIDEO_POINTS}"
                tvDownloadVideoPoints.setText(updateTextColor(downloadContent,downloadStart.length+1,downloadContent.length))
            }
            var showVideoCount = data.showVideoCount()
            if (showVideoCount == 0){
                var showVideoStart = String.format(getString(R.string.app_points_watch_video),1)
                var showVideoContent = "${showVideoStart} +${1*PointsManager.SHOW_VIDEO_POINTS}"
                tvShowVideoPoints.setText(updateTextColor(showVideoContent,showVideoStart.length+1,showVideoContent.length))
            }else{
                var showVideoStart = String.format(getString(R.string.app_points_watch_video),showVideoCount)
                var showVideoContent = "${showVideoStart} +${showVideoCount*PointsManager.SHOW_VIDEO_POINTS}"
                tvShowVideoPoints.setText(updateTextColor(showVideoContent,showVideoStart.length+1,showVideoContent.length))
            }

            updateDailyPoints(tvDailyLogin,data.isDailyLogin.not())
            updateDailyPoints(tvReadNews,data.newsPoints()>0)
            updateDailyPoints(tvDownloadVideo,data.downloadVideoPoints()>0)
            updateDailyPoints(tvShowVideo,data.showVideoPoints()>0)
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
                        signRejoinPop?.dismiss()
                        signRejoinPop = SignRejoinPop(this@PointsActivity){
                            acBinding.tvSign.performClick()
                        }
                        signRejoinPop?.createPop()
                    }
                }
            }, failBack = {})
        }
    }

    var signRejoinPop:SignRejoinPop?=null

    override fun onBackPressed() {
        acBinding.ivBack.performClick()
    }

    private fun updateDailyPoints(
        textView: AppCompatTextView,
        enable:Boolean
    ) {
        if (enable){
            textView.text = getString(R.string.app_claim)
            textView.setBackgroundResource(R.drawable.shape_84r_5755d9)
            textView.setTextColor(ContextCompat.getColor(this,R.color.white))
        }else{
            textView.text = getString(R.string.app_receive)
            textView.setBackgroundResource(R.drawable.shape_points_receive)
            textView.setTextColor(ContextCompat.getColor(this,R.color.color_blue_B5B5EA))
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