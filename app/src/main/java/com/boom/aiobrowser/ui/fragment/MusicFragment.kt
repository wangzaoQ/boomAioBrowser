package com.boom.aiobrowser.ui.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.databinding.BrowserFragmentMusicBinding
import com.boom.aiobrowser.model.MusicViewModel
import com.boom.aiobrowser.tools.audio.MusicExoPlayer
import com.boom.aiobrowser.tools.audio.MusicManager
import com.boom.aiobrowser.ui.adapter.MusicAdapter
import com.boom.aiobrowser.ui.pop.MusicPop
import com.boom.base.adapter4.util.addOnDebouncedChildClick
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import java.lang.ref.WeakReference

class MusicFragment: BaseFragment<BrowserFragmentMusicBinding>() {

    private val viewModel by lazy {
        viewModels<MusicViewModel>()
    }

    override fun startLoadData() {
        val hasPermission = XXPermissions.isGranted(
            rootActivity,
            Permission.READ_MEDIA_AUDIO
        )
        if (hasPermission){
            viewModel.value.getLocalMusic()
        }
    }

    override fun setListener() {
        fBinding.smart.apply {
            setOnRefreshListener {
                startLoadData()
                fBinding.smart.finishRefresh()
            }
        }
        fBinding.ivAllow.setOneClick {
            MusicManager.requestAudioPermission(WeakReference(rootActivity), onSuccess = {
                startLoadData()
            }, onFail = {})
        }
        viewModel.value.musicLiveData.observe(this){
            fBinding.tvMusicSize.text = "(${it.size})"
            musicAdapter.submitList(it)
        }
        musicAdapter.addOnDebouncedChildClick(R.id.ivMore) { adapter, view, position ->
            var data = musicAdapter.getItem(position)?:return@addOnDebouncedChildClick
            MusicPop(rootActivity).createPop(data, callBack = {

            })
        }
    }

    val musicAdapter by lazy {
        MusicAdapter(this)
    }

    override fun setShowView() {
        val hasPermission = XXPermissions.isGranted(
            rootActivity,
            Permission.READ_MEDIA_AUDIO
        )
        if (hasPermission){
            fBinding.rlMusicPermission.visibility = View.GONE
            fBinding.llMusic.visibility = View.VISIBLE
        }else{
            fBinding.rlMusicPermission.visibility = View.VISIBLE
            fBinding.llMusic.visibility = View.GONE
        }
        fBinding.rvMusic.apply {
            layoutManager = LinearLayoutManager(rootActivity,LinearLayoutManager.VERTICAL,false)
            adapter = musicAdapter
        }
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BrowserFragmentMusicBinding {
        return BrowserFragmentMusicBinding.inflate(layoutInflater)
    }
}