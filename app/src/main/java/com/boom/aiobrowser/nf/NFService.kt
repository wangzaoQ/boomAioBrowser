package com.boom.aiobrowser.nf

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.os.IBinder
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.ui.isAndroid12
import com.boom.aiobrowser.ui.isAndroid14

class NFService : Service() {

    override fun onCreate() {
        super.onCreate()
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
        if (NFManager.nfForeground ==null){
            NFShow.getForegroundNF()
        }
        if (isAndroid14()){
            startForeground(NFManager.nfForegroundId,
                NFManager.nfForeground!!, FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        }else{
            startForeground(NFManager.nfForegroundId, NFManager.nfForeground!!)
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