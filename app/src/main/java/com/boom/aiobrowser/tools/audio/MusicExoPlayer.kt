package com.boom.aiobrowser.tools.audio

import android.content.Context
import android.media.AudioManager
import android.net.Uri
import androidx.media.AudioAttributesCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.Util
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlaybackException
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.SimpleExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.source.LoadEventInfo
import androidx.media3.exoplayer.source.MediaLoadData
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.util.EventLogger
import androidx.media3.ui.PlayerControlView
import androidx.media3.ui.PlayerView
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.tools.AppLogs

import com.google.android.exoplayer2.*

import java.io.IOException

/**
 * Created by cyl on 2018/5/11.
 */

object MusicExoPlayer: BasePlayer(), Player.Listener {

    private val TAG = "MusicExoPlayer"


    private val uAmpAudioAttributes = AudioAttributes.Builder()
        .setContentType(C.CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    //exoPlayer播放器
    private val exoPlayer: SimpleExoPlayer by lazy {
        SimpleExoPlayer.Builder(APP.instance).build().apply {
            setAudioAttributes(uAmpAudioAttributes, false)
        }
    }

    private var playbackListener: PlaybackListener? = null
    private var mediaDataSourceFactory: DataSource.Factory? = null
    private var audioFocusWrapper: AudioFocusWrapper? = null

    //    private var bandwidthMeter: DefaultBandwidthMeter = DefaultBandwidthMeter.Builder(this).build()
    private var videoTrackSelectionFactory: AdaptiveTrackSelection.Factory = AdaptiveTrackSelection.Factory()
    private val trackSelector = DefaultTrackSelector(APP.instance,videoTrackSelectionFactory)
    private var loadControl: LoadControl? = null

    private val audioManager = APP.instance.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val audioAttributes = AudioAttributesCompat.Builder()
        .setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
        .setUsage(AudioAttributesCompat.USAGE_MEDIA)
        .build()

    init {
        initPlayer(true)
    }

    override fun setPlayBackListener(listener: PlaybackListener?) {
        super.setPlayBackListener(listener)
        playbackListener = listener;
    }

    /**
     *
     * 初始化播放器
     */
    private fun initPlayer(playOnReady: Boolean) {
        //生成数据原实力
        mediaDataSourceFactory = DefaultDataSourceFactory(
            APP.instance,
            Util.getUserAgent(APP.instance, "MusicLakeApp")
        )
        //创建 player
        loadControl = DefaultLoadControl()
        exoPlayer.playWhenReady = playOnReady
        exoPlayer.addAnalyticsListener(PlayerEventLogger())
        audioFocusWrapper = AudioFocusWrapper(audioAttributes, audioManager, exoPlayer)
        exoPlayer.addListener(this)

        // ExoPlayer will manage the MediaSession for us.
//        mediaSessionConnector = MediaSessionConnector(mediaSession).also { connector ->
//            // Produces DataSource instances through which media data is loaded.
//            val dataSourceFactory = DefaultDataSourceFactory(
//                this, Util.getUserAgent(this, UAMP_USER_AGENT), null
//            )
//
//            // Create the PlaybackPreparer of the media session connector.
//            val playbackPreparer = UampPlaybackPreparer(
//                mediaSource,
//                exoPlayer,
//                dataSourceFactory
//            )
//
//            connector.setPlayer(exoPlayer)
//            connector.setPlaybackPreparer(playbackPreparer)
//            connector.setQueueNavigator(UampQueueNavigator(mediaSession))
//        }
    }

    fun bindView(playerView: PlayerView) {
        playerView.player = exoPlayer
    }

    fun bindControlView(controlView: PlayerControlView) {
        controlView.player = exoPlayer
    }

    fun addPlaybackListener(listener: PlaybackListener) {
        playbackListener = listener
    }

    fun removePlayBackListener() {
        playbackListener = null
    }

    override fun start() {
        super.start()
        exoPlayer.playWhenReady = true
    }

    override fun setVolume(vol: Float) {
        super.setVolume(vol)
        AppLogs.dLog(TAG, "vol = $vol")
        try {
            exoPlayer.volume = vol
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setDataSource(uri: String?) {
        super.setDataSource(uri)
        val mediaSource = uri?.let { buildMediaSource(it) }
        AppLogs.dLog(TAG, "setDataSource $uri $playWhenReady ${mediaSource == null}")
        exoPlayer?.playWhenReady = playWhenReady
        audioFocusWrapper?.setPlayWhenReady(playWhenReady)
        //准备播放来源。
        mediaSource?.let { exoPlayer?.prepare(it) }
    }

    /**
     * 单首歌曲转化成mediaSource
     */
    private fun buildMediaSource(url: String): MediaSource? {
        AppLogs.dLog(TAG, "buildMediaSource $url")
        val mediaItem = MediaItem.Builder().setUri(Uri.parse(url)).build()
        return buildMediaSource(mediaItem)
    }

    override fun isPlaying(): Boolean {
        exoPlayer?.let {
            return it.playbackState == Player.STATE_READY && it.playWhenReady
        }
        return false
    }

    /**
     * 返回正在播放状态
     */
    fun isLoading(): Boolean {
        exoPlayer?.let {
            return it.playbackState == Player.STATE_BUFFERING
        }
        return false
    }

    /**
     * 播放暂停
     */
    fun playPause() {
        exoPlayer?.playWhenReady = !isPlaying()
        audioFocusWrapper?.setPlayWhenReady(isPlaying())
    }

    override fun stop() {
        super.stop()
        exoPlayer?.stop()
    }

    override fun pause() {
        super.pause()
        exoPlayer?.playWhenReady = false
    }

    override fun position(): Long {
        exoPlayer?.let {
            return it.currentPosition
        }
        return 0
    }

    override fun isPrepared(): Boolean {
        exoPlayer?.let {
            return it.playbackState != Player.STATE_IDLE
        }
        return super.isPrepared()
    }


    /**
     * 滑动播放位置
     */
    override fun seekTo(positionMillis: Long) {
        super.seekTo(positionMillis)
        exoPlayer?.let {
            AppLogs.eLog(TAG, "seekTo $positionMillis ${it.duration}")
            if (positionMillis < 0 || positionMillis > it.duration)
                return
            it.seekTo(positionMillis)
        }
    }

    override fun duration(): Long {
        exoPlayer?.duration.let {
            if (it == null || it <= 0) return 0
            return it
        }
    }

    /**
     * 播放位置
     */
    override fun bufferedPercentage(): Int {
        super.bufferedPercentage()
        exoPlayer?.bufferedPercentage.let {
            if (it == null || it <= 0) return 0
            return it
        }
    }


    /**
     * 播放sessionId
     */
    override fun getAudioSessionId(): Int {
        exoPlayer.audioSessionId.let {
            if (it <= 0) return 0
            return it
        }
    }


    /**
     * 释放player
     */
    private fun destroyPlayer() {
        AppLogs.dLog(TAG, "destroyPlayer() called")
        if (exoPlayer != null) {
            exoPlayer.stop()
            exoPlayer.release()
        }
    }

    override fun release() {
        super.release()
        destroyPlayer()
    }

    private fun buildMediaSource(mediaItem: MediaItem): MediaSource? {
        mediaDataSourceFactory?.let {
            return ProgressiveMediaSource.Factory(it).createMediaSource(mediaItem)
        }
        return null
    }

    /*********************************************************************************
     *  Audio监听事件
     *********************************************************************************
     */
    /**
     * 获取audioSessionId 均衡器
     */

    /*********************************************************************************
     *  播放监听事件
     *********************************************************************************
     */
    private class PlayerEventLogger : EventLogger(trackSelector) {

        override fun onAudioSessionIdChanged(eventTime: AnalyticsListener.EventTime, audioSessionId: Int) {
            super.onAudioSessionIdChanged(eventTime, audioSessionId)
            AppLogs.dLog(TAG, "onAudioSessionIdChanged ${eventTime.realtimeMs} $audioSessionId")
        }

        override fun onTimelineChanged(eventTime: AnalyticsListener.EventTime, reason: Int) {
            super.onTimelineChanged(eventTime, reason)
            AppLogs.dLog(TAG, "onTimelineChanged ${eventTime.realtimeMs} $reason")
            playbackListener?.onPlaybackProgress(
                eventTime.currentPlaybackPositionMs,
                eventTime.realtimeMs,
                eventTime.totalBufferedDurationMs
            )
        }


        override fun onLoadingChanged(eventTime: AnalyticsListener.EventTime, isLoading: Boolean) {
            super.onLoadingChanged(eventTime, isLoading)
            AppLogs.dLog(TAG, "onLoadingChanged ${eventTime.realtimeMs} $isLoading")
            playbackListener?.onLoading(isLoading)
        }

        override fun onPlayerStateChanged(
            eventTime: AnalyticsListener.EventTime,
            playWhenReady: Boolean,
            state: Int
        ) {
            super.onPlayerStateChanged(eventTime, playWhenReady, state)
            AppLogs.dLog(TAG, "onPlayerStateChanged ${eventTime.realtimeMs} $playWhenReady $state")
            audioFocusWrapper?.setPlayWhenReady(playWhenReady)
            if (state == Player.STATE_ENDED) {
                playbackListener?.onCompletionNext()
            } else if (state == Player.STATE_READY) {
                playbackListener?.onPlayerStateChanged(playWhenReady)
            }
        }

        override fun onPlayerError(
            eventTime: AnalyticsListener.EventTime,
            error: PlaybackException
        ) {
            super.onPlayerError(eventTime, error)
            AppLogs.dLog(TAG, "onPlayerError ${eventTime.realtimeMs} ${error.message}")
            playbackListener?.onError()
        }

        override fun onLoadError(
            eventTime: AnalyticsListener.EventTime,
            loadEventInfo: LoadEventInfo,
            mediaLoadData: MediaLoadData,
            error: IOException,
            wasCanceled: Boolean
        ) {
            super.onLoadError(eventTime, loadEventInfo, mediaLoadData, error, wasCanceled)
            AppLogs.dLog(TAG, "onLoadError ${error.message}")
            if (error.message?.contains("403") == true) {
                AppLogs.dLog(TAG, "onLoadError 播放地址异常")
            } else {
                playbackListener?.onError()
            }
        }
    }

}

