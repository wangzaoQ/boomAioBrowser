package com.boom.aiobrowser.ui.activity

import android.view.KeyEvent
import android.view.LayoutInflater
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.SizeUtils.dp2px
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.BrowserActivityHomeGuideBinding
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.other.JumpConfig
import com.boom.aiobrowser.other.ParamsConfig
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
        var url = ""
        acBinding.ivJump.setOneClick {
            var title = getString(R.string.video_tiktok)
            when (fromApp) {
                getString(R.string.video_tiktok) -> {
                    url = "https://www.tiktok.com/"
                }
                getString(R.string.app_x)->{
                    url = "https://x.com/"
                }
                getString(R.string.app_instagram)->{
                    url = "https://www.instagram.com/"
                }
                else -> {}
            }
            if (url.isNotEmpty()){
                APP.jumpLiveData.postValue(JumpDataManager.getCurrentJumpData(tag = "HomeGuideActivity 点击跳转").apply {
                    jumpType = JumpConfig.JUMP_WEB
                    jumpTitle = title
                    jumpUrl = url
                })
                finish()
            }
        }

        acBinding.etGuide.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                var searchText = acBinding.etGuide.text.toString().trim()
                WebParseActivity.toWebParseActivity(this,0,searchText)
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    var fromApp = ""

    override fun setShowView() {
        fromApp = intent.getStringExtra(ParamsConfig.JUMP_FROM)?:""
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
        when (fromApp) {
            getString(R.string.video_tiktok) -> {
                guideList.add(0)
                guideList.add(1)
                acBinding.ivJump.setImageResource(R.mipmap.ic_tt)
            }
            getString(R.string.app_x)->{
                guideList.add(0)
                guideList.add(1)
                guideList.add(2)
                guideList.add(3)
                acBinding.ivJump.setImageResource(R.mipmap.ic_x)
            }
            getString(R.string.app_instagram)->{
                guideList.add(0)
                guideList.add(1)
                guideList.add(2)
                guideList.add(3)
                acBinding.ivJump.setImageResource(R.mipmap.ic_instagram)
            }
            else -> {}
        }

        var width = dp2px(7f).toFloat()
        acBinding.vpGuide.apply {
            setOrientation(ViewPager2.ORIENTATION_HORIZONTAL)
            adapter = homeGuideAdapter
            homeGuideAdapter.setFromAPP(fromApp)
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
        acBinding.ivRight.setOneClick {
            if (position == dataList.size-1)return@setOneClick
            acBinding.vpGuide.setCurrentItem(position+1,true)
        }
        acBinding.ivLeft.setOneClick {
            if (position == 0)return@setOneClick
            acBinding.vpGuide.setCurrentItem(position-1,true)
        }
    }


}