package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.view.View
import android.view.animation.Animation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.databinding.BrowserPopMoreBinding
import com.boom.aiobrowser.databinding.BrowserPopSearchBinding
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.ui.activity.AboutActivity
import com.boom.aiobrowser.ui.activity.HistoryActivity
import com.boom.aiobrowser.ui.activity.MainActivity
import com.boom.base.adapter4.QuickAdapterHelper
import com.boom.base.adapter4.dragswipe.listener.OnItemSwipeListener
import com.boom.base.adapter4.util.addOnDebouncedChildClick
import com.boom.base.adapter4.util.setOnDebouncedItemClick
import pop.basepopup.BasePopupWindow
import pop.util.animation.AnimationHelper
import pop.util.animation.TranslationConfig

class MorePop(context: Context) : BasePopupWindow(context) {

    init {
        setContentView(R.layout.browser_pop_more)
    }

    var popBinding : BrowserPopMoreBinding?=null
    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        popBinding = BrowserPopMoreBinding.bind(contentView)
    }

    fun createPop(){
        popBinding?.apply {
            ivDelete.setOnClickListener {
                dismiss()
            }
            llNewTab.setOnClickListener {
                if (context is MainActivity){
                    (context as MainActivity).showTabPop()
                }
                dismiss()
            }
            llClearData.setOnClickListener {
                if (context is MainActivity){
                    (context as MainActivity).showTabPop()
                }
                dismiss()
            }
            llHistory.setOnClickListener {
                if (context is BaseActivity<*>){
                    (context as MainActivity).startActivity(Intent(context,HistoryActivity::class.java))
                }
                dismiss()
            }
            llAbout.setOnClickListener {
                if (context is BaseActivity<*>){
                    (context as MainActivity).startActivity(Intent(context,AboutActivity::class.java))
                }
                dismiss()
            }
        }
        showPopupWindow()
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

}