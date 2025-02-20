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
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.postDelayed
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADShowManager
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.databinding.BrowserActivityMainBinding
import com.boom.aiobrowser.firebase.FirebaseConfig
import com.boom.aiobrowser.nf.NFManager
import com.boom.aiobrowser.other.JumpConfig
import com.boom.aiobrowser.other.LoginConfig.SIGN_LOGIN
import com.boom.aiobrowser.other.LoginConfig.SIGN_LOGIN_ONE_TAP
import com.boom.aiobrowser.other.ParamsConfig
import com.boom.aiobrowser.other.ShortManager
import com.boom.aiobrowser.other.isAndroid12
import com.boom.aiobrowser.point.AD_POINT
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointManager.PointCallback
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.BrowserManager
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.FragmentManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.UIManager
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.tools.getListByGson
import com.boom.aiobrowser.tools.inputStream2Byte
import com.boom.aiobrowser.tools.jobCancel
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.fragment.DownloadManageFragment
import com.boom.aiobrowser.ui.fragment.MainFragment
import com.boom.aiobrowser.ui.fragment.MainRootFragment
import com.boom.aiobrowser.ui.fragment.MeFragment
import com.boom.aiobrowser.ui.fragment.NewsHomeFragment
import com.boom.aiobrowser.ui.fragment.StartFragment
import com.boom.aiobrowser.ui.fragment.WebFragment
import com.boom.aiobrowser.ui.pop.DefaultPop
import com.boom.aiobrowser.ui.pop.DownloadVideoGuidePop
import com.boom.aiobrowser.ui.pop.MorePop
import com.boom.aiobrowser.ui.pop.NFGuidePop
import com.boom.drag.EasyFloat
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import okhttp3.Response
import pop.basepopup.BasePopupWindow.OnDismissListener
import java.lang.ref.WeakReference


class MainActivity : BaseActivity<BrowserActivityMainBinding>() {

    val fManager by lazy {
        FragmentManager()
    }

    var startFragment :StartFragment?=null

    val mainRootFragment by lazy {
        MainRootFragment()
    }
    val homeFragment by lazy {
        MainFragment()
    }
    val newsHomeFragment by lazy {
        NewsHomeFragment()
    }
    val downloadHomeFragment by lazy {
        DownloadManageFragment()
    }

    val meFragment by lazy {
        MeFragment()
    }

    private var fragments = ArrayList<Fragment>()

    override fun getBinding(inflater: LayoutInflater): BrowserActivityMainBinding {
        return BrowserActivityMainBinding.inflate(layoutInflater)
    }

    override fun setListener() {
        for ( i in 0 until acBinding.llMainControl.childCount){
            acBinding.llMainControl.getChildAt(i).setOneClick {
                clickIndex(i)
            }
        }
        updateUI(0)
        APP.homeJumpLiveData.observe(this){
            acBinding.fragmentMain.setCurrentItem(it,true)
        }
        APP.firstToDownloadLiveData.observe(this){
            var mMainNavFragment = fragments.get(0).childFragmentManager.findFragmentById(R.id.fragment_view)
            var currentFragmentInstance = mMainNavFragment?.getChildFragmentManager()?.getPrimaryNavigationFragment();
            if (currentFragmentInstance != null && currentFragmentInstance is WebFragment) {
                JumpDataManager.toMain(true)
            }
            acBinding.root.postDelayed(500) {
                acBinding.fragmentMain.setCurrentItem(0, true)
            }
        }
        APP.jumpLiveData.observe(this){
            acBinding.fragmentMain.setCurrentItem(1,true)
        }
        APP.showRateLiveData.observe(this){
            if (ShortManager.allowRate()){
                ShortManager.addRate(WeakReference(this@MainActivity))
            }
        }
        APP.topicJumpData.observe(this){
           var topicList = CacheManager.defaultTopicList
            var index = -1
            for (i in 0 until topicList.size){
                if (it.id == topicList.get(i).id){
                    index = i
                    break
                }
            }
            if (index>=0){
                if (fragments.size == 5){
                    acBinding.fragmentMain.currentItem = 3
                    (fragments.get(3) as? NewsHomeFragment)?.apply {
                        runCatching {
                            fBinding.vp.setCurrentItem(index,true)
                        }
                    }
                }
            }else{
                jumpActivity<TopicListActivity>(Bundle().apply {
                    putString("topic", toJson(it))
                })
            }
        }

    }

    private fun clickIndex(index: Int) {
        acBinding.fragmentMain.setCurrentItem(index, false)
        updateUI(index)
//        var manager = AioADShowManager(this, ADEnum.INT_AD, tag = "底部按钮切换") {}
//        manager.showScreenAD(AD_POINT.aobws_tap_int,false)
    }



