package com.boom.aiobrowser.tools

import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.data.WebCategoryData
import com.boom.aiobrowser.data.WebSourceData
import com.boom.aiobrowser.other.JumpConfig
import com.boom.aiobrowser.other.WebConfig
import java.util.Locale

object WebSourceManager {

    fun getDefaultTabJump():MutableList<JumpData>{
        var defaultList = mutableListOf<JumpData>()
//        instagram、tiktok、x、Facebook、
        defaultList.add(JumpData().apply {
            jumpType = JumpConfig.JUMP_WEB
            jumpUrl = "https://www.instagram.com/"
            jumpTitle = APP.instance.getString(R.string.app_instagram)
        })
        defaultList.add(JumpData().apply {
            jumpType = JumpConfig.JUMP_WEB
            jumpUrl = "https://www.tiktok.com/"
            jumpTitle = APP.instance.getString(R.string.app_tt)
        })
        defaultList.add(JumpData().apply {
            jumpType = JumpConfig.JUMP_WEB
            jumpUrl = "https://x.com/"
            jumpTitle = APP.instance.getString(R.string.app_x)
        })
        defaultList.add(JumpData().apply {
            jumpType = JumpConfig.JUMP_WEB
            jumpUrl = "https://www.facebook.com/"
            jumpTitle = APP.instance.getString(R.string.app_fb)
        })
        for (i in 0 until 20){
            defaultList.add(JumpData().apply {
                jumpType = JumpConfig.JUMP_WEB
                jumpUrl = "https://www.facebook.com/"
                jumpTitle = APP.instance.getString(R.string.app_fb)
            })
        }
        return defaultList
    }

    fun getSourceList():MutableList<WebCategoryData>{
        var sourceList = mutableListOf<WebCategoryData>()
        sourceList.add(WebCategoryData().apply {
            titleRes = R.string.app_social
            uiCheck = true
            checkRes = R.mipmap.ic_social_unable
            unCheckRes = R.mipmap.ic_social_enable
        })
        sourceList.add(WebCategoryData().apply {
            titleRes = R.string.app_film
            checkRes = R.mipmap.ic_film_unable
            unCheckRes = R.mipmap.ic_film_enable
        })
        sourceList.add(WebCategoryData().apply {
            titleRes = R.string.app_news
            checkRes = R.mipmap.ic_category_news_unable
            unCheckRes = R.mipmap.ic_category_news_enable
        })
        sourceList.add(WebCategoryData().apply {
            titleRes = R.string.app_entertainment
            checkRes = R.mipmap.ic_film_unable
            unCheckRes = R.mipmap.ic_film_enable
        })
        sourceList.add(WebCategoryData().apply {
            titleRes = R.string.app_tools
            checkRes = R.mipmap.ic_tools_unable
            unCheckRes = R.mipmap.ic_tools_enable
        })
        sourceList.add(WebCategoryData().apply {
            titleRes = R.string.app_health
            checkRes = R.mipmap.ic_health_unable
            unCheckRes = R.mipmap.ic_health_enable
        })
        sourceList.add(WebCategoryData().apply {
            titleRes = R.string.app_travel
            checkRes = R.mipmap.ic_travel_unable
            unCheckRes = R.mipmap.ic_travel_enable
        })
        sourceList.add(WebCategoryData().apply {
            titleRes = R.string.app_music
            checkRes = R.mipmap.ic_music_unable
            unCheckRes = R.mipmap.ic_music_enable
        })
        sourceList.add(WebCategoryData().apply {
            titleRes = R.string.app_shopping
            checkRes = R.mipmap.ic_shopping_unable
            unCheckRes = R.mipmap.ic_shopping_enable
        })
        sourceList.add(WebCategoryData().apply {
            titleRes = R.string.app_sports
            checkRes = R.mipmap.ic_sports_unable
            unCheckRes = R.mipmap.ic_sports_enable
        })
        sourceList.add(WebCategoryData().apply {
            titleRes = R.string.app_video
            checkRes = R.mipmap.ic_video_unable
            unCheckRes = R.mipmap.ic_video_enable
        })
        return sourceList
    }

    fun getSourceDetailsList():MutableList<WebSourceData>{
        var sourceDetailsList = mutableListOf<WebSourceData>()
        sourceDetailsList.add(WebSourceData().apply {
            titleRes = R.string.app_social
            sourceList = getSocialSource()
        })
        sourceDetailsList.add(WebSourceData().apply {
            titleRes = R.string.app_film
            sourceList = getSocialSource()
        })
        sourceDetailsList.add(WebSourceData().apply {
            titleRes = R.string.app_news
            sourceList = getSocialSource()
        })
        sourceDetailsList.add(WebSourceData().apply {
            titleRes = R.string.app_entertainment
            sourceList = getSocialSource()
        })
        sourceDetailsList.add(WebSourceData().apply {
            titleRes = R.string.app_entertainment
            sourceList = getSocialSource()
        })
        return sourceDetailsList
    }

    private fun getSocialSource(): MutableList<JumpData> {
        var list = mutableListOf<JumpData>()
        var local = Locale.getDefault()
        list.add(JumpData().apply {
            jumpUrl = WebConfig.FB_URL
            jumpTitle = APP.instance.getString(R.string.app_fb)
        })
        list.add(JumpData().apply {
            jumpUrl = WebConfig.INS_URL
            jumpTitle = APP.instance.getString(R.string.app_instagram)
        })
        list.add(JumpData().apply {
            jumpUrl = WebConfig.X_URL
            jumpTitle = APP.instance.getString(R.string.app_x)
        })
        list.add(JumpData().apply {
            jumpUrl = WebConfig.WhatsApp_URL
            jumpTitle = APP.instance.getString(R.string.app_whats)
        })
        when (local.language) {
            "pt" -> {
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.Orkut_URL
                    jumpTitle = APP.instance.getString(R.string.app_orkut)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.Tiktok_URL
                    jumpTitle = APP.instance.getString(R.string.app_tt)
                })
            }
            else -> {
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.Reddit_URL
                    jumpTitle = APP.instance.getString(R.string.app_reddit)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.Snapchat_URL
                    jumpTitle = APP.instance.getString(R.string.app_snapchat)
                })
            }
        }
        return list
    }
}