package com.chen.fy.patshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.chen.fy.patshow.R;
import com.chen.fy.patshow.model.IdentifyResponse;
import com.chen.fy.patshow.network.interfaces.IdentifyService;
import com.chen.fy.patshow.network.IdentifyServiceCreator;
import com.chen.fy.patshow.util.RUtil;
import com.chen.fy.patshow.util.ShowUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IdentifyActivity extends AppCompatActivity {

    private ImageView ivPhoto;
    private final String TAG = "IdentifyActivity";
    private String mPhotoPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.identify_layout);

        ShowUtils.changeStatusBarTextImgColor(this, false);

        initView();
        initData();
    }

    private void initView() {
        ivPhoto = findViewById(R.id.iv_photo_identify);
        ivPhoto.setOnClickListener(v -> ShowUtils.zoomPicture(this, v, mPhotoPath));
        findViewById(R.id.iv_return_identify).setOnClickListener(v -> finish());
        findViewById(R.id.btn_identify).setOnClickListener(v -> startIdentify());
    }

    private void initData() {
        if (getIntent() != null) {
            mPhotoPath = getIntent().getStringExtra(RUtil.toString(R.string.photo_path));
            Glide.with(this).load(mPhotoPath).into(ivPhoto);
        }

    }

    //上传图片
    private void startIdentify() {

        final BasePopupView loading = new XPopup.Builder(this)
                .asLoading("识别中...")
                .show();
        loading.setOnTouchListener((v, event) -> true);

        RequestBody file = RequestBody.create(MediaType.parse("image/jpg"), new File(mPhotoPath));
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("img"
                , RUtil.toString(R.string.photo_name), file);
        IdentifyService photoService = IdentifyServiceCreator.create(IdentifyService.class);
        photoService.postPhoto(filePart).enqueue(new Callback<IdentifyResponse>() {
            @Override
            public void onResponse(@NonNull Call<IdentifyResponse> call
                    , @NonNull Response<IdentifyResponse> response) {
                IdentifyResponse response1 = response.body();
                if (response1 != null) {
                    String name = response1.getName();
                    identifyDone(name);
                } else {
                    Log.e(TAG, "identify response is null");
                }
                loading.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<IdentifyResponse> call, @NonNull Throwable t) {
                loading.dismiss();
                Log.e(TAG, "upload file error");
            }
        });
    }

    private void identifyDone(String name) {
        if (name.equals(getResources().getString(R.string.identify_error))) {
            Toast.makeText(this, "识别失败，请重新进行", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, SuccessIdentifyActivity.class);
        intent.putExtra(RUtil.toString(R.string.photo_path), mPhotoPath);
        intent.putExtra(RUtil.toString(R.string.server_back), name);
        startActivity(intent);
    }
}
