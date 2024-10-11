package com.boom.downloader.listener;

import com.boom.downloader.model.VideoTaskItem;

import java.util.List;

public interface IDownloadInfosCallback {

    void onDownloadInfos(List<VideoTaskItem> items);
}
