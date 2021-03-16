package com.chen.fy.patshow.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class IdentifyServiceCreator {

    private static final String BASE_URL = "http://121.4.199.173:8888";

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static <T> T create(Class<T> tClass) {
        return retrofit.create(tClass);
    }

}
