package com.boom.aiobrowser.ui.activity.clean

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
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
import com.boom.aiobrowser.tools.CacheManager
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
import com.boom.aiobrowser.tools.isCacheGranted
import com.boom.aiobrowser.tools.rotateAnim
import com.boom.aiobrowser.ui.activity.clean.load.CleanLoadActivity
import com.boom.aiobrowser.ui.activity.clean.load.CompleteLoadActivity
import com.boom.aiobrowser.ui.adapter.ScanAdapter2
import com.boom.aiobrowser.ui.isAndroid11
import com.boom.aiobrowser.ui.isAndroid12
import com.boom.aiobrowser.ui.isRealAndroid11
import com.boom.aiobrowser.ui.pop.CachePop
import com.boom.base.adapter4.BaseQuickAdapter
import com.boom.base.adapter4.util.addOnDebouncedChildClick
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
//            currentSizeLiveData.observe(this@CleanScanActivity){
//                acBinding.tvSize.text = it
//            }
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

    var updateJob:Job?=null

    var selectedAllLength = 0L

    var isScan = false

    private fun updateSelectedSize(cacheLength :Long=0L) {
        if (cacheLength!=0L){
            AppLogs.dLog(acTAG,"cacheLength:${cacheLength!!.formatSize()}")
        }
        selectedAllLength = 0
        updateJob?.cancel()
        updateJob = addLaunch(success = {
            var isCurrent = true
            var jumpChild = false
            scanAdapter.items.forEach {
                if (it.dataType== ViewItem.TYPE_PARENT){
                    isCurrent = false
                    jumpChild = false
                    it as ScanData
                    if (it.itemChecked){
                        selectedAllLength+=it.allLength
                        jumpChild = true
                    }
                }
                if (jumpChild.not()){
                    if (it.dataType == ViewItem.TYPE_CHILD){
                        isCurrent = true
                        it as FilesData
                        if (it.itemChecked){
                            if (it.tempList.isNullOrEmpty()){
                                selectedAllLength+=it.fileSize?:0L
                            }else{
                                it.tempList?.forEach {
                                    selectedAllLength+=it.fileSize?:0L
                                }
                            }
                        }
                    }
                }
            }
            withContext(Dispatchers.Main){
//                AppLogs.dLog(acTAG,"显示结果:${(allLength+(cacheLength?:0L)).formatSize()} item:${scanAdapter.items.size}")
                acBinding.tvTitle.text = (selectedAllLength+cacheLength).formatSize()
                acBinding.tvSelectedSize.text = (selectedAllLength+cacheLength).formatSize()
                if (isScan){
                    acBinding.cleanButton.text = getString(R.string.app_scanning)
                }else{
                    acBinding.cleanButton.text = getString(R.string.app_clean)
                }
                acBinding.cleanButton.setOneClick {
                    if (isScan)return@setOneClick
//                    if (selectedAllLength == 0L)return@setOneClick
                    if (isAndroid12()){
                        var data = scanAdapter.items.get(0) as ScanData
                        if (data.itemChecked.not()&& cacheFiles.isNullOrEmpty()){
                            //如果未选中内存就跳内存引导
                            showCache{
                                CleanLoadActivity.startCleanLoadActivity(
                                    this@CleanScanActivity,
                                    (selectedAllLength)
                                )
                            }
                        }else{
                            //如果选了内存直接清理
                            var data = scanAdapter.items.get(0) as ScanData
                            if (isAndroid12() && data.itemChecked){
                                val intent = Intent(StorageManager.ACTION_CLEAR_APP_CACHE)
                                startActivityForResult(intent,101)
                            }else{
                                CleanLoadActivity.startCleanLoadActivity(
                                    this@CleanScanActivity,
                                    (selectedAllLength)
                                )
                            }
                        }
                    }else{
                        CleanLoadActivity.startCleanLoadActivity(
                            this@CleanScanActivity,
                            (selectedAllLength)
                        )
                    }
                    CacheManager.saveCleanTips()
                }
            }
        }, failBack = {},Dispatchers.IO)
    }

    override fun setShowView() {
        acBinding.apply {
            cpv.apply {
                setAnimation("clean_load.json")
                playAnimation()
            }
            rv.apply {
                layoutManager = LinearLayoutManager(this@CleanScanActivity, LinearLayoutManager.VERTICAL,false)
                // 设置预加载，请调用以下方法
                adapter = scanAdapter
                scanAdapter.setOnDebouncedItemClick{adapter, view, position ->
                    var data = scanAdapter.getItem(position)
                    if(data == null)return@setOnDebouncedItemClick
                    if (data.dataType == ViewItem.TYPE_PARENT){
                        data as ScanData
                        if (data.isLoading)return@setOnDebouncedItemClick
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
                            list.forEach {
                                it.itemChecked = data.itemChecked
                            }

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
                    if(data == null || data.enableChecked.not())return@addOnDebouncedChildClick
                    if (data.dataType == ViewItem.TYPE_PARENT){
                        data as ScanData
                        if (data.type == CleanConfig.DATA_TYPE_CACHE){
                            if (cacheFiles.isNullOrEmpty()){
                                showCache()
                                return@addOnDebouncedChildClick
                            }
                        }
                        data.itemChecked = data.itemChecked.not()
                        updateChildChecked(position,data.itemChecked)
                    }else {
                        data as FilesData
                        if (isAndroid12() && data.scanType == DATA_TYPE_CACHE)return@addOnDebouncedChildClick
                        data.itemChecked = data.itemChecked.not()
                        scanAdapter.notifyItemChanged(position,"updateCheck")
                        updateParentChecked(position)
                    }
                    updateSelectedSize()
                }
                scanAdapter.addOnDebouncedChildClick(R.id.rlCacheTips) { adapter, view, position ->
                    showCache()
                }
            }
        }
        if (isRealAndroid11().not()){
            scanAdapter.add(ScanData().createCacheData(this@CleanScanActivity,false).apply {
                isLoading = true
            })
        }

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
        isScan = true
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
                delay(600)
                var list = mutableListOf<ViewItem>()
                if (isRealAndroid11().not()){
                    list.add(ScanData().createCacheData(this@CleanScanActivity).apply {
                        checkedAll(CleanConfig.cacheFiles.isNotEmpty())
                    })
                    CleanConfig.cacheFiles.forEach {
                        it.scanType = CleanConfig.DATA_TYPE_CACHE
                        it.itemChecked = true
                        list.add(it)
                        if (isAndroid12()){
                            it.enableChecked = false
                        }
                    }
                }

                list.add(ScanData().createJunkData(this@CleanScanActivity).apply {
                    checkedAll(true)
                })
                var junkIndex = list.size-1
                var junkLength = 0L
                CleanConfig.junkFiles.forEach {
                    it.scanType = CleanConfig.DATA_TYPE_JUNK
                    it.itemChecked = true
                    list.add(it)
                    var tempLength = 0L
                    it.tempList?.forEach {
                        junkLength+=it.fileSize?:0L
                        tempLength+=it.fileSize?:0L
                    }
                    if (tempLength == 0L){
                        it.enableChecked = false
                    }
                }
                (list.get(junkIndex) as ScanData).apply {
                    allLength = junkLength
                    if (junkLength == 0L){
                        enableChecked = false
                    }
                }
                var apkLength = 0L
                list.add(ScanData().createApksData(this@CleanScanActivity).apply {
                    checkedAll(true)
                })
                var apkIndex = list.size-1
                CleanConfig.apkFiles.forEach {
                    it.scanType = CleanConfig.DATA_TYPE_APK
                    it.itemChecked = true
                    list.add(it)
                    apkLength+=it.fileSize?:0L
                }
                (list.get(apkIndex) as ScanData).apply {
                    allLength = apkLength
                    if (apkLength == 0L){
                        enableChecked = false
                    }
                }
                list.add(ScanData().createResidualData(this@CleanScanActivity).apply {
                    checkedAll(true)
                })
                var residualIndex = list.size-1
                var residualLength = 0L
                CleanConfig.residualFiles.forEach {
                    it.scanType = CleanConfig.DATA_TYPE_RESIDUAL
                    it.itemChecked = true
                    list.add(it)
                    residualLength+=it.fileSize?:0L
                }
                (list.get(residualIndex) as ScanData).apply {
                    allLength = residualLength
                    if (residualLength == 0L){
                        enableChecked = false
                    }
                }
                list.add(ScanData().createADData(this@CleanScanActivity).apply {
                    checkedAll(true)
                })
                var adIndex = list.size-1
                var adLength = 0L
                CleanConfig.adFiles.forEach {
                    it.scanType = CleanConfig.DATA_TYPE_AD
                    it.itemChecked = true
                    list.add(it)
                    adLength+=it.fileSize?:0L
                }
                (list.get(adIndex) as ScanData).apply {
                    allLength = adLength
                    if (adLength == 0L){
                        enableChecked = false
                    }
                }
                scanAdapter.submitList(list)
                updateSelectedSize()
                if (isCacheGranted(false)){
                    if (cacheFiles.isNullOrEmpty()){
                        delay(1000)
                        clickCache(false)
                    }
                }else{
                    isScan = false
                }
            }, failBack = {
                isScan = false
                AppLogs.eLog(acTAG,it)
            },Dispatchers.Main)
        },5000L)
    }

    private fun showCache(cancelUnit:()-> Unit={}) {
        CachePop(this@CleanScanActivity).createPop{
            if (it == 0){
                clickCache()
            }else{
                cancelUnit.invoke()
            }
        }
    }

    private fun clickCache(forceScan:Boolean = true) {
        var permissionManager = CachePermissionManager(WeakReference(this@CleanScanActivity), onGranted = {
            scanAdapter.itemAnimation = null

            var data = scanAdapter.getItem(0)
            data as ScanData
            data!!.isLoading = true
            scanAdapter.notifyItemChanged(0,"updateLoad")
            viewModel.startScanCache(forceScan,onComplete={
                isScan = false
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
                    updateSelectedSize()
                }, failBack = {
                    AppLogs.eLog(acTAG,it)
                    updateSelectedSize()
                },Dispatchers.Main)
            }, onScanPath = {
                isScan = true
                addLaunch(success = {
                    scanAdapter.getItem(0)!!.apply {
                        (this as ScanData).apply {
                            allLength = it
                        }
                    }
                    scanAdapter.notifyItemChanged(0,"updateCache")
                    updateSelectedSize(it)
                }, failBack = {
                    AppLogs.eLog(acTAG,it)
                    updateSelectedSize()
                },Dispatchers.Main)
            })
        }, onDenied = {
            isScan = false
        })
        permissionManager.requestCachePermission(forceScan)
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


    var REQUEST_CODE_FOR_DIR: Int = 5411122

    //通过SAF获取权限
    fun startForSAF(activity: Activity) {
        val uri =
            Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata")
        val documentFile = DocumentFile.fromTreeUri(activity, uri)
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.setFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                    or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
        )
        checkNotNull(documentFile)
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, documentFile.uri)
        activity.startActivityForResult(intent, REQUEST_CODE_FOR_DIR)
    }

    @SuppressLint("WrongConstant")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var uri: Uri? = null
        if (requestCode == REQUEST_CODE_FOR_DIR && (data?.data.also { uri = it }) != null) {
            uri?.apply {
                if (data == null) return
                contentResolver.takePersistableUriPermission(
                    this,
                    data.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                )
                clickCache(false)
            }
        } else if (requestCode == 101) {
            if (resultCode == -1) {
                CleanLoadActivity.startCleanLoadActivity(
                    this@CleanScanActivity,
                    (selectedAllLength)
                )
            } else {
                var length = 0L
                addLaunch(success = {
                    var jumpChild = false
                    scanAdapter.items.forEach {
                        if (it.dataType == ViewItem.TYPE_PARENT) {
                            it as ScanData
                            if (it.type != CleanConfig.DATA_TYPE_CACHE) {
                                jumpChild = false
                                if (it.itemChecked) {
                                    length += it.allLength
                                    jumpChild = true
                                }
                            } else {
                                jumpChild = true
                            }
                        }
                        if (jumpChild.not()) {
                            if (it.dataType == ViewItem.TYPE_CHILD) {
                                it as FilesData
                                if (it.itemChecked) {
                                    if (it.tempList.isNullOrEmpty()) {
                                        length += it.fileSize?:0L
                                    } else {
                                        it.tempList?.forEach {
                                            length += it.fileSize?:0L
                                        }
                                    }
                                }
                            }
                        }
                    }
                    withContext(Dispatchers.Main){
                        if (length>0){
                            CleanLoadActivity.startCleanLoadActivity(
                                this@CleanScanActivity,
                                (length)
                            )
                        }else{
                            CompleteLoadActivity.startCompleteLoadActivity(this@CleanScanActivity,0,0)
                        }
                    }
                }, failBack = {})
            }
        }
    }
}