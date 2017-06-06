package com.zhuang.notepad.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.zhuang.notepad.R;

/**
 * TODO: document your custom view class.
 */
public class ImageTextView extends View {
    private String mText;//显示文字
    private int mTextColor = Color.BLACK;//默认颜色
    private float mTextSize = 16;//默认字体大小
    private Drawable mIcon;//图标
    private float mIconSize;//图标大小
    private float iconTextGap;//图标与文字大小

    private TextPaint mTextPaint;//文字画笔
    private float mTextWidth;//文字宽度
    private float mTextHeight;//文字高度
    private float mTextBottom;//

    public ImageTextView(Context context) {
        super(context);
        init(null, 0);
    }

    public ImageTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ImageTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ImageTextView, defStyle, 0);

        mText = a.getString(R.styleable.ImageTextView_text);
        mTextColor = a.getColor(R.styleable.ImageTextView_textColor, mTextColor);
        mTextSize = a.getDimension(R.styleable.ImageTextView_textSize,
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_SP, mTextSize, getResources().getDisplayMetrics()));
        mIconSize = a.getDimension(R.styleable.ImageTextView_iconSize, mIconSize);
        iconTextGap = a.getDimension(R.styleable.ImageTextView_iconTextGap, iconTextGap);

        if (a.hasValue(R.styleable.ImageTextView_icon)) {
            mIcon = a.getDrawable(R.styleable.ImageTextView_icon);
            mIcon.setCallback(this);
        }

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        init();
    }

    private void init() {
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        mTextWidth = mTextPaint.measureText(mText);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom - fontMetrics.ascent;
        mTextBottom = fontMetrics.bottom;
    }

    public void invalidateTextPaintAndMeasurements() {
        init();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = (int) (mTextWidth + paddingLeft + paddingRight + mIconSize + iconTextGap);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            float contentHeight = mTextHeight > mIconSize ? mTextHeight : mIconSize;
            height = (int) (contentHeight + paddingTop + paddingBottom);
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingRight = getPaddingRight();

        int contentHeight = getHeight() - paddingTop - paddingBottom;

        // Draw the text.
        canvas.drawText(mText,
                paddingLeft + mIconSize + iconTextGap,
                paddingTop + (contentHeight + mTextHeight) / 2 - mTextBottom,
                mTextPaint);

        // Draw the example drawable on top of the text.
        if (mIcon != null) {
            mIcon.setBounds(paddingLeft, (contentHeight - (int) mIconSize) / 2 + paddingTop,
                    paddingLeft + (int) mIconSize, (int) ((contentHeight - mIconSize) / 2 + mIconSize + paddingTop));
            mIcon.draw(canvas);
        }
    }

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
        invalidateTextPaintAndMeasurements();
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
        invalidateTextPaintAndMeasurements();
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float mTextSize) {
        this.mTextSize = mTextSize;
        invalidateTextPaintAndMeasurements();
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public void setIcon(Drawable mIcon) {
        this.mIcon = mIcon;
    }
}
