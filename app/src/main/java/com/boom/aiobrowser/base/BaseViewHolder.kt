package com.boom.aiobrowser.base// BaseViewHolder.kt
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

// BaseViewHolder.kt
open class BaseViewHolder<T : ViewBinding>(viewBinding: T) : RecyclerView.ViewHolder(viewBinding.root) {
    var viewBinding: T? = null

    init {
        this.viewBinding = viewBinding
    }
}