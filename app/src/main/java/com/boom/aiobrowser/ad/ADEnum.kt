package com.boom.aiobrowser.ad

import com.boom.aiobrowser.ad.AioADDataManager.LOAD_STATUS_START
import com.boom.aiobrowser.data.AioRequestData

enum class ADEnum(var adName: String = "", var adLoadStatus: Int = LOAD_STATUS_START,adShowType:Int,
                  var adRequestList: MutableList<AioRequestData> = mutableListOf()) {
    LAUNCH_AD("aobws_launch",LOAD_STATUS_START,0),
    INT_AD("aobws_main_one",LOAD_STATUS_START,0),
    BANNER_AD_NEWS_DETAILS_TOP("aobws_ban_newtp",LOAD_STATUS_START,0),
    DEFAULT_AD("aobws_refer_nat",LOAD_STATUS_START,1),
    REWARD_AD("aobws_local_reward",LOAD_STATUS_START,1)
}


