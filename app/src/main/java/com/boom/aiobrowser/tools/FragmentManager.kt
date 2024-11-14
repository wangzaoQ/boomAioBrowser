package com.boom.aiobrowser.tools

import android.R
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.applovin.impl.fm


class FragmentManager {
    fun addFragment(fragmentManager: FragmentManager, fragment: Fragment, frameId: Int) {
        destroyF(fragmentManager)
        val transaction = fragmentManager.beginTransaction()
        transaction.add(frameId, fragment)
        transaction.commitAllowingStateLoss()
    }

    private fun destroyF(fragmentManager: FragmentManager) {
        val fragmentList: List<Fragment> = fragmentManager.fragments
        if (fragmentList != null) {
            for (i in fragmentList.indices) {
                fragmentManager.beginTransaction().remove(fragmentList[i]).commitAllowingStateLoss()
            }
        }
    }


    fun replaceFragment(fragmentManager: FragmentManager, fragment: Fragment, frameId: Int) {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(frameId, fragment)
        transaction.commitAllowingStateLoss()
    }


    fun showFragment(fragmentManager: FragmentManager, fragment: Fragment, frameId: Int) {
        val transaction = fragmentManager.beginTransaction()
        transaction.show(fragment)
        transaction.commitAllowingStateLoss()
    }

    /**
     * fragment 切换
     *
     * @param from
     * @param to
     */
    fun switchFragment(fragmentManager: FragmentManager,layoutId:Int,from: Fragment, to: Fragment,toTag:String) {
        val transaction = fragmentManager.beginTransaction()
        if (!to.isAdded) { // 先判断是否被add过
            transaction.hide(from)
                .add(layoutId, to, toTag)
                .commit() // 隐藏当前的fragment，add下一个到Activity中
        } else {
            transaction.hide(from).show(to).commit() // 隐藏当前的fragment，显示下一个
        }
    }

    fun hideFragment(fragmentManager: FragmentManager, fragment: Fragment) {
        val transaction = fragmentManager.beginTransaction()
        transaction.hide(fragment)
        transaction.commitAllowingStateLoss()
    }

    fun addFragmentTag(fragmentManager: FragmentManager, fragment: Fragment, frameId: Int,tag:String) {
        val transaction = fragmentManager.beginTransaction()
        transaction.add(frameId, fragment,tag)
        transaction.commitAllowingStateLoss()
    }
}