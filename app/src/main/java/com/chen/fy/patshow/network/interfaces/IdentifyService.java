package com.chen.fy.patshow.network.interfaces;

import com.chen.fy.patshow.data.model.identify.IdentifyResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface IdentifyService {

    /// 景物识别
    @POST("identify")
    @Multipart
    Call<IdentifyResponse> postPhoto(@Part MultipartBody.Part file);
}
