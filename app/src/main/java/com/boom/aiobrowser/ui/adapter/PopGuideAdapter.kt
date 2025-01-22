package com.boom.aiobrowser.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.ui.fragment.guide.GuideFragment1
import com.boom.aiobrowser.ui.fragment.guide.GuideFragment2
import com.boom.aiobrowser.ui.fragment.guide.GuideFragment3

class PopGuideAdapter(var dataList:MutableList<Int>,activity: BaseActivity<*>) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun createFragment(position: Int): Fragment {
        var fragment = if (position == 0){
            GuideFragment1()
        }else if (position == 1){
            GuideFragment2()
        }else{
            GuideFragment3()
        }
        return fragment
    }

}