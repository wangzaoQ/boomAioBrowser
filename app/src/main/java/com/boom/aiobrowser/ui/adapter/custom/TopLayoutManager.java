package com.boom.aiobrowser.ui.adapter.custom;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

public class TopLayoutManager extends LinearLayoutManager {

    public TopLayoutManager(Context context) {
        super(context);
    }

    public TopLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }


    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
//        RecyclerView.SmoothScroller smoothScroller = new LinearTopSmoothScroller(recyclerView.getContext());
//        smoothScroller.setTargetPosition(position);
//        startSmoothScroll(smoothScroller);
    }
}