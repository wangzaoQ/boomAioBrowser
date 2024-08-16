package com.boom.aiobrowser.firebase

import com.boom.aiobrowser.APP

object FirebaseConfig {

    const val AD_DEFAULT_JSON = "ewogICJwbnZkc2ttYiI6IDQwLAogICJxd21rc3pieCI6IDE1LAogICJhb2J3c19sYXVuY2giOiBbCiAgICB7CiAgICAgICJrdHlnemR6biI6ICJjYS1hcHAtcHViLTM5NDAyNTYwOTk5NDI1NDQvOTI1NzM5NTkyMSIsCiAgICAgICJ0eWJ4dW1wbiI6ICJhZG1vYiIsCiAgICAgICJweGR0emdobyI6ICJvcCIsCiAgICAgICJzd3B1emhodiI6IDEzODAwLAogICAgICAibnB4b3R1c2ciOiAzCiAgICAgICB9CiAgXQp9"

    //控制应用内所有广告间的间隔的广告cd，本地默认60s
    var AD_CD_ALL = if (APP.isDebug)10 else 60
}