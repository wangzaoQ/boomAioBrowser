package com.boom.aiobrowser.data

class JumpData : BaseData() {
    fun updateData(currentData: JumpData) {
        this.jumpType = currentData.jumpType
        this.jumpTitle = currentData.jumpTitle
        this.jumpUrl = currentData.jumpUrl
    }

}