    private fun updateUI(index: Int) {
        var endIndex = index
//        if (index == 2 ){
//            endIndex = index+1
//        }
        for ( start in 0 until acBinding.llMainControl.childCount){
            var ll = acBinding.llMainControl.getChildAt(start) as LinearLayoutCompat
            for (i in 0 until ll.childCount){
                ll.getChildAt(i).isEnabled = (start == endIndex).not()
//                if (start == endIndex){
//                    ll.getChildAt(i).scaleX = 1.2f
//                    ll.getChildAt(i).scaleY = 1.2f
//                }else{
//                    ll.getChildAt(i).scaleX = 1.0f
//                    ll.getChildAt(i).scaleY = 1.0f
//                }
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
        var isLaunch = false
        runCatching {
            if (intent != null && Intent.ACTION_MAIN.equals(intent.getAction())) {
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER)) {
                    // 应用是从Launcher启动的
                    isLaunch = true
                }
            }
        }
        NFManager.clickPoint(nfData,nfTo,enumName,isLaunch)

        acBinding.root.postDelayed({
            var count = 0
            for ( i in 0 until APP.instance.lifecycleApp.stack.size){
                var activity = APP.instance.lifecycleApp.stack.get(i)
                if (activity is MainActivity && activity.isFinishing.not() && activity.isDestroyed.not()){
                    count++
                    AppLogs.dLog("testAPP","MainActivity setView 当前count:${count} MainActivity.status  isDestroyed:${activity.isDestroyed}  activity.isFinished:${activity.isFinishing}")
                }
            }
            if (CacheManager.browserStatus == 1){
                CacheManager.browserStatus = 0
            }
            AppLogs.dLog("testAPP","MainActivity setView MainActivity.最终count:${count}")

            if (count == 1){
                acBinding.fragmentMain.apply {
                    fragments.apply {
                        add(downloadHomeFragment)
                        add(mainRootFragment)
                        add(homeFragment)
                        add(newsHomeFragment)
                        add(meFragment)
                    }
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
                            if (position == 1){
                                PointEvent.posePoint(PointEventKey.news,Bundle().apply {
                                    putString(PointValueKey.from_type,if (APP.instance.toNewsFrom == 1)"home_news_more" else "news")
                                },true,object : PointCallback{
                                    override fun onSuccess(response: Response) {
                                        APP.instance.toNewsFrom = 0
                                    }
                                })
                            }else if (position == 2){
                                var isFirst = false
                                if (CacheManager.isFirstShowDownload){
                                    isFirst = true
                                    CacheManager.isFirstShowDownload = false
                                }
                                PointEvent.posePoint(PointEventKey.download_page, Bundle().apply {
                                    putInt(PointValueKey.open_type,if (isFirst) 0 else 1)
                                })
                            }
                            if (position == 1){
                                APP.downloadButtonLiveData.postValue(0)
                            }else{
                                EasyFloat.dismiss(tag = "webPop",true)
                            }
                        }

                        override fun onPageScrollStateChanged(state: Int) {
                        }
                    })
                }
            }
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
        var allowShowPop = true
        // 0 默认  1 browser 2 news
        var jumpType = 0
        if (UIManager.isBuyUser()){
            if (CacheManager.campaignId.isNullOrEmpty().not()){
                var campaignId = CacheManager.campaignId
                if (FirebaseConfig.browserJumpList.contains(campaignId)){
                    //browser
                    jumpType = 1
                }else if (FirebaseConfig.newsJumpList.contains(campaignId)){
                    //news
                    jumpType = 2
                }
            }
            if (jumpType == 0){
                when (FirebaseConfig.defaultUserData?.other?:"") {
                    "news"-> {
                        jumpType = 4
                    }
                    else -> {
                        jumpType = 3
                    }
                }
            }
        }else{
            if (CacheManager.isFirstStart){
                CacheManager.isAUser = true
                CacheManager.AUserTime = System.currentTimeMillis()
            }
        }
        CacheManager.isFirstStart = false
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
                        DownloadActivity.startActivity(this@MainActivity,"nf_download")
                    }
                    allowShowPop = false
                }
                NFEnum.NF_SEARCH_VIDEO.menuName->{
                    if (nfTo == 1){
                        jumpActivity<SearchActivity>(Bundle().apply {
                            putString(PointValueKey.from_type,"app_nf")
                        })
                    }else if (nfTo == 4){
                        DownloadActivity.startActivity(this@MainActivity,"nf_fix")
                    }else if (nfTo == 2){
                        jumpActivity<HomeGuideActivity>(Bundle().apply {
                            putString(ParamsConfig.JUMP_FROM,getString(R.string.app_x))
                        })
                    }else if (nfTo == 3){
                        jumpActivity<HomeGuideActivity>(Bundle().apply {
                            putString(ParamsConfig.JUMP_FROM,getString(R.string.app_instagram))
                        })
                    }
                    allowShowPop = false
                }
                NFEnum.NF_NEWS.menuName,NFEnum.NF_HOT.menuName,NFEnum.NF_NEW_USER.menuName,NFEnum.NF_LOCAL.menuName,NFEnum.NF_EDITOR.menuName,NFEnum.NF_UNLOCK.menuName,NFEnum.NF_NEWS_FCM.menuName,NFEnum.NF_DEFAULT.menuName,NFEnum.NF_TREND.menuName->{
                    var dataList = getListByGson(nfData,NewsData::class.java)
                    var data =  if (dataList.isNullOrEmpty()){
                        getBeanByGson(nfData,NewsData::class.java)
                    }else{
                        dataList[0]
                    }
//                    var jumpData = JumpDataManager.getCurrentJumpData(tag="首页通知新闻跳转")
//                    jumpData.apply {
//                        jumpUrl= data?.uweek?:""
//                        jumpType = JumpConfig.JUMP_WEB
//                        jumpTitle = data?.tconsi?:""
//                        isJumpClick = true
//                    }
//                    JumpDataManager.updateCurrentJumpData(jumpData,tag="首页通知新闻跳转")
//                    APP.jumpLiveData.postValue(jumpData)
                    if (enumName == NFEnum.NF_TREND.menuName && nfTo == 0){
                        jumpActivity<TrendingNewsListActivity>()
                    }else{
                        if (data == null)return
                        if (data.vbreas.isNullOrEmpty()){
                            jumpActivity<WebDetailsActivity>(Bundle().apply {
                                putString(ParamsConfig.JSON_PARAMS, toJson(data))
                            })
                        }else{
                            var videoList = mutableListOf<NewsData>()
                            dataList?.forEach {
                                if (it.vbreas.isNullOrEmpty().not()){
                                    videoList.add(it)
                                }
                            }
                            VideoListActivity.startVideoListActivity(this,0,videoList,enumName,"app_nf")
                        }
                    }
                    allowShowPop = false
                }
                ParamsConfig.WIDGET->{
                    if (nfTo == 1){
                        var data = getBeanByGson(nfData,NewsData::class.java)
                        jumpActivity<WebDetailsActivity>(Bundle().apply {
                            putString(ParamsConfig.JSON_PARAMS, toJson(data))
                        })
                    }else if (nfTo == 0){
                        jumpActivity<SearchActivity>(Bundle().apply {
                            putString(PointValueKey.from_type,"app_widget")
                        })
                    }
                    allowShowPop = false
                }

                else -> {}
            }
        }
        if (APP.instance.shareText.isNotEmpty() && allowShowPop){
//            ProcessingTextPop(this).createPop(APP.instance.shareText, PointValue.share){
//                WebParseActivity.toWebParseActivity(this@MainActivity,1,APP.instance.shareText)
//            }
            var index = APP.instance.shareText.indexOf("http")
            if (index>=0){
                APP.instance.shareText = APP.instance.shareText.substring(index,APP.instance.shareText.length)
            }
            APP.jumpLiveData.postValue(JumpDataManager.addTabToOtherWeb(APP.instance.shareText,title="","分享网页",true))
        }

        if (isNormal.not()){
            finish()
            return
        }
        fManager.hideFragment(supportFragmentManager, startFragment!!)
        acBinding.llMainControl.visibility = View.VISIBLE
        if (jumpType == 1||jumpType==3){
            PointEvent.posePoint(PointEventKey.attribution_download)
            if (jumpType == 3){
                PointEvent.posePoint(PointEventKey.attribution_other,Bundle().apply {
                    putString("from","download")
                })
            }
        }else if (jumpType == 2||jumpType==4){
            PointEvent.posePoint(PointEventKey.attribution_news)
            if (jumpType == 4){
                PointEvent.posePoint(PointEventKey.attribution_other,Bundle().apply {
                    putString("from","news")
                })
            }
            acBinding.fragmentMain.setCurrentItem(3,false)
        }
