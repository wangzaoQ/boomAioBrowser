package com.boom.aiobrowser.ui.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.databinding.BrowserActivityMainBinding
import com.boom.aiobrowser.model.NewsViewModel
import com.boom.aiobrowser.nf.NFManager
import com.boom.aiobrowser.nf.NFShow
import com.boom.aiobrowser.point.PointValue
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.BrowserManager
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.FragmentManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.clearClipboard
import com.boom.aiobrowser.tools.download.DownloadCacheManager
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.tools.getClipContent
import com.boom.aiobrowser.tools.inputStream2Byte
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.ParamsConfig
import com.boom.aiobrowser.ui.fragment.StartFragment
import com.boom.aiobrowser.ui.fragment.TestWebFragment
import com.boom.aiobrowser.ui.fragment.WebFragment
import com.boom.aiobrowser.ui.pop.DefaultPop
import com.boom.aiobrowser.ui.pop.DisclaimerPop
import com.boom.aiobrowser.ui.pop.DownLoadPop
import com.boom.aiobrowser.ui.pop.MorePop
import com.boom.aiobrowser.ui.pop.ProcessingTextPop
import com.boom.aiobrowser.ui.pop.TabPop
import com.boom.downloader.VideoDownloadManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import pop.basepopup.BasePopupWindow.OnDismissListener
import java.lang.ref.WeakReference
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
                    if (CacheManager.browserStatus == 0){
                        CacheManager.saveRecentSearchData(it)
                    }
                }
                JumpConfig.JUMP_HOME ->{
                    val navOptions = NavOptions.Builder()
                        .setEnterAnim(R.anim.in_alpha)
                        .setExitAnim(R.anim.out_alpha)
//                        .setPopUpTo(R.id.fragmentFile, true) // 将目标Fragment从Back Stack中移除
                        .build()
                    navController?.navigate(R.id.fragmentMain, Bundle().apply {
                        putString(ParamsConfig.JSON_PARAMS, toJson(it))
                    },navOptions)
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
        when (index) {
            0 -> {
//                APP.jumpLiveData.postValue(JumpDataManager.getCurrentJumpData(tag = "点击 home tab ").apply {
//                    jumpType = JumpConfig.JUMP_HOME
//                    jumpTitle = APP.instance.getString(R.string.app_home)
//                })
                updateUI(index)
            }
            1 ->{
//                APP.jumpLiveData.postValue(JumpDataManager.getCurrentJumpData(tag = "点击 file tab").apply {
//                    jumpType = JumpConfig.JUMP_FILE
//                    jumpTitle = APP.instance.getString(R.string.app_files)
//                })
                startActivity(Intent(this@MainActivity,DownloadActivity::class.java).apply {
                    putExtra("fromPage","home_tab")
                })
            }
            2 ->{
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


    var nfTo = ""
    var nfData:VideoDownloadData?= null

    override fun setShowView() {
        APP.instance.shareText = ""
        val action = intent.action //获取Intent的Action
        val type = intent.type //获取Intent的Type
        addLaunch(success = {
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                var finishList = mutableListOf<Activity>()
                for (i in 0 until APP.instance.lifecycleApp.stack.size){
                    var activity = APP.instance.lifecycleApp.stack.get(i)
                    if (activity== this@MainActivity){
                        continue
                    }
                    finishList.add(activity)
                }
                AppLogs.dLog(acTAG,"结束的activity 数量: ${finishList.size}")
                finishList.forEach {
                    it.finish()
                }
                if (type.startsWith("text/")) {
                    //我们这里处理所有的文本类型
                    //一般的文本处理，我们直接显示字符串 ------如图1
                    APP.instance.shareText  = intent.getStringExtra(Intent.EXTRA_TEXT)?:""
                    AppLogs.dLog(acTAG,"获取分享的内容1: $APP.instance.shareText ")
                    if (APP.instance.shareText .isNullOrEmpty()) {
                        //文本文件处理，从Uri中获取输入流，然后将输入流转换成字符串
                        var textUri =intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                        if (textUri != null) {
                            runCatching {
                                contentResolver.openInputStream(textUri)?.apply {
                                    APP.instance.shareText  = inputStream2Byte(this)?:""
                                    AppLogs.dLog(acTAG,"获取分享的内容2: $APP.instance.shareText ")
                                }
                            }
                        }
                    }
                }
            }
        }, failBack = {

        })
        navController = Navigation.findNavController(this@MainActivity,R.id.fragment_view)
        nfTo = intent.getStringExtra(ParamsConfig.NF_TO)?:""
        nfData = getBeanByGson(intent.getStringExtra(ParamsConfig.NF_DATA)?:"",VideoDownloadData::class.java)
        NFManager.clickPoint(nfData)
        acBinding.root.postDelayed({
            var count = 0
            for ( i in 0 until APP.instance.lifecycleApp.stack.size){
                var activity = APP.instance.lifecycleApp.stack.get(i)
                if (activity is MainActivity){
                    count++
                }
            }
            if (CacheManager.browserStatus == 1){
                CacheManager.browserStatus = 0
            }
            if (count == 1){
                loadNews()
            }
            CacheManager.videoDownloadTempList = mutableListOf()
        },500)
        startFragment = StartFragment()
        startFragment?.apply {
            APP.instance.isHideSplash = false
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
    }

    fun hideStart(isNormal: Boolean) {
        APP.instance.isHideSplash = true
        if (nfTo.isNullOrEmpty()){
//            mainFragment.jump()
//            val mMainNavFragment = supportFragmentManager.findFragmentById(R.id.fragmentMain)
//            //获取指定的fragment
//            val fragment = mMainNavFragment!!.childFragmentManager.primaryNavigationFragment
//            if (fragment is MainFragment) {
//                fragment.jump()
//            }
        }else{
            if (nfTo == JumpConfig.JUMP_VIDEO && nfData!=null){
                jumpActivity<VideoPreActivity>(Bundle().apply {
                    putString("video_path", toJson(nfData))
                })
            }else{
                startActivity(Intent(this,DownloadActivity::class.java).apply {
                    putExtra("fromPage","nf_${nfTo}")
                })
            }
        }
        if (APP.instance.shareText.isNotEmpty()){
            ProcessingTextPop(this).createPop(APP.instance.shareText, PointValue.share){
                jumpActivity<WebParseActivity>(Bundle().apply {
                    putString("url",APP.instance.shareText )
                })
            }
        }else{

        }
        if (isNormal.not()){
            finish()
            return
        }
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

}