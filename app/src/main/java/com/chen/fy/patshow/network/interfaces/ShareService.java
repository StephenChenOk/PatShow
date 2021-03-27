package com.chen.fy.patshow.network.interfaces;

import com.chen.fy.patshow.data.model.home.ShareResponse;
import com.chen.fy.patshow.data.model.home.ShareResponseBase;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface ShareService {

    /// 分享图片
    @POST("share")
    @Multipart
    Call<ShareResponseBase> share(@Part("content") RequestBody content, @Part MultipartBody.Part file);

    /// 获取图片
    @GET("list")
    Call<List<ShareResponse>> getList();
}
