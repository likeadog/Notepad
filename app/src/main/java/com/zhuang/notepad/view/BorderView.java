package com.zhuang.notepad.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.zhuang.notepad.R;

/**
 * Created by zhuang on 2017/6/8.
 */

public class BorderView extends FrameLayout{

    private int borderColor = Color.RED;
    private float borderWidth = 1;
    Paint mPaint;

    public BorderView(@NonNull Context context) {
        super(context);
        init(null,0);
    }

    public BorderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs,0);
    }

    public BorderView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs,defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BorderView, defStyle, 0);
        borderColor = a.getColor(R.styleable.BorderView_borderColor, borderColor);
        borderWidth = a.getDimension(R.styleable.BorderView_borderWidth, borderWidth);
        a.recycle();

        // Set up a default TextPaint object
        mPaint = new Paint();
        // 设置画笔的各个属性
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(borderWidth);
        mPaint.setAntiAlias(true);
        mPaint.setColor(borderColor);
    }

    public void setBorderColor(int color){
        mPaint.setColor(color);

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // 获取布局控件宽高
        int width = getWidth();
        int height = getHeight();
        // 创建矩形框
        Rect mRect = new Rect(0, 0, width, height);
        // 绘制边框
        canvas.drawRect(mRect, mPaint);
        // 最后必须调用父类的方法
        super.dispatchDraw(canvas);
    }
}
