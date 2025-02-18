package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.SizeUtils.dp2px
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADShowManager
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.BrowserPopDownloadVideoGuideBinding
import com.boom.aiobrowser.firebase.FirebaseConfig
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.other.JumpConfig
import com.boom.aiobrowser.point.AD_POINT
import com.boom.aiobrowser.ui.adapter.PopGuideAdapter
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle
import pop.basepopup.BasePopupWindow

class DownloadVideoGuidePop(context: Context) : BasePopupWindow(context) {

    init {
        setContentView(R.layout.browser_pop_download_video_guide)
    }

    var defaultBinding: BrowserPopDownloadVideoGuideBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = BrowserPopDownloadVideoGuideBinding.bind(contentView)
    }


    fun createPop(fromType:Int,callBack: () -> Unit){
        defaultBinding?.apply {
            vp.apply {
                offscreenPageLimit = 3
                setOrientation(ViewPager2.ORIENTATION_HORIZONTAL)
                var dataList = mutableListOf<Int>()
                dataList.add(0)
                dataList.add(1)
                dataList.add(2)
                adapter = PopGuideAdapter(dataList,context as BaseActivity<*>)
//                isUserInputEnabled = false
                var width = dp2px(7f).toFloat()
                indicator.apply {
                    setSliderColor(context.getColor(R.color.color_tab_n), context.getColor(R.color.color_tab_s))
                    setSliderWidth(width)
                    setSliderHeight(width)
                    setSlideMode(IndicatorSlideMode.SMOOTH)
                    setIndicatorStyle(IndicatorStyle.CIRCLE)
                    setPageSize(adapter!!.itemCount)
                    notifyDataChanged()
                }
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {
                        super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                        indicator.onPageScrolled(
                            position,
                            positionOffset,
                            positionOffsetPixels
                        )
                    }

                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        APP.videoGuideLiveData.postValue(position)
                        indicator.onPageSelected(position)
                        when (position) {
                            2 -> {
                                indicator.visibility = View.GONE
                                ivRight.visibility = View.GONE
                            }
                            else -> {
                                indicator.visibility = View.VISIBLE
                                ivRight.visibility = View.VISIBLE
                            }
                        }
                    }
                })
            }

            ivRight.setOnClickListener {
                if (vp.currentItem<2){
                    vp.currentItem += 1
                }
            }
            tvSkip.setOnClickListener {
                var manager = AioADShowManager(context as BaseActivity<*>, ADEnum.INT_AD, tag = "下载弹窗skip"){
                    PointEvent.posePoint(PointEventKey.download_tutorial_skip, Bundle().apply {
                        putInt(PointValueKey.page,vp.currentItem)
                    })
                    dismiss()
                }
                manager.showScreenAD(AD_POINT.aobws_downguide_int)
            }
        }
        showPopupWindow()
        PointEvent.posePoint(PointEventKey.download_tutorial, Bundle().apply {
            putInt(PointValueKey.open_type,if (CacheManager.isVideoFirst) 0 else 1)
        })
        CacheManager.isVideoFirst = false
        APP.videoGuideLiveData.observe(context as BaseActivity<*>){
            if (it == 10){
                dismiss()
            }else if (it == 11){
                defaultBinding?.vp?.apply {
                    currentItem -= 1
                }
            }
        }
    }

    override fun dismiss() {
        APP.videoGuideLiveData.removeObservers(context as BaseActivity<*>)
        super.dismiss()
    }
}