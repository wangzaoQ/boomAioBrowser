package com.boom.aiobrowser.ui.activity

import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.NewsActivityWebSourceBinding
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.tools.WebSourceManager.getSourceDetailsList
import com.boom.aiobrowser.tools.WebSourceManager.getSourceList
import com.boom.aiobrowser.ui.adapter.CategoryNewsAdapter
import com.boom.aiobrowser.ui.adapter.SafeFlexboxLayoutManager
import com.boom.aiobrowser.ui.adapter.WebSourceAdapter
import com.boom.aiobrowser.ui.adapter.custom.MySmooth
import com.boom.aiobrowser.ui.adapter.custom.MySmooth2
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
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
        acBinding.ivBack.setOneClick {
            finish()
        }
        categorySmallAdapter.setOnDebouncedItemClick{adapter, view, position ->
            if (oldCheck == position || oldCheck<0)return@setOnDebouncedItemClick
            categorySmallAdapter.items.get(oldCheck).uiCheck = false
            categorySmallAdapter.items.get(position).uiCheck = true
            oldCheck = position
            if (acBinding.rvCategory.layoutManager is SafeFlexboxLayoutManager){
                switchUI()
            }
            acBinding.rvCategory.requestLayout()
            acBinding.rvCategory.postDelayed({
                categorySmallAdapter.notifyDataSetChanged()
                val smooth = MySmooth(this@WebSourceActivity)
                smooth.targetPosition = position;
                acBinding.rvCategory.layoutManager?.startSmoothScroll(smooth)
                var listTo = -1
                for (i in 0 until listAdapter.items.size){
                    var data = listAdapter.items.get(i)
                    if (data.titleRes == categorySmallAdapter.items.get(position).titleRes){
                        listTo = i
                        break
                    }
                }
                if(listTo >=0){
                    acBinding.rvContent.postDelayed({
                    val smooth2 = MySmooth2(this@WebSourceActivity)
                    smooth2.targetPosition = listTo;
                    acBinding.rvContent.layoutManager?.startSmoothScroll(smooth2)
                    },0)
                }

            },0)
        }
    }

    private fun switchUI() {
        if (acBinding.rvCategory.layoutManager is LinearLayoutManager){
            acBinding.rvCategory.layoutManager = SafeFlexboxLayoutManager(this@WebSourceActivity).apply {
//                justifyContent = JustifyContent.FLEX_START
////                    flexWrap = FlexWrap.WRAP
//                flexDirection = FlexDirection.ROW; // 设置主轴方向为行
                flexDirection = FlexDirection.ROW
                setFlexWrap(FlexWrap.WRAP)
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
        PointEvent.posePoint(PointEventKey.web_store)
    }

    var move = false
}