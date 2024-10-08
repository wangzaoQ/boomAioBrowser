package com.boom.aiobrowser.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.ui.fragment.guide.GuideFragment

class PopGuideAdapter(var dataList:MutableList<Int>,activity: BaseActivity<*>) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun createFragment(position: Int): Fragment {
        var fragment = GuideFragment.newInstance(position)
        return fragment
    }

}