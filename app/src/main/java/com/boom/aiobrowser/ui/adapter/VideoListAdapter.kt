package com.boom.aiobrowser.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.ui.fragment.video.VideoListFragment

class VideoListAdapter(var activity: BaseActivity<*>,var list: MutableList<NewsData>,var fromType:String)  : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        var fragment = VideoListFragment.newInstance(list,position,fromType)
        return fragment
    }

}