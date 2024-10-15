package com.boom.aiobrowser.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.tools.toJson

@Entity(tableName = "nf_tab")
public class NFModel{
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0

    var videoId:String?=""

    var nfID :Int = 0
    var channelId :String = ""

    override fun toString(): String {
        return toJson(this)
    }
}
