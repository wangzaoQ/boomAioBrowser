package com.boom.aiobrowser.ui.fragment

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.SizeUtils.dp2px
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.TopicBean
import com.boom.aiobrowser.databinding.NewsFragmentHomeBinding
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.ui.adapter.NewsPagerStateAdapter
import com.boom.indicator.ViewPagerHelper
import com.boom.indicator.buildins.commonnavigator.CommonNavigator
import com.boom.indicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import com.boom.indicator.buildins.commonnavigator.abs.IPagerIndicator
import com.boom.indicator.buildins.commonnavigator.abs.IPagerTitleView
import com.boom.indicator.buildins.commonnavigator.indicators.WrapPagerIndicator
import com.boom.indicator.buildins.commonnavigator.titles.SimplePagerTitleView
import com.google.android.material.tabs.TabLayout

class NewsHomeFragment : BaseFragment<NewsFragmentHomeBinding>() {


    var fragmentAdapter: NewsPagerStateAdapter? = null

    override fun startLoadData() {
    }

    override fun setListener() {
        var list = CacheManager.defaultTopicList
        if (list.isNullOrEmpty()) {
            APP.topicLiveData.observe(this) {
                updateNewsHome(it)
            }
        } else {
            updateNewsHome(list)
        }
    }

    private fun updateNewsHome(list: MutableList<TopicBean>) {
        fBinding.apply {
//            tabLayout.setBackgroundColor(Color.WHITE)
            val commonNavigator: CommonNavigator = CommonNavigator(rootActivity)
//            commonNavigator.setScrollPivotX(0.35f)
            commonNavigator.rightPadding = dp2px(9f)
            commonNavigator.leftPadding = dp2px(9f)
            commonNavigator.isIndicatorOnTop = true
            commonNavigator.setAdapter(object : CommonNavigatorAdapter() {
                override fun getCount(): Int {
                    return list.size
                }

                override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                    val simplePagerTitleView: SimplePagerTitleView = SimplePagerTitleView(context)
                    simplePagerTitleView.setText(list.get(index).topic)
                    simplePagerTitleView.setNormalColor(Color.BLACK)
                    simplePagerTitleView.setSelectedColor(Color.WHITE)
                    simplePagerTitleView.setSelectedBg(com.boom.indicator.R.drawable.shape_custom_tab_unable)
                    simplePagerTitleView.setUnSelectedBg(com.boom.indicator.R.drawable.shape_custom_tab_enable)
                    simplePagerTitleView.setOnClickListener(View.OnClickListener {
                        vp.setCurrentItem(
                            index
                        )
                    })
                    return simplePagerTitleView
                }
//
//                override fun getIndicator(context: Context?): IPagerIndicator {
//                    val indicator: WrapPagerIndicator = WrapPagerIndicator(context)
//                    indicator.setFillColor(R.color.tran)
//                    return indicator
//                }
                override fun getIndicator(context: Context?): IPagerIndicator? {
                    return null
                }
            })
            tabLayout.setNavigator(commonNavigator)

            vp.apply {
                fragmentAdapter = NewsPagerStateAdapter(
                    list,
                    childFragmentManager,
                    FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
                )
                adapter = fragmentAdapter
                addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {
                    }

                    override fun onPageSelected(position: Int) {

                    }

                    override fun onPageScrollStateChanged(state: Int) {
                    }
                })
            }
            ViewPagerHelper.bind(tabLayout, vp)

            vp.currentItem = 0
        }
    }

    override fun setShowView() {

    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NewsFragmentHomeBinding {
        return NewsFragmentHomeBinding.inflate(layoutInflater)
    }

    override fun onDestroy() {
        APP.topicLiveData.removeObservers(this)
        super.onDestroy()
    }
}