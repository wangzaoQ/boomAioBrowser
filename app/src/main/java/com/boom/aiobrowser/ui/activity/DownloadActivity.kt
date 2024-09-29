package com.boom.aiobrowser.ui.activity

import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.databinding.VideoActivityDownloadBinding
import com.boom.aiobrowser.ui.fragment.DownloadFragment
import com.boom.aiobrowser.ui.fragment.FileManageFragment
import com.boom.aiobrowser.ui.fragment.MainFragment

class DownloadActivity : BaseActivity<VideoActivityDownloadBinding>() {
    override fun getBinding(inflater: LayoutInflater): VideoActivityDownloadBinding {
        return VideoActivityDownloadBinding.inflate(layoutInflater)
    }

    val fragments: MutableList<BaseFragment<*>> by lazy {
        mutableListOf<BaseFragment<*>>()
    }

    override fun setListener() {
        acBinding.ivBack.setOneClick {
            finish()
        }
        acBinding.tvProgress.setOneClick {
            acBinding.vpRoot.currentItem = 0
        }
        acBinding.tvDone.setOneClick {
            acBinding.vpRoot.currentItem = 1
        }
    }

    override fun setShowView() {
        fragments.add(DownloadFragment.newInstance(0))
        fragments.add(DownloadFragment.newInstance(1))
        acBinding.vpRoot.apply {
            offscreenPageLimit = fragments.size
            adapter = object : FragmentPagerAdapter(
                supportFragmentManager,
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            ) {
                override fun getItem(position: Int): Fragment {
                    return fragments[position]
                }

                override fun getCount(): Int {
                    return fragments.size
                }
            }
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    updateUI(position)
                }

                override fun onPageScrollStateChanged(state: Int) {
                }
            })
        }
        APP.videoLiveData.observe(this){
            if (fragments.size>0){
                var map = it
                it.keys.forEach {
                    (fragments.get(0) as DownloadFragment).updateStatus(it,map.get(it)){}
                }
            }
        }
    }

    private fun updateUI(position: Int) {
        acBinding.tvProgress.isEnabled = (position != 0)
        acBinding.tvDone.isEnabled = (position != 1)
    }

    override fun onDestroy() {
        APP.videoUpdateLiveData.postValue(0)
        super.onDestroy()
    }
}