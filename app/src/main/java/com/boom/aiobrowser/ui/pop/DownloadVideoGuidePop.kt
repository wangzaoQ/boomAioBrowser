package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.SizeUtils.dp2px
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.BrowserPopDownloadVideoGuideBinding
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.other.JumpConfig
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


    fun createPop(callBack: () -> Unit){
        defaultBinding?.apply {
            vp.apply {
                setOrientation(ViewPager2.ORIENTATION_HORIZONTAL)
                var dataList = mutableListOf<Int>()
                dataList.add(0)
                dataList.add(1)
                dataList.add(2)
                dataList.add(3)
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
                        indicator.onPageSelected(position)
                        if (position == 3){
                            btnCommit.visibility = View.VISIBLE
                        }else{
                            btnCommit.visibility = View.GONE
                        }
                    }
                })
            }
            tvSkip.setOnClickListener {
                PointEvent.posePoint(PointEventKey.download_tutorial_skip, Bundle().apply {
                    putInt(PointValueKey.page,vp.currentItem)
                })
                PointEvent.posePoint(PointEventKey.home_page_first)
                dismiss()
            }
            btnCommit.setOnClickListener {
                PointEvent.posePoint(PointEventKey.download_tutorial_try)
                var data = JumpDataManager.getCurrentJumpData(tag = "DownloadVideoGuidePop guide")
                data.jumpType = JumpConfig.JUMP_WEB
                data.jumpUrl = "https://mixkit.co/free-stock-video/young-people-dancing-intensely-4606/"
                JumpDataManager.updateCurrentJumpData(data,tag = "DownloadVideoGuidePop guide")
                APP.jumpLiveData.postValue(data)
                JumpDataManager.closeAll()
                PointEvent.posePoint(PointEventKey.tutorial_webpage)
                PointEvent.posePoint(PointEventKey.home_page_first)
                dismiss()
            }
        }
        showPopupWindow()
        PointEvent.posePoint(PointEventKey.download_tutorial, Bundle().apply {
            putInt(PointValueKey.open_type,if (CacheManager.isVideoFirst) 0 else 1)
        })
        CacheManager.isVideoFirst = false
    }


}