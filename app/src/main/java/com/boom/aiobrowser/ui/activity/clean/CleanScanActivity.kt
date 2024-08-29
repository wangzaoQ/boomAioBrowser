package com.boom.aiobrowser.ui.activity.clean

import android.animation.ValueAnimator
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SizeUtils.dp2px
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.data.ScanData
import com.boom.aiobrowser.data.ViewItem
import com.boom.aiobrowser.databinding.BrowserActivityCleanScanBinding
import com.boom.aiobrowser.model.CleanViewModel
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.BigDecimalUtils
import com.boom.aiobrowser.tools.CachePermissionManager
import com.boom.aiobrowser.tools.clean.CleanConfig
import com.boom.aiobrowser.tools.clean.CleanConfig.DATA_TYPE_AD
import com.boom.aiobrowser.tools.clean.CleanConfig.DATA_TYPE_APK
import com.boom.aiobrowser.tools.clean.CleanConfig.DATA_TYPE_CACHE
import com.boom.aiobrowser.tools.clean.CleanConfig.DATA_TYPE_JUNK
import com.boom.aiobrowser.tools.clean.CleanConfig.DATA_TYPE_RESIDUAL
import com.boom.aiobrowser.tools.clean.CleanConfig.adFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.apkFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.cacheFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.junkFiles
import com.boom.aiobrowser.tools.clean.CleanConfig.residualFiles
import com.boom.aiobrowser.tools.clean.formatSize
import com.boom.aiobrowser.tools.rotateAnim
import com.boom.aiobrowser.ui.activity.clean.load.CleanLoadActivity
import com.boom.aiobrowser.ui.adapter.ScanAdapter2
import com.boom.base.adapter4.BaseQuickAdapter
import com.boom.base.adapter4.util.addOnDebouncedChildClick
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference


class CleanScanActivity: BaseActivity<BrowserActivityCleanScanBinding>()  {

    private val viewModel by viewModels<CleanViewModel>()

    override fun getBinding(inflater: LayoutInflater): BrowserActivityCleanScanBinding {
        return BrowserActivityCleanScanBinding.inflate(layoutInflater)
    }

    var absVerticalOffset = 0


