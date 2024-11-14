package com.boom.aiobrowser.ui.view

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.blankj.utilcode.util.KeyboardUtils
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.databinding.LayoutTopBinding
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.other.JumpConfig
import com.boom.aiobrowser.other.ParamsConfig
import com.boom.aiobrowser.other.SearchConfig
import com.boom.aiobrowser.ui.activity.SearchActivity
import com.boom.aiobrowser.ui.pop.SearchPop
import java.lang.ref.WeakReference

class TopTitleView : FrameLayout {

    var binding: LayoutTopBinding =
        LayoutTopBinding.inflate(LayoutInflater.from(context), this, true)


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        binding.ivToolbarSearch.setOnClickListener {
            SearchPop.showPop(
                WeakReference(binding.ivToolbarSearch.context as BaseActivity<*>),
                binding.ivToolbarSearch
            )
        }
        binding.toolBarSearch.setOnClickListener {
            var data = JumpDataManager.getCurrentJumpData(updateData = jumpData,tag ="TopTitleView 点击搜索").apply {
                jumpType = JumpConfig.JUMP_SEARCH
            }

            context.startActivity(Intent(context,SearchActivity::class.java).apply {
                putExtra(ParamsConfig.JSON_PARAMS, toJson(data))
            })
        }
    }

    fun updateTopView(
        type: Int,
        searchRecent: (content: String) -> Unit = {},
        searchResult: (content: String) -> Unit = {},
        searchRefresh: () -> Unit={},
    ) {
        if (type == 0) {
            binding.tvToolbarSearch.visibility = View.VISIBLE
            binding.etToolBarSearch.visibility = View.GONE
        } else if (type == 1) {
            binding.tvToolbarSearch.visibility = View.GONE
            binding.etToolBarSearch.visibility = View.VISIBLE
            binding.etToolBarSearch.setOnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    searchResult.invoke(binding.etToolBarSearch.text.toString().trim())
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
            binding.etToolBarSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    var content = binding.etToolBarSearch.text.toString().trim()
                    searchRecent.invoke(content)
                }
            })
            binding.etToolBarSearch.postDelayed({
                requestFocus()
                KeyboardUtils.showSoftInput(binding.etToolBarSearch)
            },0)
        }else if (type == 2){
            //webFragment
            binding.tvToolbarSearch.visibility = View.VISIBLE
            binding.etToolBarSearch.visibility = View.GONE
            binding.ivRefresh.visibility = View.VISIBLE
            binding.ivRefresh.setOnClickListener {
                searchRefresh.invoke()
            }
        }
        if (CacheManager.browserStatus == 1){
            binding.ivPrivate.visibility = View.VISIBLE
        }else{
            binding.ivPrivate.visibility = View.GONE
        }
    }

    fun updateEngine(type: Int, update: Boolean = true) {
        if (update) {
            CacheManager.engineType = type
        }
        when (type) {
            SearchConfig.SEARCH_ENGINE_GOOGLE -> {
                binding.ivToolbarSearch.setImageResource(R.mipmap.ic_search_gg)
            }

            SearchConfig.SEARCH_ENGINE_BING -> {
                binding.ivToolbarSearch.setImageResource(R.mipmap.ic_search_bing)
            }

            SearchConfig.SEARCH_ENGINE_YAHOO -> {
                binding.ivToolbarSearch.setImageResource(R.mipmap.ic_search_yahoo)
            }

            SearchConfig.SEARCH_ENGINE_PERPLEXITY -> {
                binding.ivToolbarSearch.setImageResource(R.mipmap.ic_search_perplexity)
            }
        }
    }
    var jumpData:JumpData?=null

    fun setData(jumpData: JumpData?) {
        this.jumpData = jumpData
    }
}