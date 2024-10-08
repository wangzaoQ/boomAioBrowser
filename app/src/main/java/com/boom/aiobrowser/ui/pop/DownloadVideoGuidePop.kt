package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.SizeUtils.dp2px
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.BrowserPopClearBinding
import com.boom.aiobrowser.databinding.BrowserPopDownloadVideoGuideBinding
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
                var index = vp.currentItem
                if (index == 3){
                    dismiss()
                }else{
                    vp.setCurrentItem(index+1,true)
                }
            }
            btnCommit.setOnClickListener {
                dismiss()
            }
        }
        showPopupWindow()
    }


}