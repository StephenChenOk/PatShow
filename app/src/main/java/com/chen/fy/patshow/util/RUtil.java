package com.chen.fy.patshow.util;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.chen.fy.patshow.MyApplication;

public class RUtil {

    public static String toString(int key){
        return MyApplication.getContext().getResources().getString(key);
    }

    public static int toInt(int key){
        return MyApplication.getContext().getResources().getInteger(key);
    }

    /// R -> Bitmap
    public static Bitmap RToBitmap(Resources resources, int resource){
        return BitmapFactory.decodeResource(resources, resource);
    }
}
