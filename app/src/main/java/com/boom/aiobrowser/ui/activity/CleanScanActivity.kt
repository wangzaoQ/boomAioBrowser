package com.boom.aiobrowser.ui.activity

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
import com.boom.aiobrowser.data.ScanData
import com.boom.aiobrowser.databinding.BrowserActivityCleanScanBinding
import com.boom.aiobrowser.model.CleanViewModel
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.BigDecimalUtils
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.formatSize
import com.boom.aiobrowser.tools.rotateAnim
import com.boom.aiobrowser.ui.adapter.ScanAdapter
import com.boom.base.adapter4.BaseQuickAdapter
import com.boom.base.adapter4.util.addOnDebouncedChildClick
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


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
        ScanAdapter(){
            updateSelectedSize()
        }
    }

    private fun updateSelectedSize() {
        addLaunch(success = {
            var allLength = 0L
            scanAdapter.items.forEach {
                it.childList.forEach {
                    if(it.itemChecked){
                        it.tempList?.forEach {
                            allLength+=it.fileSize
                        }
                        allLength+=it.fileSize
                    }
                }
            }
            withContext(Dispatchers.Main){
                acBinding.tvTitle.text = allLength.formatSize()
                acBinding.tvSelectedSize.text = allLength.formatSize()
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
                    if (data == null || data.childList.isNullOrEmpty()) {
                        return@setOnDebouncedItemClick
                    }
                    data.itemExpend = data.itemExpend.not()
                    scanAdapter.notifyItemChanged(position,"updateExpend")
                }
                scanAdapter.addOnDebouncedChildClick(R.id.ivEnd) { adapter, view, position ->
                    var data = scanAdapter.getItem(position)
                    if (data == null || data.childList.isNullOrEmpty()) {
                        return@addOnDebouncedChildClick
                    }
                    data.itemChecked = data.itemChecked.not()
                    data.childList.forEach {
                        it.itemChecked = data.itemChecked
                    }
                    scanAdapter.notifyItemChanged(position,"updateSelected")
                    updateSelectedSize()
                }
            }
            cpv.animation = 2000L.rotateAnim()
        }
        scanAdapter.submitList(mutableListOf<ScanData>().apply {
            add(ScanData().createJunkData(this@CleanScanActivity,false).apply {
                isLoading = true
            })
        })
        addLaunch(success = {
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
                    or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED)
            acBinding.ctl.layoutParams = params
//            acBinding.root.postDelayed({
//
//            },300L)
            var list = mutableListOf<ScanData>()
            list.add(ScanData().createJunkData(this@CleanScanActivity).apply {
                checkedAll(true)
            })
            list.add(ScanData().createApksData(this@CleanScanActivity).apply {
                checkedAll(true)
            })
            list.add(ScanData().createResidualData(this@CleanScanActivity).apply {
                checkedAll(true)
            })
            list.add(ScanData().createADData(this@CleanScanActivity).apply {
                checkedAll(true)
            })
            scanAdapter.submitList(list)
            acBinding.cleanButton.text = getString(R.string.app_clean)
            updateSelectedSize()
        })
    }
}