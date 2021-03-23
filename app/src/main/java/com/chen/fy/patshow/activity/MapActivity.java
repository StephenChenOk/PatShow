package com.chen.fy.patshow.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chen.fy.patshow.R;

/**
 * 地图
 */
public class MapActivity extends AppCompatActivity {

    public static void start(Context context){
        final Intent intent = new Intent(context,MapActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);
    }
}
