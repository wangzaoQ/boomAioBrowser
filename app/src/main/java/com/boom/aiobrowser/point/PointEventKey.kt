package com.boom.aiobrowser.point

object PointEventKey {

    const val aobws_ad_chance = "aobws_ad_chance"
    const val aobws_ad_impression = "aobws_ad_impression"
    const val aobws_ad_load = "aobws_ad_load"


    const val nn_session = "nn_session"
    const val session_st = "session_st"
    const val launch_page = "launch_page"
    //启动页进入按钮点击
    const val launch_page_start = "launch_page_start"
    //app 停留时长
    const val app_stay = "app_stay"
    //设置默认弹窗展示
    const val default_pop = "default_pop"
    //设置默认弹窗设置按钮点击
    const val default_pop_set = "default_pop_set"
    //设置默认弹窗成功
    const val default_pop_set_s = "default_pop_set_s"
    //设置默认弹窗失败
    const val default_pop_set_f = "default_pop_set_f"


    //首页搜索输入框点击
    const val home_page_search = "home_page_search"

    //首页搜索输入框搜索引擎设置入口点击
    const val home_page_searchengine = "home_page_searchengine"
    // 首页点击新闻列表
    const val home_page_feeds = "home_page_feeds"
    //首页滑动
    const val home_page_slide = "home_page_slide"
    //首页刷新
    const val home_page_refresh = "home_page_refresh"
    // 首页停留时间
    const val home_page_stay = "home_page_stay"
    //首页点击快捷入口
    const val home_page_tool_c = "home_page_tool_c"
    //下载浮窗
    const val home_page_dl = "home_page_dl"

    //搜索历史记录点击
    const val search_page_history = "search_page_history"
    // 搜索页展示
    const val search_page = "search_page"
    //搜索页确认搜索
    const val search_page_go = "search_page_go"
    //搜索快捷点击
    const val search_page_qb = "search_page_qb"

    // 免责声明
    const val webpage_download_pop_disclaimer = "webpage_download_pop_disclaimer"
    // 记录按钮
    const val webpage_download_pop_record = "webpage_download_pop_record"
    // 详情页下载弹窗展示
    const val webpage_download_pop = "webpage_download_pop"
    // 详情页展示
    const val webpage_page = "webpage_page"

    //更多弹窗
    const val profile_pop = "profile_pop"
    const val profile_close = "profile_close"

    //更多设置默认浏览器开关
    const val profile_setdefault = "profile_setdefault"
    //更多-新tab点击
    const val profile_newtab = "profile_newtab"
    //更多-一键清除点击
    const val profile_cleardate = "profile_cleardate"
    // 更多-历史记录点击
    const val profile_history = "profile_history"
    //更多-关于 点击
    const val profile_about = "profile_about"
    //更多-下载
    const val profile_download = "profile_download"
    //下载列表页
    const val download_page = "download_page"
    //下载完成
    const val download_success = "download_success"
    //播放
    const val download_page_play = "download_page_play"
    // 更多
    const val download_page_more = "download_page_more"
    //重命名
    const val download_page_more_ra = "download_page_more_ra"
    //删除
    const val download_page_more_delete = "download_page_more_delete"
    //下载教程
    const val download_tutorial = "download_tutorial"
    // 下载教程 跳过
    const val download_tutorial_skip = "download_tutorial_skip"
    // 下载教程 确认
    const val download_tutorial_try = "download_tutorial_try"
    //搜索结果页返回
    const val webpage_back = "webpage_back"
    //搜索结果页前进
    const val webpage_ahead = "webpage_ahead"

    const val webpage_home = "webpage_home"
    const val webpage_download = "webpage_download"
    const val download_page_more_fi = "download_page_more_fi"

    // 下载中
    const val download_push_conduct = "download_push_conduct"
    //下载失败
    const val download_push_fail = "download_push_fail"
    //下载完成
    const val download_push_success = "download_push_success"

    //无下载内容弹窗
    const val webpage_page_pop_nodl = "webpage_page_pop_nodl"
    //反馈弹窗
    const val webpage_page_pop_fb = "webpage_page_pop_fb"
    const val webpage_page_pop_cancel = "webpage_page_pop_cancel"

    // 视频播放 页面展示
    const val video_playback_page = "video_playback_page"
    // 来源点击
    const val video_playback_source = "video_playback_source"

    //标签
    const val webpage_tag = "webpage_tag"
    //一键即焚
    const val webpage_delete = "webpage_delete"

    const val webpage_download_pop_dl = "webpage_download_pop_dl"
    const val clipboard = "clipboard"
    const val clipboard_open = "clipboard_open"


}


object PointValueKey{
    //用户输入的内容
    const val input_text = "input_text"
    const val click_source = "click_source"
    const val news_id = "news_id"
    const val refresh_type = "refresh_type"
    const val type = "type"
    const val url = "url"

    const val model_type = "model_type"
    const val open_type = "open_type"
    const val from_page = "from_page"
    const val video_source = "video_source"
    const val video_url = "video_url"
    const val page = "page"


    const val ad_pos_id = "ad_pos_id"
    const val ad_time = "ad_time"
    const val ad_key = "ad_key"

    const val ponit_action = "ponit_action"

}

object PointValue{
    const val show = "show"
    const val click = "click"
    const val parse = "parse"
    const val share = "share"
    const val clipboard = "clipboard"
}


object AD_POINT{
    const val aobws_launch = "aobws_launch"
    const val aobws_download_int = "aobws_download_int"
    const val aobws_return_int = "aobws_return_int"
    const val aobws_news_one = "aobws_news_one"
    const val aobws_download_one = "aobws_download_one"
}