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
	"itackl": "11255382774120449",
	"tconsi": "Downloads gratuitos e instantâneos de vídeos",
	"uweek": "https://variety.com/?p=1236214866"
},
{
	"itackl": "11245891313467393",
	"tconsi": "Local breaking news",
	"iassum": "https://i.dailymail.co.uk/1s/2024/11/19/16/92244343-14098119-A_Florida_couple_created_their_dream_home_by_converting_a_former-a-13_1732035149723.jpg"
},
{
	"itackl": "11255327741444097",
	"tconsi": "See What is Happening",
	"iassum": "https://deadline.com/wp-content/uploads/2024/11/hard-knocks-in-season-afc-north-hbo-max.jpg"
},{
	"itackl": "11255327741444097",
	"tconsi": "Private videos, incognito browsing and downloading",
	"iassum": "https://deadline.com/wp-content/uploads/2024/11/hard-knocks-in-season-afc-north-hbo-max.jpg"
}]
"""

    //控制应用内所有广告间的间隔的广告cd，本地默认60s
    var AD_CD_ALL = if (APP.isDebug)10 else 60

    var pushData: PushData?=null
    var switchDefaultPop: Boolean = false
    var switchDownloadGuidePop: Boolean = false
}