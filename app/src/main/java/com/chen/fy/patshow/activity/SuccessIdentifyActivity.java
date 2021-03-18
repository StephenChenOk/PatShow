package com.chen.fy.patshow.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.chen.fy.patshow.R;
import com.chen.fy.patshow.util.FileUtils;
import com.chen.fy.patshow.util.RUtil;
import com.chen.fy.patshow.util.ShowUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import cn.bmob.v3.a.a.This;

public class SuccessIdentifyActivity extends AppCompatActivity {

    private final int EDIT_PHOTO_CODE = 1;
    private RelativeLayout rlImgBox;

    private String mPhotoPath;
    private String mNewPhotoPath;
    private ImageView ivImage;
    private ViewStub vsMark;
    private TextView tvName;

    private BottomSheetBehavior sheetBehavior;
    private View mantleView;

    // 是否已经保存照片，默认只可以保存一次
    private boolean isSaved = false;
    // 当前text logo是否为白色
    private boolean isWhite = true;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            // 保存临时文件，已贴上text logo
            mNewPhotoPath = FileUtils.saveTempImage(SuccessIdentifyActivity.this, rlImgBox);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.success_identify_layout);

        bindView();
        initData();
        handler.sendEmptyMessageDelayed(0, 500);
    }

    private void bindView() {
        ivImage = findViewById(R.id.iv_image_success);
        vsMark = findViewById(R.id.vs_mark);
        tvName = findViewById(R.id.tv_name_success);
        rlImgBox = findViewById(R.id.rl_img_box);

        // click listener
        /// 放大图片
        ivImage.setOnClickListener(v -> ShowUtils.zoomPicture(this, ivImage, mNewPhotoPath));
        /// 保存图片
        findViewById(R.id.tv_save_success_identify).setOnClickListener(v -> {
            if (!isSaved) {
                // 保存照片到相册
                mNewPhotoPath = FileUtils.saveViewToGallery(SuccessIdentifyActivity.this, rlImgBox);
                isSaved = true;
            } else {
                Toast.makeText(SuccessIdentifyActivity.this,
                        "已成功保存", Toast.LENGTH_SHORT).show();
            }
        });
        /// 简介
        findViewById(R.id.rl_info_box).setOnClickListener(v -> {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            mantleView.setVisibility(View.VISIBLE);
        });
        /// 编辑
        findViewById(R.id.rl_edit_box).setOnClickListener(v -> {
            Intent intent = new Intent(this, EditPhotoActivity.class);
            intent.putExtra(RUtil.toString(R.string.photo_path), mPhotoPath);
            intent.putExtra(RUtil.toString(R.string.new_photo_path), mNewPhotoPath);
            startActivityForResult(intent, EDIT_PHOTO_CODE);
        });

        // bottom sheet
        View sheet = findViewById(R.id.success_identify_bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(sheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        // MantleView
        mantleView = findViewById(R.id.mantle_success_identify);
        mantleView.setOnClickListener(v -> {
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            mantleView.setVisibility(View.GONE);
        });
    }

    private void initData() {
        if (getIntent() != null) {
            mPhotoPath = getIntent().getStringExtra(RUtil.toString(R.string.photo_path));
            Glide.with(this).load(mPhotoPath).into(ivImage);
            String name = getIntent().getStringExtra(RUtil.toString(R.string.server_back));
            tvName.setText(name);
            addTextLogoMark(name);
        }
    }

    /// 添加文字Logo水印
    private void addTextLogoMark(String name) {
        vsMark.inflate();
        ImageView ivImg = findViewById(R.id.iv_imgMark);
        ivImg.setImageResource(getImgID(name));
        ivImg.setOnClickListener(v -> {
            // 改变text logo 颜色
            ivImg.setImageResource(getImgID(name));
            handler.sendEmptyMessage(0);
        });
    }

    private int getImgID(String name) {
        switch (name) {
            case "象鼻山":
                if (isWhite) {
                    isWhite = false;
                    return R.drawable.xbs_logo_black;
                } else {
                    isWhite = true;
                    return R.drawable.xbs_logo_white;
                }
            case "普贤塔":
                if (isWhite) {
                    isWhite = false;
                    return R.drawable.pxt_logo_black;
                } else {
                    isWhite = true;
                    return R.drawable.pxt_logo_white;
                }
            default:
                return -1;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_PHOTO_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                /// 改变图片
                mPhotoPath = data.getStringExtra(RUtil.toString(R.string.photo_path));
                Glide.with(this).load(mPhotoPath).into(ivImage);
            }
        }
    }
}