    override fun setListener() {
        viewModel.apply {
            currentPathLiveData.observe(this@CleanScanActivity){
                acBinding.tvPath.text = "Storage : $it"
            }
            currentSizeLiveData.observe(this@CleanScanActivity){
                acBinding.tvSize.text = it
            }
        }

        acBinding.mainAppBar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                absVerticalOffset = Math.abs(verticalOffset) //AppBarLayout竖直方向偏移距离px
//                val totalScrollRange = appBarLayout!!.totalScrollRange //AppBarLayout总的距离px
                val totalScrollRange = dp2px(187f)
                if (absVerticalOffset == 0)return
                var offset = BigDecimalUtils.mul(BigDecimalUtils.div(255.toDouble(), totalScrollRange.toDouble(),1),absVerticalOffset.toDouble()).toInt()
//                var offset = absVerticalOffset / 2
//                offset = 255 - offset
                AppLogs.dLog("onOffsetChanged", "offset=$offset")
                if (offset > 255) {
                    offset = 255
                } else if (offset <= 0) {
                    offset = 0
                }
                if (offset <10) {
                    acBinding.mainCl.alpha = 1f
                    acBinding.tvTitle.alpha = 0f
                } else {
                    val div = BigDecimalUtils.div(offset.toDouble(), 255.0, 2)
                    AppLogs.dLog("onOffsetChanged", "div=$div")
                    acBinding.tvTitle.alpha = div.toFloat()
                    acBinding.mainCl.alpha = 1-div.toFloat()
                }
            }
        })
        acBinding.ivBack.setOneClick {
            finish()
        }
    }

    val scanAdapter by lazy {
        ScanAdapter2(){
            updateSelectedSize()
        }
    }

    private fun updateSelectedSize() {
        addLaunch(success = {
            var allLength = 0L
            scanAdapter.items.forEach {
                if (it.dataType == ViewItem.TYPE_PARENT){
                    (it as ScanData).apply {
                        allLength+=it.allLength
                    }
                }
            }
            withContext(Dispatchers.Main){
                acBinding.tvTitle.text = allLength.formatSize()
                acBinding.tvSelectedSize.text = allLength.formatSize()
                acBinding.cleanButton.setOneClick {
                    CleanLoadActivity.startCleanLoadActivity(this@CleanScanActivity,allLength)
                }
            }
        }, failBack = {},Dispatchers.IO)
    }

    override fun setShowView() {
        acBinding.apply {
            rv.apply {
                layoutManager = LinearLayoutManager(this@CleanScanActivity, LinearLayoutManager.VERTICAL,false)
                // 设置预加载，请调用以下方法
                adapter = scanAdapter
                scanAdapter.setOnDebouncedItemClick{adapter, view, position ->
                    var data = scanAdapter.getItem(position)
                    if(data == null)return@setOnDebouncedItemClick
                    if (data.dataType == ViewItem.TYPE_PARENT){
                        data as ScanData
                        data.itemExpend = data.itemExpend.not()
//                        scanAdapter.notifyItemChanged(position,"updateExpend")
                        if (position == scanAdapter.items.size-1)return@setOnDebouncedItemClick
                        if (data.itemExpend){
                            var list = (when (data.type) {
                                    DATA_TYPE_JUNK-> {
                                        junkFiles
                                    }
                                    DATA_TYPE_APK-> {
                                        apkFiles
                                    }
                                    DATA_TYPE_RESIDUAL-> {
                                        residualFiles
                                    }
                                    DATA_TYPE_AD-> {
                                        adFiles
                                    }
                                    DATA_TYPE_CACHE-> {
                                        cacheFiles
                                    }
                                    else -> {
                                        mutableListOf()
                                    }
                                }
                            )
                            if (list.isNotEmpty()) scanAdapter.addAll(position+1,list)
                        }else{
                            var index = position
                            for (i in position+1 until  scanAdapter.items.size){
                                var data = scanAdapter.items.get(i)
                                if (data.dataType == ViewItem.TYPE_PARENT){
                                    index = i
                                    break
                                }
                            }
                            scanAdapter.removeAtRange(IntRange(position+1,index-1))
                        }
                    }
                }
                scanAdapter.addOnDebouncedChildClick(R.id.ivEnd) { adapter, view, position ->
                    var data = scanAdapter.getItem(position)
                    if(data == null)return@addOnDebouncedChildClick
                    if (data.dataType == ViewItem.TYPE_PARENT){
                        data as ScanData
                        data.itemChecked = data.itemChecked.not()
                        updateChildChecked(position,data.itemChecked)
                    }else {
                        data as FilesData
                        data.itemChecked = data.itemChecked.not()
                        scanAdapter.notifyItemChanged(position,"updateCheck")
                        updateParentChecked(position)
                    }
                    updateSelectedSize()
                }
                scanAdapter.addOnDebouncedChildClick(R.id.rlCacheTips) { adapter, view, position ->
                    clickCache()
                }
            }
            cpv.animation = 2000L.rotateAnim()
        }
        scanAdapter.add(ScanData().createCacheData(this@CleanScanActivity,false).apply {
            isLoading = true
        })

        addLaunch(success = {
            delay(500)
            scanAdapter.add(ScanData().createJunkData(this@CleanScanActivity,false).apply {
                isLoading = true
            })
            delay(500)
            scanAdapter.add(ScanData().createApksData(this@CleanScanActivity,false).apply {
                isLoading = true
            })
            delay(500)
            scanAdapter.add(ScanData().createResidualData(this@CleanScanActivity,false).apply {
                isLoading = true
            })
            delay(500)
            scanAdapter.add(ScanData().createADData(this@CleanScanActivity,false).apply {
                isLoading = true
            })
        }, failBack = {},Dispatchers.Main)
        scanAdapter.setItemAnimation(BaseQuickAdapter.AnimationType.SlideInRight)
        viewModel.startScan(Environment.getExternalStorageDirectory(), onScanPath = {

        }, onComplete = {
            addLaunch(success = {
                acBinding.ctl.setBackgroundColor(ContextCompat.getColor(this@CleanScanActivity,R.color.bg_scan_complete))
                acBinding.flProgress.visibility = View.GONE
                acBinding.llComplete.visibility = View.VISIBLE
                acBinding.tvPath.text = getString(R.string.app_scan_complete)
                acBinding.tvPath.setBackgroundDrawable(ContextCompat.getDrawable(this@CleanScanActivity,R.drawable.shape_bg_clean_path2))
                var animator = ValueAnimator.ofInt(acBinding.mainCl.height, dp2px(187f))
                animator.duration = 500L
                animator.addUpdateListener { valueAnimator -> // 获取当前动画的高度值
                    val animatedValue = valueAnimator.animatedValue as Int
                    // 设置 View 的新高度
                    var params = acBinding.mainCl.layoutParams as FrameLayout.LayoutParams
                    params.height = animatedValue
                    acBinding.mainCl.layoutParams = params
                }
                animator.start()
                val params = acBinding.ctl.getLayoutParams() as AppBarLayout.LayoutParams
                params.scrollFlags = (AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                        or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED or AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP)
                acBinding.ctl.layoutParams = params
                acBinding.root.postDelayed({
                    var list = mutableListOf<ViewItem>()
                    list.add(ScanData().createCacheData(this@CleanScanActivity).apply {
                        checkedAll(true)
                    })
                    CleanConfig.cacheFiles.forEach {
                        it.scanType = CleanConfig.DATA_TYPE_CACHE
                        it.itemChecked = true
                        list.add(it)
                    }
                    list.add(ScanData().createJunkData(this@CleanScanActivity).apply {
                        checkedAll(true)
                    })
                    CleanConfig.junkFiles.forEach {
                        it.scanType = CleanConfig.DATA_TYPE_JUNK
                        it.itemChecked = true
                        list.add(it)
                    }
                    list.add(ScanData().createApksData(this@CleanScanActivity).apply {
                        checkedAll(true)
                    })
                    CleanConfig.apkFiles.forEach {
                        it.scanType = CleanConfig.DATA_TYPE_APK
                        it.itemChecked = true
                        list.add(it)
                    }
                    list.add(ScanData().createResidualData(this@CleanScanActivity).apply {
                        checkedAll(true)
                    })
                    CleanConfig.residualFiles.forEach {
                        it.scanType = CleanConfig.DATA_TYPE_RESIDUAL
                        it.itemChecked = true
                        list.add(it)
                    }
                    list.add(ScanData().createADData(this@CleanScanActivity).apply {
                        checkedAll(true)
                    })
                    CleanConfig.adFiles.forEach {
                        it.scanType = CleanConfig.DATA_TYPE_AD
                        it.itemChecked = true
                        list.add(it)
                    }
                    scanAdapter.submitList(list)
                    acBinding.cleanButton.text = getString(R.string.app_clean)
                    updateSelectedSize()
                    if (cacheFiles.isNullOrEmpty()){
                        acBinding.root.postDelayed(Runnable { clickCache() }
                        ,100)
                    }
                },600L)
            }, failBack = {},Dispatchers.Main)
        },5000L)
    }

    private fun clickCache() {
        var permissionManager = CachePermissionManager(WeakReference(this@CleanScanActivity), onGranted = {
            var data = scanAdapter.getItem(0)
            data as ScanData
            data!!.isLoading = true
//            scanAdapter.notifyItemChanged(0,"updateLoad")
            viewModel.startScanCache(onComplete={
                addLaunch(success = {
                    scanAdapter.getItem(0)!!.apply {
                        (this as ScanData).apply {
                            isLoading = false
                            if (CleanConfig.cacheFiles.isNotEmpty()){
                                scanAdapter.addAll(1,CleanConfig.cacheFiles)
                            }
                        }
                    }
                    data!!.isLoading = false
                    delay(100)
                    scanAdapter.notifyItemChanged(0,"updateLoad")
                }, failBack = {},Dispatchers.Main)
            }, onScanPath = {
                addLaunch(success = {
                    scanAdapter.getItem(0)!!.apply {
                        (this as ScanData).apply {
                            allLength = it
                        }
                    }
                    scanAdapter.notifyItemChanged(0,"updateCache")
                }, failBack = {},Dispatchers.Main)
            })
        }, onDenied = {
        })
        permissionManager.requestCachePermission()
    }

    private fun updateParentChecked(position: Int) {
        var parentIndex = -1
        var allCheck = true
        for (i in position downTo 0){
           var data = scanAdapter.getItem(i)
            if (data == null)continue
            if (data.dataType == ViewItem.TYPE_PARENT){
                parentIndex = i
                data as ScanData
                break
            }else{
                data as FilesData
                if (data.itemChecked.not()){
                    allCheck = false
                }
            }
        }
        for (i in position until scanAdapter.items.size){
            var data = scanAdapter.getItem(i)
            if (data == null)continue
            if (data.dataType == ViewItem.TYPE_PARENT){
                data as ScanData
                break
            }else{
                data as FilesData
                if (data.itemChecked.not()){
                    allCheck = false
                }
            }
        }
        if (parentIndex>=0){
            var data = scanAdapter.getItem(parentIndex) as ScanData
            data.itemChecked = allCheck
            scanAdapter.notifyItemChanged(parentIndex,"updateCheck")
        }
    }

    private fun updateChildChecked(position: Int, itemChecked: Boolean) {
        if (position == scanAdapter.items.size-1){
            scanAdapter.notifyItemChanged(position)
        }
        var endIndex = -1
        for (i in position+1 until scanAdapter.items.size){
            var data = scanAdapter.items.get(i)
            if (data.dataType == ViewItem.TYPE_PARENT){
                endIndex = i
                break
            }
            data as FilesData
            data.itemChecked = itemChecked
        }
        if (endIndex>=1){
            scanAdapter.notifyItemRangeChanged(position,endIndex-position,"updateCheck")
        }
    }
}