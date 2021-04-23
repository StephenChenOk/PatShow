package com.chen.fy.patshow.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chen.fy.patshow.R;
import com.chen.fy.patshow.home.share.data.ShareInfo;
import com.chen.fy.patshow.network.MainServiceCreator;
import com.chen.fy.patshow.network.interfaces.ShareService;
import com.chen.fy.patshow.user.data.UserAdapter;
import com.chen.fy.patshow.user.view.LoginActivity;
import com.chen.fy.patshow.user.view.MyInfoActivity;
import com.chen.fy.patshow.util.RUtil;
import com.chen.fy.patshow.util.ShowUtils;
import com.chen.fy.patshow.util.UserSP;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserActivity extends AppCompatActivity {

    private final int MY_INFO_CODE = 1;

    private ImageView civHeadIcon;
    private TextView tvNickName;
    private TextView tvLocation;
    private TextView tvCount;

    private UserAdapter mAdapter;
    private List<ShareInfo> mList = new ArrayList<>();

    public static void start(Context context) {
        Intent intent = new Intent(context, UserActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);

        bindView();
        initData();
    }

    private void bindView() {
        civHeadIcon = findViewById(R.id.civ_head_icon);
        tvNickName = findViewById(R.id.tv_name);
        tvLocation = findViewById(R.id.tv_location);
        tvCount = findViewById(R.id.tv_count);

        // recyclerView
        RecyclerView recyclerView = findViewById(R.id.rv_user);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new UserAdapter(this, R.layout.user_item);
        mAdapter.setData(mList);
        recyclerView.setAdapter(mAdapter);

        setClick();
    }

    private void setClick() {
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        civHeadIcon.setOnClickListener(v -> gotoMyInfo());
        tvNickName.setOnClickListener(v -> gotoMyInfo());
        tvLocation.setOnClickListener(v -> gotoMyInfo());

        civHeadIcon.setOnClickListener(v ->
                ShowUtils.zoomPicture(this, v, R.drawable.head_icon_1));
    }

    private void initData() {
        fillUserInfo();
        fillPhotos();
    }

    private void gotoMyInfo() {
        Intent intent = new Intent(this, MyInfoActivity.class);
        startActivityForResult(intent, MY_INFO_CODE);
    }

    private void fillUserInfo() {
        SharedPreferences sp = UserSP.getUserSP();
        String headIconUrl = sp.getString(RUtil.toString(R.string.head_icon), "");
        String nickname = sp.getString(RUtil.toString(R.string.nickname), "");

        // fill
        tvNickName.setText(nickname);
        Glide.with(this).load(headIconUrl).into(civHeadIcon);
        tvLocation.setText(getLocation());
    }

    private String getLocation() {
        return "Guilin";
    }

    private void fillPhotos() {
        int userID = UserSP.getUserID();
        ShareService shareService = MainServiceCreator.create(ShareService.class);
        // 自动开启子线程在后台执行
        shareService.getList(userID).enqueue(new Callback<List<ShareInfo>>() {
            @Override
            public void onResponse(@NonNull Call<List<ShareInfo>> call
                    , @NonNull Response<List<ShareInfo>> response) {
                List<ShareInfo> list = response.body();
                if (list != null) {
                    mList = response.body();
                    if (mList != null) {
                        tvCount.setText(String.valueOf(mList.size()));
                        mAdapter.setData(mList);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ShareInfo>> call
                    , @NonNull Throwable t) {
                Log.i("getList", "getList Failure");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_INFO_CODE && resultCode == RESULT_OK) {
            LoginActivity.start(this);
            finish();
        }
    }
}
