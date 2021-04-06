package com.chen.fy.patshow.view.activity.identify;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chen.fy.patshow.R;
import com.chen.fy.patshow.data.adapter.edit.ColorAdapter;
import com.chen.fy.patshow.data.adapter.edit.MappingAdapter;
import com.chen.fy.patshow.data.adapter.edit.TextLogoAdapter;
import com.chen.fy.patshow.util.EditorUtil;
import com.chen.fy.patshow.util.FileUtils;
import com.chen.fy.patshow.util.RUtil;
import com.chen.fy.patshow.util.ShowUtils;
import com.chen.fy.patshow.view.customize.QFRelativeLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class EditPhotoActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    /// 原生图片路径
    private String mPhotoPath;
    /// 新图片路径，存在Logo
    private String mNewPhotoPath;
    /// 当前编辑状态：文字/贴纸
    private TextView tvType;
    /// 保存按钮
    private TextView tvSave;
    /// 要编辑的图片外层Box
    private RelativeLayout rlImgBox;
    /// 要编辑的图片
    private ImageView ivImage;
    /// text bottom sheet
    private BottomSheetBehavior mTextBottomSheetBehavior;
    /// text input bottom sheet
    private BottomSheetBehavior mTextInputBottomSheetBehavior;
    /// 输入文字框
    private EditText etInput;
    /// 样式
    private ViewStub vsStyle;
    /// 字体
    private ViewStub vsTypeface;
    // mapping bottom sheet
    private BottomSheetBehavior mMappingBottomSheetBehavior;
    /// 标志： 象鼻山、普贤塔
    private ViewStub vsMark;
    /// 添加的文字Box
    private QFRelativeLayout mTextBox;
    /// 添加的贴图Box
    private QFRelativeLayout mMappingBox;
    /// 文字
    private TextView tvText;
    /// 贴图
    private ImageView ivMapping;
    /// 是否已经是加粗
    private boolean isBold = false;
    /// 是否已经是斜体
    private boolean isItalics = false;
    /// 蒙层
    private View mMantleView;
    /// 透明View, 实现点击蒙层效果
    private View transparentView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 使软键盘不将布局顶上去
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //隐藏状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ShowUtils.changeStatusBarTextImgColor(this, true);
        setContentView(R.layout.edit_photo);
        bindView();
        initData();
    }

    @Override
    public void onBackPressed() {
        if (mTextInputBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            // text input
            mTextInputBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            showMantle(false);
            tvType.setText(getResources().getString(R.string.text_type));
        } else if (mTextBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            // text
            mTextBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            tvType.setVisibility(View.GONE);
        } else if (mMappingBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            // mapping
            mMappingBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            tvType.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    private void bindView() {
        tvType = findViewById(R.id.tv_type_edit);
        rlImgBox = findViewById(R.id.rl_img_box);
        ivImage = findViewById(R.id.iv_image_edit);
        findViewById(R.id.ll_text_box).setOnClickListener(v -> {
            addTextView();
            tvType.setText(getResources().getString(R.string.text_type));
            tvType.setVisibility(View.VISIBLE);
        });
        findViewById(R.id.ll_mapping_box).setOnClickListener(v -> {
            mMappingBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            tvType.setText(getResources().getString(R.string.mapping_type));
            tvType.setVisibility(View.VISIBLE);
        });
        findViewById(R.id.tv_cancel_edit).setOnClickListener(v -> {
            rlImgBox.removeAllViews();
            rlImgBox.addView(ivImage);
            Glide.with(this).load(mPhotoPath).into(ivImage);
            saveAble();
        });
        // 保存按钮初始化
        tvSave = findViewById(R.id.tv_save_edit);
        tvSave.setOnClickListener(v -> {
            // 保存照片到相册
            mNewPhotoPath = FileUtils.saveViewToGallery(EditPhotoActivity.this, rlImgBox);
            // 关闭Activity
            Intent intent = new Intent();
            intent.putExtra(RUtil.toString(R.string.new_photo_path), mNewPhotoPath);
            setResult(Activity.RESULT_OK, intent);
            finish();
        });
        tvSave.setClickable(false);

        bindTextBottomSheet();
        bindTextInputBottomSheet();
        bindMappingBottomSheet();
    }

    private void initData() {
        if (getIntent() != null) {
            mPhotoPath = getIntent().getStringExtra(RUtil.toString(R.string.photo_path));
            mNewPhotoPath = getIntent().getStringExtra(RUtil.toString(R.string.new_photo_path));
            // 不适用缓存
            RequestOptions requestOptions = new RequestOptions().
                    skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE);
            Glide.with(this).applyDefaultRequestOptions(requestOptions).load(mNewPhotoPath).into(ivImage);
        }
    }

    // 文本界面
    private void bindTextBottomSheet() {
        findViewById(R.id.iv_cancel_text).setOnClickListener(v -> {
            tvType.setVisibility(View.GONE);
            rlImgBox.removeView(mTextBox);
            rlImgBox.removeView(mMappingBox);
            mTextBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });
        findViewById(R.id.iv_done_text).setOnClickListener(v -> {
            tvType.setVisibility(View.GONE);
            mTextBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            textDefine();
            saveAble();
        });

        View bottomSheet = findViewById(R.id.text_bottom_sheet);
        mTextBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        // RecyclerView
        RecyclerView rvTextLogo = findViewById(R.id.rv_text_logo);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvTextLogo.setLayoutManager(layoutManager);
        TextLogoAdapter adapter = new TextLogoAdapter(EditorUtil.getTextLogoDrawables(), position -> {
            rlImgBox.removeView(mTextBox);
            addMapping(EditorUtil.getTextLogoDrawables().get(position));
        });
        rvTextLogo.setAdapter(adapter);
        // 隐藏
        mTextBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    // 文本输入界面
    private void bindTextInputBottomSheet() {
        etInput = findViewById(R.id.et_input);
        etInput.addTextChangedListener(this);
        findViewById(R.id.ll_keyboard_box).setOnClickListener(this);
        findViewById(R.id.ll_style_box).setOnClickListener(this);
        findViewById(R.id.ll_typeface_box).setOnClickListener(this);
        findViewById(R.id.iv_define).setOnClickListener(v -> {
            tvType.setVisibility(View.GONE);
            //textDefine();
            resetText();
        });
        View bottomSheet = findViewById(R.id.text_input_bottom_sheet);
        mTextInputBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        mTextInputBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        bindViewStub();
    }

    /// 贴图界面
    private void bindMappingBottomSheet() {
        findViewById(R.id.iv_cancel_mapping).setOnClickListener(v -> {
            tvType.setVisibility(View.GONE);
            rlImgBox.removeView(mMappingBox);
            mMappingBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });
        findViewById(R.id.iv_done_mapping).setOnClickListener(v -> {
            tvType.setVisibility(View.GONE);
            mappingDefine();
            saveAble();
        });
        // RecyclerView
        RecyclerView rvMapping = findViewById(R.id.rv_mapping);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvMapping.setLayoutManager(layoutManager);
        MappingAdapter adapter = new MappingAdapter(EditorUtil.getMappingDrawables(), position -> {
            addMapping(EditorUtil.getMappingDrawables().get(position));
        });
        rvMapping.setAdapter(adapter);
        // bottom sheet
        View bottomSheet = findViewById(R.id.mapping_bottom_sheet);
        mMappingBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        // 隐藏
        mMappingBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }


    private void bindViewStub() {
        vsStyle = findViewById(R.id.vs_text_style);
        vsTypeface = findViewById(R.id.vs_text_typeface);
        vsStyle.inflate();
        vsTypeface.inflate();

        bindTextStyle();
    }

    /// 文本样式
    private void bindTextStyle() {
        // 颜色
        RecyclerView rvColor = findViewById(R.id.rv_color);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvColor.setLayoutManager(manager);
        ColorAdapter adapter = new ColorAdapter(EditorUtil.getColorDrawables(), position -> {
            int colorID = EditorUtil.getColorID(position);
            tvText.setTextColor(getResources().getColor(colorID));
            tvText.setText(tvText.getText().toString());
        });
        rvColor.setAdapter(adapter);

        // 透明度
        AppCompatSeekBar seekBar = findViewById(R.id.sb_transparency);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvText.setAlpha((float) progress / 100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // 加粗
        findViewById(R.id.rl_bold_box).setOnClickListener(this);
        // 斜体
        findViewById(R.id.rl_italic_box).setOnClickListener(this);
    }

    /// 显示蒙层
    private void showMantle(boolean isShow) {
        if (mMantleView == null) {
            // 初始化
            mMantleView = findViewById(R.id.mantle);
            float imgW = ivImage.getWidth();
            float imgH = ivImage.getHeight();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) imgW, (int) imgH);
            mMantleView.setLayoutParams(params);
        }
        if (isShow) {
            mMantleView.setVisibility(View.VISIBLE);
            tvType.setText(getResources().getString(R.string.empty));
        } else {
            mMantleView.setVisibility(View.GONE);
        }
    }

    /// 弹出软键盘
    private void showSoftInputFromWindow(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, 0);
    }

    /// 收起软键盘
    private void hideSoftInputFromWindow(EditText editText) {
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        editText.clearFocus();
        InputMethodManager inputManager =
                (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    /// 可保存图片
    private void saveAble(){
        tvSave.setClickable(true);
        tvSave.setTextColor(getResources().getColor(R.color.whiteColor));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_input:
            case R.id.ll_keyboard_box:
                // 获取焦点，弹出软键盘
                showSoftInputFromWindow(etInput);
                break;
            case R.id.ll_style_box:
                vsTypeface.setVisibility(View.GONE);
                vsStyle.setVisibility(View.VISIBLE);
                hideSoftInputFromWindow(etInput);
                break;
            case R.id.ll_typeface_box:
                vsStyle.setVisibility(View.GONE);
                vsTypeface.setVisibility(View.VISIBLE);
                hideSoftInputFromWindow(etInput);
                break;
            case R.id.rl_bold_box:
                // 加粗
                if (isItalics) {
                    if (isBold) {
                        isBold = false;
                        tvText.setTypeface(null, Typeface.ITALIC);
                    } else {
                        isBold = true;
                        tvText.setTypeface(null, Typeface.BOLD_ITALIC);
                    }
                } else {
                    if (isBold) {
                        isBold = false;
                        tvText.setTypeface(null, Typeface.NORMAL);
                    } else {
                        isBold = true;
                        tvText.setTypeface(null, Typeface.BOLD);
                    }
                }
                break;
            case R.id.rl_italic_box:
                if (isBold) {
                    if (isItalics) {
                        isItalics = false;
                        tvText.setTypeface(null, Typeface.BOLD);
                    } else {
                        isItalics = true;
                        tvText.setTypeface(null, Typeface.BOLD_ITALIC);
                    }
                } else {
                    if (isItalics) {
                        isItalics = false;
                        tvText.setTypeface(null, Typeface.NORMAL);
                    } else {
                        isItalics = true;
                        tvText.setTypeface(null, Typeface.ITALIC);
                    }
                }
                break;
        }
    }

    /// 文字编辑，点击确定
    private void textDefine() {
        mTextBox.setOnTouchListener(null);
        tvText.setOnClickListener(null);  //移除监听
        tvText.setBackgroundResource(0);  //移除背景
        mTextInputBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        hideSoftInputFromWindow(etInput);
    }

    /// 贴图，点击确定
    private void mappingDefine() {
        if (mMappingBox != null) {
            mMappingBox.setOnTouchListener(null);
        }
        mMappingBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    /// 添加文字
    private void addTextView() {
        // QFRelativeLayout
        mTextBox = new QFRelativeLayout(this);

        // 获取图片宽高
        float imgW = ivImage.getWidth();
        float imgH = ivImage.getHeight();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                (int) imgW, (int) imgH);
        mTextBox.setLayoutParams(params);
        mTextBox.setGravity(Gravity.CENTER);
        // text
        tvText = new TextView(this);
        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        tvText.setText("请输入");
        tvText.setPadding(50, 50, 50, 50);
        tvText.setBackground(getResources().getDrawable(R.drawable.text_box));
        tvText.setTextColor(Color.WHITE);
        tvText.setTextSize(20);
        tvText.setLayoutParams(textParams);
        tvText.setOnClickListener(v -> {
            // 添加透明View，可点击
            transparentView = new View(this);
            transparentView.setLayoutParams(params);
            transparentView.setOnClickListener(v_ -> {
                tvType.setVisibility(View.GONE);
                resetText();
                // 添加背景
                tvText.setBackground(getResources().getDrawable(R.drawable.text_box));
            });
            rlImgBox.addView(transparentView);
            // 消除背景
            tvText.setBackgroundResource(0);
            // Text上移
            viewMove(tvText, 0, 0, 0, -1);
            // 弹出底部栏
            mTextInputBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            // 覆盖蒙层
            showMantle(true);
        });
        // text 添加到 QFRelativeLayout
        mTextBox.addView(tvText);
        // QFRelativeLayout 添加到 box
        rlImgBox.addView(mTextBox);
        // 显示text bottom view
        mTextBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    /// 添加贴图
    private void addMapping(int mappingID) {
        // QFRelativeLayout
        mMappingBox = new QFRelativeLayout(this);
        // 获取图片宽高
        float imgW = ivImage.getWidth();
        float imgH = ivImage.getHeight();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) imgW, (int) imgH);
        mMappingBox.setLayoutParams(params);
        mMappingBox.setGravity(Gravity.CENTER);
        // image
        ivMapping = new ImageView(this);
        RelativeLayout.LayoutParams mappingParams = new RelativeLayout.LayoutParams(
                (int) RelativeLayout.LayoutParams.WRAP_CONTENT, (int) imgH / 2);
        ivMapping.setLayoutParams(mappingParams);

        ivMapping.setImageResource(mappingID);
        // image 添加到 QFRelativeLayout
        mMappingBox.addView(ivMapping);
        // QFRelativeLayout 添加到 box
        rlImgBox.addView(mMappingBox);
    }

    /// 视图移动
    private void viewMove(View view, float fromX, float toX, float fromY, float toY) {
        Animation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, fromX,
                Animation.RELATIVE_TO_SELF, toX,
                Animation.RELATIVE_TO_SELF, fromY,
                Animation.RELATIVE_TO_SELF, toY);//设置平移的起点和终点
        translateAnimation.setDuration(500);
        translateAnimation.setFillEnabled(true);//使其可以填充效果从而不回到原地
        translateAnimation.setFillAfter(true);//不回到起始位置
        //如果不添加setFillEnabled和setFillAfter则动画执行结束后会自动回到远点
        view.startAnimation(translateAnimation);//给imageView添加的动画效果
    }

    /// 重置TextBox
    private void resetText() {
        // 移除遮挡View
        rlImgBox.removeView(transparentView);
        // 隐藏底部栏
        mTextInputBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        // 视图下移
        viewMove(tvText, 0, 0, -1, 0);
        // 删除蒙层
        showMantle(false);
    }

    /// EditView输入监听
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        tvText.setText(s);
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
