package com.boom.aiobrowser.point

import android.content.Context
import android.text.TextUtils
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager

object Install {

    var TAG = "Install"
    private const val REFER_MAX = 10

    var isLoading = false
    fun requestRefer(context: Context, count: Int = 0, onComplete: (String?) -> Unit) {
        AppLogs.dLog(TAG,"requestRefer 方法执行")
        if (TextUtils.isEmpty(CacheManager.installRefer).not()){
            AppLogs.dLog(TAG,"refer 已有缓存")
            return
        }
        if (count >= REFER_MAX) {
            onComplete(null)
            return
        }
        AppLogs.dLog(TAG,"开始请求 refer")
        runCatching {
            val referrerClient = InstallReferrerClient.newBuilder(context).build()
            referrerClient.startConnection(object : InstallReferrerStateListener {

                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    runCatching {
                        when (responseCode) {
                            InstallReferrerClient.InstallReferrerResponse.OK -> {
                                // Connection established.
                                runCatching {
                                    val response: ReferrerDetails = referrerClient.installReferrer
                                    val referrerUrl: String = response.installReferrer?:""
                                    AppLogs.dLog(TAG, "requestRefer count=$count refer=$referrerUrl")
                                    var refer = referrerClient.installReferrer?.installReferrer ?: ""
                                    if (TextUtils.isEmpty(refer)){
                                        requestRefer(context, count + 1, onComplete)
                                        return
                                    }
                                    AppLogs.dLog(TAG,"install打点")
                                    PointEvent.install(response)
                                }
                            }
                            else -> {
                                requestRefer(context, count + 1, onComplete)
                            }
                        }
                    }
                    runCatching {
                        referrerClient.endConnection()
                    }
                }

                override fun onInstallReferrerServiceDisconnected() {
                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                    AppLogs.eLog(TAG,"onInstallReferrerServiceDisconnected")
                }
            })
        }.onFailure {
            AppLogs.eLog(TAG,it.stackTraceToString())
            requestRefer(context, count + 1, onComplete)
        }
    }
}