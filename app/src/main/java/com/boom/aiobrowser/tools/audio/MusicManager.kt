package com.boom.aiobrowser.tools.audio

import android.annotation.SuppressLint
import android.content.Context
import android.provider.MediaStore
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.MusicData
import com.boom.aiobrowser.nf.NFManager.TAG
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.AppLogs.dLog
import com.boom.aiobrowser.tools.toJson
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import java.lang.ref.WeakReference

object MusicManager {


    fun requestAudioPermission(weakReference: WeakReference<BaseActivity<*>>, onSuccess: () -> Unit = {}, onFail: () -> Unit = {}) {
        var activity = weakReference.get()
        if (activity == null){
            onFail.invoke()
            return
        }
        val hasPermission = XXPermissions.isGranted(
            activity!!,
            Permission.READ_MEDIA_AUDIO
        )
        if (hasPermission){
            onSuccess.invoke()
            return
        }
        val xxPermissions = XXPermissions.with(activity!!)
        xxPermissions.permission(Permission.READ_MEDIA_AUDIO)
        runCatching {
            xxPermissions.request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                    AppLogs.dLog(TAG,"onGranted:${allGranted}")
                    onSuccess.invoke()
                }

                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                    super.onDenied(permissions, doNotAskAgain)
                    onFail.invoke()
                }
            })
        }.onFailure {
            onFail.invoke()
        }
    }


    @SuppressLint("Range")
    fun getAllSongs(context: Context): List<MusicData> {
        //容器

        val musics: MutableList<MusicData> = ArrayList()
        //获取音乐文件路径
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//        val projection = arrayOf(
//            MediaStore.Downloads.DISPLAY_NAME,
//            MediaStore.Downloads._ID,
//            MediaStore.Downloads.RELATIVE_PATH
//        )

        //开始查询
        @SuppressLint("Recycle") val cursor =
            context.contentResolver.query(uri, null, null, null, null)
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

//    /**
//     * 通过MediaStore获取文件uri
//     * @return 获取失败返回null
//     */
//    @RequiresApi(Build.VERSION_CODES.Q)
//    fun getFileUri(context: Context, path: String): Uri? {
//        // projection代表数据库中需要检索出来的列，也可以不写，query的第二个参数传null，写了性能更好
//        val projection = arrayOf(
//            MediaStore.Downloads.DISPLAY_NAME,
//            MediaStore.Downloads._ID,
//            MediaStore.Downloads.RELATIVE_PATH
//        )
//        // 从path解析出路径和文件名
//        val directoryPath = path.substringBeforeLast("/")
//        val fileName = path.substringAfterLast("/")
//
//        // SQL语句，路径匹配和文件名匹配
//        val selection =
//            "${MediaStore.Downloads.RELATIVE_PATH} LIKE ? AND ${MediaStore.Downloads.DISPLAY_NAME} = ?"
//        // SQL语句参数
//        val selectionArgs = arrayOf("%$directoryPath%", fileName)
//
//        val contentResolver: ContentResolver = context.contentResolver
//        val uri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
//        // 使用ContentResolver查找，获得数据库指针
//        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
//
//        var fileUri: Uri? = null
//        if (cursor?.moveToFirst() == true) {
//            val columnIndex = cursor.getColumnIndex(MediaStore.Downloads._ID)
//            val fileId = cursor.getLong(columnIndex)
//            fileUri = Uri.withAppendedPath(uri, fileId.toString())
//            cursor.close()
//        }
//        return fileUri
//    }


}