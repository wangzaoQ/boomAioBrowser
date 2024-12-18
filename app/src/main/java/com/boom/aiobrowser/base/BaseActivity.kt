package com.boom.aiobrowser.base

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.KeyboardUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.point.PointValue
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.clearClipboard
import com.boom.aiobrowser.tools.getClipContent
import com.boom.aiobrowser.ui.activity.MainActivity
import com.boom.aiobrowser.ui.activity.WebParseActivity
import com.boom.aiobrowser.ui.pop.LoadingPop
import com.boom.aiobrowser.ui.pop.ProcessingTextPop
import com.boom.newsnow.view.statusbar.StatusBarHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


abstract class BaseActivity<V : ViewBinding> :AppCompatActivity() {
    lateinit var acBinding: V
    var newsScope= MainScope()
    private var status = false

    var acTAG =javaClass.simpleName

    var stayTime = 0L
    var timeResult: ((Long) -> Unit?)? =null

    var life = BaseActivityLife()

    override fun onStart() {
        super.onStart()
        status = false
    }


    var job:Job?=null

    override fun onResume() {
        super.onResume()
        APP.instance.isGoOther = false
        status = true
        stayTime = System.currentTimeMillis()
        if (APP.instance.isHideSplash.not())return
        job?.cancel()
        job = addLaunch(success = {
            while (APP.instance.isHideSplash.not()){
                delay(1000)
            }
            delay(1000)
            if (APP.instance.shareText.isEmpty()){
                var copy = getClipContent()
                AppLogs.dLog(acTAG,"copy:${copy} APP.instance.copyText:${APP.instance.copyText}")
                if (copy.isNullOrEmpty().not() && APP.instance.copyText != copy){
                    APP.instance.copyText = copy
                    if (APP.instance.lifecycleApp.stack.size>0){
                       var topActivity =  APP.instance.lifecycleApp.stack.get(APP.instance.lifecycleApp.stack.size-1)
                        AppLogs.dLog(acTAG,"is MainActivity:${topActivity is MainActivity}")
                        if (topActivity is MainActivity){
                            withContext(Dispatchers.Main){
                                ProcessingTextPop(this@BaseActivity).createPop(copy?:"", PointValue.clipboard){
                                    var index = copy.indexOf("http")
                                    if (index>=0){
                                        copy = copy.substring(index,copy.length)
                                    }
                                    APP.jumpLiveData.postValue(JumpDataManager.addTabToOtherWeb(copy, title = "","复制链接",true))
                                }
                            }
                        }
                    }
                }
            }
        }, failBack = {})
    }

    abstract fun getBinding(inflater: LayoutInflater): V

    override fun onPause() {
        status = false
        if (stayTime == 0L)return
        var time = (System.currentTimeMillis()-stayTime)/1000
        timeResult?.invoke(time)
        stayTime = 0
        super.onPause()
    }

    override fun onStop() {
        status = false
        super.onStop()
    }

    fun getActivityStatus(): Boolean {
        return status && (Lifecycle.State.RESUMED == lifecycle.currentState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val displayMetrics = this.resources.displayMetrics
        (displayMetrics.heightPixels / 779f).let {
            displayMetrics.density = it
            displayMetrics.scaledDensity = it
            displayMetrics.densityDpi = (160 * it).toInt()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window?.let {
                val lp = it.attributes
                lp.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                it.attributes = lp
            }
        }
        acBinding = getBinding(layoutInflater)
        setContentView(acBinding.root)
        updateStatusBar(true)
        setShowView()
        setListener()
        setDataListener()
    }


    abstract fun setListener()
    open fun setDataListener(){}
    abstract fun setShowView()


    fun updateStatusBar(status:Boolean) {
        StatusBarHelper.initStatusBarMode(this, status)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this,if (status) R.color.white else R.color.black)
        }
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
    }


    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {

    }

    var needAnimal = false

    override fun finish() {
        super.finish()
        if (needAnimal){
            // 设置结束动画
            overridePendingTransition(R.anim.in_alpha, R.anim.out_alpha)
        }
    }


    override fun onDestroy() {
        newsScope.cancel()
        super.onDestroy()
    }


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            var v = currentFocus
            if (allowHideKeyboard(v, ev)) {
//                hideKeyBoard(v)
                v?.apply {
                    KeyboardUtils.hideSoftInput(this)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    fun hideKeyBoard(view:View?){
        if (view == null)return
        runCatching {
            var imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private fun allowHideKeyboard(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            var l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            var left = l[0]
            var top = l[1]
            var bottom = top + v.getHeight()
            var right = left + v.getWidth()
            return !(event.x > left && event.x < right
                    && event.y > top && event.y < bottom)
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }


    fun View.setOneClick(action: suspend (View) -> Unit) {
        // 启动一个 actor
        val eventActor = MainScope().actor<View>(Dispatchers.Main) { // 执行时不接收新来的消息
//        val eventActor = GlobalScope.actor<View>(Dispatchers.Main, capacity = Channel.CONFLATED) { // 总是执行最新的消息,会停掉之前的操作
            for (event in channel) {
                action(event) // 将事件传递给 action
            }
        }
        // 设置一个监听器来启用 actor
        setOnClickListener {
            eventActor.trySend(it).isSuccess
        }
    }

    fun addLaunch(success: suspend CoroutineScope.() -> Unit, failBack: suspend CoroutineScope.(errorContent: String) -> Unit?, dispatcher: CoroutineDispatcher?=Dispatchers.IO) :Job{
        return newsScope.launch(dispatcher?:Dispatchers.IO) {
            runCatching {
                success()
            }.onFailure {
                withContext(Dispatchers.Main){
                    failBack(it.stackTraceToString())
                }
            }
        }
    }

    var loadingPop:LoadingPop?=null

    fun showPop(){
        var isShowing = loadingPop?.isShowing?:false
        if (isShowing.not()){
            loadingPop = LoadingPop(this)
            loadingPop!!.createPop()
        }
    }

    fun hidePop(){
        loadingPop?.dismiss()
    }


}