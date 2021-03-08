package com.chen.fy.patshow.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * 蒙层
 */
public class MantleView extends LinearLayout {
    private OnClickMantleListener mListener;

    public MantleView(Context context) {
        super(context);
    }

    public MantleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MantleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MantleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Paint p = new Paint();
        p.setColor(Color.parseColor("#88000000"));
        canvas.drawRect(0, 0, getWidth(), getHeight(), p);
        super.dispatchDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mListener.onMantleClick();
        return true;
    }

    public void setMantleClickListener(OnClickMantleListener listener) {
        mListener = listener;
    }
    //蒙层点击事件
    public interface OnClickMantleListener {
        void onMantleClick();
    }
}
