package com.boom.aiobrowser.data

open class ViewItem {

    companion object{
        const val TYPE_PARENT = 0
        const val TYPE_CHILD = 1

    }

    open var dataType :Int?=0
    open var enableChecked = true

}