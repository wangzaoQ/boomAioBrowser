package com.boom.aiobrowser.model

import androidx.lifecycle.MutableLiveData
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.data.SearchResultData
import com.boom.aiobrowser.net.SearchNet
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.toJson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.util.Locale

class SearchViewModel:BaseDataModel() {

    var call:Call?= null

    var searchViewModel = MutableLiveData<MutableList<SearchResultData>>()


    fun searchResult(content:String){
        var url = "https://google.com/complete/search?output=toolbar&q=${content}&hl=${Locale.getDefault().language}"
        var request = Request.Builder().get().url(url).build()
        call?.cancel()
        call = SearchNet.net.newCall(request)
        call?.enqueue(object :
            Callback {
            override fun onFailure(call: Call, e: IOException) {
                AppLogs.eLog(TAG,e.stackTraceToString())
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyStr = response?.body?.string() ?: ""
                var list = parseXML(bodyStr)
                AppLogs.dLog(TAG,"搜索出的数据:${toJson(list)}")
                var dataList = mutableListOf<SearchResultData>()
                var recentList = CacheManager.recentSearchDataList
                recentList.forEach {
                    if (it.jumpTitle.startsWith(content)){
                        dataList.add(SearchResultData().apply {
                            type = 1
                            searchContent = it.jumpTitle
                        })
                    }
                }
                list.forEach {
                    dataList.add(SearchResultData().apply {
                        searchContent = it
                        type = 0
                    })
                }
                searchViewModel.postValue(dataList)
            }
        })
    }


    fun parseXML(xml: String): MutableList<String> {
        val suggestions = mutableListOf<String>()
        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()

        parser.setInput(xml.reader())

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.name == "suggestion") {
                val data = parser.getAttributeValue(null, "data")
                suggestions.add(data)
            }
            eventType = parser.next()
        }

        return suggestions
    }

}