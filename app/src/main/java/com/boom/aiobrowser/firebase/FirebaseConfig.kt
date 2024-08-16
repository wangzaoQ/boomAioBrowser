package com.boom.aiobrowser.firebase

import com.boom.aiobrowser.APP

object FirebaseConfig {

    const val AD_DEFAULT_JSON = "ewogICJwbnZkc2ttYiI6IDUsCiAgInF3bWtzemJ4IjogMSwKICAiYW9id3NfbGF1bmNoIjogWwogICAgewogICAgICAia3R5Z3pkem4iOiAiY2EtYXBwLXB1Yi0zOTQwMjU2MDk5OTQyNTQ0LzkyNTczOTU5MjEiLAogICAgICAidHlieHVtcG4iOiAiYWRtb2IiLAogICAgICAicHhkdHpnaG8iOiAib3AiLAogICAgICAic3dwdXpoaHYiOiAxMzgwMCwKICAgICAgIm5weG90dXNnIjogMwogICAgICAgfQogIF0KfQ=="

    //控制应用内所有广告间的间隔的广告cd，本地默认60s
    var AD_CD_ALL = if (APP.isDebug)10 else 60
}