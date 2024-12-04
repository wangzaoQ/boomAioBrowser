package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils.dp2px
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.NewsItemTrendingListBinding
import com.boom.base.adapter4.BaseQuickAdapter
import kotlin.random.Random

class TrendingNewsAdapter: BaseQuickAdapter<NewsData, TrendingNewsAdapter.VH>() {
    class VH(
        parent: ViewGroup,
        val viewBinding: NewsItemTrendingListBinding = NewsItemTrendingListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: NewsData?) {
        if (item == null) return
        holder.viewBinding?.apply{
            tvNum.text = "${position+1}"
            if (item.isLoading){
                rlTemp.visibility = View.VISIBLE
                (ivTemp1.layoutParams as LinearLayoutCompat.LayoutParams).width = dp2px(Random.nextInt(68, 129).toFloat())
            }else{
                rlTemp.visibility = View.GONE
                if (position == 0){
                    ivTips.visibility = View.VISIBLE
                    ivTips.setImageResource(R.mipmap.ic_trending_hot)
                    tvNum.setTextColor(ContextCompat.getColor(context,R.color.color_red_fc0000))
                }else if (position == 1){
                    ivTips.visibility = View.VISIBLE
                    ivTips.setImageResource(R.mipmap.ic_trending_new)
                    tvNum.setTextColor(ContextCompat.getColor(context,R.color.color_red_fc5d00))
                }else if (position == 2){
                    ivTips.visibility = View.VISIBLE
                    ivTips.setImageResource(R.mipmap.ic_trending_hot)
                    tvNum.setTextColor(ContextCompat.getColor(context,R.color.color_red_fca800))
                }else{
                    ivTips.visibility = View.GONE
                    if (item.isTrendTop){
                        ivTips2.visibility = View.VISIBLE
                    }else{
                        ivTips2.visibility = View.GONE
                    }
                    tvNum.setTextColor(ContextCompat.getColor(context,R.color.black_33))
                }
                if (item.tdetai.isNullOrEmpty().not()){
                    tvTitle.text = item.tdetai!!.get(0)
                    tvContent.text = item.tconsi
                }
            }
        }
    }
}