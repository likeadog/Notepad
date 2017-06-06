package com.zhuang.notepad.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * recyclerview的分割线
 * Created by zhuang on 2017/4/10.
 */

public class Divider extends RecyclerView.ItemDecoration {
    private int defaultPadding = 10;

    public Divider(Context context) {
        defaultPadding = DPUtil.dip2px(context, defaultPadding);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = defaultPadding;//类似加了一个bottom padding
    }
}
