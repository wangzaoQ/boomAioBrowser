package com.boom.aiobrowser.other

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
    const val JUMP_WEB_TYPE="JUMP_WEB_TYPE"

}

object ParamsConfig{
    const val WIDGET: String = "widget"
    const val SHORT: String = "short"
    const val NF_DATA = "nf_data"
    const val NF_TO = "nf_to"
    const val NF_ENUM_NAME = "nf_enum_name"

    const val JSON_PARAMS="JSON_PARAMS"

    const val JUMP_FROM="JUMP_FROM"
    const val JUMP_URL="JUMP_URL"
}

object UrlConfig{
    const val PRIVATE_URL = "https://sites.google.com/view/aiobrowser-privacy-policy/home"
    const val SERVICE_URL = "https://sites.google.com/view/aio-browser-service/home"
}

object TopicConfig{
    const val TOPIC_PUBLIC_SAFETY = "Public Safety"
    const val TOPIC_ENTERTAINMENT = "Entertainment"
    const val TOPIC_POLITICS = "Politics"
    const val TOPIC_LOCAL = "Local"
    const val TOPIC_FOR_YOU = "For You"

}

object WebConfig{
    var FB_URL = "https://www.facebook.com/"
    var INS_URL = "https://www.instagram.com/"
    var X_URL = "https://x.com/"
    var WhatsApp_URL = "https://www.whatsapp.com/"
    var Reddit_URL = "https://www.reddit.com/"
    var Snapchat_URL = "https://www.snapchat.com/"
    var Orkut_URL = "https://www.orkut.com/"
    var Tiktok_URL = "https://www.tiktok.com/"
}

object NewsConfig{

    const val TOPIC_TAG="topic_"

    var LOCAL_TOPIC_JSON= """
[
  {
    "nsand":"For You",
    "lscrat":[
      {
        "nsand":"pt",
        "tchoic":"Para Você"
      },
      {
        "lcompl":"ja",
        "tchoic":"あなたのために"
      },
      {
        "lcompl":"ko",
        "tchoic":"당신을 위해"
      },{
        "lcompl":"es",
        "tchoic":"Para ti"
      }
    ]
  }
]
"""
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

fun isAndroid14():Boolean{
    return Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU
}