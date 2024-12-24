package com.boom.aiobrowser.tools

import android.content.Context.TELEPHONY_SERVICE
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat.getSystemService
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.firebase.FirebaseConfig


object UIManager {

    var TAG = APP.instance.TAG

    /**
     * 1. 先判断cloak
     * 2. refer adjust
     */
    fun isBuyUser(): Boolean {
//        (APP.instance.getSystemService(TELEPHONY_SERVICE) as TelephonyManager)?.apply{
//            if (simState != TelephonyManager.SIM_STATE_READY){
//                AppLogs.dLog(TAG,"无sim卡为A包")
//                return false
//            }
//        }

//        if (APP.isDebug){
//            AppLogs.dLog(TAG,"debug环境 测试为买量用户")
//            return true
//        }
        if (CacheManager.cloakValue == "orgasm"){
            AppLogs.dLog(TAG,"cloak 命中买量")
            return true
        }
        var refer = CacheManager.installRefer
        var config = FirebaseConfig.referConfig
        var configList = config.split(",")
        var index = -1
        for (i in 0 until configList.size){
            if (refer.contains(configList.get(i),true)){
                index = i
                break
            }
        }
        if (index>=0){
            AppLogs.dLog(TAG,"refer 命中买量 match:${configList.get(index)}")
            return true
        }
        var buyAdjust = CacheManager.adJustFrom.equals("Organic", true).not()
        if (buyAdjust){
            AppLogs.dLog(TAG,"buyAdjust 命中买量 match:${buyAdjust}")
            return true
        }
        var buyAF = CacheManager.afFrom.equals("Organic", true).not()
        if (buyAF){
            AppLogs.dLog(TAG,"buyAF 命中买量 match:${buyAF}")
            return true
        }
        AppLogs.dLog(TAG,"未命中买量当前为非买量用户")
        return false
    }
}