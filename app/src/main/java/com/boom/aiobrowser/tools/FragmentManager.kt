package com.boom.aiobrowser.tools

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

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


    fun showFragment(fragmentManager: FragmentManager, fragment: Fragment) {
        val transaction = fragmentManager.beginTransaction()
        transaction.show(fragment)
        transaction.commitAllowingStateLoss()
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