package com.boom.aiobrowser.model

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.data.MusicData
import com.boom.aiobrowser.tools.AppLogs.dLog
import com.boom.aiobrowser.tools.toJson


class MusicViewModel :BaseDataModel() {
    var musicLiveData = MutableLiveData<MutableList<MusicData>>()

    fun getLocalMusic(){
        loadData(loadBack = {
            musicLiveData.postValue(getSongsForMedia(APP.instance, makeSongCursor(APP.instance, null, null)))
        }, failBack = {})
    }



    fun makeSongCursor(context: Context, selection: String?, paramArrayOfString: Array<String>?): Cursor? {
        val songSortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        return makeSongCursor(context, selection, paramArrayOfString, songSortOrder)
    }
    /**
     * 搜素本地音乐
     */
    private fun makeSongCursor(context: Context, selection: String?, paramArrayOfString: Array<String>?, sortOrder: String?): Cursor? {
        var selectionStatement = "duration>60000 AND is_music=1 AND title != ''"

        if (!TextUtils.isEmpty(selection)) {
            selectionStatement = "$selectionStatement AND $selection"
        }
//        return context.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//            arrayOf("_id", "title", "artist", "album", "duration", "track", "artist_id", "album_id", MediaStore.Audio.Media.DATA, "is_music"), selectionStatement, paramArrayOfString, sortOrder)
//
        return context.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null)
    }


    private fun getSongsForMedia(context: Context, cursor: Cursor?): MutableList<MusicData> {
        return getSongsForMedia(context, cursor, null);
    }
//
//    private fun getSongsForMedia(context: Context, cursor: Cursor?): MutableList<Music> {
//        return getSongsForMedia(context, cursor, null);
//    }

    /**
     * Android 扫描获取到的数据
     *
     * @param context
     * @param cursor
     * @return
     */
    private fun getSongsForMedia(context: Context, cursor: Cursor?, folderPath: String?): MutableList<MusicData> {
        var musics = mutableListOf<MusicData>()
        //开始遍历游标
        while (cursor!!.moveToNext()) {
            val music = MusicData()
//             文件名
            runCatching {
                music.fileName = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
            }
            // 歌曲名
            runCatching {
                music.title = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.TITLE))
            }
            // 时长
            runCatching {
                music.duration = cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media.DURATION))
            }
            // 歌手名
            music.artist =
                cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ARTIST))
            // 专辑名
            music.album = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ALBUM))
            // 年代
            if (cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.YEAR)) != null) {
                music.year = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.YEAR))
            } else {
                music.year = "未知"
            }
            runCatching {
                if (cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DATA)) != null) {
                    music.uri = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DATA))
                }
            }
            runCatching {
                //排除过小的音频文件
                if (cursor!!.getLong(cursor!!.getColumnIndex(MediaStore.Audio.Media.DURATION)) >= 30000) {
                    musics.add(music)
                }
            }
            dLog("AudioUtils", "getAllSongs: " + toJson(music))
        }
        //关闭游标
        cursor!!.close()

        return musics
    }

    fun getCoverUri(context: Context, albumId: String): String? {
        if (albumId == "-1") {
            return null
        }
        var uri: String? = null
        try {
            val cursor = context.contentResolver.query(
                Uri.parse("content://media/external/audio/albums/$albumId"),
                arrayOf("album_art"), null, null, null
            )
            if (cursor != null) {
                cursor.moveToNext()
                uri = cursor.getString(0)
                cursor.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return uri
    }



}