package com.boom.aiobrowser.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.databinding.NewsFragmentBinding

class NewsFragment: BaseFragment<NewsFragmentBinding>() {

    var topic:String = ""

    override fun startLoadData() {

    }

    override fun setListener() {
    }

    override fun setShowView() {
        topic =  arguments?.getString("selectedTopic")?:""
    }

    companion object {
        fun newInstance(topic: String): BaseFragment<*>{
            val args = Bundle()
            args.putString("selectedTopic",topic)
            val fragment = NewsFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): NewsFragmentBinding {
        return NewsFragmentBinding.inflate(layoutInflater)
    }
}