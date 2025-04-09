package com.boom.aiobrowser.nf

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.os.IBinder
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.data.NFEnum
import com.boom.aiobrowser.other.isAndroid12
import com.boom.aiobrowser.other.isAndroid14

class NFService : Service() {

    override fun onCreate() {
        super.onCreate()
        APP.instance.showForeground = true
        showNF()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        APP.instance.showForeground = true
        showNF()
        return if (isAndroid12()){
            START_NOT_STICKY
        }else{
            START_STICKY
        }
    }

    private fun showNF() {
        var nf = NFShow.getForegroundNF()
        if (isAndroid14()){
            startForeground(NFEnum.NF_SEARCH_VIDEO.position,
                nf!!, FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        }else{
            startForeground(NFEnum.NF_SEARCH_VIDEO.position, nf!!)
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
    override fun onDestroy() {
        APP.instance.showForeground = false
        super.onDestroy()
    }

}