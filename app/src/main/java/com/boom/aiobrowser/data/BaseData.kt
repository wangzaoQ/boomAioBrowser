package com.boom.aiobrowser.data

open class BaseData {
    var dataId = System.currentTimeMillis()
    //跳转类型
    var jumpType = ""
    var jumpTitle = ""
    var jumpUrl = ""
    //是否选中 切换tab
    var isCurrent = false
    var autoDownload = false
    var isSelected = false

    var updateTime = System.currentTimeMillis()

//    var canGoBack = true
}