package com.boom.aiobrowser.tools

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.activity.MainActivity
import java.lang.ref.WeakReference

object JumpDataManager {

    val TAG = "JumpDataManager"

    fun clearAllTab(){
        CacheManager.tabDataListNormal = mutableListOf()
        CacheManager.tabDataListPrivate = mutableListOf()
        CacheManager.browserStatus = 0
        var data = addTab(CacheManager.browserStatus,"清理所有数据后添加")
        APP.jumpLiveData.postValue(data)
    }

     fun addTab(browserStatus:Int,tag:String): JumpData{
         AppLogs.dLog(TAG, "addTab $tag")
         var data = JumpData().apply {
            jumpType = JumpConfig.JUMP_HOME
            jumpTitle = APP.instance.getString(R.string.app_home)
            isCurrent = true
        }
        addBrowserTab(data,browserStatus,true,tag = "tabPop 增加tab")
        return data
    }

    fun updateCurrentJumpData(currentData: JumpData,tag:String){
        AppLogs.dLog(TAG, "updateCurrentJumpData $tag")
        var list = getBrowserTabList(CacheManager.browserStatus,tag)
        var index = -1
        for (i in 0 until list.size){
            if (list.get(i).isCurrent){
                index = i
                break
            }
        }
        if (index == -1)return
        var data = list.get(index)
        data.updateData(currentData)
        saveBrowserTabList(CacheManager.browserStatus,list,tag)
    }

    fun resetSelectedByStatus(updateData: JumpData,resetSelectedByStatus:Int,tag: String){
        var list = getBrowserTabList(resetSelectedByStatus,tag)
        list.forEach {
            it.isCurrent = false
            if (it.dataId == updateData.dataId){
                it.isCurrent = true
            }
        }
        saveBrowserTabList(resetSelectedByStatus,list,tag)
    }

    fun getCurrentJumpData(isReset:Boolean = false,updateTime:Boolean = false,updateData: JumpData?=null,tag:String): JumpData {
        AppLogs.dLog(TAG, "getCurrentJumpData $tag")
        var list = getBrowserTabList(CacheManager.browserStatus,tag)
        var index = -1
        for (i in 0 until list.size){
            if (list.get(i).isCurrent){
                index = i
                break
            }
        }
        if (index == -1){
            return JumpData().apply {
                JumpConfig.JUMP_NONE
            }
        }
        var data = list.get(index)
        if (isReset){
            data.jumpTitle = APP.instance.getString(R.string.app_home)
            data.jumpUrl = ""
            data.jumpType = JumpConfig.JUMP_HOME
        }
        if (updateData!=null){
            data.updateData(updateData)
        }
        if (updateTime){
            data.updateTime = System.currentTimeMillis()
        }
        return data
    }



    fun getBrowserTabList(browserStatus:Int,tag:String):MutableList<JumpData>{
        AppLogs.dLog(TAG, "getBrowserTabList $tag")
        if (browserStatus == 0){
            return CacheManager.tabDataListNormal
        }else{
            return CacheManager.tabDataListPrivate
        }
    }

    fun saveBrowserTabList(status:Int,dataList:MutableList<JumpData>,tag:String){
        AppLogs.dLog(TAG, "saveBrowserTabList $tag  status:${status}")
        if (status == 0){
            CacheManager.tabDataListNormal = dataList
        }else{
            CacheManager.tabDataListPrivate = dataList
        }
    }

    fun addBrowserTab(data:JumpData,status:Int,restSelected:Boolean = false,tag:String){
        AppLogs.dLog(TAG, "addBrowserTab $tag")
        var listNormal = CacheManager.tabDataListNormal
        var listPrivate = CacheManager.tabDataListPrivate
        if (restSelected){
            listNormal.forEach {
                it.isCurrent = false
            }
            listPrivate.forEach {
                it.isCurrent = false
            }
        }
        if (status == 0){
            listNormal.add(data)
            CacheManager.tabDataListNormal = listNormal
        }else{
            listPrivate.add(data)
            CacheManager.tabDataListPrivate = listPrivate
        }
    }


    inline fun <reified T : Activity> Activity.jumpActivity(
        extra: Bundle? = null,
        flags: Int? = null,
        showAnimal:Boolean ?= true
    ) {
        startActivity(getIntent<T>(flags, extra))
        if (showAnimal != false){
            overridePendingTransition(R.anim.in_alpha, R.anim.out_alpha)
        }
    }

    inline fun <reified T : Context> Context.getIntent(flags: Int?, extra: Bundle?): Intent =
        Intent(this, T::class.java).apply {
            flags?.let { setFlags(flags) }
            extra?.let { putExtras(extra) }
        }



    fun toMain(){
        var list = mutableListOf<Activity>()
        for (i in 0 until APP.instance.lifecycleApp.stack.size){
            var currentAc = APP.instance.lifecycleApp.stack.get(i)
            if (currentAc is MainActivity){
                continue
            }else{
                list.add(currentAc)
            }
        }
        list.forEach {
            it.finish()
        }
    }

}