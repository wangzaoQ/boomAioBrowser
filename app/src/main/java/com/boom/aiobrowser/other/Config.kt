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
    const val NF_INDEX = "nf_index"

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
    const val TOPIC_WORLD = "World"
    const val TOPIC_LIFESTYLE = "Lifestyle"
    const val TOPIC_ECONOMY = "Economy"
    const val TOPIC_SPORTS = "Sports"
    const val TOPIC_RELATIONSHIP = "Relationship"
    const val TOPIC_SOCIAL_WELFARE = "Social Welfare"
    const val TOPIC_FUNNY = "Funny"



}

object LoginConfig{
    const val SIGN_LOGIN = 1011
    const val SIGN_LOGIN_ONE_TAP = 1013
}

object WebConfig{
    var FB_URL = "https://www.facebook.com/"
    var INS_URL = "https://www.instagram.com/"
    var X_URL = "https://x.com/"
    var WhatsApp_URL = "https://www.whatsapp.com/"
    var Reddit_URL = "https://www.reddit.com/"
    var Orkut_URL = "https://www.orkut.com/"
    var Tiktok_URL = "https://www.tiktok.com/"
    var URL_Netflix = "https://www.netflix.com/"
    var URL_Disney = "https://www.disneyplus.com/"
    var URL_Hulu = "https://www.hulu.com/"
    var URL_Amazon_Prime_Video = "https://www.primevideo.com/"
    var URL_HBO_Max = "https://www.hbomax.com/"
    var URL_IMDb = "https://www.imdb.com/"
    var URL_Globoplay = "https://globoplay.globo.com/"
    var URL_Looke = "https://www.looke.com.br/"
    var URL_Telecine_Play = "https://www.telecineplay.com.br/"
    // 巴西新闻 URL
    const val URL_Globo = "https://g1.globo.com/"
    const val URL_UOL = "https://www.uol.com.br/"
    const val URL_Folha = "https://www.folha.uol.com.br/"
    const val URL_Estadao = "https://www.estadao.com.br/"
    const val URL_R7 = "https://www.r7.com/"
    const val URL_Correio_Braziliense = "https://www.correiobraziliense.com.br/"
    // 美国新闻 URL
    const val URL_CNN = "https://www.cnn.com/"
    const val URL_NYT = "https://www.nytimes.com/"
    const val URL_FoxNews = "https://www.foxnews.com/"
    const val URL_NBC = "https://www.nbcnews.com/"
    const val URL_WashingtonPost = "https://www.washingtonpost.com/"
    const val URL_USAToday = "https://www.usatoday.com/"

    // 美国娱乐 URL
    const val URL_TMZ = "https://www.tmz.com/"
    const val URL_People = "https://people.com/"
    const val URL_EOnline = "https://www.eonline.com/"
    const val URL_UsWeekly = "https://www.usmagazine.com/"
    const val URL_PerezHilton = "https://perezhilton.com/"
    const val URL_JustJared = "https://www.justjared.com/"

    // 巴西娱乐 URL
    const val URL_Quem = "https://revistaquem.globo.com/"
    const val URL_Purepeople = "https://www.purepeople.com.br/"
    const val URL_Ego = "https://ego.globo.com/"
    const val URL_RD1 = "https://rd1.com.br/"
    const val URL_Caras = "https://caras.com.br/"

    // 在线工具 URL（通用）
    const val URL_Google = "https://www.google.com/"
    const val URL_ChatGPT = "https://chatgpt.com/"
    const val URL_OfficeOnline = "https://www.office.com/"

    // 美国在线工具 URL
    const val URL_Grammarly = "https://www.grammarly.com/"
    const val URL_Canva = "https://www.canva.com/"

    // 巴西在线工具 URL
    const val URL_PagSeguro = "https://pagseguro.uol.com.br/"

    // 美国健康信息 URL
    const val URL_WebMD = "https://www.webmd.com/"
    const val URL_MayoClinic = "https://www.mayoclinic.org/"
    const val URL_Healthline = "https://www.healthline.com/"
    const val URL_MedlinePlus = "https://medlineplus.gov/"
    const val URL_EverydayHealth = "https://www.everydayhealth.com/"

