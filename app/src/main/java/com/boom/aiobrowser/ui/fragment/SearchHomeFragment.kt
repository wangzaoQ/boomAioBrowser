package com.boom.aiobrowser.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.databinding.BrowserFragmentSearchHomeBinding
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.ui.activity.SearchActivity

class SearchHomeFragment: BaseFragment<BrowserFragmentSearchHomeBinding>() {
    override fun startLoadData() {

    }

    override fun setListener() {
        fBinding.rlSearch.setOneClick {
            rootActivity.jumpActivity<SearchActivity>(Bundle().apply {
                putString(PointValueKey.from_type,"search")
            })
        }
    }

    override fun setShowView() {
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BrowserFragmentSearchHomeBinding {
        return BrowserFragmentSearchHomeBinding.inflate(layoutInflater)
    }
}