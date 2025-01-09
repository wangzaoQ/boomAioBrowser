package com.boom.aiobrowser.firebase

import com.boom.aiobrowser.APP
import com.boom.aiobrowser.data.DefaultUserData
import com.boom.aiobrowser.data.PushData

object FirebaseConfig {

    // max
//    const val AD_DEFAULT_JSON = "ewogICJwbnZkc2ttYiI6IDUwLAogICJxd21rc3pieCI6IDEwLAogICJhb2J3c19sYXVuY2giOiBbCiAgICB7CiAgICAgICJrdHlnemR6biI6ICJjYS1hcHAtcHViLTM5NDAyNTYwOTk5NDI1NDQvOTI1NzM5NTkyMSIsCiAgICAgICJ0eWJ4dW1wbiI6ICJhZG1vYiIsCiAgICAgICJweGR0emdobyI6ICJvcCIsCiAgICAgICJzd3B1emhodiI6IDEzODAwLAogICAgICAibnB4b3R1c2ciOiAzCiAgICAgICB9LAp7CiAgICAgICJrdHlnemR6biI6ICJjYS1hcHAtcHViLTM5NDAyNTYwOTk5NDI1NDQvMTAzMzE3MzcxMiIsCiAgICAgICJ0eWJ4dW1wbiI6ICJhZG1vYiIsCiAgICAgICJweGR0emdobyI6ICJpbnQiLAogICAgICAic3dwdXpoaHYiOiAxMzgwMCwKICAgICAgIm5weG90dXNnIjogMgogICAgICAgfQogIF0sCiJhb2J3c19tYWluX29uZSI6IFsgIAp7CiAgICAgICJrdHlnemR6biI6ICJjYS1hcHAtcHViLTM5NDAyNTYwOTk5NDI1NDQvMTAzMzE3MzcxMiIsCiAgICAgICJ0eWJ4dW1wbiI6ICJhZG1vYiIsCiAgICAgICJweGR0emdobyI6ICJpbnQiLAogICAgICAic3dwdXpoaHYiOiAxMzgwMCwKICAgICAgIm5weG90dXNnIjogMgogICAgICAgfSwKewogICAgICAia3R5Z3pkem4iOiAiMjQ4ZDAzODU2OTg3ZTA2MCIsCiAgICAgICJ0eWJ4dW1wbiI6ICJtYXgiLAogICAgICAicHhkdHpnaG8iOiAiaW50IiwKICAgICAgInN3cHV6aGh2IjogMTM4MDAsCiAgICAgICJucHhvdHVzZyI6IDMKICAgICAgIH0KICBdLAoiYW9id3NfZGV0YWlsX2JuYXQiOiBbICAKewogICAgICAia3R5Z3pkem4iOiAiY2EtYXBwLXB1Yi0zOTQwMjU2MDk5OTQyNTQ0LzIyNDc2OTYxMTAiLAogICAgICAidHlieHVtcG4iOiAiYWRtb2IiLAogICAgICAicHhkdHpnaG8iOiAibmF0IiwKICAgICAgInN3cHV6aGh2IjogMTM4MDAsCiAgICAgICJucHhvdHVzZyI6IDIKICAgICAgIH0sCnsKICAgICAgImt0eWd6ZHpuIjogImZjNzA1NGNiNWY0ZWYzNjYiLAogICAgICAidHlieHVtcG4iOiAibWF4IiwKICAgICAgInB4ZHR6Z2hvIjogIm5hdCIsCiAgICAgICJzd3B1emhodiI6IDEzODAwLAogICAgICAibnB4b3R1c2ciOiAzCiAgICAgICB9CiAgXSwKImFvYndzX2Rvd25sb2FkX2JuYXQiOiBbICAKewogICAgICAia3R5Z3pkem4iOiAiY2EtYXBwLXB1Yi0zOTQwMjU2MDk5OTQyNTQ0LzkyMTQ1ODk3NDEiLAogICAgICAidHlieHVtcG4iOiAiYWRtb2IiLAogICAgICAicHhkdHpnaG8iOiAiYmFuIiwKICAgICAgInN3cHV6aGh2IjogMTM4MDAsCiAgICAgICJucHhvdHVzZyI6IDIKICAgICAgIH0sCnsKICAgICAgImt0eWd6ZHpuIjogIjc2Zjg1NWI2OTZmNTRlNDYiLAogICAgICAidHlieHVtcG4iOiAibWF4IiwKICAgICAgInB4ZHR6Z2hvIjogImJhbiIsCiAgICAgICJzd3B1emhodiI6IDEzODAwLAogICAgICAibnB4b3R1c2ciOiAzCiAgICAgICB9CiAgXSwKImFvYndzX2Jhbl9vbmUiOiBbICAKewogICAgICAia3R5Z3pkem4iOiAiY2EtYXBwLXB1Yi0zOTQwMjU2MDk5OTQyNTQ0LzkyMTQ1ODk3NDEiLAogICAgICAidHlieHVtcG4iOiAiYWRtb2IiLAogICAgICAicHhkdHpnaG8iOiAiYmFuIiwKICAgICAgInN3cHV6aGh2IjogMTM4MDAsCiAgICAgICJucHhvdHVzZyI6IDIKICAgICAgIH0KICBdCn0="
    //admob
    const val AD_DEFAULT_JSON = "ewogICAgInBudmRza21iIjogNTAsCiAgICAicXdta3N6YngiOiAxMCwKICAgICJhb2J3c19sYXVuY2giOiBbCiAgICAgICAgewogICAgICAgICAgICAia3R5Z3pkem4iOiAiY2EtYXBwLXB1Yi0zOTQwMjU2MDk5OTQyNTQ0LzkyNTczOTU5MjEiLAogICAgICAgICAgICAidHlieHVtcG4iOiAiYWRtb2IiLAogICAgICAgICAgICAicHhkdHpnaG8iOiAib3AiLAogICAgICAgICAgICAic3dwdXpoaHYiOiAxMzgwMCwKICAgICAgICAgICAgIm5weG90dXNnIjogMwogICAgICAgIH0sCiAgICAgICAgewogICAgICAgICAgICAia3R5Z3pkem4iOiAiY2EtYXBwLXB1Yi0zOTQwMjU2MDk5OTQyNTQ0LzEwMzMxNzM3MTIiLAogICAgICAgICAgICAidHlieHVtcG4iOiAiYWRtb2IiLAogICAgICAgICAgICAicHhkdHpnaG8iOiAiaW50IiwKICAgICAgICAgICAgInN3cHV6aGh2IjogMTM4MDAsCiAgICAgICAgICAgICJucHhvdHVzZyI6IDIKICAgICAgICB9CiAgICBdLAogICAgImFvYndzX21haW5fb25lIjogWwogICAgICAgIHsKICAgICAgICAgICAgImt0eWd6ZHpuIjogImNhLWFwcC1wdWItMzk0MDI1NjA5OTk0MjU0NC8xMDMzMTczNzEyIiwKICAgICAgICAgICAgInR5Ynh1bXBuIjogImFkbW9iIiwKICAgICAgICAgICAgInB4ZHR6Z2hvIjogImludCIsCiAgICAgICAgICAgICJzd3B1emhodiI6IDEzODAwLAogICAgICAgICAgICAibnB4b3R1c2ciOiA0CiAgICAgICAgfSwKICAgICAgICB7CiAgICAgICAgICAgICJrdHlnemR6biI6ICIyNDhkMDM4NTY5ODdlMDYwIiwKICAgICAgICAgICAgInR5Ynh1bXBuIjogIm1heCIsCiAgICAgICAgICAgICJweGR0emdobyI6ICJpbnQiLAogICAgICAgICAgICAic3dwdXpoaHYiOiAxMzgwMCwKICAgICAgICAgICAgIm5weG90dXNnIjogMwogICAgICAgIH0KICAgIF0sCiAgICAiYW9id3NfZGV0YWlsX2JuYXQiOiBbCiAgICAgICAgewogICAgICAgICAgICAia3R5Z3pkem4iOiAiY2EtYXBwLXB1Yi0zOTQwMjU2MDk5OTQyNTQ0LzIyNDc2OTYxMTAiLAogICAgICAgICAgICAidHlieHVtcG4iOiAiYWRtb2IiLAogICAgICAgICAgICAicHhkdHpnaG8iOiAibmF0IiwKICAgICAgICAgICAgInN3cHV6aGh2IjogMTM4MDAsCiAgICAgICAgICAgICJucHhvdHVzZyI6IDQKICAgICAgICB9LAogICAgICAgIHsKICAgICAgICAgICAgImt0eWd6ZHpuIjogImZjNzA1NGNiNWY0ZWYzNjYiLAogICAgICAgICAgICAidHlieHVtcG4iOiAibWF4IiwKICAgICAgICAgICAgInB4ZHR6Z2hvIjogIm5hdCIsCiAgICAgICAgICAgICJzd3B1emhodiI6IDEzODAwLAogICAgICAgICAgICAibnB4b3R1c2ciOiAzCiAgICAgICAgfQogICAgXSwKICAgICJhb2J3c19kb3dubG9hZF9ibmF0IjogWwogICAgICAgIHsKICAgICAgICAgICAgImt0eWd6ZHpuIjogImNhLWFwcC1wdWItMzk0MDI1NjA5OTk0MjU0NC85MjE0NTg5NzQxIiwKICAgICAgICAgICAgInR5Ynh1bXBuIjogImFkbW9iIiwKICAgICAgICAgICAgInB4ZHR6Z2hvIjogImJhbiIsCiAgICAgICAgICAgICJzd3B1emhodiI6IDEzODAwLAogICAgICAgICAgICAibnB4b3R1c2ciOiA0CiAgICAgICAgfSwKICAgICAgICB7CiAgICAgICAgICAgICJrdHlnemR6biI6ICI3NmY4NTViNjk2ZjU0ZTQ2IiwKICAgICAgICAgICAgInR5Ynh1bXBuIjogIm1heCIsCiAgICAgICAgICAgICJweGR0emdobyI6ICJiYW4iLAogICAgICAgICAgICAic3dwdXpoaHYiOiAxMzgwMCwKICAgICAgICAgICAgIm5weG90dXNnIjogMwogICAgICAgIH0KICAgIF0sCiAgICAiYW9id3NfYmFuX29uZSI6IFsKICAgICAgICB7CiAgICAgICAgICAgICJrdHlnemR6biI6ICJjYS1hcHAtcHViLTM5NDAyNTYwOTk5NDI1NDQvOTIxNDU4OTc0MSIsCiAgICAgICAgICAgICJ0eWJ4dW1wbiI6ICJhZG1vYiIsCiAgICAgICAgICAgICJweGR0emdobyI6ICJiYW4iLAogICAgICAgICAgICAic3dwdXpoaHYiOiAxMzgwMCwKICAgICAgICAgICAgIm5weG90dXNnIjogMgogICAgICAgIH0KICAgIF0sCiAgICAiYW9id3NfYmFuX25ld3RwIjogWwogICAgICAgIHsKICAgICAgICAgICAgImt0eWd6ZHpuIjogImNhLWFwcC1wdWItMzk0MDI1NjA5OTk0MjU0NC85MjE0NTg5NzQxIiwKICAgICAgICAgICAgInR5Ynh1bXBuIjogImFkbW9iIiwKICAgICAgICAgICAgInB4ZHR6Z2hvIjogImJhbiIsCiAgICAgICAgICAgICJzd3B1emhodiI6IDEzODAwLAogICAgICAgICAgICAibnB4b3R1c2ciOiAyCiAgICAgICAgfQogICAgXSwKICAgICJhb2J3c19iYW5fbmV3aW4iOiBbCiAgICAgICAgewogICAgICAgICAgICAia3R5Z3pkem4iOiAiY2EtYXBwLXB1Yi0zOTQwMjU2MDk5OTQyNTQ0LzkyMTQ1ODk3NDEiLAogICAgICAgICAgICAidHlieHVtcG4iOiAiYWRtb2IiLAogICAgICAgICAgICAicHhkdHpnaG8iOiAiYmFuIiwKICAgICAgICAgICAgInN3cHV6aGh2IjogMTM4MDAsCiAgICAgICAgICAgICJucHhvdHVzZyI6IDIKICAgICAgICB9LAp7CiAgICAgICAgICAgICJrdHlnemR6biI6ICJjYS1hcHAtcHViLTM5NDAyNTYwOTk5NDI1NDQvMjI0NzY5NjExMCIsCiAgICAgICAgICAgICJ0eWJ4dW1wbiI6ICJhZG1vYiIsCiAgICAgICAgICAgICJweGR0emdobyI6ICJuYXQiLAogICAgICAgICAgICAic3dwdXpoaHYiOiAxMzgwMCwKICAgICAgICAgICAgIm5weG90dXNnIjogNAogICAgICAgIH0KICAgIF0KfQ=="


//    const val AD_DEFAULT_JSON = ""

