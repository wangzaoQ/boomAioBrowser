package com.boom.aiobrowser.model

import com.boom.aiobrowser.data.WebConfigData
import com.boom.aiobrowser.net.NetController
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppViewModel : BaseDataModel() {

    fun getWebConfig() {
        loadData(loadBack = {
            var pageList = mutableListOf<WebConfigData>()
            var fetchList = mutableListOf<WebConfigData>()
            NetController.getWebConfig().data?.forEach {
                if (it.kdepen == "FETCH") {
                    fetchList.add(WebConfigData().apply {
                        cType = it.kdepen
                        cUrl = it.dsurpr
                        cDetail = NetController.getWebDetail(it.dsurpr, it.kdepen).data ?: ""
                    })
                } else if (it.kdepen == "PAGE") {
                    pageList.add(WebConfigData().apply {
                        cType = it.kdepen
                        cUrl = it.dsurpr
                        cDetail = NetController.getWebDetail(it.dsurpr, it.kdepen).data ?: ""
                    })
                }
            }
            if (pageList.isNotEmpty()){
                CacheManager.pageList = pageList
            }
            if (fetchList.isNotEmpty()){
                CacheManager.fetchList = fetchList
            }
        }, failBack = {
            AppLogs.eLog(TAG,it.stackTraceToString())
        },1)
    }
}