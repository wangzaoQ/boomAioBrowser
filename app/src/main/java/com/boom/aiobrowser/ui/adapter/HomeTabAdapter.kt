package com.boom.aiobrowser.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.ui.fragment.HomeTabFragment

class HomeTabAdapter(var dataList:MutableList<MutableList<JumpData>>, activity: BaseActivity<*>) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun createFragment(position: Int): Fragment {
        var fragment = HomeTabFragment.newInstance(dataList.get(position))
        return fragment
    }

}