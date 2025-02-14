package com.boom.aiobrowser.ui.adapter.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.ADEnum
import com.boom.aiobrowser.ad.AioADDataManager
import com.boom.aiobrowser.ad.AioADShowManager
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.base.BaseViewHolder
import com.boom.aiobrowser.data.NewsData
import com.boom.aiobrowser.databinding.BrowserItemHomeAdBinding
import com.boom.aiobrowser.point.AD_POINT
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.point.PointValueKey
import com.boom.base.adapter4.BaseQuickAdapter

internal class ADItem(parent: ViewGroup) : BaseViewHolder<BrowserItemHomeAdBinding>(
    BrowserItemHomeAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) {

    fun bind(item: NewsData, fragment: BaseFragment<*>?, position:Int, adapter: BaseQuickAdapter<*, *>) {
        viewBinding?.apply {
            var adPosId = AD_POINT.aobws_news_one
            var adEnum = ADEnum.NATIVE_AD
            if (item.adTag == ADEnum.BANNER_AD_NEWS_DETAILS.adName) {
                adPosId = ADEnum.BANNER_AD_NEWS_DETAILS.adName
                adEnum = ADEnum.BANNER_AD_NEWS_DETAILS
                line.setBackgroundColor(
                    ContextCompat.getColor(
                        adapter.context,
                        R.color.white
                    )
                )
            } else {
                line.setBackgroundColor(
                    ContextCompat.getColor(
                        adapter.context,
                        R.color.color_black_F7F7F9
                    )
                )
            }
            if (AioADDataManager.adFilter1().not()) {
                PointEvent.posePoint(PointEventKey.aobws_ad_chance, Bundle().apply {
                    putString(PointValueKey.ad_pos_id, adPosId)
                })
            }
            AioADShowManager(
                if (fragment == null) (adapter.context as BaseActivity<*>) else fragment!!.rootActivity,
                adEnum,
                "原生"
            ) {

            }.showNativeAD(flRoot, adPosId)
        }
    }
}