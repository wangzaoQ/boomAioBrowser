package com.boom.aiobrowser.data

class HistoryData {

    companion object{
       const val HISTORY_TITLE = 0
       const val HISTORY_ITEM = 1
    }

    var type = HISTORY_ITEM
    var jumpData:JumpData?=null
    var title:String=""
    fun getItemType(): Int {
        return type
    }

}