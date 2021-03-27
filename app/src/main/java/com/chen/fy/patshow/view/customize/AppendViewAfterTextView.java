package com.chen.fy.patshow.view.customize;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.icu.text.IDNA;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chen.fy.patshow.R;

/**
 * 在TextView后添加View
 */
public class AppendViewAfterTextView extends FrameLayout implements ViewTreeObserver.OnGlobalLayoutListener {

    private TextView tvInfo;
    private TextView tvShow;
    private FrameLayout.LayoutParams paramsShow;
    private int space = 10;

    // xml属性
    private String text;
    private float textSize;
    private int textColor_Main;
    private int textColor_Show;
    private int maxLine;

    public AppendViewAfterTextView(@NonNull Context context) {
        super(context);
        init();
    }

    public AppendViewAfterTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AppendViewAfterTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        // 解析XML文件属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AppendViewAfterTextView);
        text = typedArray.getString(R.styleable.AppendViewAfterTextView_text);
        textSize = typedArray.getDimension(R.styleable.AppendViewAfterTextView_textSize, 0);
        textColor_Main = typedArray.getColor(R.styleable.AppendViewAfterTextView_textColor_Main, 0);
        textColor_Show = typedArray.getColor(R.styleable.AppendViewAfterTextView_textColor_Show, 0);
        maxLine = typedArray.getInteger(R.styleable.AppendViewAfterTextView_textColor_Main, 0);
        typedArray.recycle();

        init();
    }

    private void init() {
        tvInfo = new TextView(getContext());
        tvInfo.setText(text);
        tvInfo.setTextSize(textSize);
        tvInfo.setTextColor(textColor_Main);
        tvInfo.setMaxLines(maxLine);
        tvInfo.setEllipsize(TextUtils.TruncateAt.END);

        tvShow = new TextView(getContext());
        tvShow.setTextSize(textSize);
        tvInfo.setTextColor(textColor_Show);

        paramsShow = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvShow.setLayoutParams(paramsShow);

        addView(tvInfo, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(tvShow);
    }

    @Override
    public void onGlobalLayout() {
        //为保证TextView.getLayout()!=null，在这里再执行相关逻辑
        setMoreViewPosition();
        //记得移除，不然会一直回调
        tvInfo.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    private void setMoreViewPosition() {
        Layout layout = tvInfo.getLayout();
        if (layout == null)
            return;
        int lineCount = layout.getLineCount();
        float lineWidth = layout.getLineWidth(lineCount - 1);

        //获取最后行最后一个字符的下标
        int lineEnd = layout.getLineEnd(lineCount - 1);
        if (lineWidth + tvShow.getMeasuredWidth() + space - (getWidth() - getPaddingRight() - getPaddingLeft()) > 0) {//最后一行显示不下，将最后一行换行
            if (text.length() > 2) {
                //分两个字符到tvSpecial那一行，更协调
                text = text.subSequence(0, text.length() - 2) + "\n" + text.subSequence(text.length() - 2, text.length());
                setText(text);
                return;//setText会重新触发onGlobalLayout
            }
        }
//        int lineH = layout.getLineBottom(lineCount - 1) - layout.getLineTop(lineCount - 1);
        int lineH = layout.getHeight() / layout.getLineCount();
        int lastLineRight = (int) layout.getSecondaryHorizontal(lineEnd);
        paramsShow.leftMargin = lastLineRight + space;
        //tvSpecial的中间和tv最后一行的中间上下对齐
        paramsShow.topMargin = layout.getHeight() - tvInfo.getPaddingBottom() - lineH / 2 - tvShow.getHeight() / 2;
        tvShow.setLayoutParams(paramsShow);
    }

    public void setText(final String text) {
        this.text = text;
        tvInfo.setText(this.text);
        tvInfo.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    public void setSpecialViewText(String text) {
        tvShow.setText(text);
    }

    public void setTextColor(int color) {
        tvInfo.setTextColor(color);
    }
}
