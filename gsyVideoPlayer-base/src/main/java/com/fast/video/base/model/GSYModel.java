package com.fast.video.base.model;

import java.io.BufferedInputStream;
import java.io.File;
import java.util.Map;

/**
 * 视频内部接受数据model
 * Created by shuyu on 2016/11/11.
 */

public class GSYModel {

    String url;

    File mCachePath;

    Map<String, String> mapHeadData;

    float speed = 1;

    boolean looping;

    boolean isCache;

    String overrideExtension;

    /**
     * 视频元数据输入流
     */
    BufferedInputStream videoBufferedInputStream;

    public GSYModel(String url, Map<String, String> mapHeadData, boolean loop, float speed, boolean isCache, File cachePath, String overrideExtension) {
        this.url = url;
        this.mapHeadData = mapHeadData;
        this.looping = loop;
        this.speed = speed;
        this.isCache = isCache;
        this.mCachePath = cachePath;
        this.overrideExtension = overrideExtension;
    }

    public GSYModel(BufferedInputStream videoBufferedInputStream, Map<String, String> mapHeadData, boolean loop, float speed, boolean isCache, File cachePath, String overrideExtension) {
        this.mapHeadData = mapHeadData;
        this.looping = loop;
        this.speed = speed;
        this.isCache = isCache;
        this.mCachePath = cachePath;
        this.overrideExtension = overrideExtension;
        this.videoBufferedInputStream = videoBufferedInputStream;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getMapHeadData() {
        return mapHeadData;
    }

    public void setMapHeadData(Map<String, String> mapHeadData) {
        this.mapHeadData = mapHeadData;
    }

    public boolean isLooping() {
        return looping;
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public boolean isCache() {
        return isCache;
    }

    public void setCache(boolean cache) {
        isCache = cache;
    }

    public File getCachePath() {
        return mCachePath;
    }

    public void setCachePath(File cachePath) {
        this.mCachePath = cachePath;
    }

    public String getOverrideExtension() {
        return overrideExtension;
    }

    public void setOverrideExtension(String overrideExtension) {
        this.overrideExtension = overrideExtension;
    }

    public BufferedInputStream getVideoBufferedInputStream() {
        return videoBufferedInputStream;
    }

    public void setVideoBufferedInputStream(BufferedInputStream videoBufferedInputStream) {
        this.videoBufferedInputStream = videoBufferedInputStream;
    }
}
