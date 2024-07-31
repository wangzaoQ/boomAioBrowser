package com.boom.aiobrowser.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.JumpData
import com.boom.aiobrowser.databinding.BrowserFragmentSearchBinding
import com.boom.aiobrowser.databinding.BrowserFragmentTempBinding
import com.boom.aiobrowser.model.SearchViewModel
import com.boom.aiobrowser.net.SearchNet
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.getBeanByGson
import com.boom.aiobrowser.ui.JumpConfig
import com.boom.aiobrowser.ui.ParamsConfig

class SearchFragment : BaseFragment<BrowserFragmentSearchBinding>() {

    private val viewModel by viewModels<SearchViewModel>()

    var jumpData:JumpData?=null

    override fun startLoadData() {

    }

    override fun setListener() {
    }

    override fun setShowView() {
        jumpData = getBeanByGson(arguments?.getString(ParamsConfig.JSON_PARAMS)?:"",JumpData::class.java)

        fBinding.topRoot.updateTopView(1, searchRecent = {}, searchResult = {
//            viewModel.searchResult(it)
            var url = SearchNet.getSearchUrl(it)
            APP.jumpLiveData.postValue(JumpData().apply {
                jumpType = JumpConfig.JUMP_WEB
                jumpTitle = it
                jumpUrl = url
            })
        })
        fBinding.topRoot.updateEngine(CacheManager.engineType)

        fBinding.topRoot.binding.etToolBarSearch.setText("${jumpData?.jumpUrl}")
        fBinding.topRoot.binding.etToolBarSearch.selectAll()
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BrowserFragmentSearchBinding {
        return BrowserFragmentSearchBinding.inflate(layoutInflater)
    }
}