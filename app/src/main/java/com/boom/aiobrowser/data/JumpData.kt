package com.boom.aiobrowser.data

class JumpData : BaseData() {
    fun updateData(currentData: JumpData) {
        this.jumpType = currentData.jumpType
        this.jumpTitle = currentData.jumpTitle
        this.jumpUrl = currentData.jumpUrl
        this.nextJumpUrl = currentData.nextJumpUrl
        this.nextJumpType = currentData.nextJumpType
//        this.isCurrent = currentData.isCurrent
        this.autoDownload = currentData.autoDownload
    }

    var nextJumpUrl:String?=""
    var nextJumpType:String?=""

    var isJumpClick = false
    var imgRes:Int?=0

}