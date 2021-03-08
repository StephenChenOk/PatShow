package com.chen.fy.patshow;

import android.app.Application;
import android.content.Context;

import cn.bmob.v3.Bmob;

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Bmob.initialize(this, "360aa6d4420277421a357d6acfe4651b");
    }

    public static Context getContext(){
        return context;
    }
}
