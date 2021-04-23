package com.chen.fy.patshow.network.interfaces;

import com.chen.fy.patshow.user.data.BaseLoginResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AccountService {

    @POST("user/login")
    Call<BaseLoginResponse> login(@Body RequestBody json);

    @POST("user/register")
    Call<BaseLoginResponse> register(@Body RequestBody json);

}
