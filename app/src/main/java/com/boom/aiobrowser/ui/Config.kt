package com.boom.aiobrowser.ui

import android.os.Build

object SearchConfig{
    const val SEARCH_ENGINE_GOOGLE = 0
    const val SEARCH_ENGINE_BING = 1
    const val SEARCH_ENGINE_YAHOO = 2
    const val SEARCH_ENGINE_PERPLEXITY = 3

}

object JumpConfig{
    const val JUMP_NONE="JUMP_NONE"
    const val JUMP_HOME="JUMP_HOME"
    const val JUMP_WEB="JUMP_WEB"
    const val JUMP_SEARCH="JUMP_SEARCH"
    const val JUMP_FILE="JUMP_FILE"
}

object ParamsConfig{
    const val JSON_PARAMS="JSON_PARAMS"
}

object UrlConfig{
    const val PRIVATE_URL = "https://sites.google.com/view/aiobrowser-privacy-policy/home"
    const val SERVICE_URL = "https://sites.google.com/view/aio-browser-service/home"
}


fun isAndroid12(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
}

fun isAndroid11(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
}

fun isRealAndroid11():Boolean{
    return Build.VERSION.SDK_INT == Build.VERSION_CODES.R
}

fun isAndroid8(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
}