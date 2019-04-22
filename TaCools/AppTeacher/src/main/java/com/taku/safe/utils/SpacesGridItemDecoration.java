package com.taku.safe.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class SpacesGridItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesGridItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildLayoutPosition(view);

        outRect.top = 0;
        outRect.bottom = space;

        // Add top margin only for the first item to avoid double space between items
        if (position % 2 == 0) {
            outRect.left = 0;
            outRect.right = space / 2;
        } else {
            outRect.left = space / 2;
            outRect.right = 0;
        }
    }
}
