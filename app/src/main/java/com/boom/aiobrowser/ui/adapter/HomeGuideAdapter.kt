package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.BrowserFragmentSearchGuideBinding
import com.boom.aiobrowser.databinding.BrowserItemMainNewsBinding
import com.boom.aiobrowser.tools.GlideManager
import com.boom.aiobrowser.tools.TimeManager
import com.boom.aiobrowser.ui.adapter.NewsMainAdapter.VH
import com.boom.aiobrowser.ui.fragment.guide.GuideFragment
import com.boom.aiobrowser.ui.fragment.guide.HomeGuideFragment
import com.boom.base.adapter4.BaseQuickAdapter

class HomeGuideAdapter() : BaseQuickAdapter<Int, HomeGuideAdapter.VH>() {
    class VH(
        parent: ViewGroup,
        val viewBinding: BrowserFragmentSearchGuideBinding = BrowserFragmentSearchGuideBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: Int?) {
        if (item == null)return
        holder.viewBinding.apply {
            tvTips.text = "${item+1}"
            tvTitle.text = "${context.getString(R.string.app_method)} ${item+1}"
            when (item) {
                0 -> {
                    tvContent.text = context.getString(R.string.app_tt_tips_1)
                    ivBg.setImageResource(R.mipmap.bg_guide_tiktok1)
                }
                1 -> {
                    tvContent.text = context.getString(R.string.app_tt_tips_2)
                    ivBg.setImageResource(R.mipmap.bg_guide_tiktok2)
                }
                else -> {}
            }
        }
    }
}