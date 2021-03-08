package com.chen.fy.patshow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.chen.fy.patshow.interfaces.PicturesItemClickListener;
import com.chen.fy.patshow.R;
import com.chen.fy.patshow.model.IdentifyResponse;
import com.chen.fy.patshow.network.IdentifyService;
import com.chen.fy.patshow.network.ServiceCreator;
import com.chen.fy.patshow.util.RUtil;
import com.chen.fy.patshow.util.UiUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivPhoto;
    private final String TAG = "UploadActivity";
    private String mPhotoPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_layout);

        UiUtils.changeStatusBarTextImgColor(this, false);

        initView();
        initData();
    }

    private void initView() {
        ivPhoto = findViewById(R.id.iv_photo_upload);
        ivPhoto.setOnClickListener(this);
        findViewById(R.id.iv_return_upload).setOnClickListener(this);
        findViewById(R.id.btn_upload).setOnClickListener(this);
    }

    private void initData() {
        if (getIntent() != null) {
            mPhotoPath = getIntent().getStringExtra(RUtil.toString(R.string.photo_path));
            Glide.with(this).load(mPhotoPath).into(ivPhoto);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_photo_upload:
                PicturesItemClickListener listener = new PicturesItemClickListener();
                listener.onPicturesItemClick(ivPhoto, mPhotoPath);
                break;
            case R.id.iv_return_upload:
                finish();
                break;
            case R.id.btn_upload:
                startUpload();
                break;
        }
    }

    //上传图片
    private void startUpload() {

        final BasePopupView loading = new XPopup.Builder(this)
                .asLoading("正在上传中")
                .show();
        loading.setOnTouchListener((v, event) -> true);

        RequestBody file = RequestBody.create(MediaType.parse("image/jpg"), new File(mPhotoPath));
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("img"
                , RUtil.toString(R.string.photo_name), file);
        IdentifyService photoService = ServiceCreator.create(IdentifyService.class);
        photoService.postPhoto(filePart).enqueue(new Callback<IdentifyResponse>() {
            @Override
            public void onResponse(@NonNull Call<IdentifyResponse> call
                    , @NonNull Response<IdentifyResponse> response) {
                IdentifyResponse response1 = response.body();
                if (response1 != null) {
                    String name = response1.getName();
                    identifySuccess(name);
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

    private void identifySuccess(String name) {
        Intent intent = new Intent(this, SuccessIdentifyActivity.class);
        intent.putExtra(RUtil.toString(R.string.photo_path), mPhotoPath);
        intent.putExtra(RUtil.toString(R.string.server_back),name);
        startActivity(intent);
    }
}