    const val NF_JSON = """
{
	"aio_for_you": {
		"in_time": 30,
		"times": 99,
		"first_time": 0
	},
	"aio_editor": {
		"in_time": 30,
		"times": 99,
		"first_time" : 0
	},
	"aio_local": {
		"in_time": 30,
		"times": 99,
		"first_time": 0
	},
	"aio_hot": {
		"in_time": 30,
		"times": 99,
		"first_time": 0
	},
	"aio_newuser": {
		"in_time": 30,
		"times": 99,
		"first_time": 0
	},
	"aio_unlock": {
		"in_time": 5,
		"times": 99,
		"first_time": 0
	},
	"aio_trend": {
		"in_time": 5,
		"times": 99,
		"first_time": 0
	},
	"notify_gap": 30
}
"""
    const val ADDRESS_JSON = """
[
{
    "encCountry": "United States",
    "encCountryNo": "US",
    "tongue": "en",
    "allCity": [
      {
        "cncCity": "New York",
        "lon": -74.006,
        "lat": 40.7128,
        "adm": "NY",
        "code": "3651000"
      },
      {
        "cncCity": "Los Angeles",
        "lon": -118.2437,
        "lat": 34.0522,
        "adm": "CA",
        "code": "0644000"
      },
      {
        "cncCity": "Chicago",
        "lon": -87.6298,
        "lat": 41.8781,
        "adm": "lL",
        "code": "1714000"
      },
      {
        "cncCity": "Houston",
        "lon": -95.3698,
        "lat": 29.7604,
        "adm": "TX",
        "code": "4835000"
      },
      {
        "cncCity": "Philadelphia",
        "lon": -75.1652,
        "lat": 39.9526,
        "adm": "PA",
        "code": "4260000"
      },{
        "cncCity": "Jacksonville",
        "lon": -81.6557,
        "lat": 30.3322,
        "adm": "FL",
        "code": "1235000"
      },
      {
        "cncCity": "Washington, D.C.",
        "lon": -77.0369,
        "lat": 38.9072,
        "adm": "DC",
        "code": "1150000"
      },

      {
        "cncCity": "Boston",
        "lon": -71.0589,
        "lat": 42.3601,
        "adm": "MA",
        "code": "2507000"
      },
      {
        "cncCity": "Seattle",
        "lon": -122.3321,
        "lat": 47.6062,
        "adm": "WA",
        "code": "5363000"
      },
      {
        "cncCity": "San Francisco",
        "lon": -122.4194,
        "lat": 37.7749,
        "adm": "WA",
        "code": "0667000"
      }
    ]
  },
  {
    "encCountry": "Brazil",
    "encCountryNo": "BR",
    "tongue": "pt",
    "allCity": [
      {
        "cncCity": "São Paulo",
        "lon": -46.6333,
        "lat": -23.5505,
        "code": "3550308"
      },
      {
        "cncCity": "Rio de Janeiro",
        "lon": -43.1729,
        "lat": -22.9068,
        "code": "3304557"
      },
      {
        "cncCity": "Belo Horizonte",
        "lon": -43.9378,
        "lat": -19.9167,
        "code": "3106200"
      },{
        "cncCity": "Porto Alegre",
        "lon": -51.2300,
        "lat": -30.0346,
        "code": "4314902"
      },
      {
        "cncCity": "Brasília",
        "lon": -47.8825,
        "lat": -15.7939,
        "code": "5300108"
      },
      {
        "cncCity": "Salvador",
        "lon": -38.5023,
        "lat": -12.9722,
        "code": "2927408"
      },
      {
        "cncCity": "Fortaleza",
        "lon": -38.5427,
        "lat": -3.7319,
        "code": "2304400"
      },
      {
        "cncCity": "Curitiba",
        "lon": -49.2768,
        "lat": -25.4284,
        "code": "4106902"
      },
      {
        "cncCity": "Campinas",
        "lon": -47.0626,
        "lat": -23.1896,
        "code": 3509502
      },
      {
        "cncCity": "Natal",
        "lon": -35.2110,
        "lat": -5.7945,
        "code": 2408102
      }
    ]
  }
]
"""

