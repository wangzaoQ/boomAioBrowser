package com.boom.aiobrowser.net

import android.text.TextUtils
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object NetRequest {
    private val TAG = NetRequest::class.java.simpleName

    var keyTag = "session"
    suspend fun <T : Any> request(paramsMap:HashMap<String,Any>?=null,call: suspend () -> NetResponse<T>): NetResponse<T> {
        return withContext(Dispatchers.IO) {
            call.invoke()
        }.apply {
//            if (code != 200) {
////                throw ToastException(msg?:"")
//            }
//            if (data == null){
//            }
            if (paramsMap!=null){
                var sessionKey = paramsMap["sessionKey"]as?String?:""
                if (sessionKey.isNullOrEmpty().not()){
                    AppLogs.dLog("sessionKey","result:${sessionKey}_${keyTag}:${session?.sstop ?: ""}")
                    CacheManager.saveSession(sessionKey,session?.sstop?:"")
                }
            }
            return this
        }
    }

}