package com.boom.downloader.task;

import com.boom.downloader.common.DownloadConstants;
import com.boom.downloader.listener.IDownloadTaskListener;
import com.boom.downloader.model.VideoTaskItem;
import com.boom.downloader.utils.LogUtils;
import com.boom.downloader.utils.VideoDownloadUtils;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

public abstract class VideoDownloadTask {

    protected static final int THREAD_COUNT = 6;
    protected static final int BUFFER_SIZE = VideoDownloadUtils.DEFAULT_BUFFER_SIZE;
    protected final VideoTaskItem mTaskItem;
    protected final String mFinalUrl;
    protected Map<String, String> mHeaders;
    protected File mSaveDir;
    protected String mSaveName;
    protected ThreadPoolExecutor mDownloadExecutor;
    protected IDownloadTaskListener mDownloadTaskListener;
    protected volatile boolean mDownloadFinished = false;
    protected final Object mDownloadLock = new Object();
    protected long mLastCachedSize = 0L;
    protected long mCurrentCachedSize = 0L;
    protected long mLastInvokeTime = 0L;
    protected float mSpeed = 0.0f;
    protected float mPercent = 0.0f;

    protected VideoDownloadTask(VideoTaskItem taskItem, Map<String, String> headers) {
        mTaskItem = taskItem;
        mHeaders = headers;
        mFinalUrl = taskItem.getFinalUrl();
//        mSaveName = VideoDownloadUtils.computeMD5(taskItem.getUrl());
        mSaveName = taskItem.getFileName();
        String name = "";
        try{
            if (mSaveName.contains(".")){
                name = mSaveName.substring(0,mSaveName.indexOf("."));
            }
        }catch (Exception e){

        }
        if (name.isEmpty()){
            name = mSaveName;
        }
        LogUtils.i(DownloadConstants.TAG,"VideoDownloadTask name:"+name + " mSaveName:"+mSaveName);
        mSaveDir = new File(VideoDownloadUtils.getDownloadConfig().getCacheRoot(),name);
        if (!mSaveDir.exists()) {
            mSaveDir.mkdir();
        }
        mTaskItem.setSaveDir(mSaveDir.getAbsolutePath());
    }

    public void setDownloadTaskListener(IDownloadTaskListener listener) {
        mDownloadTaskListener = listener;
    }

    public abstract void startDownload();

    public abstract void resumeDownload();

    public abstract void pauseDownload();

    protected void notifyOnTaskPaused() {
        if (mDownloadTaskListener != null) {
            mDownloadTaskListener.onTaskPaused();
        }
    }

    protected void notifyOnTaskFailed(Exception e) {
        if (mDownloadExecutor != null && mDownloadExecutor.isShutdown()) {
            return;
        }
        mDownloadExecutor.shutdownNow();
        mDownloadTaskListener.onTaskFailed(e);
    }

    protected void setThreadPoolArgument(int corePoolSize, int maxPoolSize) {
        if (mDownloadExecutor != null && !mDownloadExecutor.isShutdown()) {
            mDownloadExecutor.setCorePoolSize(corePoolSize);
            mDownloadExecutor.setMaximumPoolSize(maxPoolSize);
        }
    }
}
