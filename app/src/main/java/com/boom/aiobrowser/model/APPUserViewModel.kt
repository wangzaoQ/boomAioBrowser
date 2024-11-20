package com.boom.aiobrowser.model

import androidx.lifecycle.MutableLiveData
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.UserData
import com.boom.aiobrowser.net.NetController
import com.boom.aiobrowser.net.NetRequest
import com.boom.aiobrowser.tools.CacheManager
import com.google.gson.JsonObject

class APPUserViewModel: BaseDataModel() {

    var uerLiveData = MutableLiveData<UserData>()

    /**
    "kind": "Google", // kdepen 可选值 Google, Apple
    "token": "ej...", // tedge 第三方认证的 token，若 token 过期会返回HTTP:401
    "account": "g:xj2", // ahole 第三方 id
    "name": "xjx2", // nsand 第三方 user name
    "icon_url": "https://g.com/xj", // icompa 第三方头像 icon url
    "email": "xj@gmail.com" // edetai 第三方邮箱
     */


    fun createUser(user: UserData) {
        loadData(loadBack = {
            var obj = JsonObject()
            obj.apply {
                addProperty("kdepen",user.from)
                addProperty("ahole",user.otherId)
                addProperty("nsand",user.name)
                addProperty("icompa",user.url)
                addProperty("edetai",user.email)
//                addProperty("tedge",user.token)
            }
            var data = NetRequest.request<UserData> { NetController.createUser(obj) }.data
            user.itackl = data?.itackl?:""
            CacheManager.saveUser(user)
            uerLiveData.postValue(user)
        }, failBack = {
        },1)
    }
}