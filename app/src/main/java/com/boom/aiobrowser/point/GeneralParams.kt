package com.boom.aiobrowser.point
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.TelephonyManager
import com.blankj.utilcode.util.DeviceUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.BuildConfig
import com.boom.aiobrowser.tools.CacheManager.getID
import org.json.JSONObject
import java.net.URLEncoder
import java.util.Locale
import java.util.UUID

object GeneralParams {
    val telephonyManager: TelephonyManager by lazy {
        APP.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }
    val locale: Locale by lazy {
        Locale.getDefault()
    }


    fun getPackageInfo(): PackageInfo {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return APP.instance.packageManager.getPackageInfo(APP.instance.packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            return APP.instance.packageManager.getPackageInfo(APP.instance.packageName, 0)
        }
    }


    fun urlEncoder(str:String):String{
        return URLEncoder.encode(str,"UTF-8")
    }

    fun getGenericParams(): JSONObject {
        return JSONObject().apply {
            put("jungian",JSONObject().apply {
                put("paycheck", Build.VERSION.RELEASE)
                put("magma", BuildConfig.APPLICATION_ID)
                put("allotted", System.currentTimeMillis())
                put("hackle", UUID.randomUUID().toString())
            })
            put("bush",JSONObject().apply {
                put("buckaroo","scylla")
                put("hardy",getID())
            })
            put("orbit",JSONObject().apply {
                put("quebec",Build.MANUFACTURER)
                put("referent",getID())
                put("kidnap",DeviceUtils.getModel())
            })
            put("emphasis",JSONObject().apply {
                put("trait", urlEncoder(BuildConfig.VERSION_NAME))
                put("sought", urlEncoder(APP.instance.GID))
                put("sappy",urlEncoder(telephonyManager.networkOperator))
                put("edgy","${locale.language}_${locale.country}")
            })
        }
    }
}