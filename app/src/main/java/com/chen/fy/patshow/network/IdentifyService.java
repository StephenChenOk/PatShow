package com.chen.fy.patshow.network;

import com.chen.fy.patshow.model.IdentifyResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface IdentifyService {

    @POST("identify")
    @Multipart
    Call<IdentifyResponse> postPhoto(@Part MultipartBody.Part file);

}
