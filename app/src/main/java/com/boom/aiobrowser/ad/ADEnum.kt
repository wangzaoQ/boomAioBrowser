package com.boom.aiobrowser.ad

import com.boom.aiobrowser.ad.AioADDataManager.LOAD_STATUS_START
import com.boom.aiobrowser.data.AioRequestData

enum class ADEnum(var adName: String = "", var adLoadStatus: Int = LOAD_STATUS_START,
                  var adRequestList: MutableList<AioRequestData> = mutableListOf()) {
    LAUNCH_AD("aobws_launch",LOAD_STATUS_START),
    INT_AD("aobws_main_one",LOAD_STATUS_START),
    NATIVE_AD("aobws_detail_bnat",LOAD_STATUS_START),
    BANNER_AD_NEWS_DETAILS_TOP("aobws_ban_newtp",LOAD_STATUS_START),
    BANNER_AD_NEWS_DETAILS("aobws_ban_newin",LOAD_STATUS_START),
}


