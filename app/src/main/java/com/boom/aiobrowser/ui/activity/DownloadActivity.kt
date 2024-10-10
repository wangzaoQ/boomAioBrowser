package com.boom.aiobrowser.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.databinding.VideoActivityDownloadBinding
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.download.DownloadCacheManager
import com.boom.aiobrowser.ui.fragment.DownloadFragment
import com.boom.aiobrowser.ui.pop.DownloadVideoGuidePop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    var fromPage = ""

    override fun setShowView() {
        fromPage = intent.getStringExtra("fromPage")?:""
        fragments.add(DownloadFragment.newInstance(0,fromPage))
        fragments.add(DownloadFragment.newInstance(1,fromPage))
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
            runCatching {
                if (fragments.size>0){
                    var map = it
                    it.keys.forEach {
                        (fragments.get(0) as DownloadFragment).updateStatus(it,map.get(it)){}
                        if (it == VideoDownloadData.DOWNLOAD_SUCCESS){
                            (fragments.get(0) as DownloadFragment).startLoadData()
                        }
                    }
                }
            }
        }
        var isFirst = false
        if (CacheManager.isFirstShowDownload){
            isFirst = true
            CacheManager.isFirstShowDownload = false
        }
        acBinding.root.postDelayed({
            PointEvent.posePoint(PointEventKey.download_page, Bundle().apply {
                putInt(PointValueKey.open_type,if (isFirst) 0 else 1)
                putString(PointValueKey.from_page,fromPage)
            })
        },0)
        addLaunch(success = {
            var list = DownloadCacheManager.queryAllModel()
            withContext(Dispatchers.Main){
                if (list.isNullOrEmpty()){
                    acBinding.llGuide.visibility = View.VISIBLE
                    acBinding.llGuide.setOnClickListener {
                        DownloadVideoGuidePop(this@DownloadActivity).createPop {  }
                    }
                }
            }
        }, failBack = {})
    }

    private fun updateUI(position: Int) {
        acBinding.tvProgress.isEnabled = (position != 0)
        acBinding.tvDone.isEnabled = (position != 1)
    }

    override fun onDestroy() {
        super.onDestroy()
//        APP.videoUpdateLiveData.postValue(0)
    }
}