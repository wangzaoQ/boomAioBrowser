package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boom.aiobrowser.R
import com.boom.aiobrowser.databinding.BrowserFragmentSearchGuideBinding
import com.boom.base.adapter4.BaseQuickAdapter

class HomeGuideAdapter() : BaseQuickAdapter<Int, HomeGuideAdapter.VH>() {

    var fromApp = ""
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

            when (fromApp) {
                context.getString(R.string.video_tiktok) -> {
                    when (item) {
                        0 -> {
                            tvContent.text = context.getString(R.string.app_tt_tips_1)
                            ivBg.setImageResource(R.mipmap.bg_guide_tiktok1)
                        }
                        1 -> {
                            tvContent.text = context.getString(R.string.app_common_tips1)
                            ivBg.setImageResource(R.mipmap.bg_guide_common)
                        }
                        else -> {}
                    }
                }
                context.getString(R.string.app_x) -> {
                    when (item) {
                        0 -> {
                            tvContent.text = context.getString(R.string.app_x_tips_1)
                            ivBg.setImageResource(R.mipmap.bg_guide_x1)
                        }
                        1 -> {
                            tvContent.text = context.getString(R.string.app_common_tips1)
                            ivBg.setImageResource(R.mipmap.bg_guide_common)
                        }
                        2 -> {
                            tvContent.text = context.getString(R.string.app_x_tips_2)
                            ivBg.setImageResource(R.mipmap.bg_guide_x2)
                        }
                        3 -> {
                            tvContent.text = context.getString(R.string.app_common_tips1)
                            ivBg.setImageResource(R.mipmap.bg_guide_common)
                        }
                        else -> {}
                    }
                }
                context.getString(R.string.app_instagram) -> {
                    when (item) {
                        0 -> {
                            tvContent.text = context.getString(R.string.app_ins_tips_1)
                            ivBg.setImageResource(R.mipmap.bg_guide_ins1)
                        }
                        1 -> {
                            tvContent.text = context.getString(R.string.app_common_tips1)
                            ivBg.setImageResource(R.mipmap.bg_guide_common)
                        }
                        2 -> {
                            tvContent.text = context.getString(R.string.app_ins_tips_2)
                            ivBg.setImageResource(R.mipmap.bg_guide_ins2)
                        }
                        3 -> {
                            tvContent.text = context.getString(R.string.app_common_tips1)
                            ivBg.setImageResource(R.mipmap.bg_guide_common)
                        }
                        else -> {}
                    }
                }
                else -> {}
            }
        }
    }

    fun setFromAPP(fromApp:String){
        this.fromApp = fromApp
    }
}