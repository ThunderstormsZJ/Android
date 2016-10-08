package cn.spinsoft.wdq.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by hushujun on 15/11/26.
 */
public class BitmapUtils {
    private static final String TAG = BitmapUtils.class.getSimpleName();

    public static Bitmap getMediaFrame(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.MINI_KIND);

        bitmap = ThumbnailUtils.extractThumbnail(bitmap, 720, 387);
        return bitmap;
    }

    public static String cacheBitmap(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }

        String path = Environment.getExternalStorageDirectory().toString();
        int lastPos = path.length() - 1;
        if (path.charAt(lastPos) != '/') {
            path += "/";
        }
        path = path + Long.toString(System.currentTimeMillis()) + ".png";

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        String result = null;

        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }
        try {
            bis = new BufferedInputStream(new ByteArrayInputStream(stream.toByteArray()));
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            byte[] b = new byte[4096];
            int length;
            while ((length = bis.read(b)) != -1) {
                bos.write(b, 0, length);
                bos.flush();
            }
            result = file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    // 读取图像的旋转度
    public static int readBitmapDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    // 旋转图片
    public static Bitmap rotateBitmap(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        bitmap.recycle();
        return resizedBitmap;
    }

    public static void saveBitmap(Bitmap bitmap, String path) {
        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }

        FileOutputStream fOut = null;
        try {
            f.createNewFile();
            fOut = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (fOut != null)
                    fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap decodeThumbBitmap(String path, int viewWidth, int viewHeight) {
        if (TextUtils.isEmpty(path)) {
            LogUtil.e(TAG, "get Image form file,  this path is empty ");
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        //设置为true,表示解析Bitmap对象，该对象不占内存
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        //设置缩放比例
        options.inSampleSize = computeScale(options, viewWidth, viewHeight, false);
        //设置为false,解析Bitmap对象加入到内存中
        options.inJustDecodeBounds = false;
        //返回Bitmap对象
        return BitmapFactory.decodeFile(path, options);
    }

    private static int computeScale(BitmapFactory.Options options, int viewWidth, int viewHeight, boolean clip) {
        int inSampleSize = 1;
        if (viewWidth == 0 || viewWidth == 0) {
            return inSampleSize;
        }
        int bitmapWidth = options.outWidth;
        int bitmapHeight = options.outHeight;
        LogUtil.d(TAG, "decodeClipBitmap::bitmapWidth=" + bitmapWidth + ",bitmapHeight=" + bitmapHeight);
        //假如Bitmap的宽度或高度大于我们设定图片的View的宽高，则计算缩放比例(小数部分收尾)
        if (bitmapWidth > viewWidth || bitmapHeight > viewHeight) {
            int widthScale = Math.round((float) bitmapWidth / (float) viewWidth - 0.5f);
            int heightScale = Math.round((float) bitmapHeight / (float) viewHeight - 0.5f);

            if (clip) {
                inSampleSize = Math.min(widthScale, heightScale);
            } else {
                inSampleSize = Math.max(widthScale, heightScale);
            }
        }
        return inSampleSize;
    }

    public static Bitmap scaleBitmap(String path, int width, int height) {
        if (TextUtils.isEmpty(path)) {
            LogUtil.e(TAG, "get Image form file,  this path is empty ");
            return null;
        }
        int angle = readBitmapDegree(path);

        BitmapFactory.Options options = new BitmapFactory.Options();
        //设置为true,表示解析Bitmap对象，该对象不占内存
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        //设置缩放比例
        options.inSampleSize = computeScale(options, width, height, true);
        //设置为false,解析Bitmap对象加入到内存中
        options.inJustDecodeBounds = false;
        //得到压缩后的bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        if (angle != 0) {
            bitmap = rotateBitmap(angle, bitmap);
        }
        return bitmap;
    }

    /**
     * 压缩并裁剪出适合view显示的bitmap
     *
     * @param path
     * @param viewWidth
     * @param viewHeight
     * @return
     */
    public static Bitmap decodeClipBitmap(String path, int viewWidth, int viewHeight) {
        LogUtil.i(TAG, "decodeClipBitmap:path=" + path + ",to width=" + viewWidth + ",to height=" + viewHeight);
        Bitmap bitmap = scaleBitmap(path, viewWidth, viewHeight);
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        LogUtil.i(TAG, "decodeClipBitmap::bitmapWidth=" + width + ",bitmapHeight=" + height);
//        float outScale = (float) viewWidth / (float) viewHeight;//输出横纵比
//        float bmScale = (float) width / (float) height;//图片横纵比
//        float destScale = 1.0f;
//        int destWidth = width;
//        int destHeight = height;
//        int startX = 0;
//        int startY = 0;
//        if (outScale > bmScale) {//裁剪高度,以宽为标准
//            destScale = width / viewWidth;
//            destHeight = (int) (height * destScale);
//
//        } else if (outScale < bmScale) {//裁剪宽度,以高为标准
//
//        } else {
//
//        }


//        float tempScale = viewWidth / (float) width;//先以图片的width为标准，裁剪height
//        int tempHeight = (int) (viewHeight / tempScale);//反算裁剪时需要的图片高度
//        if (tempHeight > height) {//如果需要的图片高度大于图片实际高度
//            tempScale = viewHeight / (float) height;//以图片的height为标准，裁剪width
//            int tempWidth = (int) (viewWidth / tempScale);//反算裁剪时需要的图片宽度
//            width = tempWidth;
//        } else {
//            height = tempHeight;
//        }
//        int startX = 0;
//        int startY = 0;
//        if (width > viewWidth) {
//            startX = Math.round((width - viewWidth) / 2f - 0.5f);
//        } else if (height > viewHeight) {
//            startY = Math.round((height - viewHeight) / 2f - 0.5f);
//        }
//        Matrix matrix = new Matrix();
//        matrix.postScale(tempScale, tempScale);
//        return Bitmap.createBitmap(bitmap, startX, startY, width, height, matrix, true);
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();

        int wh = w > h ? h : w;// 裁切后所取的正方形区域边长

        int retX = w > h ? (w - h) / 2 : 0;//基于原图，取正方形左上角x坐标
        int retY = w > h ? 0 : (h - w) / 2;

        //下面这句是关键
        return Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null, false);
    }
}
