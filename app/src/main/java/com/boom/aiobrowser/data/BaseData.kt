package com.boom.aiobrowser.data

open class BaseData {
    var dataId = System.currentTimeMillis()
    //跳转类型
    var jumpType = ""
    var jumpTitle = ""
    var jumpUrl = ""
    //是否选中
    var isCurrent = false
    var autoDownload = false

    var updateTime = System.currentTimeMillis()

//    var canGoBack = true
}