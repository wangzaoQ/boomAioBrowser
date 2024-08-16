package com.boom.aiobrowser.ui.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.databinding.BrowserActivityMainBinding
import com.boom.aiobrowser.model.NewsViewModel
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.BrowserManager
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.FragmentManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.ParamsConfig
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


    override fun setListener() {
        APP.jumpLiveData.observe(this){
            // 通过Action进行导航，跳转到secondFragment
            when (it.jumpType) {
                JumpConfig.JUMP_WEB -> {
                    if (it.isJumpClick){
                        it.isJumpClick = false
                        CacheManager.linkedUrlList = LinkedList()
                    }
                    val navOptions = NavOptions.Builder()
                        .setEnterAnim(R.anim.in_alpha)
                        .setExitAnim(R.anim.out_alpha)
                        .build()

                    navController?.navigate(R.id.fragmentWeb, Bundle().apply {
                        putString(ParamsConfig.JSON_PARAMS, toJson(it))
                    },navOptions)
                    updateBottom(true,false,it, tag = "JumpConfig.JUMP_WEB")
                }
                JumpConfig.JUMP_SEARCH -> {
                    val navOptions = NavOptions.Builder()
                        .setEnterAnim(R.anim.in_alpha)
                        .setExitAnim(R.anim.out_alpha)
                        .build()

                    navController?.navigate(R.id.fragmentSearch, Bundle().apply {
                        putString(ParamsConfig.JSON_PARAMS, toJson(it))
                    },navOptions)
                    updateBottom(true,false,it,tag = "JumpConfig.JUMP_SEARCH")
                }
                JumpConfig.JUMP_HOME ->{
                    val navOptions = NavOptions.Builder()
                        .setEnterAnim(R.anim.in_alpha)
                        .setExitAnim(R.anim.out_alpha)
                        .setPopUpTo(R.id.fragmentMain, true) // 将目标Fragment从Back Stack中移除
                        .build()

                    navController?.navigate(R.id.fragmentMain, Bundle().apply {
                        putString(ParamsConfig.JSON_PARAMS, toJson(it))
                    },navOptions)
                    updateBottom(false,true,it,tag = "JumpConfig.JUMP_HOME")
                }
                else -> {}
            }
        }
        APP.bottomLiveData.observe(this){
            updateBottomClick(it)
        }
        acBinding.tvTabCount.setOnClickListener {
            showTabPop()
        }
//        acBinding.ivClear.setOnClickListener {

//            acBinding.ivClearAnimal.run {
//                setAnimation("test.json")
//                playAnimation()
//            }
//            acBinding.ivClear.postDelayed({
//              JumpDataManager.clearAllTab()
//            },1000)
//            Glide.with(this)
//                .asGif()
//                .apply(RequestOptions().format(DecodeFormat.PREFER_RGB_565))
//                .load("file:///android_asset/clear.gif")
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .into(acBinding.ivClearAnimal)
//        }
        acBinding.ivMore.setOnClickListener {
            morePop = MorePop(this@MainActivity)
            morePop?.createPop()
        }
    }

    var morePop : MorePop?=null

    fun showTabPop() {
        var tabPop = TabPop(this@MainActivity)
        tabPop.createPop()
        tabPop.setOnDismissListener(object :OnDismissListener(){
            override fun onDismiss() {
                updateTabCount()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        morePop?.updateUI()
    }

    private fun updateBottomClick(type:String) {
        if (type== JumpConfig.JUMP_SEARCH || type == JumpConfig.JUMP_WEB){
            acBinding.ivClear.setOneClick {
                var data = JumpDataManager.getCurrentJumpData(tag = "updateBottomClick")
                data.apply {
                    jumpTitle = APP.instance.getString(R.string.app_home)
                    jumpType = JumpConfig.JUMP_HOME
                    isCurrent = true
                }
                JumpDataManager.updateCurrentJumpData(data,tag = "updateBottomClick")
                APP.jumpLiveData.postValue(data)
            }
            acBinding.ivClear.setImageResource(R.mipmap.ic_home)
        }else {
            acBinding.ivClear.setOneClick {
                clearData()
            }
            acBinding.ivClear.setImageResource(R.mipmap.ic_web_clear)
        }
    }

    fun clearData(){
        ClearPop(this).createPop {
            CacheManager.clearAll()
            APP.jumpLiveData.postValue(JumpDataManager.getCurrentJumpData(tag="清理数据后获取当前item"))
        }
    }

    fun loadNews(){
        viewModel.getNewsData()
    }

    fun updateBottom(showBack:Boolean,showNext:Boolean,jumpData:JumpData?=null,tag:String) {
        AppLogs.dLog(acTAG,"updateBottom:${tag}")
        acBinding.apply {
            if(showNext){
                if (jumpData?.nextJumpUrl.isNullOrEmpty()){
                    ivRight.isEnabled = false
                }else{
                    ivRight.isEnabled = true
                    ivRight.setOneClick {
                        jumpData?.jumpUrl = jumpData?.nextJumpUrl?:""
                        jumpData?.nextJumpUrl = ""
                        jumpData?.jumpType = JumpConfig.JUMP_WEB
                        APP.jumpLiveData.postValue(jumpData)
                    }
                }
            }else{
                ivRight.isEnabled = false
            }
            if (showBack){
                ivLeft.setOneClick {
                    var mMainNavFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_view)
                    var currentFragmentInstance = mMainNavFragment?.getChildFragmentManager()?.getPrimaryNavigationFragment();
                    if (currentFragmentInstance != null && currentFragmentInstance is WebFragment) {
                        currentFragmentInstance.goBack()
                    }else{
                        onBackPressed()
                    }
                }
                ivLeft.isEnabled = true
            }else{
                ivLeft.isEnabled = false
            }
        }
//        updateBottomClick()
    }

    override fun setShowView() {
        if (CacheManager.browserStatus == 1){
            CacheManager.browserStatus = 0
        }
        navController = Navigation.findNavController(this@MainActivity,R.id.fragment_view)
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
        acBinding.llControl.visibility = View.VISIBLE
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


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        var mMainNavFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_view)
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

    fun updateTabCount() {
        acBinding.tvTabCount.text = "${JumpDataManager.getBrowserTabList(CacheManager.browserStatus,tag ="MainActivity 更新tab 数量").size}"
    }

}