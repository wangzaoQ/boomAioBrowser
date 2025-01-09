package com.boom.aiobrowser.tools

import android.app.ActivityManager
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.EncodeUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.BuildConfig
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.data.TopicData
import com.boom.aiobrowser.firebase.FirebaseManager
import com.boom.aiobrowser.other.isAndroid11
import com.boom.aiobrowser.tools.web.WebScan
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
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.LinkedList
import java.util.Locale
import java.util.Vector
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.math.min


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

fun <T> getVectorByGson(json: String?, cls: Class<T>?): Vector<T>? {
    runCatching {
        val mList = Vector<T>()
        val array = JsonParser().parse(json).asJsonArray
        if (array != null && array.size() > 0) {
            for (elem in array) {
                mList.add(gson.fromJson(elem, cls))
            }
        }
        return mList
    }.onFailure {
        AppLogs.eLog("gson",it.stackTraceToString())
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
   return FirebaseManager.matchCountry()
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
        CacheManager.dayShowAddWidget = true
        CacheManager.dayShowAddShort = true
        CacheManager.dayShowBattery = true
        CacheManager.dayDownloadCount = 0
        CacheManager.dayFirstDownloadVideoSuccess = true
        CacheManager.dayPreloadCount = 0
        CacheManager.adDayValue = 0.0
        NFEnum.values().forEach {
            CacheManager.saveNFShowLastTime(it.menuName,0)
            CacheManager.saveDayNFShowCount(it.menuName,0)
        }
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

//fun matchCountryShort():String{
//    return when (Locale.getDefault().language) {
//        "pt" -> {
//            "BR"
//        }
//        "ja" -> {
//            "JP"
//        }
//
//        "in" -> {
//            "ID"
//        }
//
//        "ko" -> {
//            "KR"
//        }
//
//        "es" ->{
//            "MX"
//        }
//        "ar"->{
//
//        }
//
//        else -> {
//            "US"
//        }
//    }
//}


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

fun registerDirectory(@NonNull context: Context, cls: Class<*>?, z: Boolean) {
    val packageManager = context.packageManager
    val componentName = ComponentName(context, cls!!)
    val i2 = if (z) {
        1
    } else {
        2
    }
    try {
        if (i2 != packageManager.getComponentEnabledSetting(componentName)) {
            packageManager.setComponentEnabledSetting(componentName, i2, 1)
        }
    } catch (unused: RuntimeException) {
    }
}

fun Context.shareToShop(title: String? = "") {
    runCatching {
        var shareText =
            if (TextUtils.isEmpty(title).not()) {
                "${title}\n${"https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"}\n${APP.instance.getString(R.string.app_share_end)}"
            } else {
                "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
            }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        this.startActivity(intent)
    }
}

fun getTopicDataLan(data: TopicData): String {
    var topic = ""
    var language = Locale.getDefault().language
    data.lscrat?.forEachIndexed { index, data ->
        if (data.lperfo == language){
            topic = data.tcry?:""
        }
    }
    if (topic.isNullOrEmpty()){
        topic = data.nsand?:""
    }
    return topic
}

fun <T> partitionList(list: List<T>, size: Int): List<List<T>> {
    val totalSize = list.size
    val numPartitions = (totalSize + size - 1) / size // 计算分割后的子集合数量
    val partitions: MutableList<List<T>> = ArrayList(numPartitions) // 提前分配空间

    var i = 0
    while (i < totalSize) {
        partitions.add(
            list.subList(
                i,
                min((i + size).toDouble(), totalSize.toDouble()).toInt()
            )
        )
        i += size
    }
    return partitions
}
fun MutableList<String>.getNewsTopic():String{
    var builder = StringBuilder()
    forEach {
        builder.append(it).append(",")
    }
    var topic = builder.toString()
    if (topic.isNullOrEmpty().not()){
        topic = topic.substring(0, topic.length - 1)
    }
    return topic
}

fun extractDomain(url: String): MutableList<String> {
  runCatching {
      // 解析 URL
      val url = URL(url)
      // 获取主机部分（如：www.google.com.hk）
      val host = url.host
      // 处理主机部分，剔除 "www." 和子域名
      val parts = host.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
          .toTypedArray()
      // 如果有更多部分，假设顶级域名后面是主域名
      // 例如：google.com.hk -> parts = ["google", "com", "hk"]
      return parts.toMutableList()
  }
    return mutableListOf<String>()
}


/**
 * AES ECB 加密
 *
 * @param message 需要加密的字符串
 * @param key     密匙
 * @return 返回加密后密文，编码为base64
 */
//@RequiresApi(Build.VERSION_CODES.O)
//fun encryptECB(message: String, key: String?): String? {
//    val cipherMode = "AES/ECB/PKCS5Padding"
//    val charsetName = "UTF-8"
//    try {
//        val content = message.toByteArray(charset(charsetName))
//        val keyByte = Base64.getDecoder().decode(key)
//        val keySpec = SecretKeySpec(keyByte, "AES")
//        val cipher = Cipher.getInstance(cipherMode)
//        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
//        val data = cipher.doFinal(content)
//        val encoder = Base64.getEncoder()
//        return encoder.encodeToString(data)
//    } catch (e: java.lang.Exception) {
//        e.printStackTrace()
//    }
//    return null
//}

fun String.encryptECB(key: String): String {
    val key = key
    val cipherMode = "AES/ECB/PKCS5Padding"
    val content = this.toByteArray()
    val keyByte = EncodeUtils.base64Decode(key)
    val keySpec = SecretKeySpec(keyByte, "AES")
    val cipher = Cipher.getInstance(cipherMode)
    cipher.init(Cipher.ENCRYPT_MODE, keySpec)
    val data = cipher.doFinal(content)
    return EncodeUtils.base64Encode2String(data)

}



