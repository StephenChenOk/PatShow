package com.chen.fy.patshow.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.chen.fy.patshow.MyApplication;

// 点赞
public class Like {

    private static final String STORAGE = "Rate";

    public static Like get() {
        return new Like();
    }

    private SharedPreferences storage;

    private Like() {
        storage = MyApplication.getContext().getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
    }

    // 是否点赞过
    public boolean isLiked(String itemId) {
        return storage.getBoolean(itemId, false);
    }

    public void setLiked(String itemId, boolean isRated) {
        storage.edit().putBoolean(itemId, isRated).apply();
    }

}
