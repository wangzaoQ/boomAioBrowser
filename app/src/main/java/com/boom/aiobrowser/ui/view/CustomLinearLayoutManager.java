package com.boom.aiobrowser.ui.view;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

public class CustomLinearLayoutManager extends LinearLayoutManager {
    public CustomLinearLayoutManager(Context context) {
        super(context,LinearLayoutManager.VERTICAL,false);
    }

    @Override
    public boolean canScrollVertically() {
        return false; // 禁止父 RecyclerView 滚动
    }
}
