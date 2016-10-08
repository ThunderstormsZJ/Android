package cn.spinsoft.wdq.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by zhoujun on 2016-4-6.
 */
public class StartActivityUtils {

    /**
     * 开启剪裁界面
     * @param uri
     * @param activity
     */
    public static void startPhotoZoom(Uri uri,int width,int height,Activity activity,Uri outPutUri ) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", width);
        intent.putExtra("aspectY", height);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", false);
        /*intent.putExtra("scale", true);//黑边
        intent.putExtra("scaleUpIfNeeded", true);//黑边*/
        activity.startActivityForResult(intent, Constants.Ints.REQUEST_CODE_IMAGE_CLIPPED);
    }
}
