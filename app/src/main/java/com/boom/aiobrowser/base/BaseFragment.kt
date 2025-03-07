package com.boom.aiobrowser.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.boom.aiobrowser.tools.AppLogs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.actor

/**
 * 新的懒加载,需要viewpager设置BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
 *
 */
abstract class BaseFragment<V : ViewBinding> :Fragment(){
    val fragmentTAG by lazy { javaClass.simpleName }

    lateinit var rootActivity: BaseActivity<*>
    lateinit var fBinding: V
    var timeStay = 0L
    var timeCallBack: ((Long) -> Unit?)? =null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity<*>) {
            rootActivity = context
        }
    }

    abstract fun startLoadData()
    abstract fun setListener()
    open fun setDataListener(){}
    abstract fun setShowView()

    var showView:View?=null


    abstract fun getBinding(inflater: LayoutInflater, container: ViewGroup?): V

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        AppLogs.dLog(fragmentTAG,"onCreateView")
//        if (showView!=null){
//            AppLogs.dLog(fragmentTAG,"onCreateView1")
//            (showView?.parent as? ViewGroup)?.apply {
//                AppLogs.dLog(fragmentTAG,"onCreateView1.1")
//                removeView(showView!!)
//           }
//        }else{
//            AppLogs.dLog(fragmentTAG,"onCreateView2")
//            fBinding = getBinding(inflater, container)
//            AppLogs.dLog(fragmentTAG,"setShowView")
//            setShowView()
//            AppLogs.dLog(fragmentTAG,"setListener")
//            setListener()
//            AppLogs.dLog(fragmentTAG,"setDataListener")
//            setDataListener()
//            showView = fBinding.root
//        }
        AppLogs.dLog(fragmentTAG,"onCreateView2")
        fBinding = getBinding(inflater, container)
        AppLogs.dLog(fragmentTAG,"setShowView")
        setShowView()
        AppLogs.dLog(fragmentTAG,"setListener")
        setListener()
        AppLogs.dLog(fragmentTAG,"setDataListener")
        setDataListener()
        showView = fBinding.root
        return showView
    }


    var isLoad = true
    override fun onResume() {
        super.onResume()
        if (isLoad) {
            startLoadData()
        }
        isLoad = false
        timeStay = System.currentTimeMillis()
        AppLogs.dLog(fragmentTAG,"onResume")
    }

    override fun onPause() {
        super.onPause()
        if (timeStay == 0L)return
        var time = (System.currentTimeMillis()-timeStay)/1000
        timeCallBack?.invoke(time)
        timeStay = 0
        AppLogs.dLog(fragmentTAG,"onPause")
    }

    fun View.setOneClick(action: (View) -> Unit) {
        // 启动一个 actor
//        val eventActor = rootActivity.newsScope.actor<View>(Dispatchers.Main) { // 执行时不接收新来的消息
//            for (event in channel) {
//                action(event) // 将事件传递给 action
//            }
//        }
        // 设置一个监听器来启用 actor
        setOnClickListener {
            action.invoke(it)
        }
    }

    override fun onDestroy() {
        AppLogs.dLog(fragmentTAG,"onDestroy")
        super.onDestroy()
    }

}