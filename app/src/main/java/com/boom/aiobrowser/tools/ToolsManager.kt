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
import com.boom.aiobrowser.tools.clean.UriManager
import com.boom.aiobrowser.tools.clean.UriManager.URI_SEPARATOR
import com.boom.aiobrowser.ui.isAndroid11
import com.boom.aiobrowser.ui.isAndroid12
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
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

fun Activity.openFile(path: String, isForceChoose: Boolean = false) {
    if (path.isApk()) {
        ToastUtils.showShort(getString(R.string.app_file_can_not_open))
        return
    }
    val newUri = getFinalUriFromPath(path) ?: Uri.parse(path)
    Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(newUri, getMimeTypeFromUri(newUri))
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        runCatching {
            if (resolveActivity(packageManager) != null) {
                if (isForceChoose) startActivity(Intent.createChooser(this@apply, "Open with")) else startActivity(this@apply)
            } else ToastUtils.showShort(getString(R.string.app_file_can_not_open))
        }.onFailure {
            ToastUtils.showShort(it.message ?: "")
        }
    }
}

fun Activity.shareUseIntent(path: String) {
    val newUri = getFinalUriFromPath(path) ?: Uri.parse(path)
    Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_STREAM, newUri)
        type = getMimeTypeFromUri(newUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        runCatching {
            if (resolveActivity(packageManager) != null) {
                startActivity(Intent.createChooser(this@apply, getString(R.string.app_share_with)))
            } else ToastUtils.showShort(getString(R.string.app_no_app))
        }.onFailure {
            AppLogs.eLog("shareUseIntent",it.stackTraceToString())
            ToastUtils.showShort(R.string.app_file_no_support)
        }
    }
}

fun Context.getFinalUriFromPath(path: String): Uri? {
    return try {
        getPublicUri(path, BuildConfig.APPLICATION_ID)
    } catch (e: Exception) {
        null
    }
}



fun Context.getPublicUri(path: String, applicationId: String): Uri? {
    val uri = Uri.parse(path)
    return if (uri.scheme == "content") uri
    else {
        val newPath = if (uri.toString().startsWith("/")) uri.toString() else uri.path
        if (newPath.isNullOrEmpty()) return null
        getFilePublicUri(File(newPath), applicationId)
    }
}

fun Context.getFilePublicUri(file: File, applicationId: String): Uri? {
    var uri = getMediaContentUri(file)
    if (uri == null) uri = FileProvider.getUriForFile(this, "$applicationId.provider", file)
    return uri
}


fun Context.getMediaContentUri(file: File): Uri? {
    var extension = FileUtils.getFileExtension(file.name)
    val uri = when {
        extension.isImage() -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        extension.isVideo() -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        extension.isAudio() -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        else -> MediaStore.Files.getContentUri("external")
    }
    return getMediaContent(file.path, uri)
}

fun Context.getMediaContent(path: String, uri: Uri): Uri? {
    val projection = arrayOf(MediaStore.Images.Media._ID)
    val selection = MediaStore.Images.Media.DATA + "= ?"
    val selectionArgs = arrayOf(path)
    var cursor: Cursor? = null
    try {
        cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
        if (cursor?.moveToFirst() == true) {
            return Uri.withAppendedPath(uri, cursor.getIntValue(MediaStore.MediaColumns.DISPLAY_NAME).toString())
        }
    } catch (_: Exception) {

    } finally {
        cursor?.close()
    }
    return null
}

fun Cursor.getIntValue(key: String) = getInt(getColumnIndexOrThrow(key))
fun Context.getMimeTypeFromUri(uri: Uri): String {
    return contentResolver.getType(uri) ?: "*/*"
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



fun Context.existsGrantedUriPermission(
    uri: String?,
    activity: Context?
): String? {
    if (activity == null)return null
    AppLogs.dLog("PermissionManager:", "请求权限的原始uri是:$uri")
    //请求权限的原始uri是:content://com.android.externalstorage.documents/document/primary%3AAndroid%2Fdata
    //获取需要授权uri的字符串，还不能匹配，还需要进行处理
    val reqUri = uri?.replace("documents/document/primary", "documents/tree/primary")?:""
    AppLogs.dLog("PermissionManager:", "请求权限处理后的uri(为了进行判断是否已经授权)是:$reqUri")

    //获取已授权并已存储的uri列表
    val uriPermissions = activity!!.contentResolver.persistedUriPermissions
    AppLogs.dLog("PermissionManager:", "已经授权的uri集合是:$uriPermissions")
    //已经授权的uri集合是:[UriPermission {uri=content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata, modeFlags=3, persistedTime=1669980673302}]
    var tempUri: String
    //遍历并判断请求的uri字符串是否已经被授权
    for (uriP in uriPermissions) {
        tempUri = uriP.uri.toString()
        //如果父目录已经授权就返回已经授权
        if (reqUri.matches(Regex(tempUri + URI_SEPARATOR + ".*")) || reqUri == tempUri && (uriP.isReadPermission || uriP.isWritePermission)) {
            AppLogs.dLog("PermissionManager:",  reqUri + "已经授权")
            return tempUri
        }
    }
    AppLogs.dLog("PermissionManager:",  reqUri + "未授权")
    return null
}

/**
 * forceScan = true 则强制用原始Uri 判断 false则用先存的uri判断
 */
fun Context.isCacheGranted(forceScan:Boolean = true):Boolean{
    return if (isAndroid12()){
        XXPermissions.isGranted(this, Permission.PACKAGE_USAGE_STATS)
    }else if (isAndroid11()){
//        if (forceScan){
//            val isGet: Boolean = TextUtils.isEmpty(this.existsGrantedUriPermission(UriManager.URI_STORAGE_SAVED_ANRROID_DATA, this)).not()
//            //这里会对activity重新赋值
//            if (isGet) {
//                return true
//            }else{
//                return false
//            }
//        }else{
//            val uriPermissions = this!!.contentResolver.persistedUriPermissions
//           return uriPermissions.size>0
//        }
        return false
    }else{
        return isStoragePermissionGranted()
    }
}