    // 巴西健康信息 URL
    const val URL_MinhaVida = "https://www.minhavida.com.br/"
    const val URL_HospitalEinstein = "https://www.einstein.br/"
    const val URL_Hipocentro = "https://www.hipocentro.com.br/"
    const val URL_SaudeAbril = "https://saude.abril.com.br/"

    // 美国旅游网站 URL
    const val URL_TripAdvisor = "https://www.tripadvisor.com/"
    const val URL_Expedia = "https://www.expedia.com/"
    const val URL_Kayak = "https://www.kayak.com/"
    const val URL_Booking = "https://www.booking.com/"
    const val URL_LonelyPlanet = "https://www.lonelyplanet.com/"
    const val URL_Airbnb = "https://www.airbnb.com/"

    // 巴西旅游网站 URL
    const val URL_Decolar = "https://www.decolar.com/"
    const val URL_MaxMilhas = "https://www.maxmilhas.com.br/"
    const val URL_Hurb = "https://www.hurb.com/"
    const val URL_MelhoresDestinos = "https://www.melhoresdestinos.com.br/"
    const val URL_Viajanet = "https://www.viajanet.com.br/"

    // 美国音乐平台 URL
    const val URL_Spotify = "https://www.spotify.com/"
    const val URL_Pandora = "https://www.pandora.com/"
    const val URL_AppleMusic = "https://www.apple.com/apple-music"
    const val URL_SoundCloud = "https://soundcloud.com/"
    const val URL_AmazonMusic = "https://music.amazon.com/"
    const val URL_Tidal = "https://www.tidal.com/"

    // 巴西音乐平台 URL
    const val URL_Deezer = "https://www.deezer.com/"
    const val URL_SuaMusica = "https://www.suamusica.com.br/"
    const val URL_AppleMusic_BR = "https://www.apple.com/br/music/"
    const val URL_PalcoMP3 = "https://www.palcomp3.com.br/"

    // 美国购物网站 URL
    const val URL_Amazon = "https://www.amazon.com/"
    const val URL_eBay = "https://www.ebay.com/"
    const val URL_Walmart = "https://www.walmart.com/"
    const val URL_Target = "https://www.target.com/"
    const val URL_BestBuy = "https://www.bestbuy.com/"
    const val URL_Etsy = "https://www.etsy.com/"

    // 巴西购物网站 URL
    const val URL_MercadoLivre = "https://www.mercadolivre.com.br/"
    const val URL_Americanas = "https://www.americanas.com.br/"
    const val URL_MagazineLuiza = "https://www.magazineluiza.com.br/"
    const val URL_Submarino = "https://www.submarino.com.br/"
    const val URL_Netshoes = "https://www.netshoes.com.br/"
    const val URL_AliExpress = "https://www.aliexpress.com/"

    // 美国体育网站 URL
    const val URL_ESPN = "https://www.espn.com/"
    const val URL_BleacherReport = "https://www.bleacherreport.com/"
    const val URL_YahooSports = "https://sports.yahoo.com/"
    const val URL_CBSSports = "https://www.cbssports.com/"
    const val URL_NBA = "https://www.nba.com/"
    const val URL_NFL = "https://www.nfl.com/"

    // 巴西体育网站 URL
    const val URL_GloboEsporte = "https://ge.globo.com/"
    const val URL_ESPNBrasil = "https://www.espn.com.br/"
    const val URL_Lance = "https://www.lance.com.br/"
    const val URL_EsporteInterativo = "https://www.esporteinterativo.com.br/"
    const val URL_FutebolInterior = "https://www.futebolinterior.com.br/"

    // 美国视频平台 URL
    const val URL_YouTube = "https://www.youtube.com/"
    const val URL_Vimeo = "https://www.vimeo.com/"
    const val URL_Dailymotion = "https://www.dailymotion.com/"
    const val URL_Twitch = "https://www.twitch.tv/"
    const val URL_TikTok = "https://www.tiktok.com/"

    // 巴西视频平台 URL
    const val URL_Kwai = "https://www.kwai.com/"
}

object NewsConfig{

    const val TOPIC_TAG="topic_"
    const val LOCAL_TAG="area_"
    const val NO_SESSION_TAG="no_session_"
    const val NO_NF_VIDEO_TAG="nf_video_"

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