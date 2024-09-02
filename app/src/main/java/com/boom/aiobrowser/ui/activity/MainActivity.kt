package com.boom.aiobrowser.ui.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Environment
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.viewpager.widget.ViewPager
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.databinding.BrowserActivityMainBinding
import com.boom.aiobrowser.model.CleanViewModel
import com.boom.aiobrowser.model.NewsViewModel
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.BrowserManager
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.FragmentManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.ParamsConfig
import com.boom.aiobrowser.ui.fragment.FileManageFragment
import com.boom.aiobrowser.ui.fragment.MainFragment
import com.boom.aiobrowser.ui.fragment.StartFragment
import com.boom.aiobrowser.ui.fragment.WebFragment
import com.boom.aiobrowser.ui.pop.ClearPop
import com.boom.aiobrowser.ui.pop.DefaultPop
import com.boom.aiobrowser.ui.pop.MorePop
import com.boom.aiobrowser.ui.pop.TabPop
import pop.basepopup.BasePopupWindow.OnDismissListener
import java.util.LinkedList

class MainActivity : BaseActivity<BrowserActivityMainBinding>() {


    private val viewModel by viewModels<NewsViewModel>()


    val fManager by lazy {
        FragmentManager()
    }


    var startFragment :StartFragment?=null

    override fun getBinding(inflater: LayoutInflater): BrowserActivityMainBinding {
        return BrowserActivityMainBinding.inflate(layoutInflater)
    }

  var navController :NavController?=null

    val mainFragment by lazy {
        MainFragment()
    }

    val fileFragment by lazy {
        FileManageFragment()
    }


    val fragments :MutableList<BaseFragment<*>> by lazy {
        mutableListOf<BaseFragment<*>>().apply {
            add(mainFragment)
            add(fileFragment)
        }
    }

    override fun setListener() {
        APP.jumpLiveData.observe(this){
            // 通过Action进行导航，跳转到secondFragment
            when (it.jumpType) {
                JumpConfig.JUMP_WEB -> {
                    if (it.isJumpClick){
                        it.isJumpClick = false
                        CacheManager.linkedUrlList = LinkedList()
                    }
                    jumpActivity<WebDetailsActivity>(Bundle().apply {
                        putString(ParamsConfig.JSON_PARAMS, toJson(it))
                    })
//                    val navOptions = NavOptions.Builder()
//                        .setEnterAnim(R.anim.in_alpha)
//                        .setExitAnim(R.anim.out_alpha)
//                        .build()
//
//                    navController?.navigate(R.id.fragmentWeb, Bundle().apply {
//                        putString(ParamsConfig.JSON_PARAMS, toJson(it))
//                    },navOptions)
//                    acBinding.llMainControl.visibility = View.GONE
//                    updateBottom(true,false,it, tag = "JumpConfig.JUMP_WEB")
                }
                JumpConfig.JUMP_SEARCH -> {
                    jumpActivity<SearchActivity>(Bundle().apply {
                        putString(ParamsConfig.JSON_PARAMS, toJson(it))
                    })
//                    val navOptions = NavOptions.Builder()
//                        .setEnterAnim(R.anim.in_alpha)
//                        .setExitAnim(R.anim.out_alpha)
//                        .build()
//
//                    navController?.navigate(R.id.fragmentSearch, Bundle().apply {
//                        putString(ParamsConfig.JSON_PARAMS, toJson(it))
//                    },navOptions)
//                    updateBottom(true,false,it,tag = "JumpConfig.JUMP_SEARCH")
                }
                JumpConfig.JUMP_HOME ->{
//                    val navOptions = NavOptions.Builder()
//                        .setEnterAnim(R.anim.in_alpha)
//                        .setExitAnim(R.anim.out_alpha)
//                        .setPopUpTo(R.id.fragmentFile, true) // 将目标Fragment从Back Stack中移除
//                        .build()
//
//                    navController?.navigate(R.id.fragmentMain, Bundle().apply {
//                        putString(ParamsConfig.JSON_PARAMS, toJson(it))
//                    },navOptions)
                    acBinding.vpRoot.setCurrentItem(0)
                }
                JumpConfig.JUMP_FILE ->{
//                    val navOptions = NavOptions.Builder()
//                        .setEnterAnim(R.anim.in_alpha)
//                        .setExitAnim(R.anim.out_alpha)
//                        .setPopUpTo(R.id.fragmentMain, true) // 将目标Fragment从Back Stack中移除
//                        .build()
//
//                    navController?.navigate(R.id.fragmentFile, Bundle().apply {
//                        putString(ParamsConfig.JSON_PARAMS, toJson(it))
//                    },navOptions)
                    acBinding.vpRoot.setCurrentItem(1)
                    acBinding.llMainControl.visibility = View.VISIBLE
                }
                else -> {}
            }
        }
//        APP.bottomLiveData.observe(this){
//            updateBottomClick(it)
//        }
        for ( i in 0 until acBinding.llMainControl.childCount){
            acBinding.llMainControl.getChildAt(i).setOneClick {
                clickIndex(i)
            }
        }
        updateUI(0)
    }

