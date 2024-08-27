package com.boom.aiobrowser.data

import com.boom.aiobrowser.R

class FileManageData() {
    var manageName = ""
    var type = FILE_TYPE_DOWNLOADS

    companion object{
        var FILE_TYPE_DOWNLOADS = 0
        var FILE_TYPE_LARGE_FILE = 1
        var FILE_TYPE_IMAGES = 2
        var FILE_TYPE_VIDEOS = 3
        var FILE_TYPE_APKS = 4
        var FILE_TYPE_MUSIC = 5
        var FILE_TYPE_ZIP = 6
        var FILE_TYPE_DOCUMENTS = 7

        fun createManageData(type:Int):FileManageData{
            return FileManageData().apply {
                this.type = type
            }
        }
    }

    fun getImage():Int{
       return when (type) {
            FILE_TYPE_DOWNLOADS-> {
                R.mipmap.ic_file_downloads
            }
           FILE_TYPE_LARGE_FILE-> {
                R.mipmap.ic_file_large_files
            }
           FILE_TYPE_IMAGES->{
               R.mipmap.ic_file_images
           }
           FILE_TYPE_VIDEOS->{
               R.mipmap.ic_file_videos
           }
           FILE_TYPE_APKS->{
               R.mipmap.ic_file_apks
           }
           FILE_TYPE_MUSIC->{
               R.mipmap.ic_files_music
           }
           FILE_TYPE_ZIP->{
               R.mipmap.ic_file_zip
           }
           FILE_TYPE_DOCUMENTS->{
               R.mipmap.ic_file_document
           }
            else -> {
                R.mipmap.ic_file_downloads
            }
        }
    }
    fun getContent():Int{
        return when (type) {
            FILE_TYPE_DOWNLOADS-> {
                R.string.app_downloads
            }
            FILE_TYPE_LARGE_FILE-> {
                R.string.app_large_file
            }
            FILE_TYPE_IMAGES->{
                R.string.app_images
            }
            FILE_TYPE_VIDEOS->{
                R.string.app_videos
            }
            FILE_TYPE_APKS->{
                R.string.app_apks
            }
            FILE_TYPE_MUSIC->{
                R.string.app_music
            }
            FILE_TYPE_ZIP->{
                R.string.app_zip
            }
            FILE_TYPE_DOCUMENTS->{
                R.string.app_documents
            }
            else -> {
                R.string.app_downloads
            }
        }
    }
}