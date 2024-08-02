package com.boom.aiobrowser.net

import com.boom.aiobrowser.data.SessionData
import com.google.gson.annotations.SerializedName

open class NetResponse<T> {
    @field:SerializedName("code") var code: Int?=0
    @field:SerializedName("mtrack") var msg: String?=""
    @field:SerializedName(value = "edear") var session: SessionData?=null
    @field:SerializedName(value = "dgas") var data: T?=null

}