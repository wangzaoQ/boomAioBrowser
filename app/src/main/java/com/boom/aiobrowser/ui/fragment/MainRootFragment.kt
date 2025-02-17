package com.boom.aiobrowser.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.databinding.BrowserFragmentMainRootBinding
import com.boom.aiobrowser.other.JumpConfig
import com.boom.aiobrowser.other.ParamsConfig
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.toJson
import com.boom.drag.EasyFloat
import java.util.LinkedList


class MainRootFragment :BaseFragment<BrowserFragmentMainRootBinding>() {

    var navController : NavController?=null


    override fun startLoadData() {

    }

    override fun setListener() {
        APP.jumpLiveData.observe(this){
            // 通过Action进行导航，跳转到secondFragment
            when (it.jumpType) {
                JumpConfig.JUMP_WEB -> {
//                    val navController = Navigation.findNavController(rootActivity, R.id.fragment_view)
//                    val currentDestinationId = navController.currentDestination!!.id
//                    if (currentDestinationId == R.id.fragmentWeb)
                    val currentFragment = childFragmentManager.findFragmentById(R.id.fragment_view)?.getChildFragmentManager()?.primaryNavigationFragment
                    if (currentFragment is WebFragment && it.autoDownload.not() && it.jumpUrl!= getString(R.string.video_local_title)){
                        currentFragment.updateData(it)
//                        currentFragment.mAgentWeb!!.go(it.jumpUrl)
                        return@observe
                    }
                    CacheManager.videoDownloadTempList = mutableListOf()
                    if (it.isJumpClick){
                        it.isJumpClick = false
                        CacheManager.linkedUrlList = LinkedList()
                    }
                    val navOptions = NavOptions.Builder()
                        .setEnterAnim(R.anim.in_alpha)
                        .setExitAnim(R.anim.out_alpha)
                        .setPopUpTo(R.id.fragment_view, false)
                        .build()

                    navController?.navigate(R.id.fragmentWeb, Bundle().apply {
                        putString(ParamsConfig.JSON_PARAMS, toJson(it))
                    },navOptions)
                    if (CacheManager.browserStatus == 0){
                        CacheManager.saveRecentSearchData(it)
                    }
//                    if (CacheManager.dayShowBattery){
//                        rootActivity.addLaunch(success = {
//                            delay(2000)
//                            BatteryUtil(WeakReference(context as BaseActivity<*>)).requestIgnoreBatteryOptimizations()
//                        }, failBack = {})
//                    }
                }
                JumpConfig.JUMP_HOME ->{
                    it.jumpUrl=""
                    it.jumpTitle = getString(R.string.app_home)
                    JumpDataManager.updateCurrentJumpData(it,"MainFragment onResume 更新 jumpData")

                    val navOptions = NavOptions.Builder()
                        .setEnterAnim(R.anim.in_alpha)
                        .setExitAnim(R.anim.out_alpha)
//                        .setPopUpTo(R.id.fragmentFile, true) // 将目标Fragment从Back Stack中移除
                        .build()
                    navController?.navigate(R.id.fragmentMain, Bundle().apply {
                        putString(ParamsConfig.JSON_PARAMS, toJson(it))
                    },navOptions)
                    EasyFloat.dismiss(tag = "webPop")
                }
                else -> {}
            }
        }
    }



    override fun setShowView() {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.fragment_view) as NavHostFragment
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navController = navHostFragment.navController
//        navController = Navigation.findNavController(rootActivity,R.id.fragment_view)

    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BrowserFragmentMainRootBinding {
        return BrowserFragmentMainRootBinding.inflate(layoutInflater)
    }
}