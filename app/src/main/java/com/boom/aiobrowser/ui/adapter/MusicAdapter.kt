package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.FileUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.AppInfo
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.data.LocationData
import com.boom.aiobrowser.data.MusicData
import com.boom.aiobrowser.databinding.BrowserItemCityBinding
import com.boom.aiobrowser.databinding.BrowserItemMusicBinding
import com.boom.aiobrowser.databinding.CleanProcessItemBinding
import com.boom.aiobrowser.databinding.FileItemListBinding
import com.boom.aiobrowser.databinding.FileItemPhotoBinding
import com.boom.aiobrowser.tools.GlideManager
import com.boom.aiobrowser.tools.clean.CleanToolsManager
import com.boom.aiobrowser.tools.clean.FileFilter.isApk
import com.boom.aiobrowser.tools.clean.FileFilter.isAudio
import com.boom.aiobrowser.tools.clean.FileFilter.isDoc
import com.boom.aiobrowser.tools.clean.FileFilter.isImage
import com.boom.aiobrowser.tools.clean.FileFilter.isVideo
import com.boom.aiobrowser.tools.clean.FileFilter.isZip
import com.boom.aiobrowser.tools.clean.formatSize
import com.boom.aiobrowser.tools.clean.getDocImg
import com.boom.base.adapter4.BaseQuickAdapter

class MusicAdapter(var fragment:BaseFragment<*>?) : BaseQuickAdapter<MusicData, MusicAdapter.VH>() {
    class VH(parent: ViewGroup, val viewBinding: BrowserItemMusicBinding = BrowserItemMusicBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: MusicData?) {
        if (item == null)return
        holder.viewBinding.apply {
            GlideManager.loadImg(fragment,ivMusic,item.uri,R.mipmap.ic_music_default,R.mipmap.ic_music_default)
            tvMusic.text = item.title?:""
            tvArtist.text = item.artist?:""
        }
    }

}