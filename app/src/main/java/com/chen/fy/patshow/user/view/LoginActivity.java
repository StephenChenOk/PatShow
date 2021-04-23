package com.chen.fy.patshow.user.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chen.fy.patshow.R;
import com.chen.fy.patshow.network.MainServiceCreator;
import com.chen.fy.patshow.network.interfaces.AccountService;
import com.chen.fy.patshow.user.UserActivity;
import com.chen.fy.patshow.user.data.BaseLoginResponse;
import com.chen.fy.patshow.user.data.User;
import com.chen.fy.patshow.util.RUtil;
import com.chen.fy.patshow.util.ShowUtils;
import com.google.gson.Gson;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPwd;

    private boolean bPwdSwitch = false;
    private ImageView ivPwdSwitch;

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        ShowUtils.changeStatusBarTextImgColor(this,true);
        initView();
    }

    private void initView() {
        etUsername = findViewById(R.id.et_username);
        etPwd = findViewById(R.id.et_pwd);
        ivPwdSwitch = findViewById(R.id.iv_pwd_switch);

        setClick();
    }

    private void setClick() {
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_login).setOnClickListener(v -> doLogin());
        findViewById(R.id.btn_login_to_register).setOnClickListener(v -> {
            Intent intent2 = new Intent(this, RegisterActivity.class);
            startActivity(intent2);
        });
        ivPwdSwitch.setOnClickListener(v -> switchPwdIcon());
    }

    private void doLogin() {
        String account = etUsername.getText().toString();
        String pwd = etPwd.getText().toString();
        if (isValidInput(account, pwd)) {
            login(account, pwd);
        }
    }

    private void switchPwdIcon() {
        bPwdSwitch = !bPwdSwitch;
        if (bPwdSwitch) {
            ivPwdSwitch.setImageResource(R.drawable.ic_visibility_on_24dp);
            etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);   //显示密码
        } else {
            ivPwdSwitch.setImageResource(R.drawable.ic_visibility_off_24dp);
            etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD |
                    InputType.TYPE_CLASS_TEXT);     //隐藏密码
            etPwd.setTypeface(Typeface.DEFAULT);    //设置字体样式
        }
    }

    /// 判断是否是否合法
    private boolean isValidInput(String account, String pwd) {
        if (account.isEmpty() || pwd.isEmpty()) {
            Toast.makeText(this, "输入不可为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void login(String account, String pwd) {
        if (account.isEmpty() || pwd.isEmpty()) {
            Toast.makeText(this, "输入不可为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 生成Json
        HashMap<String, String> map = new HashMap<>();
        map.put(RUtil.toString(R.string.account_key), account);
        map.put(RUtil.toString(R.string.password_key), pwd);
        Gson gson = new Gson();
        String postData = gson.toJson(map);
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"), postData);

        // 发起请求
        AccountService service = MainServiceCreator.create(AccountService.class);
        service.login(requestBody).enqueue(new Callback<BaseLoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseLoginResponse> call
                    , @NonNull Response<BaseLoginResponse> response) {
                BaseLoginResponse base = response.body();
                if (base != null) {
                    parseResponse(base);
                } else {
                    Log.e(TAG, "response body is null");
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseLoginResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "login failure");
            }
        });
    }

    private void parseResponse(BaseLoginResponse base) {
        User user = base.getUser();
        SharedPreferences.Editor editor = getSharedPreferences(
                RUtil.toString(R.string.userInfo_sp_name), MODE_PRIVATE).edit();
        // 存储id
        editor.putInt(RUtil.toString(R.string.id_key), user.getId());
        // 存储用户名
        editor.putString(RUtil.toString(R.string.nickname), user.getNickname());
        // 存储头像
        String headUrl = "http://121.4.199.173:8088/" + user.getHeadUrl();
        editor.putString(RUtil.toString(R.string.head_icon), headUrl);
        // 存储账号
        editor.putString(RUtil.toString(R.string.account_key), user.getAccount());

        editor.apply();
        Toast.makeText(this, RUtil.toString(R.string.login_success), Toast.LENGTH_SHORT).show();
        UserActivity.start(this);
        finish();

    }
}