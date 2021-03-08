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
import androidx.recyclerview.widget.RecyclerView;

import com.chen.fy.patshow.interfaces.IOnDeleteItemClickListener;
import com.chen.fy.patshow.MyApplication;
import com.chen.fy.patshow.interfaces.PicturesItemClickListener;
import com.chen.fy.patshow.R;
import com.chen.fy.patshow.adapter.AlbumAdapter;
import com.chen.fy.patshow.model.Like;
import com.chen.fy.patshow.model.ShowItem;
import com.chen.fy.patshow.util.DateUtils;
import com.chen.fy.patshow.util.RUtil;
import com.chen.fy.patshow.util.UiUtils;
import com.google.android.material.snackbar.Snackbar;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.app.TakePhotoActivity;
import org.devio.takephoto.model.TResult;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class MainActivity extends TakePhotoActivity implements DiscreteScrollView.OnItemChangedListener,
        View.OnClickListener, IOnDeleteItemClickListener {

    private final int REQUEST_CODE = 1;
    private final String TAG = "MainActivity";

    private Like mLike;
    private AlbumAdapter albumAdapter;

    private ImageView rateItemButton;
    private DiscreteScrollView itemPicker;

    private InfiniteScrollAdapter infiniteAdapter;

    private List<ShowItem> mItems  = new ArrayList<>();

    private TakePhoto mTakePhoto;
    private Uri mUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        applyPermission();
        initView();
        initData();
        configTakePhoto();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void initView() {
        UiUtils.changeStatusBarTextImgColor(this, false);
        rateItemButton = findViewById(R.id.btn_item_like);

        //滑动的图片
        itemPicker = findViewById(R.id.item_picker);
        itemPicker.setOrientation(DSVOrientation.HORIZONTAL);
        itemPicker.addOnItemChangedListener(this);

        findViewById(R.id.btn_item_like).setOnClickListener(this);
        findViewById(R.id.fab_call).setOnClickListener(this);
        findViewById(R.id.btn_item_comment).setOnClickListener(this);

        findViewById(R.id.rl_more_box).setOnClickListener(this);
    }

    private void initData() {
        mLike = Like.get();

        //初始化适配器
        albumAdapter = new AlbumAdapter(MainActivity.this, mItems);
        albumAdapter.setClickListener(new PicturesItemClickListener());
        albumAdapter.setDeleteItemClickListener(MainActivity.this);
        infiniteAdapter = InfiniteScrollAdapter.wrap(albumAdapter);
        itemPicker.setAdapter(infiniteAdapter);
        itemPicker.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());

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

    //从Bmob中获取数据
    private void refresh() {
        BmobQuery<ShowItem> bmobQuery = new BmobQuery<>();
        bmobQuery.findObjects(new FindListener<ShowItem>() {
            @Override
            public void done(List<ShowItem> items, BmobException e) {
                if (e == null) {
                    showPhoto(items);
                }
            }
        });
    }

    private void showPhoto(List<ShowItem> items) {
        //排序
        Collections.sort(items, (o1, o2) -> {
            Date date1 = DateUtils.stringToDate(o1.getUpdatedAt());
            Date date2 = DateUtils.stringToDate(o2.getUpdatedAt());
            if (date1 != null && date2 != null) {
                return date2.compareTo(date1);
            }
            return 1;
        });
        mItems.clear();
        mItems.addAll(items);
        albumAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_item_like:
                int realPosition = infiniteAdapter.getRealPosition(itemPicker.getCurrentItem());
                ShowItem current = mItems.get(realPosition);
                mLike.setLiked(current.getObjectId(), !mLike.isLiked(current.getObjectId()));
                changeRateButtonState(current);
                break;
            case R.id.fab_call:
                openCamera();
                break;
            case R.id.btn_item_comment:
                showUnsupportedSnackBar();
                break;
            case R.id.rl_more_box:
                break;
            default:
                break;
        }
    }

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

        Intent intent = new Intent(this, UploadActivity.class);
        intent.putExtra(RUtil.toString(R.string.photo_path),result.getImage().getOriginalPath());
        startActivity(intent);
    }

    private void onItemChanged(ShowItem item) {
        changeRateButtonState(item);
    }

    private void changeRateButtonState(ShowItem item) {
        if (mLike.isLiked(item.getObjectId())) {
            rateItemButton.setImageResource(R.drawable.ic_favorite_red_24dp);
            rateItemButton.setColorFilter(ContextCompat.getColor(this, R.color.albumRatedStar));
        } else {
            rateItemButton.setImageResource(R.drawable.ic_favorite_red_24dp);
            rateItemButton.setColorFilter(ContextCompat.getColor(this, R.color.albumSecondary));
        }
    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int position) {
        int positionInDataSet = infiniteAdapter.getRealPosition(position);
        if (!mItems.isEmpty()) {
            onItemChanged(mItems.get(positionInDataSet));
        }
    }

    private void showUnsupportedSnackBar() {
        Snackbar.make(itemPicker, R.string.msg_unsupported_op, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void deleteItem(List<ShowItem> data, int position) {
        new XPopup.Builder(this)
                .asConfirm("是否选择删除", "删除后此图片等信息将彻底消失",
                        "取消", "确定",
                        () -> delete(data, position),
                        null,
                        false)
                .bindLayout(R.layout.delete_popup)
                .show();
    }

    private void delete(List<ShowItem> data, int position) {

        final BasePopupView loadingPopup = new XPopup.Builder(this)
                .asLoading("正在删除中...")
                .show();
        loadingPopup.setOnTouchListener((v, event) -> true);

        ShowItem item = data.get(position);
        String id = item.getObjectId();

        ShowItem albumItem = new ShowItem();
        albumItem.setObjectId(id);
        albumItem.delete(new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(MyApplication.getContext(),
                            "删除成功", Toast.LENGTH_LONG).show();
                    initData();
                    loadingPopup.dismiss();
                } else {
                    Log.d(TAG, "删除失败：" + e.getMessage());
                }
            }

        });
    }

    /**
     * 动态申请危险权限
     */
    private void applyPermission() {
        //权限集合
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.
                WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.
                READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(MainActivity.this, "必须同意所有权限才可以使用本程序!", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
}