//        else{
//            PointEvent.posePoint(PointEventKey.attribution_default,Bundle().apply {
//                putString("from",if (UIManager.isBuyUser()) "b" else "a")
//            })
//        }
        var showPopCount = 0
        if (APP.isDebug){
            AppLogs.dLog(acTAG,"首页弹窗判断 isDefaultBrowser：:${BrowserManager.isDefaultBrowser()} " +
                    "isFirstShowBrowserDefault:${CacheManager.isFirstShowBrowserDefault} switchDefaultPop:${FirebaseConfig.switchDefaultPop} allowShowPop:${allowShowPop} jumpType:${jumpType}")
        }
//        if (BrowserManager.isDefaultBrowser().not() && CacheManager.isFirstShowBrowserDefault && ((jumpType > 0 && FirebaseConfig.switchDefaultPop) || jumpType == 0)){
        if (BrowserManager.isDefaultBrowser().not() && CacheManager.isFirstShowBrowserDefault && FirebaseConfig.switchDefaultPop){
            AppLogs.dLog(acTAG,"show browser pop")
            CacheManager.isFirstShowBrowserDefault = false
            var pop = DefaultPop(this@MainActivity)
            pop.setOnDismissListener(object :OnDismissListener(){
                override fun onDismiss() {
                    showPopCount++
                    showDownloadGuide(showPopCount,allowShowPop,jumpType)
                }
            })
            pop.createPop()
        }else{
            showPopCount++
            showDownloadGuide(showPopCount, allowShowPop,jumpType)
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
                    showDownloadGuide(showPopCount, allowShowPop,jumpType)
                }
            })
        }else{
            showPopCount++
            showDownloadGuide(showPopCount, allowShowPop,jumpType)
        }
        APP.jumpResumeData.postValue(if (allowShowPop.not()) 1 else 0)
    }

    private fun showDownloadGuide(showPopCount: Int, allowShowPop: Boolean,jumpType:Int) {
//        if (showPopCount == 2 && (jumpType == 0 || jumpType == 1 || jumpType == 3)){
        if (showPopCount == 2 && FirebaseConfig.switchDownloadGuidePop){
            if (CacheManager.isFirstShowDownloadGuide){
                AppLogs.dLog(acTAG,"允许开启引导弹窗")
                CacheManager.isFirstShowDownloadGuide = false
//                var homeGuidePop = HomeGuidePop(this@MainActivity)
//                homeGuidePop.createPop()
                DownloadVideoGuidePop(this@MainActivity).createPop("pop") {  }
            }else{
                AppLogs.dLog(acTAG,"不允许开启引导弹窗")
                PointEvent.posePoint(PointEventKey.home_page_first)
            }
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
        if (isFinishing) {
            return super.onKeyDown(keyCode, event)
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            var currentItem = acBinding.fragmentMain.currentItem
            if (currentItem == 1 && fragments.isNotEmpty()){
                var mMainNavFragment = fragments.get(1).childFragmentManager.findFragmentById(R.id.fragment_view)
                var currentFragmentInstance = mMainNavFragment?.getChildFragmentManager()?.getPrimaryNavigationFragment();
                if (currentFragmentInstance != null && currentFragmentInstance is WebFragment) {
                    return if (currentFragmentInstance.goBack(keyCode, event)) {
                        AppLogs.dLog(acTAG,"返回 fragment goBack")
                        true
                    } else {
                        AppLogs.dLog(acTAG,"返回 HomeFragment")
                        var manager = AioADShowManager(this, ADEnum.INT_AD, tag = "浏览器结果页返回") {
                            JumpDataManager.toMain(true)
                        }
                        manager.showScreenAD(AD_POINT.aobws_downreturn_int)
                        true
                    }
                }
                AppLogs.dLog(acTAG,"返回 activity super.onKeyDown")
            }
            return try {
                val intent = Intent(Intent.ACTION_MAIN)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.addCategory(Intent.CATEGORY_HOME)
                this.startActivity(intent)
                true
            } catch (e: Throwable) {
                e.printStackTrace()
                super.onKeyDown(keyCode, event)
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        AppLogs.dLog(acTAG,"onActivityResult  requestCode:${requestCode}  resultCode:${resultCode}")
        if (requestCode == SIGN_LOGIN || resultCode == SIGN_LOGIN_ONE_TAP && fragments.size>2 && fragments.get(3) is MeFragment ){
            (fragments.get(3) as MeFragment).result(requestCode, resultCode, data)
        }
    }

    override fun onDestroy() {
        APP.homeJumpLiveData.removeObservers(this)
        APP.firstToDownloadLiveData.removeObservers(this)
        APP.showRateLiveData.removeObservers(this)
        APP.jumpLiveData.removeObservers(this)
        APP.topicJumpData.removeObservers(this)
        APP.downloadPageLiveData.removeObservers(this)
        super.onDestroy()
    }
}