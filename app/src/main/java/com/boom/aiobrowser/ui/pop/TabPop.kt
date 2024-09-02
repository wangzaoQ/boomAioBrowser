package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.graphics.Canvas
import android.util.Log
import android.view.View
import android.view.animation.Animation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.databinding.BrowserPopTabBinding
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.activity.WebDetailsActivity
import com.boom.aiobrowser.ui.adapter.TabAdapter
import com.boom.base.adapter4.QuickAdapterHelper
import com.boom.base.adapter4.dragswipe.QuickDragAndSwipe
import com.boom.base.adapter4.dragswipe.listener.OnItemSwipeListener
import com.boom.base.adapter4.util.addOnDebouncedChildClick
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import pop.basepopup.BasePopupWindow
import pop.util.animation.AnimationHelper
import pop.util.animation.TranslationConfig

class TabPop(context: Context) : BasePopupWindow(context) {
    init {
        setContentView(R.layout.browser_pop_tab)
    }

    var popBinding: BrowserPopTabBinding? = null


    val tabAdapter by lazy {
        TabAdapter()
    }

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        popBinding = BrowserPopTabBinding.bind(contentView)
    }

    var browserStatus = 0
    var quickDragAndSwipe: QuickDragAndSwipe = QuickDragAndSwipe()
        .setDragMoveFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN)
        .setSwipeMoveFlags(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        .setItemViewSwipeEnabled(true)
        .setLongPressDragEnabled(false) //关闭默认的长按拖拽功能，通过自定义长按事件进行拖拽


    fun createPop(){
        popBinding?.rlTabRoot?.addScrollBack {
            if (it>200){
                dismiss()
            }
        }
        browserStatus = CacheManager.browserStatus
//        setPopupGravityMode(GravityMode.RELATIVE_TO_ANCHOR, GravityMode.RELATIVE_TO_ANCHOR)
//        setPopupGravity(Gravity.TOP)
//        setBackgroundColor(Color.TRANSPARENT)
        popBinding?.apply {
            rv.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                var helper = QuickAdapterHelper.Builder(tabAdapter).build()

                val swipeListener: OnItemSwipeListener = object : OnItemSwipeListener {
                    override fun onItemSwipeStart(
                        viewHolder: RecyclerView.ViewHolder?,
                        bindingAdapterPosition: Int
                    ) {
                    }

                    override fun onItemSwipeEnd(
                        viewHolder: RecyclerView.ViewHolder,
                        bindingAdapterPosition: Int
                    ) {
                    }

                    override fun onItemSwiped(
                        viewHolder: RecyclerView.ViewHolder,
                        direction: Int,
                        bindingAdapterPosition: Int
                    ) {
                    }

                    override fun onItemSwipeMoving(
                        canvas: Canvas,
                        viewHolder: RecyclerView.ViewHolder,
                        dX: Float,
                        dY: Float,
                        isCurrentlyActive: Boolean
                    ) {
                    }
                }
                quickDragAndSwipe.attachToRecyclerView(rv)
                    .setDataCallback(tabAdapter)
                    .setItemSwipeListener(swipeListener)

                // 设置预加载，请调用以下方法
                // helper.trailingLoadStateAdapter?.preloadSize = 1
                adapter = helper.adapter
                // 打开空布局功能
                tabAdapter.addOnDebouncedChildClick(R.id.ivDelete) { adapter, view, position ->
                    tabAdapter.removeAt(position)
                    if (tabAdapter.items.isNullOrEmpty()){
                        if (browserStatus == 0){
                            JumpDataManager.saveBrowserTabList(browserStatus,tabAdapter.items as MutableList<JumpData>,tag = "tabPop 删除item时更新")
                            CacheManager.browserStatus = browserStatus
                            var list= JumpDataManager.getBrowserTabList(0,tag = "tabPop 删除item时")
                            toLiveData(list.get(0))
                            dismiss()
                        }else{
                            JumpDataManager.saveBrowserTabList(browserStatus,tabAdapter.items as MutableList<JumpData>,tag = "tabPop 删除item时更新")
                            popBinding!!.emptyView.llEmpty.visibility = View.VISIBLE
                        }
                    }else{
                        var data = tabAdapter.items.get(tabAdapter.items.size-1)
                        data.isCurrent = true
                        JumpDataManager.saveBrowserTabList(browserStatus,tabAdapter.items as MutableList<JumpData>,tag = "tabPop 删除item时更新")
                        tabAdapter.notifyDataSetChanged()
                    }
                }
                tabAdapter.setOnDebouncedItemClick{adapter, view, position ->
                    addOrSelected = true
                    var data = tabAdapter.items.get(position)
                    JumpDataManager.resetSelectedByStatus(data,browserStatus,tag = "tabPop 点击item时更新 当前jumpData")
                    CacheManager.browserStatus = browserStatus
                    toLiveData(data)
                    dismiss()
                }
            }
            var browserStatus = CacheManager.browserStatus == 0
            tvNormal.isEnabled = browserStatus.not()
            tvPrivate.isEnabled = browserStatus
            tvNormal.setOnClickListener {
                updateStatus()
                loadNormalData()
            }
            tvPrivate.setOnClickListener {
                updateStatus()
                loadPrivateData()
            }
        }
        popBinding!!.ivAdd.setOnClickListener {
            addOrSelected = true
            CacheManager.browserStatus = browserStatus
            toLiveData(JumpDataManager.addTab(browserStatus,"点击添加按钮"))
            dismiss()
        }
        showPopupWindow()
        if (browserStatus== 0){
            loadNormalData()
        }else{
            loadPrivateData()
        }
        intoBrowserStatus = browserStatus
        if (tabAdapter.items.isNotEmpty()){
            intoBrowserId = tabAdapter.items.get(tabAdapter.items.size-1).dataId
        }
    }



    private fun loadPrivateData() {
        var list = JumpDataManager.getBrowserTabList(browserStatus,tag = "tabPop loadPrivateData 获取tabList")
        if (list.isNullOrEmpty()){
            popBinding!!.emptyView.llEmpty.visibility = View.VISIBLE
        }
        tabAdapter.submitList(list)
    }

    private fun loadNormalData() {
        popBinding!!.emptyView.llEmpty.visibility = View.GONE
        tabAdapter.submitList(JumpDataManager.getBrowserTabList(browserStatus,tag = "tabPop loadNormalData 获取tabList"))
    }

    private fun updateStatus() {
        if (browserStatus == 1){
            browserStatus = 0
        }else{
            browserStatus = 1
        }
        popBinding?.apply {
            tvNormal.isEnabled = tvNormal.isEnabled.not()
            tvPrivate.isEnabled = tvPrivate.isEnabled.not()
        }
    }

    override fun onCreateShowAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withTranslation(TranslationConfig.FROM_BOTTOM)
            .toShow()
    }

    override fun onCreateDismissAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withTranslation(TranslationConfig.TO_BOTTOM)
            .toDismiss()
    }

    /**
     *
     * 正常模式下有一个默认的item  隐私模式无
     * if(选择/增加 item 的操作){
     *      直接跳转关闭弹窗，并自动切换到当前选择的模式
     * }else{
     *     if(进入时为正常模式){
     *     //   期间隐私模式下item的删除无影响，走下面的逻辑
     *            if(数据未发生变化){
     *                  关闭时不进行操作
     *            }else{
     *                  if(正常模式还有数据){
     *                       关闭时自动跳转到正常模式最后一个item
     *                  }else{
     *                      自动增加一个item 并关闭弹窗自动跳转
     *                  }
     *            }
     *     }else{
     *         if(数据未发生变化){
     *          关闭时不进行操作
     *        } else {
     *        //期间正常模式下item 删除后 如果正常模式还有数据则无影响走下面逻辑，如果正常模式最后一条被删除就中断操作 自动增加一个item 并关闭弹窗自动跳转
     *          if(隐私模式还有数据){
     *              关闭时自动跳转到隐私模式最后一个item
     *          }else{
     *              显示隐私模式ui，关闭后自动跳转到 正常模式最后一个item
     *          }
     *        }
     *     }
     *
     * }
     */

    var intoBrowserStatus = 0
    var intoBrowserId = 0L
    var addOrSelected = false
    override fun onDismiss() {
        if (addOrSelected.not()){
            if (intoBrowserStatus == 1 && CacheManager.tabDataListPrivate.isNotEmpty()){
                var privateList = CacheManager.tabDataListPrivate
                if (intoBrowserId!=privateList.get(privateList.size-1).dataId){
                    //如果最后一个数据发生变化
                    toLiveData(privateList.get(privateList.size-1))
                }
            }else{
                var normalList = CacheManager.tabDataListNormal
                if (intoBrowserId!=normalList.get(normalList.size-1).dataId){
                    //如果最后一个数据发生变化
                    CacheManager.browserStatus = 0
                    toLiveData(normalList.get(normalList.size-1))
                }
            }
        }
        super.onDismiss()
    }

    fun toLiveData(data:JumpData?){
        if (context is WebDetailsActivity){
            APP.jumpWebLiveData.postValue(data)
        }else{
            APP.jumpLiveData.postValue(data)
        }
    }

}