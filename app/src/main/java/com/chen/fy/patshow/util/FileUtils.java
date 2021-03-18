package com.chen.fy.patshow.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    /// 保存临时图片
    public static String saveTempImage(Context context, RelativeLayout view) {
        // 文件路径
        String filePath = "";
        // 转为bitmap
        Bitmap bitmap = createBitmap(view);
        // 检查有没有存储权限
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, "请至权限中心打开应用权限", Toast.LENGTH_SHORT).show();
        } else {
            // 新建目录appDir，并把图片存到其下
            File appDir = new File(context.getExternalFilesDir(null).getPath()
                    + "/temp");
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            // 新建文件
            String fileName = "temp.jpg";
            File file = new File(appDir, fileName);
            // 保证只存在一个temp文件
            if (file.exists()) {
                file.delete();
            }

            try {
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            filePath = Uri.fromFile(file).getPath();
        }

        return filePath;
    }

    /// 保存View视图对应的图片到手机相册中
    public static String saveViewToGallery(Context context, RelativeLayout view) {
        // 文件路径
        String filePath = "";
        // 转为bitmap
        Bitmap bitmap = createBitmap(view);
        // 检查有没有存储权限
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, "请至权限中心打开应用权限", Toast.LENGTH_SHORT).show();
        } else {
            // 新建目录appDir，并把图片存到其下
            File appDir = new File(context.getExternalFilesDir(null).getPath()
                    + "/identify_image");
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            String fileName = System.currentTimeMillis() + ".jpg";
            File file = new File(appDir, fileName);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 把file里面的图片插入到系统相册中
            try {
                MediaStore.Images.Media.insertImage(context.getContentResolver(),
                        file.getAbsolutePath(), fileName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // 通知相册更新
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();

            // 转化成路径
            filePath = Uri.fromFile(file).getPath();
        }
        return filePath;
    }

    /// View -> Bitmap
    public static Bitmap createBitmap(RelativeLayout view) {
        view.setDrawingCacheEnabled(true);
        //启用DrawingCache并创建位图
        view.buildDrawingCache();
        Bitmap cacheBitmap = view.getDrawingCache();
        //创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        //禁用DrawingCache否则会影响性能
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }
}