    private fun clickIndex(index: Int) {
       updateUI(index)
        when (index) {
            0 -> {
                APP.jumpLiveData.postValue(JumpDataManager.getCurrentJumpData(tag = "点击 home tab ").apply {
                    jumpType = JumpConfig.JUMP_HOME
                    jumpTitle = APP.instance.getString(R.string.app_home)
                })
            }
            1 ->{
                APP.jumpLiveData.postValue(JumpDataManager.getCurrentJumpData(tag = "点击 file tab").apply {
                    jumpType = JumpConfig.JUMP_FILE
                    jumpTitle = APP.instance.getString(R.string.app_files)
                })
            }
            2 ->{
                showTabPop()
            }
            3->{
                morePop = MorePop(this@MainActivity)
                morePop?.createPop()
            }
            else -> {}
        }
    }


    fun showTabPop() {
        var tabPop = TabPop(this)
        tabPop.createPop()
        tabPop.setOnDismissListener(object : OnDismissListener(){
            override fun onDismiss() {
            }
        })
    }

    private fun updateUI(index: Int) {
        if (index == 2 || index == 3){
            return
        }
        for ( start in 0 until acBinding.llMainControl.childCount){
            var ll = acBinding.llMainControl.getChildAt(start) as LinearLayoutCompat
            for (i in 0 until ll.childCount){
                ll.getChildAt(i).isEnabled = (start == index).not()
            }
        }
    }

    var morePop : MorePop?=null



    override fun onResume() {
        super.onResume()
        morePop?.updateUI()
//        cleanViewModel.startScan(Environment.getExternalStorageDirectory())
    }

    fun loadNews(){
        viewModel.getNewsData()
    }

    private fun setVp() {
        acBinding.vpRoot.apply {
            offscreenPageLimit = fragments.size
            adapter = object : FragmentPagerAdapter(
                supportFragmentManager,
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            ) {
                override fun getItem(position: Int): Fragment {
                    return fragments[position]
                }

                override fun getCount(): Int {
                    return fragments.size
                }
            }
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    updateUI(position)
                }

                override fun onPageScrollStateChanged(state: Int) {
                }
            })
        }
    }



    override fun setShowView() {
        if (CacheManager.browserStatus == 1){
            CacheManager.browserStatus = 0
        }
        setVp()
        startFragment = StartFragment()
        startFragment?.apply {
//            fManager.showFragment(supportFragmentManager,this)
            intent.data?.apply {
                if (this.toString().isNotEmpty()){
                    var data = JumpDataManager.getCurrentJumpData(tag = "MainActivity setShowView")
                    data.jumpType = JumpConfig.JUMP_WEB
                    data.jumpUrl = this.toString()
                    JumpDataManager.updateCurrentJumpData(data,tag = "MainActivity setShowView")
                }
            }
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
        loadNews()
//        acBinding.root.postDelayed({
//            var count = 0
//            for ( i in 0 until APP.instance.lifecycleApp.stack.size){
//                var activity = APP.instance.lifecycleApp.stack.get(i)
//                if (activity is BaseActivity<*>){
//                    count++
//                }
//            }
//            AppLogs.dLog(APP.instance.TAG,"当前MainActivity count:${count}")
//            if (count == 1){
//                APP.jumpLiveData.postValue(JumpData().apply {
//                    jumpType = JumpConfig.JUMP_MAIN
//                })
//            }
//        },500)
    }

    fun hideStart() {
        fManager.hideFragment(supportFragmentManager, startFragment!!)
        acBinding.llMainControl.visibility = View.VISIBLE
        if (BrowserManager.isDefaultBrowser().not() && CacheManager.isFirstShowBrowserDefault){
            CacheManager.isFirstShowBrowserDefault = false
            var pop = DefaultPop(this@MainActivity)
            pop.setOnDismissListener(object :OnDismissListener(){
                override fun onDismiss() {
                    showTips()
                }
            })
            pop.createPop()
        }else{
            showTips()
        }
    }

    private fun showTips() {
        return
        if (CacheManager.isFirstShowClear){
            CacheManager.isFirstShowClear = false
            acBinding.root.postDelayed({
                acBinding.tvClearData.visibility = View.VISIBLE
                val scaleXAnimator = ObjectAnimator.ofFloat(acBinding.tvClearData, "scaleX", 1.0f, 1.1f,1.0f)
                val scaleYAnimator = ObjectAnimator.ofFloat(acBinding.tvClearData, "scaleY", 1.0f, 1.1f,1.0f)
                val set = AnimatorSet()
                set.play(scaleXAnimator).with(scaleYAnimator)
                set.setDuration(3000)
                set.start()
                set.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator) {

                    }

                    override fun onAnimationEnd(p0: Animator) {
                        acBinding.tvClearData.visibility = View.GONE
                    }

                    override fun onAnimationCancel(p0: Animator) {
                    }

                    override fun onAnimationRepeat(p0: Animator) {
                    }

                })
            },0)
        }else{
            acBinding.tvClearData.visibility = View.GONE
        }
    }
//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//
//        var mMainNavFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_view)
////再获取当前的Fragment
//        var currentFragmentInstance = mMainNavFragment?.getChildFragmentManager()?.getPrimaryNavigationFragment();
//        if (currentFragmentInstance != null && currentFragmentInstance is WebFragment) {
//            return if (currentFragmentInstance.goBack(keyCode, event)) {
//                AppLogs.dLog(acTAG,"返回 fragment goBack")
//                true
//            } else {
//                AppLogs.dLog(acTAG,"返回 fragment super.onKeyDown")
//                super.onKeyDown(keyCode, event)
//            }
//        }
//        AppLogs.dLog(acTAG,"返回 activity super.onKeyDown")
//        return super.onKeyDown(keyCode, event)
//    }

}