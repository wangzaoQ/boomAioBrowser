package com.boom.aiobrowser.ui.view
import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.SeekBar
import com.boom.aiobrowser.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.boom.video.utils.CommonUtil
import com.boom.video.utils.Debuger
import com.boom.video.utils.GSYVideoType
import com.boom.video.video.NormalGSYVideoPlayer
import com.boom.video.video.base.GSYBaseVideoPlayer

class CustomVideoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): NormalGSYVideoPlayer(context, attrs) {

    var mCoverImage: ImageView? = null

    var mCoverOriginUrl: String? = null

    var mCoverOriginId = 0

    var mDefaultRes = 0


//    fun SampleCoverVideo(context: Context?) {
//        this(context)
//    }
//
//    fun SampleCoverVideo(context: Context?, attrs: AttributeSet?) {
//        super(context, attrs)
//    }


    val an2 by lazy {
        AnimationUtils.loadAnimation(
            context,
            R.anim.anim_alpha_out
        )
    }
    override fun init(context: Context?) {
        super.init(context)
        mCoverImage = findViewById<View>(R.id.thumbImage) as ImageView
        if (mThumbImageViewLayout != null &&
            (mCurrentState == -1 || mCurrentState == CURRENT_STATE_NORMAL || mCurrentState == CURRENT_STATE_ERROR)
        ) {
            mThumbImageViewLayout.visibility = VISIBLE
        }
    }


    fun loadCoverImage(url: String) {
        mCoverOriginUrl = url
//        mDefaultRes = res
        Glide.with(context.applicationContext)
            .setDefaultRequestOptions(
                RequestOptions()
                    .frame(1000000)

//                    .centerCrop()
//                    .error(res)
//                    .placeholder(res)
            )
            .load(url)
            .into(mCoverImage!!)
    }

    override fun getLayoutId(): Int {
        return R.layout.video_layout_cover
    }

    override fun startPlayLogic() {
        first = true
        super.startPlayLogic()
    }

    var first = true


    /******************* 下方两个重载方法，在播放开始前不屏蔽封面，不需要可屏蔽  */
    override fun onSurfaceUpdated(surface: Surface?) {
        super.onSurfaceUpdated(surface)
        if (mThumbImageViewLayout != null && mThumbImageViewLayout.visibility == VISIBLE && first) {
            mThumbImageViewLayout.visibility = GONE
            first = false
            Debuger.printfLog("Sample onSurfaceUpdated")

            mThumbImageViewLayout.startAnimation(an2)
            mThumbImageViewLayout.visibility = INVISIBLE
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        first = true
    }

    override fun setViewShowState(view: View, visibility: Int) {
        Debuger.printfLog("Sample setViewShowState")
        if (view === mThumbImageViewLayout && visibility != VISIBLE) {
            return
        }
        super.setViewShowState(view, visibility)
    }

    override fun onSurfaceAvailable(surface: Surface?) {
        super.onSurfaceAvailable(surface)
        if (GSYVideoType.getRenderType() != GSYVideoType.TEXTURE) {
            if (mThumbImageViewLayout != null && mThumbImageViewLayout.visibility == VISIBLE) {
                mThumbImageViewLayout.visibility = GONE
            }
        }
    }

    /******************* 下方重载方法，在播放开始不显示底部进度和按键，不需要可屏蔽  */
    protected var byStartedClick = false

    override fun onClickUiToggle(e: MotionEvent?) {
        if (mIfCurrentIsFullscreen && mLockCurScreen && mNeedLockFull) {
            setViewShowState(mLockScreen, VISIBLE)
            return
        }
        byStartedClick = true
        super.onClickUiToggle(e)
    }

    override fun changeUiToNormal() {
        super.changeUiToNormal()
        byStartedClick = false
    }

    override fun changeUiToPreparingShow() {
        super.changeUiToPreparingShow()
        Debuger.printfLog("Sample changeUiToPreparingShow")
        setViewShowState(mBottomContainer, INVISIBLE)
        setViewShowState(mStartButton, INVISIBLE)
    }

    override fun changeUiToPlayingBufferingShow() {
        super.changeUiToPlayingBufferingShow()
        Debuger.printfLog("Sample changeUiToPlayingBufferingShow")
        if (!byStartedClick) {
            setViewShowState(mBottomContainer, INVISIBLE)
            setViewShowState(mStartButton, INVISIBLE)
        }
    }

    override fun changeUiToPlayingShow() {
        super.changeUiToPlayingShow()
        Debuger.printfLog("Sample changeUiToPlayingShow")
        if (!byStartedClick) {
            setViewShowState(mBottomContainer, INVISIBLE)
            setViewShowState(mStartButton, INVISIBLE)
        }
    }

    override fun startAfterPrepared() {
        super.startAfterPrepared()
        Debuger.printfLog("Sample startAfterPrepared")
        setViewShowState(mBottomContainer, INVISIBLE)
        setViewShowState(mStartButton, INVISIBLE)
        setViewShowState(mBottomProgressBar, VISIBLE)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        byStartedClick = true
        super.onStartTrackingTouch(seekBar)
    }


    override fun startWindowFullscreen(
        context: Context?,
        actionBar: Boolean,
        statusBar: Boolean
    ): GSYBaseVideoPlayer? {
        val gsyBaseVideoPlayer: GSYBaseVideoPlayer =
            super.startWindowFullscreen(context, actionBar, statusBar)
        val sampleCoverVideo: CustomVideoView =
            gsyBaseVideoPlayer as CustomVideoView
        if (mCoverOriginUrl != null) {
            sampleCoverVideo.loadCoverImage(mCoverOriginUrl!!)
        }
        return gsyBaseVideoPlayer
    }


    override fun showSmallVideo(
        size: Point?,
        actionBar: Boolean,
        statusBar: Boolean
    ): GSYBaseVideoPlayer? {
        //下面这里替换成你自己的强制转化
        val sampleCoverVideo: CustomVideoView =
            super.showSmallVideo(size, actionBar, statusBar) as CustomVideoView
        sampleCoverVideo.mStartButton.setVisibility(GONE)
        sampleCoverVideo.mStartButton = null
        return sampleCoverVideo
    }

    override fun cloneParams(from: GSYBaseVideoPlayer, to: GSYBaseVideoPlayer) {
        super.cloneParams(from, to)
        val sf: CustomVideoView = from as CustomVideoView
        val st: CustomVideoView = to as CustomVideoView
        st.mShowFullAnimation = sf.mShowFullAnimation
    }


    /**
     * 退出window层播放全屏效果
     */
    override fun clearFullscreenLayout() {
        if (!mFullAnimEnd) {
            return
        }
        mIfCurrentIsFullscreen = false
        var delay = 0
        // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
        // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
        if (mOrientationUtils != null) {
            delay = mOrientationUtils.backToProtVideo()
            mOrientationUtils.isEnable = false
            if (mOrientationUtils != null) {
                mOrientationUtils.releaseListener()
                mOrientationUtils = null
            }
        }
        if (!mShowFullAnimation) {
            delay = 0
        }
        val vp: ViewGroup =
            CommonUtil.scanForActivity(context).findViewById(Window.ID_ANDROID_CONTENT)
        val oldF: View = vp.findViewById(fullId)
        if (oldF != null) {
            //此处fix bug#265，推出全屏的时候，虚拟按键问题
            val gsyVideoPlayer: CustomVideoView =
                oldF as CustomVideoView
            gsyVideoPlayer.mIfCurrentIsFullscreen = false
        }
        if (delay == 0) {
            backToNormal()
        } else {
            postDelayed({ backToNormal() }, delay.toLong())
        }
    }


}