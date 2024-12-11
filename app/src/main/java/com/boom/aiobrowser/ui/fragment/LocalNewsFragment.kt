package com.boom.aiobrowser.ui.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.SizeUtils.dp2px
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.LocationData
import com.boom.aiobrowser.databinding.NewsFragmentLocationBinding
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.LocationManager
import com.boom.aiobrowser.ui.activity.LocationAddActivity
import com.boom.aiobrowser.ui.activity.LocationSettingActivity
import com.boom.aiobrowser.ui.adapter.LocalNewsPagerStateAdapter
import com.boom.aiobrowser.ui.pop.LoadingPop
import com.boom.indicator.ViewPagerHelper
import com.boom.indicator.buildins.commonnavigator.CommonNavigator
import com.boom.indicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import com.boom.indicator.buildins.commonnavigator.abs.IPagerIndicator
import com.boom.indicator.buildins.commonnavigator.abs.IPagerTitleView
import com.boom.indicator.buildins.commonnavigator.titles.ScaleTransitionPagerTitleView
import com.boom.indicator.buildins.commonnavigator.titles.SimplePagerTitleView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class LocalNewsFragment :BaseFragment<NewsFragmentLocationBinding>(){
    override fun startLoadData() {
        updateNewsHome(CacheManager.alreadyAddCityList)
    }

    override fun setListener() {
        APP.locationListUpdateLiveData.observe(this){
            updateNewsHome(CacheManager.alreadyAddCityList,it)
        }
        fBinding.apply {
            flAddCity.setOneClick {
                rootActivity.jumpActivity<LocationAddActivity>()
            }
        }
    }

    override fun setShowView() {
    }

    var fragmentAdapter: LocalNewsPagerStateAdapter? = null

    var loadingPop:LoadingPop?=null

    private fun updateNewsHome(list: MutableList<LocationData>,type:Int=0) {
        loadingPop?.dismiss()
        var locationData = CacheManager.locationData
        if (locationData?.locationSuccess == true){
            list.add(0,locationData)
        }
        if (list.isNullOrEmpty()){
            fBinding.llLocationGuide.visibility = View.VISIBLE
            var locationCity = CacheManager.locationData?.locationCity?:""
            fBinding.tvNewsFrom.text = "${getString(R.string.app_news_from)} ${locationCity}?"
            var s = SpannableStringBuilder(fBinding.tvNewsFrom.text)
            var index = fBinding.tvNewsFrom.text.toString().indexOf(locationCity, ignoreCase = true)
            if (index >=0){
                s.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(rootActivity,R.color.color_blue_4442E7)),
                    index,
                    index+locationCity.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                fBinding.tvNewsFrom.setText(s)
            }
            fBinding.btnYes.setOneClick {
                var data = CacheManager.locationData
                data?.locationSuccess = true
                CacheManager.locationData = data
                updateNewsHome(CacheManager.alreadyAddCityList)
            }
            fBinding.btnNo.setOneClick {
                LocationManager.requestGPSPermission(WeakReference(rootActivity), onSuccess = {
                    var isShowing = loadingPop?.isShowing?:false
                    if (isShowing.not()){
                        loadingPop = LoadingPop(rootActivity)
                        loadingPop!!.createPop()
                        rootActivity.addLaunch(success = {
                            var area = LocationManager.getAreaByGPS()
                            if (area == null){
                                withContext(Dispatchers.Main){
                                    toLocationSetting()
                                }
                            }else{
                                withContext(Dispatchers.Main){
                                    updateNewsHome(CacheManager.alreadyAddCityList)
                                }
                            }
                        }, failBack = {
                            toLocationSetting()
                        })
                    }

                }, onFail = {
                    toLocationSetting()
                })
            }
        }else{
            fBinding.llLocationGuide.visibility = View.GONE
            fBinding.apply {
//            tabLayout.setBackgroundColor(Color.WHITE)
                val commonNavigator: CommonNavigator = CommonNavigator(rootActivity)
//            commonNavigator.setScrollPivotX(0.35f)
                commonNavigator.scrollPivotX = 0.8f
                commonNavigator.rightPadding = dp2px(8f)
                commonNavigator.leftPadding = dp2px(8f)
//            commonNavigator.isIndicatorOnTop = true
                commonNavigator.setAdapter(object : CommonNavigatorAdapter() {
                    override fun getCount(): Int {
                        return list.size
                    }

                    override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                        val simplePagerTitleView = ScaleTransitionPagerTitleView(context)
                        simplePagerTitleView.setText(list.get(index).locationCity)
                        simplePagerTitleView.setNormalColor(Color.parseColor("#2E395E"))
                        simplePagerTitleView.setSelectedColor(Color.parseColor("#1C274C"))
                        simplePagerTitleView.setTextSize(14f)
                        simplePagerTitleView.setNormalStyle(1)
                        simplePagerTitleView.setSelectedStyle(1)
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
                    fragmentAdapter = LocalNewsPagerStateAdapter(
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
                if (type == 2){
                    vp.currentItem = 0
                }else{
                    vp.currentItem = list.size-1
                }
            }
        }
    }

    private fun toLocationSetting() {
        loadingPop?.dismiss()
        rootActivity.jumpActivity<LocationSettingActivity>()
    }

    override fun onDestroy() {
        APP.locationListUpdateLiveData.removeObservers(this)
        super.onDestroy()
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NewsFragmentLocationBinding {
        return NewsFragmentLocationBinding.inflate(layoutInflater)
    }
}