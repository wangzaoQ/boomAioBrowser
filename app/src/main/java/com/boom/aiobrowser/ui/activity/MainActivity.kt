package com.boom.aiobrowser.ui.activity

import android.view.LayoutInflater
import androidx.navigation.NavController
import androidx.navigation.Navigation
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

    var startFragment :StartFragment?=null


    val mainFragment by lazy {
        MainFragment()
    }
    override fun getBinding(inflater: LayoutInflater): BrowserActivityMainBinding {
        return BrowserActivityMainBinding.inflate(layoutInflater)
    }

  var navController :NavController?=null


    override fun setListener() {
        APP.jumpLiveData.observe(this){
            // 通过Action进行导航，跳转到secondFragment
            acBinding.root.postDelayed(Runnable {
                navController?.navigate(R.id.fragmentWeb)
            },3000)
        }
    }

    override fun setShowView() {
        navController = Navigation.findNavController(this@MainActivity,R.id.fragment_view)
        startFragment = StartFragment()
        startFragment?.apply {
//            fManager.showFragment(supportFragmentManager,this)
            updateUI(intent)
            fManager.addFragmentTag(supportFragmentManager,this,R.id.fragmentStart,"StartFragment")
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
        fManager.hideFragment(supportFragmentManager, startFragment!!)
    }

}