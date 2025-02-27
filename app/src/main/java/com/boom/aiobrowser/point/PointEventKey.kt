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
    //默认浏览器设置通知关闭
    const val default_pop_close = "default_pop_close"
    //设置默认弹窗设置按钮点击
    const val default_pop_set = "default_pop_set"
    //设置默认弹窗成功
    const val default_pop_set_s = "default_pop_set_s"
    //设置默认弹窗失败
    const val default_pop_set_f = "default_pop_set_f"


    const val home_page = "home_page"
    const val home_page_first = "home_page_first"
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
    //下载页
    const val download_tab = "download_tab"
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
    // 下载教程回退键
    const val tutorial_cancel = "tutorial_cancel"
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

    //每一条下载任务
    const val webpage_download_pop_dl = "webpage_download_pop_dl"
    //下载按钮点击
    const val download_click = "download_click"

    const val webpage_download_show = "webpage_download_show"
    const val clipboard = "clipboard"
    const val clipboard_open = "clipboard_open"
    //播放页返回
    const val video_playback_return = "video_playback_return"

    //固定通知点击
    const val fixed_explore = "fixed_explore"
    //通知点击
    const val all_noti_c = "all_noti_c"
    //通知触发
    const val all_noti_t = "all_noti_t"
    //通知二次开启页面展示
    const val noti_confirm_pop = "noti_confirm_pop"
    //通知二次开启页面允许
    const val noti_confirm_pop_allow = "noti_confirm_pop_allow"
    //通知二次开启页面成功打开
    const val noti_confirm_pop_suc = "noti_confirm_pop_suc"
    //通知二次开启页面打开失败
    const val noti_confirm_pop_fail = "noti_confirm_pop_fail"
    //通知权限允许
    const val noti_req_allow = "noti_req_allow"
    //通知权限拒绝
    const val noti_req_refuse = "noti_req_refuse"
    //通知二次开启页面跳过
    const val noti_confirm_pop_skip = "noti_confirm_pop_skip"
    //添加快捷方式弹窗展示
    const val shoetcut = "shoetcut"
    //添加小组件弹窗展示
    const val widget_pop = "widget_pop"
    //widget搜索点击
    const val widget_search = "widget_search"
    //widget点击某条新闻
    const val widget_click = "widget_click"
    //评价我们弹窗展示
    const val rate_us_pop = "rate_us_pop"
    //评价我们弹窗点击喜欢
    const val rate_us_submit = "rate_us_submit"
    const val rate_send_feedback = "rate_send_feedback"
    //好评弹窗无操作关闭
    const val rate_us_pop_close = "rate_us_pop_close"
    //下载任务弹窗展示
    const val download_task = "download_task"
    //下载任务查看点击
    const val download_task_view = "download_task_view"
    //更多-添加小组件点击
    const val profile_add_widget = "profile_add_widget"
    //推送配置
    const val aio_push = "aio_push"
    //FCM 通知订阅
    const val fcm_subscription = "fcm_subscription"
    //挂件教程说明展示
    const val tutorial_download = "tutorial_download"
    //任务选择说明展示
    const val tutorial_pop = "tutorial_pop"
    const val tutorial_webpage = "tutorial_webpage"
    //教程创建弹窗说明展示
    const val tutorial_task = "tutorial_task"
    //教程列表页说明展示
    const val tutorial_download_page = "tutorial_download_page"
    //新闻承接页曝光
    const val news = "news"
    //新闻正文
    const val news_page = "news_page"
    //web store页面曝光
    const val web_store = "web_store"
    //web store页添加网站
    const val web_store_add = "web_store_add"
    //登录成功
    const val login_suc = "login_suc"
    //登录失败
    const val login_fail = "login_fail"

    //登录成功
    const val login_suc_net = "login_suc_net"
    //登录失败
    const val login_fail_net = "login_fail_net"
    //新手引导弹窗
    const val guide_pop = "guide_pop"
    //新手引导弹窗按钮点击
    const val guide_view = "guide_view"
    //新手引导弹窗关闭
    const val guide_close = "guide_close"
    //首页导航点击
    const val home_navigation_click = "home_navigation_click"

    const val fcm_data = "fcm_data"
    //搜索页面点击趋势词
    const val search_page_gtr = "search_page_gtr"
    //搜索页面点击电影
    const val search_page_movie = "search_page_movie"
    //news_tab
    const val news_tab = "news_tab"

    //趋势新闻页曝光
    const val trend_today_page = "trend_today_page"
    //趋势新闻点击
    const val trend_news = "trend_news"
    //城市页面展示
    const val city_page = "city_page"
    //城市页面当前位置点击
    const val city_page_current = "city_page_current"
    //城市页面点击搜索
    const val city_page_search = "city_page_search"
    //城市页面关注的城市点击
    const val city_page_follow = "city_page_follow"
    //城市页面删除的城市点击
    const val city_page_remove = "city_page_remove"
    //本地城市选择页面展示
    const val city_page_set = "city_page_set"
    //本地页城市点击
    const val local_page_city = "local_page_city"
    // IP定位确定展示
    const val IP_location = "IP_location"
    const val IP_location_banner = "IP_location_banner"
    // IP定位确定是点击
    const val IP_location_yes = "IP_location_yes"
    // IP定位确定否点击
    const val IP_location_no = "IP_location_no"
    //gps位置权限申请成功
    const val gps_req_enable = "gps_req_enable"
    //gps位置权限申请失败
    const val gps_req_refuse = "gps_req_refuse"
    //新闻页面点击相关阅读
    const val news_page_related = "news_page_related"
    //新闻页面点击猜你喜欢
    const val news_page_like = "news_page_like"
    //topic点击
    const val topics_click = "topics_click"
    //topic页面展示
    const val topics_page = "topics_page"
    //topic页面搜索点击
    const val topics_page_search = "topics_page_search"
    //视频播放页
    const val featured_videos_page = "featured_videos_page"
    //cloak请求
