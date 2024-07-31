package com.boom.aiobrowser.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.databinding.LayoutTopBinding
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.SearchConfig
import com.boom.aiobrowser.ui.pop.SearchPop
import java.lang.ref.WeakReference

class TopTitleView :FrameLayout {

    var binding: LayoutTopBinding = LayoutTopBinding.inflate(LayoutInflater.from(context), this, true)


    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        binding.ivToolbarSearch.setOnClickListener {
            SearchPop.showPop(WeakReference(binding.ivToolbarSearch.context as BaseActivity<*>),binding.ivToolbarSearch)
        }
        binding.toolBarSearch.setOnClickListener {
            APP.jumpLiveData.postValue(JumpData().apply {
                jumpType = JumpConfig.JUMP_SEARCH
            })
        }
    }

    fun updateEngine(type: Int,update:Boolean = true) {
        if (update){
            CacheManager.engineType = type
        }
        when (type) {
            SearchConfig.SEARCH_ENGINE_GOOGLE->{
                binding.ivToolbarSearch.setImageResource(R.mipmap.ic_search_gg)
            }
            SearchConfig.SEARCH_ENGINE_BING->{
                binding.ivToolbarSearch.setImageResource(R.mipmap.ic_search_bing)
            }
            SearchConfig.SEARCH_ENGINE_YAHOO->{
                binding.ivToolbarSearch.setImageResource(R.mipmap.ic_search_yahoo)
            }
            SearchConfig.SEARCH_ENGINE_PERPLEXITY->{
                binding.ivToolbarSearch.setImageResource(R.mipmap.ic_search_perplexity)
            }
        }
    }
}