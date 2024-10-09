package com.boom.aiobrowser.ui.fragment.guide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.databinding.BrowserFragmentVideoGuideBinding

class GuideFragment :BaseFragment<BrowserFragmentVideoGuideBinding>() {
    override fun startLoadData() {

    }

    override fun setListener() {
    }

    override fun setShowView() {
        var index = arguments?.getInt("position")

        fBinding.apply {
            if (index == 3){
                tvGuide.text = getString(R.string.app_guide_title3)
                tvContent.visibility = View.VISIBLE
                lav.visibility = View.GONE
            }else{
                lav.apply {
                    if (index == 0){
                        setAnimation("guide_1.json")
                        tvGuide.text = getString(R.string.app_guide_title0)
                    }else if(index == 1){
                        setAnimation("guide_3.json")
                        tvGuide.text = getString(R.string.app_guide_title1)
                    }else if(index == 2){
                        setAnimation("guide_2.json")
                        tvGuide.text = getString(R.string.app_guide_title2)
                    }
                    tvContent.visibility = View.GONE
                    lav.visibility = View.VISIBLE
                    playAnimation()
                }
            }
        }
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BrowserFragmentVideoGuideBinding {
        return BrowserFragmentVideoGuideBinding.inflate(layoutInflater)
    }

    companion object{
        fun newInstance(position: Int): GuideFragment{
            val args = Bundle()
            args.putInt("position",position)
            val fragment = GuideFragment()
            fragment.arguments = args
            return fragment
        }
    }
}