package com.boom.aiobrowser.ui.activity

import android.view.LayoutInflater
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.VideoDownloadData
import com.boom.aiobrowser.databinding.BrowserActivityWebParseBinding
import com.boom.aiobrowser.nf.NFManager
import com.boom.aiobrowser.nf.NFShow
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.FragmentManager
import com.boom.aiobrowser.tools.download.DownloadCacheManager
import com.boom.aiobrowser.ui.fragment.TestWebFragment
import com.boom.aiobrowser.ui.pop.DisclaimerPop
import com.boom.downloader.VideoDownloadManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference


class WebParseActivity: BaseActivity<BrowserActivityWebParseBinding>() {
    override fun getBinding(inflater: LayoutInflater): BrowserActivityWebParseBinding {
        return BrowserActivityWebParseBinding.inflate(layoutInflater)
    }

    override fun setListener() {
        APP.videoScanLiveData.observe(this){
            if ((it.size?:0L) <= 0L)return@observe
            if (CacheManager.isDisclaimerFirst){
                CacheManager.isDisclaimerFirst = false
                DisclaimerPop(this@WebParseActivity).createPop {
                    downData(data = it)
                }
            }else{
                downData(data = it)
            }
        }
    }


    private fun downData(data: VideoDownloadData) {
        NFManager.requestNotifyPermission(WeakReference((this)), onSuccess = {
            addLaunch(success = {
                var model = DownloadCacheManager.queryDownloadModel(data)
                if (model == null) {
                    data.downloadType = VideoDownloadData.DOWNLOAD_PREPARE
                    DownloadCacheManager.addDownLoadPrepare(data)
                    withContext(Dispatchers.Main) {
                        var headerMap = HashMap<String, String>()
                        data.paramsMap?.forEach {
                            headerMap.put(it.key, it.value.toString())
                        }
                        VideoDownloadManager.getInstance()
                            .startDownload(data.createDownloadData(data), headerMap)
                        NFShow.showDownloadNF(data,true)
                        finish()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        ToastUtils.showLong(APP.instance.getString(R.string.app_already_download))
                        finish()
                    }
                }
            }, failBack = {})
        }, onFail = {})
    }

    override fun setShowView() {
        FragmentManager().addFragment(supportFragmentManager,TestWebFragment.newInstance(intent.getStringExtra("url")?:""),
            R.id.flRoot
        )
    }
}