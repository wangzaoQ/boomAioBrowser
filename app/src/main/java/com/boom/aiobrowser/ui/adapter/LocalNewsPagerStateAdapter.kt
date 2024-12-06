package com.boom.aiobrowser.ui.adapter

import android.os.Parcelable
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.boom.aiobrowser.data.LocationData
import com.boom.aiobrowser.data.TopicBean
import com.boom.aiobrowser.other.NewsConfig.LOCAL_TAG
import com.boom.aiobrowser.ui.fragment.LocalNewsFragment
import com.boom.aiobrowser.ui.fragment.NewsFragment

class LocalNewsPagerStateAdapter(
    var titleList: MutableList<LocationData>,
    fm: FragmentManager,
    behavior: Int
) :
    FragmentStatePagerAdapter(fm, behavior) {

    val mPageReferenceMap: HashMap<Int, Fragment> = HashMap()

    override fun getCount(): Int {
        return titleList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titleList[position].locationCity
    }

    override fun getItem(position: Int): Fragment {
        return NewsFragment.newInstance("${LOCAL_TAG}${titleList[position].locationCity}")
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Fragment {
        val fragment = super.instantiateItem(container, position) as Fragment
        // 添加到表中，不能在getItem方法里面添加，有的时候FragmentStatePagerAdapter会恢复一些Fragment，那些Fragment的恢复不会调用getItem方法
        mPageReferenceMap.put(position, fragment)
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        if (getItemPosition(`object`) == POSITION_NONE){
            mPageReferenceMap.remove(position)
            super.destroyItem(container, position, `object`)
        }
    }


    // 根据位置获取到对应的页面，使用的时候也需要判空和判定 !fragment.isDetached() && !fragment.isRemoving()，否则会引发一些隐藏的问题
    fun getFragment(position: Int): Fragment? {
        return mPageReferenceMap.get(position)
    }

    /**
     * 由于 fragment.setArguments(); 方法传递的参数会被 FragmentStatePagerAdapter 所缓存
     * ，为了节省内存，重写这个方法返回空可以阻止FragmentStatePagerAdapter的缓存操作
     * @return
     */
    override fun saveState(): Parcelable? {
        return null
    }


    override fun getItemPosition(fragment: Any): Int {
//        (fragment as? NewsListFragment)?.apply {
//            return if (fragment.topic == CommonConfig.TOPIC_FOLLOW || fragment.topic == CommonConfig.TOPIC_FOR_YOU
//                || fragment.topic == CommonConfig.TOPIC_LOCAL || fragment.topic == CommonConfig.TOPIC_HEADLINES || fragment.topic == CommonConfig.TOPIC_FILM){
//                POSITION_UNCHANGED
//            }else{
//                POSITION_NONE
//            }
//        }
        return POSITION_UNCHANGED
    }
}