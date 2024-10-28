package com.boom.aiobrowser.ui.fragment.guide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.databinding.BrowserFragmentSearchGuideBinding

class HomeGuideFragment :BaseFragment<BrowserFragmentSearchGuideBinding>() {
    override fun startLoadData() {

    }

    override fun setListener() {
    }

    override fun setShowView() {
        var index = arguments?.getInt("position")?:0

        fBinding.apply {
            tvTips.text = "${index+1}"
            tvTitle.text = "${getString(R.string.app_method)} ${index+1}"
            when (index) {
                0 -> {
                    tvContent.text = getString(R.string.app_tt_tips_1)
                    ivBg.setImageResource(R.mipmap.bg_guide_tiktok1)
                }
                1 -> {
                    tvContent.text = getString(R.string.app_common_tips1)
                    ivBg.setImageResource(R.mipmap.bg_guide_common)
                }
                else -> {}
            }
        }
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BrowserFragmentSearchGuideBinding {
        return BrowserFragmentSearchGuideBinding.inflate(layoutInflater)
    }

    companion object{
        fun newInstance(position: Int): HomeGuideFragment{
            val args = Bundle()
            args.putInt("position",position)
            val fragment = HomeGuideFragment()
            fragment.arguments = args
            return fragment
        }
    }
}