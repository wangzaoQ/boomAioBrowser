package com.boom.aiobrowser.model

import com.boom.aiobrowser.net.SearchNet
import com.boom.aiobrowser.tools.AppLogs
import okhttp3.Request

class SearchViewModel:BaseDataModel() {

    fun searchResult(content:String){
        loadData(loadBack = {
            runCatching {
                var request = Request.Builder().get().url(SearchNet.getSearchUrl(content)).build()
                val response = SearchNet.net.newCall(request)?.execute()
                val bodyStr = response?.body?.string() ?: ""
            }

        }, failBack = {},1)

    }
}