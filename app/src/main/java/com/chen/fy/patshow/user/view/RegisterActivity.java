package com.chen.fy.patshow.user.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chen.fy.patshow.R;
import com.chen.fy.patshow.network.MainServiceCreator;
import com.chen.fy.patshow.network.interfaces.AccountService;
import com.chen.fy.patshow.user.data.BaseLoginResponse;
import com.chen.fy.patshow.util.RUtil;
import com.chen.fy.patshow.util.ShowUtils;
import com.google.gson.Gson;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity.Log";
    private EditText etUsername;
    private EditText etPwd;
    private EditText etPwd2;

    public static void start(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        ShowUtils.changeStatusBarTextImgColor(this,true);
        initView();

    }

    private void initView() {
        etUsername = findViewById(R.id.et_username);
        etPwd = findViewById(R.id.et_pwd_one);
        etPwd2 = findViewById(R.id.et_pwd_two);

        setEditTextInhibitInputSpace(etUsername);
        setEditTextInhibitInputSpace(etPwd);
        setEditTextInhibitInputSpace(etPwd2);
    }

    private void setClick() {
        findViewById(R.id.btn_register).setOnClickListener(v -> doRegister());
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
    }

    private void doRegister() {
        String account = etUsername.getText().toString();
        String pwd1 = etPwd.getText().toString();
        String pwd2 = etPwd2.getText().toString();
        if (isValidInput(account, pwd1, pwd2)) {
            register(account, pwd1);
        }
    }

    /**
     * 禁止EditText输入空格
     */
    private void setEditTextInhibitInputSpace(EditText editText) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" "))
                    return "";
                else
                    return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                String account = etUsername.getText().toString();
                String pwd1 = etPwd.getText().toString();
                String pwd2 = etPwd2.getText().toString();
                if (isValidInput(account, pwd1, pwd2)) {
                    register(account, pwd1);
                }
                break;
        }
    }

    /// 判断是否是否合法
    private boolean isValidInput(String account, String pwd1, String pwd2) {
        if (account.isEmpty() || pwd1.isEmpty() || pwd2.isEmpty()) {
            Toast.makeText(this, "输入不可为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!pwd1.equals(pwd2)) {
            Toast.makeText(this, "两次密码不相同", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void register(String account, String pwd) {
        final BasePopupView loadingPopup = new XPopup.Builder(this)
                .asLoading("注册中")
                .show();
        loadingPopup.setOnTouchListener((v, event) -> true);

        HashMap<String, String> map = new HashMap<>();
        map.put(RUtil.toString(R.string.account_key), account);
        map.put(RUtil.toString(R.string.password_key), pwd);
        Gson gson = new Gson();
        String postData = gson.toJson(map);
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"), postData);

        // 发起请求
        AccountService service = MainServiceCreator.create(AccountService.class);
        service.register(requestBody).enqueue(new Callback<BaseLoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseLoginResponse> call
                    , @NonNull Response<BaseLoginResponse> response) {
                if (response.body() != null) {
                    loadingPopup.dismiss();
                    Toast.makeText(RegisterActivity.this
                            , RUtil.toString(R.string.register_success), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e(TAG, "response body is null");
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseLoginResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "register failure");
            }
        });
    }
}