    const val DEFAULT_NEWS_JSON = """
[
{
	"itackl": "11257553494212609",
	"tconsi": "Downloads gratuitos e instantâneos de vídeos",
	"iassum": "https://images-01.ottvs.com.br/0067617/looke_4001.jpg?152923629"
},
{
	"itackl": "11257397135278081",
	"tconsi": "Local breaking news",
	"iassum": "https://media.phillyvoice.com/media/images/10192024_North_Philly_mass_shootin.2e16d0ba.fill-735x490.jpg"
},
{
	"itackl": "11257255977025537",
	"tconsi": "See What is Happening",
	"iassum": "https://cdn.dol.com.br/img/Categoria-Destaque/880000/0x0/CasalPudor_00881365_0_-t.webp?fallback=https%3A%2F%2Fcdn.dol.com.br%2Fimg%2FCategoria-Destaque%2F880000%2FCasalPudor_00881365_0_.jpg%3Fxid%3D2953450&xid=2953450"
}]
"""
    const val LTV_DEFAULT = """
{
    "aio_top10percent": 0.12,
    "aio_top20percent": 0.1,
    "aio_top30percent": 0.06,
    "aio_top40percent": 0.04,
    "aio_top50percent": 0.02
}
"""

//    const val LTV_DEFAULT = """
//{
//    "aio_top10percent": 0.65,
//    "aio_top20percent": 0.55,
//    "aio_top30percent": 0.45,
//    "aio_top40percent": 0.35,
//    "aio_top50percent": 0.25
//}
//"""

