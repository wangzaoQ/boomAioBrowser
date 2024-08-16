package com.boom.aiobrowser.ad

import com.boom.aiobrowser.ad.AioADDataManager.LOAD_STATUS_START
import com.boom.aiobrowser.data.AioRequestData

enum class ADEnum(var adName: String = "", var adLoadStatus: Int = LOAD_STATUS_START,
                  var adRequestList: MutableList<AioRequestData> = mutableListOf()) {
    LAUNCH("ad_launch",LOAD_STATUS_START),
}


