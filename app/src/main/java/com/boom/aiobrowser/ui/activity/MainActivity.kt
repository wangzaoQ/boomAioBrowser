package com.boom.aiobrowser.ui.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.databinding.BrowserActivityMainBinding
import com.boom.aiobrowser.model.NewsViewModel
import com.boom.aiobrowser.model.SearchViewModel
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.FragmentManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.ParamsConfig
import com.boom.aiobrowser.ui.fragment.StartFragment
import com.boom.aiobrowser.ui.fragment.WebFragment
import com.boom.aiobrowser.ui.pop.TabPop
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import pop.basepopup.BasePopupWindow.OnDismissListener
import java.lang.ref.WeakReference

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
                    val navOptions = NavOptions.Builder()
                        .setEnterAnim(R.anim.in_alpha)
                        .setExitAnim(R.anim.out_alpha)
                        .build()

                    navController?.navigate(R.id.fragmentWeb, Bundle().apply {
                        putString(ParamsConfig.JSON_PARAMS, toJson(it))
                    },navOptions)
                    updateBottom(true,it)
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
                JumpConfig.JUMP_HOME ->{
                    val navOptions = NavOptions.Builder()
                        .setEnterAnim(R.anim.in_alpha)
                        .setExitAnim(R.anim.out_alpha)
                        .setPopUpTo(R.id.fragmentMain, true) // 将目标Fragment从Back Stack中移除
                        .build()

                    navController?.navigate(R.id.fragmentMain, Bundle().apply {
                        putString(ParamsConfig.JSON_PARAMS, toJson(it))
                    },navOptions)
                    updateBottom(false)
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
        acBinding.tvTabCount.setOnClickListener {
            var tabPop = TabPop(this@MainActivity)
            tabPop.createPop()
            tabPop.setOnDismissListener(object :OnDismissListener(){
                override fun onDismiss() {
                    updateTabCount()
                }
            })
        }
        acBinding.ivClear.setOnClickListener {
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
        }
    }

    fun loadNews(){
        viewModel.getNewsData()
    }

    fun updateBottom(showBack:Boolean,jumpData:JumpData?=null) {
        acBinding.apply {
            var lastJumpData = CacheManager.lastJumpData
            if (jumpData!=null){
                if (jumpData.dataId == lastJumpData?.dataId){
                    CacheManager.lastJumpData = null
                }
            }
            if (CacheManager.lastJumpData == null){
                ivRight.isEnabled = false
            }else{

                ivRight.isEnabled = true
                ivRight.setOneClick {
                    APP.jumpLiveData.postValue(lastJumpData)
                }
            }

            if (showBack){
                ivLeft.setOneClick {
                    var mMainNavFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_view)
                    var currentFragmentInstance = mMainNavFragment?.getChildFragmentManager()?.getPrimaryNavigationFragment();
                    if (currentFragmentInstance != null && currentFragmentInstance is WebFragment) {
                        currentFragmentInstance.goBack()
                    }
                }
                ivLeft.isEnabled = true
            }else{
                ivLeft.isEnabled = false
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
        updateTabCount()
        loadNews()
    }

    fun hideStart() {
        fManager.hideFragment(supportFragmentManager, startFragment!!)
//        acBinding.llControl.visibility = View.VISIBLE
        if (CacheManager.isFirstShowClear){
            acBinding.tvClearData.visibility = View.VISIBLE
            val scaleXAnimator = ObjectAnimator.ofFloat(acBinding.tvClearData, "scaleX", 1.0f, 1.2f,1.0f)
            val scaleYAnimator = ObjectAnimator.ofFloat(acBinding.tvClearData, "scaleY", 1.0f, 1.2f,1.0f)
            val set = AnimatorSet()
            set.play(scaleXAnimator).with(scaleYAnimator)
            set.setDuration(1000)
            set.start()
            acBinding.root.postDelayed({
                acBinding.tvClearData.visibility = View.GONE
            },1000)
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