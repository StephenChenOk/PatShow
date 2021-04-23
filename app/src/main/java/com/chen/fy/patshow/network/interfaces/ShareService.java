package com.chen.fy.patshow.network.interfaces;

import com.chen.fy.patshow.home.share.data.ShareInfo;
import com.chen.fy.patshow.home.share.data.BaseShareResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;


public interface ShareService {

    /// 分享图片
    @POST("share")
    @Multipart
    Call<BaseShareResponse> share(@Part("content") RequestBody content, @Part MultipartBody.Part file);

    /// 获取图片
    @GET("share/list")
    Call<List<ShareInfo>> getList(@Query("user_id") Integer userId);


}
