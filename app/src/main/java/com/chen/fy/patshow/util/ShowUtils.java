package com.chen.fy.patshow.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.XPopupImageLoader;

import java.io.File;

public class ShowUtils {

    /**
     * 界面设置状态栏字体颜色
     */
    public static void changeStatusBarTextImgColor(Activity activity, boolean isBlack) {
        if (isBlack) {
            //设置状态栏黑色字体
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            //恢复状态栏白色字体
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    /// 放大图片
    public static void zoomPicture(Context context, View view, Object url) {
        new XPopup.Builder(context).asImageViewer((ImageView) view, url, new XPopupImageLoader() {
            @Override
            public void loadImage(int position, @NonNull Object uri, @NonNull ImageView imageView) {
                Glide.with(context).load(uri).into(imageView);
            }

            @Override
            public File getImageFile(@NonNull Context context, @NonNull Object uri) {
                try {
                    return Glide.with(context).downloadOnly().load(uri).submit().get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).show();
    }

}
