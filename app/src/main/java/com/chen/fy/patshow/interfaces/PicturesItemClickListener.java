package com.chen.fy.patshow.interfaces;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.chen.fy.patshow.R;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.XPopupImageLoader;

import java.io.File;

public class PicturesItemClickListener implements IOnPicturesItemClickListener {


    @Override
    public void onPicturesItemClick(final ImageView imageView, String url) {
        //2 使用XPopup开始显示放大后的图片
        new XPopup.Builder(imageView.getContext())
                .asImageViewer(imageView, url, new ImageLoader()).show();
    }

    @Override
    public void onPicturesItemClick(ImageView imageView, Uri uri) {
        //2 使用XPopup开始显示放大后的图片
        new XPopup.Builder(imageView.getContext())
                .asImageViewer(imageView, uri, new ImageLoader()).show();
    }

    /**
     * 加载放大后的图片
     */
    public static class ImageLoader implements XPopupImageLoader {
        @Override
        public void loadImage(int position, @NonNull Object url, @NonNull ImageView imageView) {
            //必须指定Target.SIZE_ORIGINAL，否则无法拿到原图，就无法享用天衣无缝的动画
            Glide.with(imageView).load(url).apply(new RequestOptions().placeholder(R.mipmap.ic_launcher_round).override(Target.SIZE_ORIGINAL)).into(imageView);
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
    }
}

interface IOnPicturesItemClickListener {
    /**
     * 点击放大图片
     * @param imageView 所要放大的图片
     * @param url 图片的url
     */
    void onPicturesItemClick(ImageView imageView, String url);

    /**
     * 点击放大图片
     * @param imageView 所要放大的图片
     * @param uri 图片的uri
     */
    void onPicturesItemClick(ImageView imageView, Uri uri);
}
