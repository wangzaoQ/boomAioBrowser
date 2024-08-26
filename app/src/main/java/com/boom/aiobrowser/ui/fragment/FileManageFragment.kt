package com.boom.aiobrowser.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.databinding.FileFragmentFileManagerBinding
import com.boom.aiobrowser.ui.adapter.FileManageAdapter
import com.boom.aiobrowser.ui.adapter.NewsMainAdapter
import com.boom.base.adapter4.QuickAdapterHelper

class FileManageFragment : BaseFragment<FileFragmentFileManagerBinding>() {

    val fileManageAdapter by lazy {
        FileManageAdapter()
    }

    override fun startLoadData() {

    }

    override fun setListener() {
    }

    override fun setShowView() {
        fBinding.apply {
            rv.apply {
                layoutManager = GridLayoutManager(rootActivity, 4)
                rv.adapter = QuickAdapterHelper.Builder(fileManageAdapter).build().adapter
            }

        }
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FileFragmentFileManagerBinding {
        return FileFragmentFileManagerBinding.inflate(layoutInflater)
    }
}