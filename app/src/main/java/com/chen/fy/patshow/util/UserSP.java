package com.chen.fy.patshow.util;

import android.content.SharedPreferences;

import com.chen.fy.patshow.MyApplication;
import com.chen.fy.patshow.R;

import static android.content.Context.MODE_PRIVATE;

public class UserSP {

    private static SharedPreferences preferences;

    public static SharedPreferences getUserSP() {
        if (preferences == null) {
            String spName = MyApplication.getContext().getResources().getString(R.string.userInfo_sp_name);
            preferences = MyApplication.getContext().getSharedPreferences(spName, MODE_PRIVATE);
        }
        return preferences;
    }


    /// 得到当前账号的id
    public static int getUserID() {
        return UserSP.getUserSP().getInt(RUtil.toString(R.string.id_key)
                , RUtil.toInt(R.integer.no_login));
    }

}
