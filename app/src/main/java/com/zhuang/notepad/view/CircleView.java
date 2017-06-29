package com.zhuang.notepad.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * 一个圆形的view，用于提醒的小点点
 */
public class CircleView extends AppCompatTextView {

    private Paint mBackgroundPaint;
    private int mBackGroundColor = Color.RED;
    private RectF mTextRectF;

    public CircleView(Context context) {
        super(context);
        init();
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mTextRectF = new RectF();
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setColor(mBackGroundColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //如果宽小于高，画圆形
        //如果宽大于高，画矩形两边圆
        if (getWidth() <= getHeight() || getText() == null) {
            float radius = getHeight() / 2;
            canvas.drawCircle(
                    getWidth()/2,
                    getHeight() / 2,
                    radius,
                    mBackgroundPaint
            );
        } else {
            mTextRectF.left = 0;
            mTextRectF.top = 0;
            mTextRectF.right = getWidth();
            mTextRectF.bottom = getHeight();
            canvas.drawRoundRect(mTextRectF, getWidth() / 6, getHeight()/2, mBackgroundPaint);
        }
        super.onDraw(canvas);
    }
}
