package com.boom.aiobrowser.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.ScanData
import com.boom.aiobrowser.databinding.CleanItemScanChildBinding
import com.boom.aiobrowser.tools.rotateAnim

class ScanItemView : LinearLayout {


    fun setScanData(item: ScanData) {
        binding.apply {
            ivTag.setImageResource(item.imgId)
            tvName.text = item.title
            if (item.isLoading){
                isAnimation = true
                ivEnd.setImageResource(item.imgId)
                ivEnd.animation = 2000L.rotateAnim()
            }else{
                isAnimation = false
                ivEnd.clearAnimation()
                if (item.itemChecked){
                    ivEnd.setImageResource(R.mipmap.ic_scan_item_checked)
                }else{
                    ivEnd.setImageResource(R.mipmap.ic_scan_item_unchecked)
                }
            }
        }
    }

    var binding: CleanItemScanChildBinding = CleanItemScanChildBinding.inflate(LayoutInflater.from(context), this, true)

    var isAnimation:Boolean = false

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
//        val ta = context?.obtainStyledAttributes(attrs, R.styleable.ScanItemStyle)
//        val icon = ta?.getResourceId(R.styleable.ScanItemStyle_itemIcon, 0)
//        val title = ta?.getString(R.styleable.ScanItemStyle_itemTitle)
//        val titleId = ta?.getResourceId(R.styleable.ScanItemStyle_itemTitleId, 0)
//        icon?.let { binding.ivTag.setImageResource(it) }
//        titleId?.let { binding.tvName.text = context.getString(it)}
//        title?.let { binding.tvTitle.text = it }
//        ta?.recycle()
    }



}