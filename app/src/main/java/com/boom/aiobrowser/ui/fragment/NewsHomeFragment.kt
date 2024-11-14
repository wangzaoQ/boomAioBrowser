package com.boom.aiobrowser.ui.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.TopicBean
import com.boom.aiobrowser.databinding.NewsFragmentHomeBinding
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.ui.adapter.NewsPagerStateAdapter
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
            tabLayout.setupWithViewPager(fBinding.vp)
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    updateTabTextView(tab, true)
                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    updateTabTextView(tab, false)
                }
            })
            vp.currentItem = 1
            for (i in 0..list.size - 1) {
                var newTab = fBinding.tabLayout.getTabAt(i)
                val view: View =
                    LayoutInflater.from(context).inflate(R.layout.news_view_custom_tab, null)
                var textView = view.findViewById<AppCompatTextView>(R.id.tvCustomTab)
                textView.text = list.get(i).topic
                newTab?.setCustomView(view)
            }
            updateTabTextView(
                fBinding.tabLayout.getTabAt(fBinding.tabLayout.selectedTabPosition),
                false
            )
        }
    }

    fun updateTabTextView(tab: TabLayout.Tab?, isEnable: Boolean) {
        tab?.customView?.findViewById<AppCompatTextView>(R.id.tvCustomTab)?.apply {
            isEnabled = isEnable
            setText(text)
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