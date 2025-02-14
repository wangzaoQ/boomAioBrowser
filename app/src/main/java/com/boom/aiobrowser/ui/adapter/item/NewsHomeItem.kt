package com.boom.aiobrowser.ui.adapter.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.SizeUtils.dp2px
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.base.BaseViewHolder
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.NewsItemHomeTopBinding
import com.boom.aiobrowser.other.JumpConfig
import com.boom.aiobrowser.other.SearchConfig
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.partitionList
import com.boom.aiobrowser.ui.adapter.HomeTabAdapter
import com.bumptech.glide.Glide
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle

internal class NewsHomeItem(parent: ViewGroup) : BaseViewHolder<NewsItemHomeTopBinding>(
    NewsItemHomeTopBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) {

    fun bind(item: NewsData, fragment: BaseFragment<*>?) {
        viewBinding?.apply {
            var tabList = CacheManager.homeTabList
            tabList.add(JumpData().apply {
                jumpType = JumpConfig.JUMP_WEB_TYPE
                jumpUrl = ""
                jumpTitle = ""
            })
            // 将集合按每 8 个分割
            val partitionSize = 8
            val dataList: MutableList<MutableList<JumpData>> = partitionList(
                tabList,
                partitionSize
            ) as MutableList<MutableList<JumpData>>
            if (true) {
                vp2.apply {
                    offscreenPageLimit = dataList.size
                    var homeTabAdapter: HomeTabAdapter? = null
                    if (adapter == null) {
                        homeTabAdapter = HomeTabAdapter(context as BaseActivity<*>)
                        adapter = homeTabAdapter
                    }
                    (adapter as HomeTabAdapter)?.update(dataList)
                }
                if (dataList.size > 1) {
                    indicator.visibility = View.VISIBLE
                    var width = dp2px(7f).toFloat()
                    indicator.apply {
                        setSliderColor(
                            context.getColor(R.color.color_tab_DAE5EC),
                            context.getColor(R.color.color_tab_5755D9)
                        )
                        setSliderWidth(width)
                        setSliderHeight(width)
                        setSlideMode(IndicatorSlideMode.SMOOTH)
                        setIndicatorStyle(IndicatorStyle.CIRCLE)
                        setPageSize(dataList.size)
                        notifyDataChanged()
                        vp2.registerOnPageChangeCallback(object :
                            ViewPager2.OnPageChangeCallback() {
                            override fun onPageScrolled(
                                position: Int,
                                positionOffset: Float,
                                positionOffsetPixels: Int
                            ) {
                                super.onPageScrolled(
                                    position,
                                    positionOffset,
                                    positionOffsetPixels
                                )
                                indicator.onPageScrolled(
                                    position,
                                    positionOffset,
                                    positionOffsetPixels
                                )
                            }

                            override fun onPageSelected(position: Int) {
                                super.onPageSelected(position)
                                indicator.onPageSelected(position)
                            }
                        })
                        rlHistory.setPadding(0, dp2px(11f), 0, dp2px(16f))
                    }

                } else {
                    indicator.visibility = View.GONE
                    rlHistory.setPadding(0, dp2px(11f), 0, dp2px(20f))
                }
                vp2.setTag(R.id.vp2, dataList)
            }
            if (CacheManager.browserStatus == 0) {
                ivPrivate.visibility = View.GONE
            } else {
                ivPrivate.visibility = View.VISIBLE
            }
            when (CacheManager.engineType) {
                SearchConfig.SEARCH_ENGINE_GOOGLE -> {
                    ivSearchEngine.setImageResource(R.mipmap.ic_search_gg)
                }

                SearchConfig.SEARCH_ENGINE_BING -> {
                    ivSearchEngine.setImageResource(R.mipmap.ic_search_bing)
                }

                SearchConfig.SEARCH_ENGINE_YAHOO -> {
                    ivSearchEngine.setImageResource(R.mipmap.ic_search_yahoo)
                }

                SearchConfig.SEARCH_ENGINE_PERPLEXITY -> {
                    ivSearchEngine.setImageResource(R.mipmap.ic_search_perplexity)
                }
            }
            var count = JumpDataManager.getBrowserTabList(CacheManager.browserStatus,tag ="mainAdapter 获取当前tab数量").size
            if (count>0){
                tvTab.text = "${count}"
                tvTab.visibility = View.VISIBLE
            }else{
                tvTab.visibility = View.GONE
            }
        }

    }
    fun bind(item: NewsData, fragment: BaseFragment<*>?,payload: String) {
        if (payload == "updateEngine") {
            if (item == null) return
            viewBinding?.apply {
                when (CacheManager.engineType) {
                    SearchConfig.SEARCH_ENGINE_GOOGLE -> {
                        ivSearchEngine.setImageResource(R.mipmap.ic_search_gg)
                    }

                    SearchConfig.SEARCH_ENGINE_BING -> {
                        ivSearchEngine.setImageResource(R.mipmap.ic_search_bing)
                    }

                    SearchConfig.SEARCH_ENGINE_YAHOO -> {
                        ivSearchEngine.setImageResource(R.mipmap.ic_search_yahoo)
                    }

                    SearchConfig.SEARCH_ENGINE_PERPLEXITY -> {
                        ivSearchEngine.setImageResource(R.mipmap.ic_search_perplexity)
                    }
                }
            }
        } else if (payload == "updateTopTab") {
            if (item == null) return
            viewBinding?.apply{
                var count = JumpDataManager.getBrowserTabList(CacheManager.browserStatus,tag ="mainAdapter 获取当前tab数量").size
                if (count>0){
                    tvTab.text = "${count}"
                    tvTab.visibility = View.VISIBLE
                }else{
                    tvTab.visibility = View.GONE
                }
            }
        }

    }
}