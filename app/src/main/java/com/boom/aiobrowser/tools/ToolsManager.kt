package com.boom.aiobrowser.tools

import android.app.ActivityManager
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.text.TextUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.tools.web.WebScan
import com.boom.aiobrowser.ui.isAndroid11
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import kotlinx.coroutines.Job
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.LinkedList
import java.util.Locale


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

fun stringToMap(content:String):Map<String,String>{
    val gson = Gson()
    val mapType = object : TypeToken<Map<String?, String?>?>() {}.getType()
    return gson.fromJson(content, mapType)
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
        CacheManager.adLastTime = 0
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

fun getUrlIcon(url:String):Any{
    runCatching {
        if (WebScan.isTikTok(url)){
            return R.mipmap.ic_tt
        }else{
            var uri = Uri.parse(url)
            var iconUrl = "${uri.scheme}://${uri.host}/favicon.ico"
//            GlideManager.loadImg(mainFragment,ivBrowser,iconUrl, errorId = R.mipmap.ic_default_browser_icon)
            return iconUrl
        }
    }.onFailure {
    }
    return ""
}

fun getUrlSource(url:String):String{
    runCatching {
        if (WebScan.isPornhub(url)){
            return "https://www.pornhub.com/"
        }
    }.onFailure {
    }
    return "https://www.tiktok.com/"
}


/**
 * 将输入流转换成字符串
 *
 * @param inputStream
 * @return
 * @throws IOException
 */
@Throws(IOException::class)
fun inputStream2Byte(inputStream: InputStream): String? {
    val bos = ByteArrayOutputStream()

    val buffer = ByteArray(1024)
    var len = -1

    while ((inputStream.read(buffer).also { len = it }) != -1) {
        bos.write(buffer, 0, len)
    }
    bos.close()
    //指定编码格式为UIT-8
    return String(bos.toByteArray(), charset("UTF-8"))
}

/**
 * 获取剪贴板的文本
 *
 * @return 剪贴板的文本
 */
/**
 * 获取系统剪贴板内容
 */
fun getClipContent(): String {
    val manager = APP.instance
        .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    if (manager != null) {
        if (manager.hasPrimaryClip() && manager.primaryClip!!.itemCount > 0) {
            val addedText = manager.primaryClip!!.getItemAt(0).text
            val addedTextString = addedText.toString()
            if (!TextUtils.isEmpty(addedTextString)) {
                return addedTextString
            }
        }
    }
    return ""
}

/**
 * 清空剪贴板内容
 */
fun clearClipboard() {
    val manager = APP.instance
        .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    if (manager != null) {
        try {
            manager.setPrimaryClip(manager.primaryClip!!)
            manager.text = null
        } catch (e: Exception) {

        }
    }
}







