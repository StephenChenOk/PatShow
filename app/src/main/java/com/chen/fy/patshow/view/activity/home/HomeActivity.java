package com.chen.fy.patshow.view.activity.home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chen.fy.patshow.R;
import com.chen.fy.patshow.view.activity.identify.IdentifyActivity;
import com.chen.fy.patshow.view.activity.identify.UploadActivity;
import com.chen.fy.patshow.data.adapter.home.HomeAdapter;
import com.chen.fy.patshow.data.model.home.ShareResponse;
import com.chen.fy.patshow.network.interfaces.ShareService;
import com.chen.fy.patshow.network.ShareServiceCreator;
import com.chen.fy.patshow.util.RUtil;
import com.chen.fy.patshow.util.ShowUtils;
import com.lxj.xpopup.XPopup;

import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.app.TakePhotoActivity;
import org.devio.takephoto.model.TResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends TakePhotoActivity {

    private static final int REQUEST_CODE = 1;
    private static final String TAG = "HomeActivity";

    private List<ShareResponse> mList = new ArrayList<>();

    private HomeAdapter mAdapter;

    private TakePhoto mTakePhoto;
    private Uri mUri;

    /// 上传/识别
    private boolean isUpload = false;

    /// 是否需要重新获取数据
    private boolean isGetList = true;

    /// 与TakePhotoActivity造成冲突
    private ActivityResultLauncher<Intent> uploadLauncher = new ComponentActivity().registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    getList();
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        applyPermission();
        initView();
        configTakePhoto();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isGetList) {
            getList();
        }
        isGetList = !isGetList;
    }

    private void initView() {
        ShowUtils.changeStatusBarTextImgColor(this, false);

        // recyclerView
        RecyclerView recyclerView = findViewById(R.id.rv_home);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new HomeAdapter(this, R.layout.home_item);
        mAdapter.setOnItemClickListener((imageView, url) -> ShowUtils.zoomPicture(
                this, imageView, url
        ));
        mAdapter.setShareResponseList(mList);
        recyclerView.setAdapter(mAdapter);

        setListener();
    }

    private void setListener() {
        // 象山景区图片
        findViewById(R.id.iv_xsjq).setOnClickListener(v ->
                ShowUtils.zoomPicture(this, v, R.drawable.xbs_main));

        // 识别按钮
        findViewById(R.id.fab_distinguish).setOnClickListener(v -> openCamera());

        // 上传
        findViewById(R.id.btn_publish_mine).setOnClickListener(v -> {
            isUpload = true;
            mTakePhoto.onPickMultiple(1);
        });

        // 简介
        findViewById(R.id.ll_introduction_box).setOnClickListener(v -> {
            UsefulInfoActivity.start(this);
        });
        // 攻略
        findViewById(R.id.ll_strategy_box).setOnClickListener(v -> {
            UsefulInfoActivity.start(this);
        });
        // 地图
        findViewById(R.id.ll_map_box).setOnClickListener(v -> {
            MapActivity.start(this);
        });
    }

    /// 初始化TakePhoto
    private void configTakePhoto() {
        if (mTakePhoto == null) {
            mTakePhoto = getTakePhoto();
            //图片文件名
            File file = new File(this.getExternalFilesDir(null)
                    , RUtil.toString(R.string.photo_name));
            mUri = Uri.fromFile(file);
        }
    }

    private void getList() {
        ShareService shareService = ShareServiceCreator.create(ShareService.class);
        // 自动开启子线程在后台执行
        shareService.getList().enqueue(new Callback<List<ShareResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<ShareResponse>> call
                    , @NonNull Response<List<ShareResponse>> response) {
                List<ShareResponse> list = response.body();
                if (list != null) {
                    mList.clear();
                    for (int i = list.size() - 1; i >= 0; i--) {
                        mList.add(list.get(i));
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ShareResponse>> call
                    , @NonNull Throwable t) {
                Log.i(TAG, "GetAllShareInfo Failure");
            }
        });
    }

    /// 图片获取
    private void openCamera() {
        new XPopup.Builder(this)
                .asCenterList("", new String[]{"拍照", "从相册选择"},
                        (position, text) -> {
                            switch (position) {
                                case 0:         //拍照
                                    isUpload = false;
                                    mTakePhoto.onPickFromCapture(mUri);
                                    break;
                                case 1:         //相册
                                    isUpload = false;
                                    mTakePhoto.onPickMultiple(1);
                                    break;
                            }
                        })
                .show();
    }

    ///  会与onActivityResult造成冲突
    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        String imgPath = result.getImage().getOriginalPath();
        if (isUpload) {
            toActivity(UploadActivity.class, imgPath);
        } else {
            toActivity(IdentifyActivity.class, imgPath);
        }
    }

    private void toActivity(Class cls, String imgPath) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(RUtil.toString(R.string.photo_path), imgPath);
        startActivity(intent);
    }

    /// 动态申请危险权限
    private void applyPermission() {
        // 权限集合
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.
                WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.
                READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.
                READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(HomeActivity.this, permissions, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(HomeActivity.this, "必须同意所有权限才可以使用本程序!", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

}

