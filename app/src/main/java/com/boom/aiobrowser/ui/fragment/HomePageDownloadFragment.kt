package com.boom.aiobrowser.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADDataManager
import com.boom.aiobrowser.ad.AioADShowManager
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.databinding.BrowserHomeDownloadBinding
import com.boom.aiobrowser.point.AD_POINT
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.BatteryUtil
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.download.DownloadCacheManager
import com.boom.aiobrowser.ui.pop.DownloadVideoGuidePop
import com.boom.aiobrowser.ui.pop.FirstDownloadTips
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class HomePageDownloadFragment : BaseFragment<BrowserHomeDownloadBinding>(){


    val fragments: MutableList<BaseFragment<*>> by lazy {
        mutableListOf<BaseFragment<*>>()
    }

    override fun startLoadData() {
        if (CacheManager.isFirstDownloadTips4){
            CacheManager.isFirstDownloadTips4 = false
            var tips4 = FirstDownloadTips(rootActivity)
            tips4?.createPop(fBinding.tvDone,4)
        }
        if (AioADDataManager.adFilter1().not()) {
            PointEvent.posePoint(PointEventKey.aobws_ad_chance, Bundle().apply {
                putString(PointValueKey.ad_pos_id, AD_POINT.aobws_download_one)
            })
        }
        AioADShowManager(rootActivity, ADEnum.NATIVE_DOWNLOAD_AD,"下载页原生"){

        }.showNativeAD(fBinding.flRoot, AD_POINT.aobws_download_one)
    }

    override fun setListener() {
        fBinding.apply {
            tvProgress.setOneClick {
                vpRoot.currentItem = 0
            }
            tvDone.setOneClick {
                vpRoot.currentItem = 1
            }
        }

        APP.videoNFLiveData.observe(this){
            runCatching {
                if (fragments.size>0){
                    (fragments.get(0) as DownloadFragment).updateByNf(it)
                }
            }
        }
        APP.downloadPageLiveData.observe(this){
            if (it == "webpage_download_pop" || it == "webpage_download_task_pop_complete"){
                fBinding.vpRoot.currentItem = 1
            }else if (it == "webpage_download_task_pop"){
                BatteryUtil(WeakReference(rootActivity)).requestIgnoreBatteryOptimizations()
            }
        }
    }

    override fun setShowView() {
        fragments.add(DownloadFragment.newInstance(0))
        fragments.add(DownloadFragment.newInstance(1))
        fBinding.vpRoot.apply {
            offscreenPageLimit = fragments.size
            adapter = object : FragmentPagerAdapter(
                childFragmentManager,
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


        var isFirst = false
        if (CacheManager.isFirstShowDownload){
            isFirst = true
            CacheManager.isFirstShowDownload = false
        }
        fBinding.root.postDelayed({
            PointEvent.posePoint(PointEventKey.download_page, Bundle().apply {
                putInt(PointValueKey.open_type,if (isFirst) 0 else 1)
//                putString(PointValueKey.from_page,fromPage)
            })
        },0)
    }

    private fun updateUI(position: Int) {
        fBinding.tvProgress.isEnabled = (position != 0)
        fBinding.tvDone.isEnabled = (position != 1)
    }

    override fun onDestroy() {
        APP.jumpLiveData.removeObservers(this)
        APP.videoNFLiveData.removeObservers(this)
        APP.videoLiveData.removeObservers(this)
        APP.downloadPageLiveData.removeObservers(this)
        super.onDestroy()
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BrowserHomeDownloadBinding {
        return BrowserHomeDownloadBinding.inflate(layoutInflater)
    }
}