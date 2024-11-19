package com.boom.aiobrowser.ui.activity

import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.NewsActivityWebSourceBinding
import com.boom.aiobrowser.tools.WebSourceManager.getSourceDetailsList
import com.boom.aiobrowser.tools.WebSourceManager.getSourceList
import com.boom.aiobrowser.ui.adapter.CategoryNewsAdapter
import com.boom.aiobrowser.ui.adapter.SafeFlexboxLayoutManager
import com.boom.aiobrowser.ui.adapter.WebSourceAdapter
import com.boom.aiobrowser.ui.adapter.custom.MySmooth
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import com.google.android.flexbox.JustifyContent

class WebSourceActivity: BaseActivity<NewsActivityWebSourceBinding>() {
    override fun getBinding(inflater: LayoutInflater): NewsActivityWebSourceBinding {
        return NewsActivityWebSourceBinding.inflate(layoutInflater)
    }

    var oldCheck = 0
    var mIndex = 0

    override fun setListener() {
        acBinding.flExpand.setOneClick {
            switchUI()
        }
        categorySmallAdapter.setOnDebouncedItemClick{adapter, view, position ->
            if (oldCheck == position || oldCheck<0)return@setOnDebouncedItemClick
            categorySmallAdapter.items.get(oldCheck).uiCheck = false
            categorySmallAdapter.items.get(position).uiCheck = true
            oldCheck = position
            if (acBinding.rvCategory.layoutManager is SafeFlexboxLayoutManager){
                switchUI()
            }
            categorySmallAdapter.notifyDataSetChanged()
            acBinding.rvCategory.postDelayed({
                val smooth = MySmooth(this@WebSourceActivity)
                smooth.targetPosition = position;
                acBinding.rvCategory.layoutManager?.startSmoothScroll(smooth);
//                smoothMoveToPosition(position,acBinding.rvCategory)
            },0)
        }
    }

    private fun switchUI() {
        if (acBinding.rvCategory.layoutManager is LinearLayoutManager){
            acBinding.rvCategory.layoutManager = SafeFlexboxLayoutManager(this@WebSourceActivity).apply {
                justifyContent = JustifyContent.FLEX_START
//                    flexWrap = FlexWrap.WRAP
            }
        }else{
            acBinding.rvCategory.layoutManager = LinearLayoutManager(this@WebSourceActivity,LinearLayoutManager.HORIZONTAL,false)
        }
    }

    val categorySmallAdapter by lazy {
        CategoryNewsAdapter()
    }

    val listAdapter by lazy {
        WebSourceAdapter()
    }


    override fun setShowView() {
        var sourceList = getSourceList()
        var sourceDetailsList = getSourceDetailsList()
        acBinding.apply {
            rvCategory.apply {
//                layoutManager = SafeFlexboxLayoutManager(this@WebSourceActivity).apply {
//                    justifyContent = JustifyContent.FLEX_START
////                    flexWrap = FlexWrap.WRAP
//                }

                layoutManager = LinearLayoutManager(this@WebSourceActivity,LinearLayoutManager.HORIZONTAL,false)
                adapter = categorySmallAdapter
                categorySmallAdapter.submitList(sourceList)
                addOnScrollListener(object : RecyclerView.OnScrollListener(){
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        //在这里进行第二次滚动（最后的100米！）
                        if (move) {
                            move = false
                            //获取要置顶的项在当前屏幕的位置，mIndex是记录的要置顶项在RecyclerView中的位置
                            val n: Int =
                                mIndex - (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                            if (0 <= n && n < acBinding.rvCategory.getChildCount()) {
                                //获取要置顶的项顶部离RecyclerView顶部的距离
                                val left: Int = acBinding.rvCategory.getChildAt(n-1).right
                                //最后的移动
                                acBinding.rvCategory.scrollBy(left, 0)
                            }
                        }
                    }
                })
            }
            rvContent.apply {
                layoutManager = LinearLayoutManager(this@WebSourceActivity,LinearLayoutManager.VERTICAL,false)
                adapter = listAdapter
                listAdapter.submitList(sourceDetailsList)
            }
        }
    }

    var move = false

    private fun smoothMoveToPosition(n: Int,rv:RecyclerView) {
        mIndex = n
        val firstItem: Int = (rv.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        val lastItem: Int = (rv.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        if (n <= firstItem) {
            rv.smoothScrollToPosition(n)
        } else if (n <= lastItem) {
            val top: Int = rv.getChildAt(n - firstItem).right
            rv.smoothScrollBy(0, top)
        } else {
            rv.smoothScrollToPosition(n)
            move = true
        }
    }

    private fun moveToPosition(n: Int,rv:RecyclerView) {
        //先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
        val firstItem: Int = (rv.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        val lastItem: Int = (rv.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        //然后区分情况
        if (n <= firstItem) {
            //当要置顶的项在当前显示的第一个项的前面时
            rv.scrollToPosition(n)
        } else if (n <= lastItem) {
            //当要置顶的项已经在屏幕上显示时
            val top: Int = rv.getChildAt(n - firstItem).getTop()
            rv.scrollBy(0, top)
        } else {
            //当要置顶的项在当前显示的最后一项的后面时
            rv.scrollToPosition(n)
            //这里这个变量是用在RecyclerView滚动监听里面的
            move = true
        }
    }
}