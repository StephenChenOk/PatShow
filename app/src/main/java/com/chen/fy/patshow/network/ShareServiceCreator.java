package com.chen.fy.patshow.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShareServiceCreator {

    private static final String BASE_URL = "http://121.4.199.173:8088/share/";

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static <T> T create(Class<T> tClass) {
        return retrofit.create(tClass);
    }

}
