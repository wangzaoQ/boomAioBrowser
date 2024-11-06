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
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.databinding.BrowserActivityMainBinding
import com.boom.aiobrowser.model.NewsViewModel
import com.boom.aiobrowser.nf.NFManager
import com.boom.aiobrowser.other.ShortManager
import com.boom.aiobrowser.point.PointValue
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.BrowserManager
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.FragmentManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.tools.inputStream2Byte
import com.boom.aiobrowser.tools.jobCancel
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.ParamsConfig
import com.boom.aiobrowser.ui.fragment.MainRootFragment
import com.boom.aiobrowser.ui.fragment.StartFragment
import com.boom.aiobrowser.ui.fragment.WebFragment
import com.boom.aiobrowser.ui.isAndroid12
import com.boom.aiobrowser.ui.pop.DefaultPop
import com.boom.aiobrowser.ui.pop.DownloadVideoGuidePop
import com.boom.aiobrowser.ui.pop.MorePop
import com.boom.aiobrowser.ui.pop.NFGuidePop
import com.boom.aiobrowser.ui.pop.ProcessingTextPop
import com.boom.aiobrowser.ui.pop.TabPop
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import pop.basepopup.BasePopupWindow.OnDismissListener
import java.lang.ref.WeakReference
import java.util.LinkedList


class MainActivity : BaseActivity<BrowserActivityMainBinding>() {




    val fManager by lazy {
        FragmentManager()
    }


    var startFragment :StartFragment?=null

    override fun getBinding(inflater: LayoutInflater): BrowserActivityMainBinding {
        return BrowserActivityMainBinding.inflate(layoutInflater)
    }


    override fun setListener() {

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
    }


    var nfTo = 0
    var nfData:String =""
    var enumName:String =""

    override fun setShowView() {
        APP.instance.isHideSplash = false
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
        nfTo = intent.getIntExtra(ParamsConfig.NF_TO,0)
        nfData = intent.getStringExtra(ParamsConfig.NF_DATA)?:""
        enumName = intent.getStringExtra(ParamsConfig.NF_ENUM_NAME)?:""
        NFManager.clickPoint(nfData,nfTo,enumName)
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
                fManager.addFragmentTag(supportFragmentManager,MainRootFragment(),R.id.fragmentMain,"MainFragment")
            }
//            CacheManager.videoDownloadTempList = mutableListOf()
        },500)
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
        showForeground()
    }

    var foregroundJob:Job?=null

    private fun showForeground() {
        if (isAndroid12()){
            foregroundJob.jobCancel()
            foregroundJob = addLaunch(success = {
                while (APP.instance.lifecycleApp.isBackGround()){
                    delay(1000)
                }
                runCatching {
                    NFManager.startForeground("mainActivity")
                }
            }, failBack = {})
        }
    }

    fun hideStart(isNormal: Boolean) {
        APP.instance.isHideSplash = true
        if (enumName.isNullOrEmpty().not()){
            when (enumName) {
                NFEnum.NF_DOWNLOAD_VIDEO.menuName -> {
                    var data = getBeanByGson(nfData,VideoDownloadData::class.java)
                    // 0 进度中点击 1 失败点击 2成功点击  3 成功点击观看视频
                    if (nfTo == 3){
                        jumpActivity<VideoPreActivity>(Bundle().apply {
                            putString("video_path", toJson(data))
                        })
                    }else{
                        startActivity(Intent(this,DownloadActivity::class.java).apply {
                            putExtra("fromPage","nf_download")
                        })
                    }
                }
                NFEnum.NF_SEARCH_VIDEO.menuName->{
                    if (nfTo == 1){
                        jumpActivity<SearchActivity>()
                    }else if (nfTo == 4){
                        startActivity(Intent(this,DownloadActivity::class.java).apply {
                            putExtra("fromPage","nf_fix")
                        })
                    }else if (nfTo == 2){
                        jumpActivity<HomeGuideActivity>(Bundle().apply {
                            putString(ParamsConfig.JUMP_FROM,getString(R.string.app_x))
                        })
                    }else if (nfTo == 3){
                        jumpActivity<HomeGuideActivity>(Bundle().apply {
                            putString(ParamsConfig.JUMP_FROM,getString(R.string.app_instagram))
                        })
                    }
                }
                NFEnum.NF_NEWS.menuName,NFEnum.NF_HOT.menuName,NFEnum.NF_NEW_USER.menuName,NFEnum.NF_LOCAL.menuName,NFEnum.NF_EDITOR.menuName,NFEnum.NF_UNLOCK.menuName,NFEnum.NF_NEWS_FCM.menuName->{
                    var data = getBeanByGson(nfData,NewsData::class.java)
                    var jumpData = JumpDataManager.getCurrentJumpData(tag="首页通知新闻跳转")
                    jumpData.apply {
                        jumpUrl= data?.uweek?:""
                        jumpType = JumpConfig.JUMP_WEB
                        jumpTitle = data?.tconsi?:""
                        isJumpClick = true
                    }
                    JumpDataManager.updateCurrentJumpData(jumpData,tag="首页通知新闻跳转")
//                    APP.jumpLiveData.postValue(jumpData)
                }
                ParamsConfig.WIDGET->{
                    if (nfTo == 1){
                        var data = getBeanByGson(nfData,NewsData::class.java)
                        var jumpData = JumpDataManager.getCurrentJumpData(tag="首页widget新闻跳转")
                        jumpData.apply {
                            jumpUrl= data?.uweek?:""
                            jumpType = JumpConfig.JUMP_WEB
                            jumpTitle = data?.tconsi?:""
                            isJumpClick = true
                        }
                        JumpDataManager.updateCurrentJumpData(jumpData,tag="首页widget新闻跳转")
                    }else if (nfTo == 0){
                        jumpActivity<SearchActivity>()
                    }
                }
                else -> {}
            }
        }
        if (APP.instance.shareText.isNotEmpty()){
            ProcessingTextPop(this).createPop(APP.instance.shareText, PointValue.share){
                WebParseActivity.toWebParseActivity(this@MainActivity,1,APP.instance.shareText)
            }
        }else{

        }

        if (isNormal.not()){
            finish()
            return
        }
        fManager.hideFragment(supportFragmentManager, startFragment!!)
        acBinding.llMainControl.visibility = View.VISIBLE
        var showPopCount = 0
        if (BrowserManager.isDefaultBrowser().not() && CacheManager.isFirstShowBrowserDefault){
            CacheManager.isFirstShowBrowserDefault = false
            var pop = DefaultPop(this@MainActivity)
            pop.setOnDismissListener(object :OnDismissListener(){
                override fun onDismiss() {
                    showPopCount++
                    showDownloadGuide(showPopCount)
                }
            })
            pop.createPop()
        }else{
            showPopCount++
            showDownloadGuide(showPopCount)
        }
        if (XXPermissions.isGranted(APP.instance, Permission.POST_NOTIFICATIONS).not()){
            //无通知权限
            var guidePop = NFGuidePop(this@MainActivity)
            guidePop.createPop {
                showForeground()
            }
            guidePop.setOnDismissListener(object :OnDismissListener(){
                override fun onDismiss() {
                    showPopCount++
                    showDownloadGuide(showPopCount)
                }
            })
        }else{
            showPopCount++
            showDownloadGuide(showPopCount)
        }

        APP.jumpResumeData.postValue(0)
    }

    private fun showDownloadGuide(showPopCount:Int) {
        if (showPopCount == 2){
            DownloadVideoGuidePop(this).createPop {  }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        AppLogs.dLog(acTAG,"onActivityResult  requestCode:${requestCode}  resultCode:${resultCode}")
    }

}