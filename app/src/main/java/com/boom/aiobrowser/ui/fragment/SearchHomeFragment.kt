package com.boom.aiobrowser.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.databinding.BrowserFragmentSearchHomeBinding
import com.boom.aiobrowser.point.PointValueKey
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.ui.activity.SearchActivity
import com.boom.aiobrowser.ui.pop.SubInfoPop
import com.boom.aiobrowser.ui.pop.SubPop

class SearchHomeFragment: BaseFragment<BrowserFragmentSearchHomeBinding>() {
    override fun startLoadData() {

    }

    override fun setListener() {
        fBinding.rlSearch.setOneClick {
            rootActivity.jumpActivity<SearchActivity>(Bundle().apply {
                putString(PointValueKey.from_type,"search")
            })
        }
        fBinding.ivVIP.setOneClick {
            if (CacheManager.isSubscribeMember.not()){
                SubPop(rootActivity, updateBack = {
                    updateVIPUI()
                }).createPop()
            }else{
                SubInfoPop(rootActivity).createPop()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateVIPUI()
    }

    override fun setShowView() {
    }

    private fun updateVIPUI() {
        if (CacheManager.isSubscribeMember){
            fBinding.ivVIP.setImageResource(R.mipmap.ic_vip_2)
        }else{
            fBinding.ivVIP.setImageResource(R.mipmap.ic_vip_1)
        }
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BrowserFragmentSearchHomeBinding {
        return BrowserFragmentSearchHomeBinding.inflate(layoutInflater)
    }
}