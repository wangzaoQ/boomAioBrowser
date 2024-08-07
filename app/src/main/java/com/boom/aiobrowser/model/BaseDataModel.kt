package com.boom.aiobrowser.model

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boom.aiobrowser.tools.AppLogs
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class BaseDataModel :ViewModel(), LifecycleObserver {

    val errorHandler: ((Throwable) -> Unit).() -> CoroutineExceptionHandler = {
        CoroutineExceptionHandler { _, throwable -> this(throwable) }
    }

    val TAG by lazy { javaClass.name }

    var failLiveData = MutableLiveData<String>()

    fun loadData(loadBack: suspend CoroutineScope.() -> Unit, failBack: (Throwable) -> Unit?, type:Int?=0) {
        runCatching {
            viewModelScope.launch(errorHandler(failBack)) {
                if (type == 0){
                    loadBack()
                }else {
                    withContext(Dispatchers.IO){
                        loadBack()
                    }
                }
            }
        }.onFailure {
            failLiveData.postValue("BaseDataModelError")
            failBack.invoke(it)
            AppLogs.eLog(TAG,it.stackTraceToString())
        }
    }
}