package com.chen.fy.patshow.user.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chen.fy.patshow.R;
import com.chen.fy.patshow.util.RUtil;
import com.chen.fy.patshow.util.ShowUtils;
import com.chen.fy.patshow.util.UserSP;

import org.devio.takephoto.app.TakePhotoActivity;


public class MyInfoActivity extends TakePhotoActivity {

    private ImageView ivHeadIcon;
    private TextView tvNickname;
    private TextView tvAccount;
    private TextView tvLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_info_layout);

        ShowUtils.changeStatusBarTextImgColor(this, true);
        initView();
        initData();
    }

    private void initView() {
        ivHeadIcon = findViewById(R.id.iv_head_icon_my_info);
        tvNickname = findViewById(R.id.tv_username_my_info);
        tvAccount = findViewById(R.id.tv_account_my_info);
        tvLocation = findViewById(R.id.tv_location_my_info);

        setClick();
    }

    private void setClick() {
        findViewById(R.id.iv_return_my_info).setOnClickListener(v -> finish());
        findViewById(R.id.btn_out_login_my_info).setOnClickListener(v -> clearLoginState());
    }

    private void initData() {
        SharedPreferences sp = UserSP.getUserSP();
        // get
        String headIconURL = sp.getString(RUtil.toString(R.string.head_icon), "");
        String nickname = sp.getString(RUtil.toString(R.string.nickname), "");
        String account = sp.getString(RUtil.toString(R.string.account_key), "");

        // fill
        Glide.with(this).load(headIconURL).into(ivHeadIcon);
        tvNickname.setText(nickname);
        tvAccount.setText(account);
        tvLocation.setText(getLocation());
    }

    private String getLocation() {
        return "Guilin";
    }

    /// 清除登录状态
    private void clearLoginState() {
        SharedPreferences.Editor editorUserInfo = getSharedPreferences(
                RUtil.toString(R.string.userInfo_sp_name), MODE_PRIVATE).edit();
        editorUserInfo.clear();
        editorUserInfo.apply();
        setResult(RESULT_OK);
        finish();
    }
}
