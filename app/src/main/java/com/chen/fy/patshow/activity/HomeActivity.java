package com.chen.fy.patshow.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chen.fy.patshow.R;
import com.chen.fy.patshow.adapter.HomeAdapter;
import com.chen.fy.patshow.model.HomeItem;
import com.chen.fy.patshow.util.RUtil;
import com.chen.fy.patshow.util.ShowUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.SimpleCallback;

import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.app.TakePhotoActivity;
import org.devio.takephoto.model.TResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends TakePhotoActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 1;

    private HomeAdapter mAdapter;
    private List<HomeItem> mList;

    private TakePhoto mTakePhoto;
    private Uri mUri;

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
    }

    private void initView() {
        ShowUtils.changeStatusBarTextImgColor(this, false);
        // 象山景区图片
        ImageView ivHome = findViewById(R.id.iv_xsjq);
        Glide.with(this).load(R.drawable.xbs_main).into(ivHome);
        ivHome.setOnClickListener(v -> ShowUtils.zoomPicture(this, v, R.drawable.xbs_main));

        // 识别按钮
        findViewById(R.id.fab_distinguish).setOnClickListener(this);

        // 上传
        findViewById(R.id.btn_publish_mine).setOnClickListener(v -> showUploadDialog());

        // recyclerView
        RecyclerView recyclerView = findViewById(R.id.rv_home);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new HomeAdapter(this, R.layout.home_item);
        mAdapter.setOnItemClickListener((imageView, url) -> ShowUtils.zoomPicture(
                this, imageView, url
        ));
        initData();
        mAdapter.setHomeList(mList);
        recyclerView.setAdapter(mAdapter);
    }

    private void initData() {
        mList = new ArrayList<>();
        HomeItem item1 = new HomeItem(R.drawable.xbs_3, "真的好好看，如诗画一般");
        HomeItem item2 = new HomeItem(R.drawable.pxt_jpg, "普贤塔打卡");
        HomeItem item3 = new HomeItem(R.drawable.xbs_1, "象鼻山打卡");
        HomeItem item4 = new HomeItem(R.drawable.xbs_4, "很壮观");
        HomeItem item5 = new HomeItem(R.drawable.xbs_2, "山水美，很值");
        HomeItem item6 = new HomeItem(R.drawable.pxt_1, "位于象山顶上，似宝剑");
        HomeItem item7 = new HomeItem(R.drawable.xbs_6, "远景很不错，一望无际");
        HomeItem item8 = new HomeItem(R.drawable.xbs_7, "第一次来，很惊艳哦");

        mList.add(item1);
        mList.add(item2);
        mList.add(item3);
        mList.add(item4);
        mList.add(item5);
        mList.add(item6);
        mList.add(item7);
        mList.add(item8);
    }

    /// 显示上传图片弹窗
    private void showUploadDialog() {

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_distinguish:
                openCamera();
                break;
            default:
                break;
        }
    }

    /// 图片获取
    private void openCamera() {
        new XPopup.Builder(this)
                .asCenterList("", new String[]{"拍照", "从相册选择"},
                        (position, text) -> {
                            switch (position) {
                                case 0:         //拍照
                                    mTakePhoto.onPickFromCapture(mUri);
                                    break;
                                case 1:         //相册
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

        Intent intent = new Intent(this, IdentifyActivity.class);
        intent.putExtra(RUtil.toString(R.string.photo_path), result.getImage().getOriginalPath());
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

