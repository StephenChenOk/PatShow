package com.chen.fy.patshow.view.activity.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.chen.fy.patshow.R;
import com.chen.fy.patshow.util.RUtil;
import com.chen.fy.patshow.util.ShowUtils;

/**
 * 实用信息
 */
public class UsefulInfoActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private TextView tvShowInfo;
    private TextView tvInfo;
    private NestedScrollView mNestedScrollView;

    private RadioButton rbIntroduction;
    private RadioButton rbTravelNotes;
    private RadioButton rbStrategy;

    private LinearLayout llIntroductionBox;
    private LinearLayout llTravelNotesBox;
    private LinearLayout llStrategyBox;
    private float mIntroductionBoxHeight;
    private float mTravelNotesBoxHeight;
    private float mStrategyBoxHeight;

    // 默认不显示全部
    private boolean isShowAll = false;

    // 时间间隔：ms
    private final int INTERVAL_TIME = 1000;

    // 是否点击btn进行滑动
    private boolean isClickBtn = false;

    public static void start(Context context) {
        final Intent intent = new Intent(context, UsefulInfoActivity.class);
        context.startActivity(intent);
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            isClickBtn = false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShowUtils.changeStatusBarTextImgColor(this, true);
        setContentView(R.layout.useful_info_layout);

        bindView();
        setListener();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mIntroductionBoxHeight = llIntroductionBox.getY();
        mTravelNotesBoxHeight = llTravelNotesBox.getY();
        mStrategyBoxHeight = llStrategyBox.getY();
    }

    private void bindView() {
        tvInfo = findViewById(R.id.tv_xsjq_info);
        tvShowInfo = findViewById(R.id.tv_show_info);
        llIntroductionBox = findViewById(R.id.ll_introduction_box);
        llTravelNotesBox = findViewById(R.id.ll_travel_notes_box);
        llStrategyBox = findViewById(R.id.ll_strategy_box);

        mNestedScrollView = findViewById(R.id.nsv_useful_info);
        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (!isClickBtn) {
                    if (scrollY <= mTravelNotesBoxHeight) {
                        rbIntroduction.setChecked(true);
                    } else if (scrollY <= mStrategyBoxHeight) {
                        rbTravelNotes.setChecked(true);
                    } else {
                        rbStrategy.setChecked(true);
                    }
                }
            }
        });
        bindRadioGroup();
    }

    private void bindRadioGroup() {
        RadioGroup radioGroup = findViewById(R.id.rg_useful_info);
        rbIntroduction = findViewById(R.id.rb_introduction);
        rbTravelNotes = findViewById(R.id.rb_travel_notes);
        rbStrategy = findViewById(R.id.rb_strategy);
        radioGroup.setOnCheckedChangeListener(this);
    }

    private void setListener() {
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        tvShowInfo.setOnClickListener(v -> {
            if (isShowAll) {
                hideAllInfo();
            } else {
                showAllInfo();
            }
            isShowAll = !isShowAll;
        });
    }

    // 显示全部简介
    private void showAllInfo() {
        // 展开
        tvInfo.setSingleLine(false);
        tvInfo.setEllipsize(null);
        tvShowInfo.setText(RUtil.toString(R.string.show));
    }

    // 隐藏全部简介
    private void hideAllInfo() {
        // 收起
        tvInfo.setEllipsize(TextUtils.TruncateAt.END);
        tvInfo.setLines(4);
        tvShowInfo.setText(RUtil.toString(R.string.hide));
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        isClickBtn = true;
        switch (checkedId) {
            case R.id.rb_introduction:
                // 平稳滑动
                mNestedScrollView.smoothScrollTo(0, 0, INTERVAL_TIME);
                break;
            case R.id.rb_travel_notes:
                mNestedScrollView.smoothScrollTo(0, (int) mTravelNotesBoxHeight, INTERVAL_TIME);
                break;
            case R.id.rb_strategy:
                mNestedScrollView.smoothScrollTo(0, (int) mStrategyBoxHeight, INTERVAL_TIME);
                break;
        }
        mHandler.sendEmptyMessageDelayed(0, INTERVAL_TIME);
    }
}
