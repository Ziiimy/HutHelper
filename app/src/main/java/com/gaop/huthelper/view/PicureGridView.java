package com.gaop.huthelper.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by gaop on 16-9-10.
 */
public class PicureGridView extends GridView{
    public PicureGridView(Context context) {
        super(context);
    }

    public PicureGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PicureGridView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {

        heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, heightSpec);
    }
}
