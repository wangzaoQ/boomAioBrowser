package com.boom.aiobrowser.tools

import android.content.Context.TELEPHONY_SERVICE
import android.os.Bundle
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat.getSystemService
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADDataManager
import com.boom.aiobrowser.firebase.FirebaseConfig
import com.boom.aiobrowser.firebase.FirebaseManager
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointManager.PointCallback
import com.boom.aiobrowser.point.PointValueKey
import okhttp3.Response


object UIManager {

    var TAG = APP.instance.TAG

    var cloakValue = ""

    fun getCloakData(): String {
        if (cloakValue.isNotEmpty()) {
            if (cloakValue.equals("orgasm", true)) {
                AppLogs.dLog(TAG, "cloak 命中正常用户")
                return "B"
            } else {
                AppLogs.dLog(TAG, "cloak 命中黑名单用户")
                return "A"
            }
        }
        return "A"
    }

    /**
     * 1命中cloak为黑名单，AF/refer归因 为非自然量 =A包，归因数据存本地，下次冷启动如cloak变为白名单则正常走校验逻辑=B包
     * 2相同逻辑命中黑名单，AF/refer归因 为自然量 =A包，下次冷启动如cloak变为白名单则正常走校验逻辑=A包
     * 3首次命中cloak为白名单，AF/refer归因 为自然量 =A包，数据存本地
     * 4首次命中cloak为白名单，AF/refer归因 为非自然量 =B包，数据存本地
     * 5如进入app过程中数据返回较慢，当返回上面对应结果时需在应用内切换A/B
     */


    var isSend = false

    fun isBuyUser(): Boolean {
        if (CacheManager.isBUser) {
            sendBPoint()
            return true
        } else {
            var cloakData = getCloakData()
            var referData = getReferData()
            if (cloakData == "A" && referData == "A") {
                AppLogs.dLog(TAG, "黑名单用户:cloakData A referData A")
                return false
            } else if (cloakData == "B" && referData == "A") {
                AppLogs.dLog(TAG, "黑名单用户:cloakData B referData A")
                return false
            } else if (cloakData == "A" && referData == "B") {
                AppLogs.dLog(TAG, "黑名单用户:cloakData A referData B")
                return false
            } else if (cloakData == "B" && referData == "B") {
                AppLogs.dLog(TAG, "正常用户:cloakData B referData B")
                CacheManager.isBUser = true
                FirebaseManager.getADConfig()
                sendBPoint()
                if (CacheManager.isAUser) {
                    PointEvent.posePoint(PointEventKey.user_a_b,Bundle().apply {
                        putLong(PointValueKey.load_time,(System.currentTimeMillis()-CacheManager.AUserTime)/1000)
                    })
                }
                AioADDataManager.preloadAD(ADEnum.DEFAULT_AD,"定位到b包用户")
                return true
            }
        }
        return false
    }

    private fun sendBPoint() {
        if (CacheManager.isSendB.not() && isSend.not()) {
            isSend = true
            PointEvent.posePoint(PointEventKey.user_source, Bundle().apply {
                putString("from", "b")
            }, callback = object : PointCallback {
                override fun onSuccess(response: Response) {
                    isSend = false
                    CacheManager.isSendB = true
                }

                override fun onFailed() {
                    super.onFailed()
                    isSend = false
                }
            })
        }
    }

    private fun getReferData(): String {
        var refer = CacheManager.installRefer
        var config = FirebaseConfig.referConfig
        var configList = config.split(",")
        var index = -1
        for (i in 0 until configList.size) {
            if (refer.contains(configList.get(i), true)) {
                index = i
                break
            }
        }
        if (index >= 0) {
            AppLogs.dLog(TAG, "refer 命中买量 match:${configList.get(index)}")
            return "B"
        }
        var buyAdjust = CacheManager.adJustFrom.contains("Organic", true).not()
        if (buyAdjust) {
            AppLogs.dLog(TAG, "buyAdjust 命中买量 match:${buyAdjust}")
            return "B"
        }
        var buyAF = CacheManager.afFrom.equals("Organic", true).not()
        if (buyAF) {
            AppLogs.dLog(TAG, "buyAF 命中买量 match:${buyAF}")
            return "B"
        }
        AppLogs.dLog(TAG, "归因未命中买量当前为非买量用户")
        return "A"
    }

    fun isSpecialUsers(jumpNoAD:Boolean=false):Boolean{
        var isSubscribe = CacheManager.isSubscribeMember
        if (isSubscribe){
            AppLogs.dLog(TAG, "当前用户是订阅会员")
            return true
        }
        var data = CacheManager.pointsData
        if ((System.currentTimeMillis()-data.tempVipDuration)<=data.tempVipStartTime){
            AppLogs.dLog(TAG, "当前用户是临时vip")
            return true
        }
        if (jumpNoAD.not()){
            if ((System.currentTimeMillis()-data.tempNoADDuration)<=data.tempNoADStartTime){
                AppLogs.dLog(TAG, "当前用户使用了临时免ad")
                return true
            }
        }
        return false
    }
}