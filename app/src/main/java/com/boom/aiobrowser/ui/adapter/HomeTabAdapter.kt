package com.boom.aiobrowser.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.ui.fragment.HomeTabFragment

class HomeTabAdapter(activity: BaseActivity<*>) : FragmentStateAdapter(activity) {


    var dataList:MutableList<MutableList<JumpData>> = mutableListOf()

    //更新数据
    fun update(taskTableBeans: MutableList<MutableList<JumpData>>) {
        this.dataList = taskTableBeans
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return dataList!!.size
    }

    override fun createFragment(position: Int): Fragment {
        var fragment = HomeTabFragment.newInstance(dataList.get(position))
        return fragment
    }
    override fun containsItem(itemId: Long): Boolean {
        return false
    }


    override fun getItemId(position: Int): Long {
        if (dataList!=null && dataList!!.size == 0||dataList!!.size<=position) {
            return 0;
        }
        return dataList.get(position).hashCode().toLong();
    }

}