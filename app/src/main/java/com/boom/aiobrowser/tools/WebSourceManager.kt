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
            sourceList = getFilmSource()
        })
        sourceDetailsList.add(WebSourceData().apply {
            titleRes = R.string.app_news
            sourceList = getNewsSource()
        })
        sourceDetailsList.add(WebSourceData().apply {
            titleRes = R.string.app_entertainment
            sourceList = getEntertainmentSources()
        })
        sourceDetailsList.add(WebSourceData().apply {
            titleRes = R.string.app_tools
            sourceList = getOnlineToolsSources()
        })
        sourceDetailsList.add(WebSourceData().apply {
            titleRes = R.string.app_travel
            sourceList = getTravelSources()
        })
        sourceDetailsList.add(WebSourceData().apply {
            titleRes = R.string.app_music
            sourceList = getMusicSources()
        })
        sourceDetailsList.add(WebSourceData().apply {
            titleRes = R.string.app_shopping
            sourceList = getShoppingSources()
        })
        sourceDetailsList.add(WebSourceData().apply {
            titleRes = R.string.app_sports
            sourceList = getSportsSources()
        })
        sourceDetailsList.add(WebSourceData().apply {
            titleRes = R.string.app_video
            sourceList = getVideoPlatformSources()
        })
        return sourceDetailsList
    }


    private fun getFilmSource(): MutableList<JumpData>{
        var list = mutableListOf<JumpData>()
        var local = Locale.getDefault()
        when (local.language) {
            "pt" -> {
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Globoplay
                    jumpTitle = APP.instance.getString(R.string.app_globoplay)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Netflix
                    jumpTitle = APP.instance.getString(R.string.app_net_flix)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Looke
                    jumpTitle = APP.instance.getString(R.string.app_looke)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Telecine_Play
                    jumpTitle = APP.instance.getString(R.string.app_telecine_play)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Amazon_Prime_Video
                    jumpTitle = APP.instance.getString(R.string.app_amazon_prime_video)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_HBO_Max
                    jumpTitle = APP.instance.getString(R.string.app_hbo_max)
                })
            }
            else -> {
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Netflix
                    jumpTitle = APP.instance.getString(R.string.app_net_flix)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Disney
                    jumpTitle = APP.instance.getString(R.string.app_net_disney)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Hulu
                    jumpTitle = APP.instance.getString(R.string.app_hu_lu)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Amazon_Prime_Video
                    jumpTitle = APP.instance.getString(R.string.app_amazon_prime_video)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_HBO_Max
                    jumpTitle = APP.instance.getString(R.string.app_hbo_max)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_IMDb
                    jumpTitle = APP.instance.getString(R.string.app_imdb)
                })
            }
        }
        return list
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

    private fun getNewsSource(): MutableList<JumpData> {
        val list = mutableListOf<JumpData>()
        val local = Locale.getDefault()
        when (local.language) {
            "pt" -> {
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Globo
                    jumpTitle = APP.instance.getString(R.string.news_globo)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_UOL
                    jumpTitle = APP.instance.getString(R.string.news_uol)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Folha
                    jumpTitle = APP.instance.getString(R.string.news_folha)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Estadao
                    jumpTitle = APP.instance.getString(R.string.news_estadao)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_R7
                    jumpTitle = APP.instance.getString(R.string.news_r7)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Correio_Braziliense
                    jumpTitle = APP.instance.getString(R.string.news_correio_braziliense)
                })
            }
            else -> {
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_CNN
                    jumpTitle = APP.instance.getString(R.string.news_cnn)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_NYT
                    jumpTitle = APP.instance.getString(R.string.news_new_york_times)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_FoxNews
                    jumpTitle = APP.instance.getString(R.string.news_fox_news)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_NBC
                    jumpTitle = APP.instance.getString(R.string.news_nbc)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_WashingtonPost
                    jumpTitle = APP.instance.getString(R.string.news_washington_post)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_USAToday
                    jumpTitle = APP.instance.getString(R.string.news_usa_today)
                })
            }
        }
        return list
    }

    private fun getEntertainmentSources(): MutableList<JumpData> {
        val list = mutableListOf<JumpData>()
        val local = Locale.getDefault()
        when (local.language) {
            "pt" -> {
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Quem
                    jumpTitle = APP.instance.getString(R.string.entertainment_quem)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Purepeople
                    jumpTitle = APP.instance.getString(R.string.entertainment_purepeople)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Ego
                    jumpTitle = APP.instance.getString(R.string.entertainment_ego)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_RD1
                    jumpTitle = APP.instance.getString(R.string.entertainment_rd1)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Caras
                    jumpTitle = APP.instance.getString(R.string.entertainment_caras)
                })
            }
            else -> {
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_TMZ
                    jumpTitle = APP.instance.getString(R.string.entertainment_tmz)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_People
                    jumpTitle = APP.instance.getString(R.string.entertainment_people)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_EOnline
                    jumpTitle = APP.instance.getString(R.string.entertainment_eonline)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_UsWeekly
                    jumpTitle = APP.instance.getString(R.string.entertainment_us_weekly)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_PerezHilton
                    jumpTitle = APP.instance.getString(R.string.entertainment_perez_hilton)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_JustJared
                    jumpTitle = APP.instance.getString(R.string.entertainment_just_jared)
                })
            }
        }
        return list
    }


    private fun getOnlineToolsSources(): MutableList<JumpData> {
        val list = mutableListOf<JumpData>()
        val local = Locale.getDefault()
        when (local.language) {
            "pt" -> {
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Google
                    jumpTitle = APP.instance.getString(R.string.tool_google)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_ChatGPT
                    jumpTitle = APP.instance.getString(R.string.tool_chatgpt)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_OfficeOnline
                    jumpTitle = APP.instance.getString(R.string.tool_office_online)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_PagSeguro
                    jumpTitle = APP.instance.getString(R.string.tool_pagseguro)
                })
            }
            else -> {
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Google
                    jumpTitle = APP.instance.getString(R.string.tool_google)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_ChatGPT
                    jumpTitle = APP.instance.getString(R.string.tool_chatgpt)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_OfficeOnline
                    jumpTitle = APP.instance.getString(R.string.tool_office_online)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Grammarly
                    jumpTitle = APP.instance.getString(R.string.tool_grammarly)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Canva
                    jumpTitle = APP.instance.getString(R.string.tool_canva)
                })
            }
        }
        return list
    }

    private fun getTravelSources(): MutableList<JumpData> {
        val list = mutableListOf<JumpData>()
        val local = Locale.getDefault()
        when (local.language) {
            "pt" -> {
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Decolar
                    jumpTitle = APP.instance.getString(R.string.travel_decolar)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Booking
                    jumpTitle = APP.instance.getString(R.string.travel_booking)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_MaxMilhas
                    jumpTitle = APP.instance.getString(R.string.travel_maxmilhas)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Hurb
                    jumpTitle = APP.instance.getString(R.string.travel_hurb)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_MelhoresDestinos
                    jumpTitle = APP.instance.getString(R.string.travel_melhores_destinos)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Viajanet
                    jumpTitle = APP.instance.getString(R.string.travel_viajanet)
                })
            }
            else -> {
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_TripAdvisor
                    jumpTitle = APP.instance.getString(R.string.travel_tripadvisor)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Expedia
                    jumpTitle = APP.instance.getString(R.string.travel_expedia)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Kayak
                    jumpTitle = APP.instance.getString(R.string.travel_kayak)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Booking
                    jumpTitle = APP.instance.getString(R.string.travel_booking)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_LonelyPlanet
                    jumpTitle = APP.instance.getString(R.string.travel_lonely_planet)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Airbnb
                    jumpTitle = APP.instance.getString(R.string.travel_airbnb)
                })
            }
        }
        return list
    }

    private fun getMusicSources(): MutableList<JumpData> {
        val list = mutableListOf<JumpData>()
        val local = Locale.getDefault()
        when (local.language) {
            "pt" -> {
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Deezer
                    jumpTitle = APP.instance.getString(R.string.music_deezer)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Spotify
                    jumpTitle = APP.instance.getString(R.string.music_spotify)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_SuaMusica
                    jumpTitle = APP.instance.getString(R.string.music_sua_musica)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_AppleMusic_BR
                    jumpTitle = APP.instance.getString(R.string.music_apple_music)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Tidal
                    jumpTitle = APP.instance.getString(R.string.music_tidal)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_PalcoMP3
                    jumpTitle = APP.instance.getString(R.string.music_palco_mp3)
                })
            }
            else -> {
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Spotify
                    jumpTitle = APP.instance.getString(R.string.music_spotify)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Pandora
                    jumpTitle = APP.instance.getString(R.string.music_pandora)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_AppleMusic
                    jumpTitle = APP.instance.getString(R.string.music_apple_music)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_SoundCloud
                    jumpTitle = APP.instance.getString(R.string.music_soundcloud)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_AmazonMusic
                    jumpTitle = APP.instance.getString(R.string.music_amazon_music)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Tidal
                    jumpTitle = APP.instance.getString(R.string.music_tidal)
                })
            }
        }
        return list
    }
    private fun getShoppingSources(): MutableList<JumpData> {
        val list = mutableListOf<JumpData>()
        val local = Locale.getDefault()
        when (local.language) {
            "pt" -> {
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_MercadoLivre
                    jumpTitle = APP.instance.getString(R.string.shopping_mercado_livre)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Americanas
                    jumpTitle = APP.instance.getString(R.string.shopping_americanas)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_MagazineLuiza
                    jumpTitle = APP.instance.getString(R.string.shopping_magazine_luiza)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Submarino
                    jumpTitle = APP.instance.getString(R.string.shopping_submarino)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Netshoes
                    jumpTitle = APP.instance.getString(R.string.shopping_netshoes)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_AliExpress
                    jumpTitle = APP.instance.getString(R.string.shopping_aliexpress)
                })
            }
            else -> {
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Amazon
                    jumpTitle = APP.instance.getString(R.string.shopping_amazon)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_eBay
                    jumpTitle = APP.instance.getString(R.string.shopping_ebay)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Walmart
                    jumpTitle = APP.instance.getString(R.string.shopping_walmart)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Target
                    jumpTitle = APP.instance.getString(R.string.shopping_target)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_BestBuy
                    jumpTitle = APP.instance.getString(R.string.shopping_best_buy)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Etsy
                    jumpTitle = APP.instance.getString(R.string.shopping_etsy)
                })
            }
        }
        return list
    }
    private fun getSportsSources(): MutableList<JumpData> {
        val list = mutableListOf<JumpData>()
        val local = Locale.getDefault()
        when (local.language) {
            "pt" -> {
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_GloboEsporte
                    jumpTitle = APP.instance.getString(R.string.sports_globo_esporte)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_ESPNBrasil
                    jumpTitle = APP.instance.getString(R.string.sports_espn_brasil)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Lance
                    jumpTitle = APP.instance.getString(R.string.sports_lance)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_EsporteInterativo
                    jumpTitle = APP.instance.getString(R.string.sports_esporte_interativo)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_FutebolInterior
                    jumpTitle = APP.instance.getString(R.string.sports_futebol_interior)
                })
            }
            else -> {
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_ESPN
                    jumpTitle = APP.instance.getString(R.string.sports_espn)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_BleacherReport
                    jumpTitle = APP.instance.getString(R.string.sports_bleacher_report)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_YahooSports
                    jumpTitle = APP.instance.getString(R.string.sports_yahoo_sports)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_CBSSports
                    jumpTitle = APP.instance.getString(R.string.sports_cbs_sports)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_NBA
                    jumpTitle = APP.instance.getString(R.string.sports_nba)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_NFL
                    jumpTitle = APP.instance.getString(R.string.sports_nfl)
                })
            }
        }
        return list
    }


    private fun getVideoPlatformSources(): MutableList<JumpData> {
        val list = mutableListOf<JumpData>()
        val local = Locale.getDefault()
        when (local.language) {
            "pt" -> {
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_YouTube
                    jumpTitle = APP.instance.getString(R.string.video_youtube)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Vimeo
                    jumpTitle = APP.instance.getString(R.string.video_vimeo)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Kwai
                    jumpTitle = APP.instance.getString(R.string.video_kwai)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Twitch
                    jumpTitle = APP.instance.getString(R.string.video_twitch)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Globoplay
                    jumpTitle = APP.instance.getString(R.string.video_globoplay)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_TikTok
                    jumpTitle = APP.instance.getString(R.string.video_tiktok)
                })
            }
            else -> {
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_YouTube
                    jumpTitle = APP.instance.getString(R.string.video_youtube)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Vimeo
                    jumpTitle = APP.instance.getString(R.string.video_vimeo)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Dailymotion
                    jumpTitle = APP.instance.getString(R.string.video_dailymotion)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Twitch
                    jumpTitle = APP.instance.getString(R.string.video_twitch)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_Hulu
                    jumpTitle = APP.instance.getString(R.string.video_hulu)
                })
                list.add(JumpData().apply {
                    jumpUrl = WebConfig.URL_TikTok
                    jumpTitle = APP.instance.getString(R.string.video_tiktok)
                })
            }
        }
        return list
    }


}