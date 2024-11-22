package com.boom.aiobrowser.firebase

import com.boom.aiobrowser.APP
import com.boom.aiobrowser.data.PushData

object FirebaseConfig {

    const val AD_DEFAULT_JSON = "ewogICJwbnZkc2ttYiI6IDUwLAogICJxd21rc3pieCI6IDEwLAogICJhb2J3c19sYXVuY2giOiBbCiAgICB7CiAgICAgICJrdHlnemR6biI6ICJjYS1hcHAtcHViLTM5NDAyNTYwOTk5NDI1NDQvOTI1NzM5NTkyMSIsCiAgICAgICJ0eWJ4dW1wbiI6ICJhZG1vYiIsCiAgICAgICJweGR0emdobyI6ICJvcCIsCiAgICAgICJzd3B1emhodiI6IDEzODAwLAogICAgICAibnB4b3R1c2ciOiAzCiAgICAgICB9LAp7CiAgICAgICJrdHlnemR6biI6ICJjYS1hcHAtcHViLTM5NDAyNTYwOTk5NDI1NDQvMTAzMzE3MzcxMiIsCiAgICAgICJ0eWJ4dW1wbiI6ICJhZG1vYiIsCiAgICAgICJweGR0emdobyI6ICJpbnQiLAogICAgICAic3dwdXpoaHYiOiAxMzgwMCwKICAgICAgIm5weG90dXNnIjogMgogICAgICAgfQogIF0sCiJhb2J3c19tYWluX29uZSI6IFsgIAp7CiAgICAgICJrdHlnemR6biI6ICJjYS1hcHAtcHViLTM5NDAyNTYwOTk5NDI1NDQvMTAzMzE3MzcxMiIsCiAgICAgICJ0eWJ4dW1wbiI6ICJhZG1vYiIsCiAgICAgICJweGR0emdobyI6ICJpbnQiLAogICAgICAic3dwdXpoaHYiOiAxMzgwMCwKICAgICAgIm5weG90dXNnIjogMgogICAgICAgfQogIF0sCiJhb2J3c19kZXRhaWxfYm5hdCI6IFsgIAp7CiAgICAgICJrdHlnemR6biI6ICJjYS1hcHAtcHViLTM5NDAyNTYwOTk5NDI1NDQvMjI0NzY5NjExMCIsCiAgICAgICJ0eWJ4dW1wbiI6ICJhZG1vYiIsCiAgICAgICJweGR0emdobyI6ICJuYXQiLAogICAgICAic3dwdXpoaHYiOiAxMzgwMCwKICAgICAgIm5weG90dXNnIjogMgogICAgICAgfQogIF0sCiJhb2J3c19kb3dubG9hZF9ibmF0IjogWyAgCnsKICAgICAgImt0eWd6ZHpuIjogImNhLWFwcC1wdWItMzk0MDI1NjA5OTk0MjU0NC85MjE0NTg5NzQxIiwKICAgICAgInR5Ynh1bXBuIjogImFkbW9iIiwKICAgICAgInB4ZHR6Z2hvIjogImJhbiIsCiAgICAgICJzd3B1emhodiI6IDEzODAwLAogICAgICAibnB4b3R1c2ciOiAyCiAgICAgICB9CiAgXSwKImFvYndzX2Jhbl9vbmUiOiBbICAKewogICAgICAia3R5Z3pkem4iOiAiY2EtYXBwLXB1Yi0zOTQwMjU2MDk5OTQyNTQ0LzkyMTQ1ODk3NDEiLAogICAgICAidHlieHVtcG4iOiAiYWRtb2IiLAogICAgICAicHhkdHpnaG8iOiAiYmFuIiwKICAgICAgInN3cHV6aGh2IjogMTM4MDAsCiAgICAgICJucHhvdHVzZyI6IDIKICAgICAgIH0KICBdCn0="

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
	"notify_gap": 30
}
"""

    const val DEFAULT_NEWS_JSON = """
[{
	"itackl": "11257822825938945",
        "tconsi ": "Private videos,incognito browsing and downloading ",
        "uweek ": "https: //p16-sign-va.tiktokcdn.com/tos-maliva-p-0068/oYZmBfzxCiskIdjQkCzoiAH2IwI0APR3Mg1Wlm~tplv-tiktokx-360p.image?dr=14555&nonce=89444&refresh_token=d57e6ff04c6fecc8ab3456546b499c10&x-expires=1732172400&x-signature=W8O%2FVGBpzlIZ5v2nEJGEYtKh0nc%3D&ftpl=1&idc=maliva&ps=13740610&s=AWEME_DETAIL&shcp=34ff8df6&shp=d05b14bd&t=4d5b0474"
},
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

    //控制应用内所有广告间的间隔的广告cd，本地默认60s
    var AD_CD_ALL = if (APP.isDebug)10 else 60

    var pushData: PushData?=null
    var switchDefaultPop: Boolean = false
    var switchDownloadGuidePop: Boolean = false
}