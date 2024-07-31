package com.boom.aiobrowser.ui.pop

import android.animation.Animator
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SizeUtils.dp2px
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.databinding.BrowserPopSearchBinding
import com.boom.aiobrowser.ui.adapter.SearchEngineAdapter
import com.boom.base.adapter4.QuickAdapterHelper
import com.boom.base.adapter4.loadState.trailing.TrailingLoadStateAdapter
import pop.basepopup.BasePopupWindow
import pop.util.animation.AnimationHelper
import pop.util.animation.TranslationConfig
import java.lang.ref.WeakReference

class SearchPop(context: Context) : BasePopupWindow(context) {

    companion object{
        fun showPop(reference:WeakReference<BaseActivity<*>>,view: AppCompatImageView) {
            var activity: BaseActivity<*>? = reference.get() ?: return
            var searchPop = SearchPop(activity!!)
            searchPop.createPop(activity,view)
        }
    }
    init {
        setContentView(R.layout.browser_pop_search)
    }
    var popBinding :BrowserPopSearchBinding?=null
    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        popBinding = BrowserPopSearchBinding.bind(contentView)
    }

    fun createPop(activity: BaseActivity<*>,view:View){
        // 这里是位置显示方式,在屏幕的侧
//        var location = IntArray(2)
//        view.getLocationOnScreen(location);
//
//        val x = Math.abs(popupWindow.getContentView().getMeasuredWidth()-view.getWidth()) / 2;
//        val y = -(popupWindow.getContentView().getMeasuredHeight()+view.getHeight());

        setPopupGravityMode(GravityMode.RELATIVE_TO_ANCHOR, GravityMode.RELATIVE_TO_ANCHOR)
        setPopupGravity(Gravity.BOTTOM)
        setBackgroundColor(Color.TRANSPARENT)
        setOffsetX(dp2px(-12f))
        setOffsetY(dp2px(22f))

        popBinding?.rvSearch?.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
            var searchAdapter = SearchEngineAdapter(){
                APP.engineLiveData.postValue(it)
                dismiss()
            }
            var helper = QuickAdapterHelper.Builder(searchAdapter)
               .build()
            adapter = helper.adapter
            searchAdapter.submitList(mutableListOf<Int>().apply {
                add(0)
                add(1)
                add(2)
                add(3)
            })
        }
        showPopupWindow(view)
    }

    override fun onCreateShowAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withTranslation(TranslationConfig.FROM_TOP)
            .toShow()
    }


    override fun onCreateDismissAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withTranslation(TranslationConfig.TO_TOP)
            .toDismiss()
    }
}