package com.boom.aiobrowser.ui.activity

import android.media.MediaPlayer
import android.view.LayoutInflater
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.data.MusicData
import com.boom.aiobrowser.databinding.BrowserActivityMusicPlayBinding
import com.boom.aiobrowser.tools.TimeManager
import com.boom.aiobrowser.tools.audio.MusicExoPlayer
import com.boom.aiobrowser.tools.audio.PlaybackListener
import com.boom.aiobrowser.tools.getBeanByGson

class MusicPlayActivity: BaseActivity<BrowserActivityMusicPlayBinding>() {

    override fun getBinding(inflater: LayoutInflater): BrowserActivityMusicPlayBinding {
        return BrowserActivityMusicPlayBinding.inflate(layoutInflater)
    }

    override fun setListener() {
    }

    var musicData:MusicData?=null

    override fun setShowView() {
        musicData = getBeanByGson(intent.getStringExtra("data"),MusicData::class.java)
        MusicExoPlayer.setDataSource(musicData?.uri)
        acBinding.apply {
            tvStartTime.text = TimeManager.getVideoTime(0L)
            tvEndTime.text = TimeManager.getVideoTime(musicData?.duration)
        }
        MusicExoPlayer.addPlaybackListener(object :PlaybackListener{
            override fun onCompletionNext() {

            }

            override fun onCompletionEnd() {
            }

            override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
            }

            override fun onPrepared() {
            }

            override fun onError() {
            }

            override fun onPlaybackProgress(position: Long, duration: Long, buffering: Long) {
            }

            override fun onLoading(isLoading: Boolean) {
            }

            override fun onPlayerStateChanged(isPlaying: Boolean) {
            }
        })
    }
}