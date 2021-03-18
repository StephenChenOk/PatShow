package com.chen.fy.patshow.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.chen.fy.patshow.R;
import com.chen.fy.patshow.model.ShareResponse;
import com.chen.fy.patshow.model.ShareResponseBase;
import com.chen.fy.patshow.network.ShareServiceCreator;
import com.chen.fy.patshow.network.interfaces.ShareService;
import com.chen.fy.patshow.util.RUtil;
import com.chen.fy.patshow.util.ShowUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadActivity extends AppCompatActivity {

    private ImageView ivPhoto;
    private final String TAG = "UploadActivity";
    private String mPhotoPath;
    private EditText etInput;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_layout);

        ShowUtils.changeStatusBarTextImgColor(this, false);

        initView();
        initData();
    }

    private void initView() {
        etInput = findViewById(R.id.et_input_upload);
        ivPhoto = findViewById(R.id.iv_photo_upload);
        ivPhoto.setOnClickListener(v -> ShowUtils.zoomPicture(this, v, mPhotoPath));
        findViewById(R.id.iv_return_upload).setOnClickListener(v -> finish());
        findViewById(R.id.btn_upload).setOnClickListener(v -> {
            String content = etInput.getText().toString();
            if (!TextUtils.isEmpty(content)) {
                doUpload(etInput.getText().toString(), mPhotoPath);
            } else {
                Toast.makeText(this, "内容不可为空", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initData() {
        if (getIntent() != null) {
            mPhotoPath = getIntent().getStringExtra(RUtil.toString(R.string.photo_path));
            Glide.with(this).load(mPhotoPath).into(ivPhoto);
        }
    }

    /// 上传
    private void doUpload(String content, String imgPath) {
        final BasePopupView loading = new XPopup.Builder(this)
                .asLoading("上传中...")
                .show();
        loading.setOnTouchListener((v, event) -> true);

        RequestBody contentBody = RequestBody.create(null, content.getBytes(Util.UTF_8));
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"), new File(imgPath));
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("img"
                , RUtil.toString(R.string.photo_name), fileBody);
        ShareService shareService = ShareServiceCreator.create(ShareService.class);
        shareService.share(contentBody, filePart).enqueue(new Callback<ShareResponseBase>() {
            @Override
            public void onResponse(@NonNull Call<ShareResponseBase> call
                    , @NonNull Response<ShareResponseBase> response) {
                ShareResponseBase responseBase = response.body();
                if (responseBase != null) {
                    String msg = responseBase.getMsg();
                    if (msg.equals("success")) {
                        //uploadSuccess(responseBase);
                    } else {
                        Log.e(TAG, msg);
                    }
                } else {
                    Log.e(TAG, "responseBase == null");
                }
                // 上传成功
                loading.dismiss();
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<ShareResponseBase> call, @NonNull Throwable t) {
                loading.dismiss();
                Log.e(TAG, "upload file error");
            }
        });
    }

    private void uploadSuccess(ShareResponseBase responseBase) {
        ShareResponse share = responseBase.getResponse();
        String imgURL = share.getImgURL();
        Intent intent = new Intent();
        intent.putExtra(RUtil.toString(R.string.upload_result), imgURL);
        setResult(Activity.RESULT_OK);
    }
}
