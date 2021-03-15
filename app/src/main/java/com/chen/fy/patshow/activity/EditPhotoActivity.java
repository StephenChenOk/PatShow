package com.chen.fy.patshow.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chen.fy.patshow.R;
import com.chen.fy.patshow.adapter.ColorAdapter;
import com.chen.fy.patshow.adapter.MappingAdapter;
import com.chen.fy.patshow.adapter.TextLogoAdapter;
import com.chen.fy.patshow.util.EditorUtil;
import com.chen.fy.patshow.util.RUtil;
import com.chen.fy.patshow.util.ShowUtils;
import com.chen.fy.patshow.view.QFRelativeLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class EditPhotoActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    /// 图片路径
    private String mPhotoPath;
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
        //initData();
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
            tvSave.setClickable(false);
            tvSave.setTextColor(getResources().getColor(R.color.blackColor4));
            rlImgBox.removeAllViews();
            rlImgBox.addView(ivImage);
        });
        // 保存按钮初始化
        tvSave = findViewById(R.id.tv_save_edit);
        tvSave.setOnClickListener(v -> {
            // 保存照片到相册
            Bitmap bitmap = createBitmap(rlImgBox);
            saveImageToGallery(EditPhotoActivity.this, bitmap);
        });
        tvSave.setClickable(false);

        bindTextBottomSheet();
        bindTextInputBottomSheet();
        bindMappingBottomSheet();
    }

    private void initData() {
        if (getIntent() != null) {
            mPhotoPath = getIntent().getStringExtra(RUtil.toString(R.string.photo_path));
            Glide.with(this).load(mPhotoPath).into(ivImage);
            mPhotoPath = getIntent().getStringExtra(RUtil.toString(R.string.photo_path));
            Glide.with(this).load(mPhotoPath).into(ivImage);
            String name = getIntent().getStringExtra(RUtil.toString(R.string.server_back));
            //addImgMark(name);
        }
    }

    private void addImgMark(String name) {
        vsMark = findViewById(R.id.vs_mark);
        vsMark.inflate();
        ImageView ivImg = findViewById(R.id.iv_imgMark);
        switch (name) {
            case "象鼻山":
                ivImg.setImageResource(R.drawable.xbs_jpg);
                break;
            case "普贤塔":
                ivImg.setImageResource(R.drawable.pxt_logo);
                break;
        }
    }

    // 文本界面
    private void bindTextBottomSheet() {
        findViewById(R.id.iv_cancel_text).setOnClickListener(v -> {
            tvType.setVisibility(View.GONE);
            rlImgBox.removeView(mTextBox);
            mTextBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });
        findViewById(R.id.iv_done_text).setOnClickListener(v -> {
            tvType.setVisibility(View.GONE);
            mTextBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            textDefine();
            tvSave.setClickable(true);
            tvSave.setTextColor(getResources().getColor(R.color.whiteColor));
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
            rlImgBox.removeView(ivMapping);
            mMappingBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });
        findViewById(R.id.iv_done_mapping).setOnClickListener(v -> {
            tvType.setVisibility(View.GONE);
            mappingDefine();
            tvSave.setClickable(true);
            tvSave.setTextColor(getResources().getColor(R.color.whiteColor));
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
                (int) RelativeLayout.LayoutParams.WRAP_CONTENT, (int) imgH/2);
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

    private Bitmap createBitmap(RelativeLayout view) {
        view.setDrawingCacheEnabled(true);
        //启用DrawingCache并创建位图
        view.buildDrawingCache();
        Bitmap cacheBitmap = view.getDrawingCache();
        //创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        //禁用DrawingCahce否则会影响性能
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public void saveImageToGallery(Context context, Bitmap bmp) {
        //检查有没有存储权限
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "请至权限中心打开应用权限", Toast.LENGTH_SHORT).show();
        } else {
            // 新建目录appDir，并把图片存到其下
            File appDir = new File(context.getExternalFilesDir(null).getPath()
                    + "BarcodeBitmap");
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            String fileName = System.currentTimeMillis() + ".jpg";
            File file = new File(appDir, fileName);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 把file里面的图片插入到系统相册中
            try {
                MediaStore.Images.Media.insertImage(context.getContentResolver(),
                        file.getAbsolutePath(), fileName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // 通知相册更新
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        }
    }
}