    const val DEFAULT_USER = """
{
    "download": "22008263268,21983018088",
    "news": "22019400202,22018175955,22039651813",
    "other": "download"
}
"""

    const val DEFAULT_REFER_CONFIG = "fb4a,facebook,gclid,not%20set,youtubeads,%7B%22,bytedance,adjust"

//    const val FILTER_DEFAULT_WEB = "tiktok,facebook,instagram,x,whatsapp,reddit,snapchat,orkut,netflix,disneyplus,hulu,primevideo,hbomax,imdb,globoplay,looke,telecineplay,cnn,nytimes,foxnews,nbcnews,washingtonpost,usatoday,globo,uol,folha,uol,estadao,r7,correiobraziliense,tmz,people,eonline,usmagazine,perezhilton,justjared,quem,purepeople,ego,rd1,caras,google,chatgpt,office,grammarly,canva,pagseguro,webmd,mayoclinic,healthline,medlineplus,everydayhealth,minhavida,einstein,hipocentro,saude,tripadvisor,expedia,kayak,airbnb,booking,lonelyplanet,decolar,maxmilhas,hurb,melhoresdestinos,viajanet,spotify,pandora,apple,soundcloud,amazon,tidal,deezer,suamusica,palcomp3,mercadolivre,americanas,magazineluiza,submarino,netshoes,aliexpress,espn,bleacherreport,yahoosports,cbssports,nba,nfl,globo,esporte,lance,esporteinterativo,futebolinterior,youtube,vimeo,dailymotion,twitch,kwai"
    const val FILTER_DEFAULT_WEB = ""
//
//    const val jumpBrowserConfig = "22008263268,21983018088"
//    const val jumpNewsConfig = "22019400202,22018175955,22039651813"

//    const val FILTER_DEFAULT_WEB = ""

    //控制应用内所有广告间的间隔的广告cd，本地默认60s
    var AD_CD_ALL = if (APP.isDebug)10 else 60

    var pushData: PushData?=null
    var switchDefaultPop: Boolean = false
    var switchDownloadGuidePop: Boolean = false
    var switchOpenFilter1: Boolean = true
    var switchOpenFilterList: MutableList<String> = mutableListOf()
    var ltvConfig = ""

    var browserJumpList: List<String> = mutableListOf()
    var newsJumpList: List<String> = mutableListOf()
    var referConfig = FirebaseConfig.DEFAULT_REFER_CONFIG
    var defaultUserData: DefaultUserData?=null


}