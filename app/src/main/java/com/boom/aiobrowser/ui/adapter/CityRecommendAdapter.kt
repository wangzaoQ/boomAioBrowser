package com.boom.aiobrowser.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.FileUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.AppInfo
import com.boom.aiobrowser.data.FilesData
import com.boom.aiobrowser.data.LocationData
import com.boom.aiobrowser.databinding.BrowserItemAlreadyCityBinding
import com.boom.aiobrowser.databinding.BrowserItemCityBinding
import com.boom.aiobrowser.databinding.BrowserItemRecommendCityBinding
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

class CityRecommendAdapter() : BaseQuickAdapter<LocationData, CityRecommendAdapter.VH>() {
    class VH(parent: ViewGroup, val viewBinding: BrowserItemRecommendCityBinding = BrowserItemRecommendCityBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: LocationData?) {
        if (item == null)return
        holder.viewBinding.apply {
            var builder = StringBuilder()
            builder.append(item.locationCity)
            if (item.locationCountryShort.isNullOrEmpty().not()){
                builder.append(",${item.locationCountryShort}")
            }
            if (item.code.isNullOrEmpty().not()){
                builder.append(",${item.code}")
            }
            tvCity.text = builder.toString()
        }
    }

}