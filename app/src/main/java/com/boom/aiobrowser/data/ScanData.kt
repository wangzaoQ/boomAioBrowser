package com.boom.aiobrowser.data

import android.content.Context
import com.boom.aiobrowser.R
import com.boom.aiobrowser.tools.clean.CleanConfig
import com.boom.aiobrowser.tools.clean.CleanConfig.adFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.apkFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.junkFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.residualFiles

class ScanData {

    var type = CleanConfig.DATA_TYPE_JUNK
    var imgId = 0
    var title = ""
    var itemChecked = false
    var itemExpend = true
    var itemSize = ""
    var childList = mutableListOf<FilesData>()
    var isLoading = false

    fun createJunkData(context:Context,addChild:Boolean=true):ScanData{
        type = CleanConfig.DATA_TYPE_JUNK
        imgId = R.mipmap.ic_junk_files
        title = context.getString(R.string.app_clean_junk_files)
        if (addChild){
            childList = junkFiles
        }
        return this
    }
    fun createApksData(context:Context,addChild:Boolean=true):ScanData{
        type = CleanConfig.DATA_TYPE_APK
        imgId = R.mipmap.ic_apks
        title = context.getString(R.string.app_clean_apk)
        if (addChild){
            childList = apkFiles
        }
        return this
    }
    fun createResidualData(context:Context,addChild:Boolean=true):ScanData{
        type = CleanConfig.DATA_TYPE_RESIDUAL
        imgId = R.mipmap.ic_residual
        title = context.getString(R.string.app_clean_residual)
        if (addChild){
            childList = residualFiles
        }
        return this
    }
    fun createADData(context:Context,addChild:Boolean=true):ScanData{
        type = CleanConfig.DATA_TYPE_AD
        imgId = R.mipmap.ic_scan_ad
        title = context.getString(R.string.app_clean_ad)
        if (addChild){
            childList = adFiles
        }
        return this
    }

    fun checkedAll(check:Boolean){
        isLoading = false
        itemChecked = check
        childList.forEach {
            it.itemChecked = check
        }
    }
}