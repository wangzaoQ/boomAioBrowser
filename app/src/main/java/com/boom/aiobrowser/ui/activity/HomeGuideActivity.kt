package com.boom.aiobrowser.ui.activity

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.SizeUtils.dp2px
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.BrowserActivityHomeGuideBinding
import com.boom.aiobrowser.databinding.VideoActivityDownloadBinding
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.adapter.HomeGuideAdapter
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle

class HomeGuideActivity : BaseActivity<BrowserActivityHomeGuideBinding>() {
    override fun getBinding(inflater: LayoutInflater): BrowserActivityHomeGuideBinding {
        return BrowserActivityHomeGuideBinding.inflate(layoutInflater)
    }

    override fun setListener() {
        acBinding.ivBack.setOneClick {
            finish()
        }
        acBinding.ivJump.setOneClick {
            var title = getString(R.string.app_tt)
            var url = "https://www.tiktok.com/"
            APP.jumpLiveData.postValue(JumpDataManager.getCurrentJumpData(tag = "HomeGuideActivity 点击跳转").apply {
                jumpType = JumpConfig.JUMP_WEB
                jumpTitle = title
                jumpUrl = url
            })
            finish()
        }

        acBinding.etGuide.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                var searchText = acBinding.etGuide.text.toString().trim()
                jumpActivity<WebParseActivity>(Bundle().apply {
                    putString("url",searchText)
                })
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    override fun setShowView() {
        showGuideRoot()
    }


    val homeGuideAdapter by lazy {
        HomeGuideAdapter()
    }

    val guideList by lazy {
        mutableListOf<Int>()
    }

    private fun showGuideRoot() {
        guideList.clear()
        guideList.add(0)
        guideList.add(1)
        var width = dp2px(7f).toFloat()
        acBinding.vpGuide.apply {
            setOrientation(ViewPager2.ORIENTATION_HORIZONTAL)
            adapter = homeGuideAdapter
        }
        acBinding.indicator.apply {
            setSliderColor(
                context.getColor(R.color.color_tab_DAE5EC),
                context.getColor(R.color.color_tab_5755D9)
            )
            setSliderWidth(width)
            setSliderHeight(width)
            setSlideMode(IndicatorSlideMode.SMOOTH)
            setIndicatorStyle(IndicatorStyle.CIRCLE)
            setPageSize(guideList.size)
            notifyDataChanged()
            acBinding.vpGuide.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                    acBinding.indicator.onPageScrolled(
                        position,
                        positionOffset,
                        positionOffsetPixels
                    )
                }

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    acBinding.indicator.onPageSelected(position)
                    setGuideIvByPosition(position, guideList)
                }
            })
        }
        homeGuideAdapter.submitList(guideList)
    }

    private fun setGuideIvByPosition(position: Int, dataList: MutableList<Int>) {
        acBinding.ivRight.setImageResource(if (position == dataList.size - 1) R.mipmap.ic_guide_right1 else R.mipmap.ic_guide_right2)
        acBinding.ivLeft.setImageResource(if (position == 0) R.mipmap.ic_guide_left1 else R.mipmap.ic_guide_left2)
    }


}