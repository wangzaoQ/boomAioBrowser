package com.boom.aiobrowser.ui.activity

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.BrowserActivityMainBinding
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.FragmentManager
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.ParamsConfig
import com.boom.aiobrowser.ui.fragment.MainFragment
import com.boom.aiobrowser.ui.fragment.StartFragment
import com.boom.aiobrowser.ui.fragment.WebFragment
import java.lang.ref.WeakReference

class MainActivity : BaseActivity<BrowserActivityMainBinding>() {

    val fManager by lazy {
        FragmentManager()
    }

    var startFragment :StartFragment?=null

    override fun getBinding(inflater: LayoutInflater): BrowserActivityMainBinding {
        return BrowserActivityMainBinding.inflate(layoutInflater)
    }

  var navController :NavController?=null


    override fun setListener() {
        APP.jumpLiveData.observe(this){
            // 通过Action进行导航，跳转到secondFragment
            when (it.jumpType) {
                JumpConfig.JUMP_WEB -> {
                    val navOptions = NavOptions.Builder()
                        .setEnterAnim(R.anim.in_alpha)
                        .setExitAnim(R.anim.out_alpha)
                        .build()

                    navController?.navigate(R.id.fragmentWeb, Bundle().apply {
                        putString(ParamsConfig.JSON_PARAMS, toJson(it))
                    },navOptions)
                }
                JumpConfig.JUMP_SEARCH -> {
                    val navOptions = NavOptions.Builder()
                        .setEnterAnim(R.anim.in_alpha)
                        .setExitAnim(R.anim.out_alpha)
                        .build()

                    navController?.navigate(R.id.fragmentSearch, Bundle().apply {
                        putString(ParamsConfig.JSON_PARAMS, toJson(it))
                    },navOptions)
                }
                else -> {}
            }

        }
        for (i in 0 until acBinding.llControl.childCount){
            acBinding.llControl.getChildAt(i).setOnClickListener {
                when(i){
                    0->{}
                    1->{}
                    2->{}
                    3->{}
                    4->{}
                }
            }
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
        acBinding.tvTabCount.text = "${CacheManager.tabDataList.size}"
    }

    fun hideStart() {
        fManager.hideFragment(supportFragmentManager, startFragment!!)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        var mMainNavFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_view);
//再获取当前的Fragment
        var currentFragmentInstance = mMainNavFragment?.getChildFragmentManager()?.getPrimaryNavigationFragment();
        if (currentFragmentInstance != null && currentFragmentInstance is WebFragment) {
            return if (currentFragmentInstance.goBack(keyCode, event)) {
                AppLogs.dLog(acTAG,"返回 fragment goBack")
                true
            } else {
                AppLogs.dLog(acTAG,"返回 fragment super.onKeyDown")
                super.onKeyDown(keyCode, event)
            }
        }
        AppLogs.dLog(acTAG,"返回 activity super.onKeyDown")
        return super.onKeyDown(keyCode, event)
    }

}