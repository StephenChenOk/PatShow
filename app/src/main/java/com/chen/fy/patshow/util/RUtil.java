package com.chen.fy.patshow.util;


import com.chen.fy.patshow.MyApplication;

public class RUtil {

    public static String toString(int key){
        return MyApplication.getContext().getResources().getString(key);
    }

    public static int toInt(int key){
        return MyApplication.getContext().getResources().getInteger(key);
    }
}
