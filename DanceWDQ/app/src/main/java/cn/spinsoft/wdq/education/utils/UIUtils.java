//package cn.spinsoft.wdq.education.utils;
//
//import android.content.Context;
//import android.content.res.ColorStateList;
//import android.content.res.Resources;
//import android.graphics.Bitmap;
//import android.graphics.drawable.Drawable;
//import android.os.Environment;
//import android.view.LayoutInflater;
//import android.view.View;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//import cn.spinsoft.wdq.education.MyApplication;
//
//
//public class UIUtils {
//
//    public static Context getContext() {
//        return MyApplication.getInstance();
//    }
//
//    public static Thread getMainThread() {
//        return MyApplication.getMainThread();
//    }
//
//    public static long getMainThreadId() {
//        return MyApplication.getMainThreadId();
//    }
//
//    /**
//     * dip转换px
//     */
//    public static int dip2px(int dip) {
//        final float scale = getContext().getResources().getDisplayMetrics().density;
//        return (int) (dip * scale + 0.5f);
//    }
//
//    /**
//     * pxz转换dip
//     */
//    public static int px2dip(int px) {
//        final float scale = getContext().getResources().getDisplayMetrics().density;
//        return (int) (px / scale + 0.5f);
//    }
//
//    public static View inflate(int resId) {
//        return LayoutInflater.from(getContext()).inflate(resId, null);
//    }
//
//    /**
//     * 获取资源
//     */
//    public static Resources getResources() {
//        return getContext().getResources();
//    }
//
//    /**
//     * 获取文字
//     */
//    public static String getString(int resId) {
//        return getResources().getString(resId);
//    }
//
//    /**
//     * 获取文字数组
//     */
//    public static String[] getStringArray(int resId) {
//        return getResources().getStringArray(resId);
//    }
//
//    /**
//     * 获取dimen
//     */
//    public static int getDimens(int resId) {
//        return getResources().getDimensionPixelSize(resId);
//    }
//
//    /**
//     * 获取drawable
//     */
//    public static Drawable getDrawable(int resId) {
//        return getResources().getDrawable(resId);
//    }
//
//    /**
//     * 获取颜色
//     */
//    public static int getColor(int resId) {
//        return getResources().getColor(resId);
//    }
//
//    /**
//     * 获取颜色选择器
//     */
//    public static ColorStateList getColorStateList(int resId) {
//        return getResources().getColorStateList(resId);
//    }
//
//    //判断当前的线程是不是在主线程
//    public static boolean isRunInMainThread() {
//        return android.os.Process.myTid() == getMainThreadId();
//    }
//
//    // 获取图片保存名称
//    public static String getCacheFileName() {
//        String fileName = null;
//        File directory = new File(getSdcardPath() + "/cherry");
//        if (!directory.exists()) {
//            directory.mkdirs();
//        }
//        fileName = directory.getAbsolutePath() + "/cache.jpg";
//
//        return fileName;
//    }
//
//    // 获取SD卡路径
//    public static String getSdcardPath() {
//        return Environment.getExternalStorageDirectory().toString();
//    }
//
//    // 保存Bitmap图片到SDcard
//    public static String saveBitmap(Bitmap bitmap) {
//        File file = new File(getFileName());
//        try {
//            FileOutputStream out = new FileOutputStream(file);
//            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
//                out.flush();
//                out.close();
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return file.getAbsolutePath();
//    }
//
//    // 获取图片保存名称
//    public static String getFileName() {
//        String fileName = null;
//        File directory = new File(getSdcardPath() + "/cherry");
//        if (!directory.exists()) {
//            directory.mkdirs();
//        }
//        fileName = directory.getAbsolutePath() + "/"
//                + System.currentTimeMillis() + ".jpg";
//
//        return fileName;
//    }
//
//
//}
//
//
//
