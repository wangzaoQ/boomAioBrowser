package com.boom.aiobrowser.data

import android.os.Parcel
import android.os.Parcelable

class MusicData{
    companion object{
        const val LOCAL = "local"
    }

    // 歌曲类型 本地/网络
    var type: String? = null
    //数据库存储id
    var id: Long = 0
    // 歌曲id
    var mid: String? = null
    // 音乐标题
    var title: String? = null
    // 艺术家
    var artist: String? = null//{123,123,13}
    // 专辑
    var album: String? = null
    // 专辑id
    var artistId: String? = null//{123,123,13}
    // 专辑id
    var albumId: String? = null
    // 专辑内歌曲个数
    var trackNumber: Int = 0
    // 持续时间
    var duration: Long = 0
    // 收藏
    var isLove: Boolean = false
    // [本地|网络]
    var isOnline: Boolean = true
    // 音乐路径
    var uri: String? = null
    // [本地|网络] 音乐歌词地址
    var lyric: String? = null
    // [本地|网络]专辑封面路径
    var coverUri: String? = null
    // [网络]专辑封面
    var coverBig: String? = null
    // [网络]small封面
    var coverSmall: String? = null
    // 文件名
    var fileName: String? = null
    // 文件大小
    var fileSize: Long = 0
    // 发行日期
    var year: String? = null
    //更新日期
    var date: Long = 0
    //在线歌曲是否限制播放，false 可以播放
    var isCp: Boolean = false
    //在线歌曲是否付费歌曲，false 不能下载
    var isDl: Boolean = true
    //收藏id
    var collectId: String? = null
    //音乐品质，默认标准模式
    var quality: Int = 128000

    //音乐品质选择
    var hq: Boolean = false //192
    var sq: Boolean = false //320
    var high: Boolean = false //999
    //是否有mv 0代表无，1代表有
    var hasMv: Int = 0

    override fun toString(): String {
        return "Music(type=$type, id=$id, mid=$mid, title=$title, " +
                "artist=$artist, album=$album, artistId=$artistId, " +
                "albumId=$albumId, trackNumber=$trackNumber," +
                " duration=$duration, isLove=$isLove, isOnline=$isOnline, " +
                "uri=$uri, lyric=$lyric, coverUri=$coverUri, coverBig=$coverBig, coverSmall=$coverSmall," +
                " fileName=$fileName, fileSize=$fileSize, year=$year, date=$date, isCp=$isCp, isDl=$isDl, " +
                "collectId=$collectId, quality=$quality," +
                "qualityList=$high $hq $sq)"
    }

}