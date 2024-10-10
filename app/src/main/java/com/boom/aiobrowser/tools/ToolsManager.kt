package com.boom.aiobrowser.tools

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.BuildConfig
import com.boom.aiobrowser.R
import com.boom.aiobrowser.tools.clean.FileFilter.isApk
import com.boom.aiobrowser.tools.clean.FileFilter.isAudio
import com.boom.aiobrowser.tools.clean.FileFilter.isImage
import com.boom.aiobrowser.tools.clean.FileFilter.isVideo

import com.boom.aiobrowser.ui.isAndroid11
import com.boom.aiobrowser.ui.isAndroid12
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import kotlinx.coroutines.Job
import java.io.File
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.LinkedList
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


val gson: Gson by lazy {
    GsonBuilder()
        .create()
}

/** 将Json数据解析成相应的映射对象列表  */
fun <T> getListByGson(json: String?, cls: Class<T>?): MutableList<T>? {
    runCatching {
        val mList = ArrayList<T>()
        val array = JsonParser().parse(json).asJsonArray
        if (array != null && array.size() > 0) {
            for (elem in array) {
                mList.add(gson.fromJson(elem, cls))
            }
        }
        return mList
    }.onFailure {
        AppLogs.eLog("gson", it.stackTraceToString())
    }
    return null
}

/** 将Json数据解析成相应的映射对象列表  */
fun <T> getLinkedListByGson(json: String?, cls: Class<T>?): LinkedList<T>? {
    runCatching {
        val mList = LinkedList<T>()
        val array = JsonParser().parse(json).asJsonArray
        if (array != null && array.size() > 0) {
            for (elem in array) {
                mList.add(gson.fromJson(elem, cls))
            }
        }
        return mList
    }.onFailure {
        AppLogs.eLog("gson", it.stackTraceToString())
    }
    return null
}


fun <T> getBeanByGson(jsonData: String?, type: Class<T>?): T? {
    runCatching {
        return gson.fromJson<T>(jsonData, type)!!
    }.onFailure {
        AppLogs.eLog("gson", it.stackTraceToString())
    }
    return null
}

fun getMapByGson(jsonData: String):HashMap<String,Any>?{
    runCatching {
        val mapType = object : TypeToken<HashMap<String?, Any?>?>() {}.getType()
        return  gson.fromJson(jsonData, mapType)
    }.onFailure {
        AppLogs.eLog("gson", it.stackTraceToString())
    }
    return null
}

fun toJson(data: Any?): String {
    runCatching {
        return gson.toJson(data)
    }.onFailure {
        AppLogs.eLog("gson", it.stackTraceToString())
    }
    return ""
}

fun decryptNet(bytes: ByteArray?): ByteArray? {
    if (bytes == null) {
        return null
    }
    val key = 88
    for (i in bytes.indices) {
        bytes[i] = (bytes[i].toInt() xor key).toByte()
    }
    return bytes
}

fun Job?.jobCancel() {
    runCatching {
        if (this == null)return
        if (this.isCancelled.not()) {
            this.cancel()
        }
    }.onFailure {
        AppLogs.eLog("JobCancel", it.stackTraceToString())
    }
}

// 返回国家缩写，例如 "US" 表示美国，"CN" 表示中国
fun getCurrentCountryCode(): String {
    val locale = Locale.getDefault()
    if (locale.country == "CN"){
        return "US"
    }else{
        return locale.country
    }
}


fun isOtherPkg(context: Context): Boolean {
    runCatching {
        val pid = android.os.Process.myPid()
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processName = manager.runningAppProcesses.find { it.pid == pid }?.processName ?: ""
        return context.packageName != processName
    }
    return false
}


fun appDataReset(){
    var todayDate = CacheManager.saveDay
    val tmpDate = SimpleDateFormat("yyyyMMdd").format(Date(System.currentTimeMillis())).toString()
    if (todayDate != tmpDate) {
        AppLogs.dLog(APP.instance.TAG,"每日数据重置")
        CacheManager.clickEveryDay = 0
        CacheManager.showEveryDay = 0
        CacheManager.saveDay = tmpDate
    }
}


fun Context.isStoragePermissionGranted(): Boolean {
    return XXPermissions.isGranted(this, Permission.Group.STORAGE)
}

fun Context.isManageAllFilesGranted(): Boolean {
    return XXPermissions.isGranted(this, Permission.MANAGE_EXTERNAL_STORAGE)
}

fun Context.isStorageGranted(): Boolean {
    return if (isAndroid11()) isManageAllFilesGranted()
    else isStoragePermissionGranted()
}






