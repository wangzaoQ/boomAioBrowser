package com.boom.aiobrowser.ui.pop

import android.content.Context
import android.os.Bundle
import android.view.View
import com.boom.aiobrowser.R
import com.boom.aiobrowser.data.MusicData
import com.boom.aiobrowser.databinding.BrowserPopMusicBinding
import com.boom.aiobrowser.tools.JumpDataManager.jumpActivity
import com.boom.aiobrowser.tools.audio.MusicExoPlayer
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.activity.MusicPlayActivity
import pop.basepopup.BasePopupWindow

class MusicPop(context: Context) : BasePopupWindow(context)  {

    init {
        setContentView(R.layout.browser_pop_music)
    }

    var defaultBinding: BrowserPopMusicBinding? = null

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        defaultBinding = BrowserPopMusicBinding.bind(contentView)
    }

    fun createPop(data: MusicData, callBack: (type:Int) -> Unit){
        defaultBinding?.apply {
            llPlay.setOnClickListener {
                context.jumpActivity<MusicPlayActivity>(Bundle().apply {
                    putString("data", toJson(data))
                })
                dismiss()
            }
        }
        showPopupWindow()
    }
}