package com.boom.aiobrowser.tools

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.boom.aiobrowser.APP
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.Job
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

fun getModel(): String {
    var model = Build.MODEL
    model = model?.trim { it <= ' ' }?.replace("\\s*".toRegex(), "") ?: ""
    return model
}

fun getId(): String {
//    var phoneId = CacheUtils.getString(KEY_CACHE_PHONE_ID)
//    if (phoneId.isNullOrEmpty().not()){
//        NowLogs.dLog("getId","ANDROID_ID:${phoneId}")
//        return phoneId
//    }
//    val id = Settings.Secure.getString(
//        NewsAPP.singleApp.contentResolver,
//        Settings.Secure.ANDROID_ID
//    )
//    NowLogs.dLog("getId","ANDROID_ID:${id}")
//    phoneId = if ("9774d56d682e549c" == id ||"0000000000000000" == id) "" else id
//    if (phoneId.isNullOrEmpty().not()) {
//        CacheUtils.saveString(KEY_CACHE_PHONE_ID,phoneId)
//        return phoneId
//    }
//    phoneId = UUID.randomUUID().toString().replace("-", "")
//    NowLogs.dLog("getId","UUID:${phoneId}")
//    if (phoneId.isNullOrEmpty().not()){
//        CacheUtils.saveString(KEY_CACHE_PHONE_ID,phoneId)
//    }
    return ""
}


/**
 * AES ECB 加密
 *
 * @param message 需要加密的字符串
 * @param key     密匙
 * @return 返回加密后密文，编码为base64
 */
//fun encryptECB(message: String, key: String?): String? {
//    val cipherMode = "AES/ECB/PKCS5Padding"
//    val charsetName = "UTF-8"
//    try {
//        val content = message.toByteArray(charset(charsetName))
//        val keyByte = android.util.Base64.decode(key!!.toByteArray(), android.util.Base64.DEFAULT)
//        val keySpec = SecretKeySpec(keyByte, "AES")
//        val cipher = Cipher.getInstance(cipherMode)
//        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
//        val data = cipher.doFinal(content)
//        return android.util.Base64.encodeToString(data,android.util.Base64.DEFAULT)
//    } catch (e: Exception) {
//        e.printStackTrace()
//    }
//    return null
//}

/**
 * AES ECB 加密
 *
 * @param message 需要加密的字符串
 * @param key     密匙
 * @return 返回加密后密文，编码为base64
 */
@RequiresApi(Build.VERSION_CODES.O)
fun encryptECB(message: String, key: String?): String? {
    val cipherMode = "AES/ECB/PKCS5Padding"
    val charsetName = "UTF-8"
    try {
        val content = message.toByteArray(charset(charsetName))
        val keyByte = Base64.getDecoder().decode(key)
        val keySpec = SecretKeySpec(keyByte, "AES")
        val cipher = Cipher.getInstance(cipherMode)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        val data = cipher.doFinal(content)
        val encoder = Base64.getEncoder()
        return encoder.encodeToString(data)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return null
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