//    const val cloak_req = "cloak_req"
    //cloak返回成功
    const val cloak_suc = "cloak_suc"
    //af返回成功
    const val af_suc = "af_suc"
    //adjust返回成功
    const val adjust_suc = "adjust_suc"
    //归因请求
    const val attribution_req = "attribution_req"
    //拿到归因结果
    const val attribution_suc = "attribution_suc"
    //归因新闻流程
    const val attribution_news = "attribution_news"
    //归因Download流程
    const val attribution_download = "attribution_download"
    //attribution_default
    const val attribution_default = "attribution_default"
    //下载完成弹窗展示
    const val download_complete = "download_complete"
    //下载完成弹窗查看点击
    const val download_complete_view = "download_complete_view"
    //用户来源
    const val user_source = "user_source"
    // a to b
    const val user_a_b = "user_a_b"
    // 归因来源
    const val track_platform = "track_platform"
    const val attribution_other = "attribution_other"
    //下载管理点击
    const val download_manager = "download_manager"
    //热门视频icon点击
    const val hot_videos = "hot_videos"
    //热门视频页展示
    const val hot_videos_page = "hot_videos_page"
    const val download_videos_page = "download_videos_page"
    //资源视频download点击
    const val download_tab_dl = "download_tab_dl"
    //订阅页展示
    const val subscribe_impression = "subscribe_impression"
    //用户点击订阅按钮
    const val subscribe_click = "subscribe_click"
    //订阅成功回调
    const val payment_success = "payment_success"
    //订阅失败回调
    const val payment_failed = "payment_failed"
    //恢复订阅点击
    const val restore_subscription = "restore_subscription"
    //订阅管理
    const val subscription_management = "subscription_management"
    //订阅闪购
    const val subscribe_pop = "subscribe_pop"
    const val webpage_show = "webpage_show"

}


object PointValueKey{
    //用户输入的内容
    const val input_text = "input_text"
    const val click_source = "click_source"
    const val news_id = "news_id"
    const val news_topic = "news_topic"
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
    const val push_type = "push_type"
    const val source_type = "source_type"
    const val source_from = "source_from"
    const val from_type = "from_type"
    const val web_store = "web_store"
    const val adjust_user = "adjust_user"
    const val load_time = "load_time"
    const val network = "network"


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
    const val aobws_play_int = "aobws_play_int"
    const val aobws_tap_int = "aobws_tap_int"

    const val aobws_return_int = "aobws_return_int"
    const val aobws_downreturn_int = "aobws_return_int"

    const val aobws_news_int = "aobws_news_int"
    const val aobws_news_other = "aobws_news_other"
    const val aobws_news_one = "aobws_news_one"
    const val aobws_download_one = "aobws_download_one"
    const val aobws_task_add = "aobws_task_add"
    const val aobws_play_bnat = "aobws_play_bnat"
    const val aobws_try_int = "aobws_try_int"
    const val aobws_newsclick_int = "aobws_newsclick_int"
    const val aobws_downclick_int = "aobws_downclick_int"
    const val aobws_downguide_int = "aobws_downguide_int"
}