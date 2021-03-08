package com.chen.fy.patshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.chen.fy.patshow.R;
import com.chen.fy.patshow.util.DateUtils;
import com.chen.fy.patshow.util.RUtil;
import com.lxj.xpopup.XPopup;

import java.util.Date;

public class SuccessIdentifyActivity extends AppCompatActivity implements View.OnClickListener {

    private String mPhotoPath;
    private RelativeLayout rlImgBox;
    private ImageView ivPhoto;
    private ViewStub vsMark;
    private TextView tvName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.success_identify_layout);

        bindView();
        initData();
    }

    private void bindView() {
        findViewById(R.id.iv_info_success).setOnClickListener(this);
        findViewById(R.id.rl_edit_box).setOnClickListener(this);
        rlImgBox = findViewById(R.id.rl_img_box);
        ivPhoto = findViewById(R.id.iv_image_success);
        vsMark = findViewById(R.id.vs_mark);
        tvName = findViewById(R.id.tv_name_success);
    }

    private void initData() {
        if (getIntent() != null) {
            mPhotoPath = getIntent().getStringExtra(RUtil.toString(R.string.photo_path));
            Glide.with(this).load(mPhotoPath).into(ivPhoto);
            String name = getIntent().getStringExtra(RUtil.toString(R.string.server_back));
            tvName.setText(name);
            addImgMark(name);
        }
    }

    /// 添加水印
    private void addMark(String name){
        vsMark.inflate();
        TextView tvName = findViewById(R.id.tv_name);
        TextView tvDate = findViewById(R.id.tv_date);
        tvName.setText(name);
        tvDate.setText(DateUtils.dateToDateString(new Date(System.currentTimeMillis())));
    }

    private void addImgMark(String name){
        vsMark.inflate();
        ImageView ivImg = findViewById(R.id.iv_imgMark);
        switch (name){
            case "象鼻山":
                ivImg.setImageResource(R.drawable.xbs_logo);
                break;
            case "普贤塔":
                ivImg.setImageResource(R.drawable.pxt_logo);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_edit_box:
                Intent intent = new Intent(this, EditPhotoActivity.class);
                intent.putExtra(RUtil.toString(R.string.photo_path), mPhotoPath);
                startActivity(intent);
                break;
            case R.id.iv_info_success:
                new XPopup.Builder(this)
                        .hasStatusBarShadow(true)
                        .autoOpenSoftInput(true)
                        .asCustom(new CustomFullScreenPopup(this))
                        .show();
                break;
        }
    }
}
