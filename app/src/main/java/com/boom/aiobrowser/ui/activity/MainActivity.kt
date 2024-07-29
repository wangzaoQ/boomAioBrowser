package com.boom.aiobrowser.ui.activity

import android.view.LayoutInflater
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.BrowserActivityMainBinding
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.FragmentManager
import com.boom.aiobrowser.ui.fragment.MainFragment
import com.boom.aiobrowser.ui.fragment.StartFragment

class MainActivity : BaseActivity<BrowserActivityMainBinding>() {

    val fManager by lazy {
        FragmentManager()
    }

    val startFragment by lazy {
//        (supportFragmentManager.findFragmentById(R.id.fragmentStart) as StartFragment)
        StartFragment()
    }


    val mainFragment by lazy {
        MainFragment()
    }
    override fun getBinding(inflater: LayoutInflater): BrowserActivityMainBinding {
        return BrowserActivityMainBinding.inflate(layoutInflater)
    }

    override fun setListener() {

    }

    override fun setShowView() {
        startFragment.apply {
//            fManager.showFragment(supportFragmentManager,this)
            fManager.addFragmentTag(supportFragmentManager,this,R.id.fragmentStart,"StartFragment")
            updateUI(intent)
        }
//        acBinding.root.postDelayed({
//            var count = 0
//            for ( i in 0 until APP.instance.lifecycleApp.stack.size){
//                var activity = APP.instance.lifecycleApp.stack.get(i)
//                if (activity is MainActivity){
//                    count++
//                }
//            }
//            AppLogs.dLog(APP.instance.lifecycleApp.TAG,"目前MainActivity 数目:${count}")
//            if (count == 1){
//                fManager.addFragmentTag(supportFragmentManager,mainFragment,R.id.fragmentMainFl,"MainFragment")
//            }
//        }, 500)
    }

    fun hideStart() {
        fManager.replaceFragment(supportFragmentManager, mainFragment,R.id.fragmentStart)
    }

}