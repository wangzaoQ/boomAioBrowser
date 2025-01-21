package com.boom.aiobrowser.tools

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.load.model.GlideUrl
import okhttp3.OkHttpClient
import java.io.InputStream
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

@GlideModule
public class OkHttpModule: AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        AppLogs.dLog("glide","registerComponents")
        val builder = OkHttpClient.Builder()
        builder.sslSocketFactory(sSLSocketFactory, trustManager)
        val okHttpClient = builder.build()
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(okHttpClient))
    }


    /** 获取一个SSLSocketFactory */
    val sSLSocketFactory: SSLSocketFactory
        get() = try {
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, arrayOf(trustManager), SecureRandom())
            sslContext.socketFactory
        } catch (e: Exception) {
            throw RuntimeException(e)
        }


    /** 获取一个忽略证书的X509TrustManager */
    val trustManager: X509TrustManager
        get() = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) { }
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) { }
            override fun getAcceptedIssuers(): Array<X509Certificate> { return arrayOf() }
        }

}
